package shapez.calculate;

/**
 * 表示一个没有角的形状、颜色信息，只有角存在与否信息的图形.
 *
 * @param id 图形 id.
 *           <p>
 *           由于 java 的位运算操作会返回 int，故此处使用 int 而非 short。
 *           <p>
 *           可将该值视为一个 16 位的数据，每 4 位表示一层，低位表示低层；
 *           每层从低位到高位依次表示右上、右下、左下、左上。
 * @author MengLeiFudge
 */
public record SimpleShape(int id) {
    public SimpleShape process(Operate operate, SimpleShape... shape) {
        return switch (operate) {
            case LEFT -> cut(3, 4);
            case RIGHT -> cut(1, 2);
            case TOP_RIGHT -> cut(1);
            case BOTTOM_RIGHT -> cut(2);
            case BOTTOM_LEFT -> cut(3);
            case TOP_LEFT -> cut(4);
            case R90 -> rotate(90);
            case R180 -> rotate(180);
            case R270 -> rotate(270);
            case STACK -> stackOn(shape[0]);
        };
    }

    /**
     * 返回旋转后的图形.
     * <p>
     * 以顺时针旋转 90 度为例，应该将每层表示右上、右下、左下的 bit 左移 1 位，表示左上的 bit 右移 3 位。
     *
     * @param angle 旋转角度
     * @return 旋转后的图形
     */
    private SimpleShape rotate(int angle) {
        switch (angle) {
            case 90 -> {
                //0x7777: 0111 0111 0111 0111  0x8888: 1000 1000 1000 1000
                return new SimpleShape(((id & 0x7777) << 1) | ((id & 0x8888) >>> 3));
            }
            case 180 -> {
                //0x3333: 0011 0011 0011 0011  0xCCCC: 1100 1100 1100 1100
                return new SimpleShape(((id & 0x3333) << 2) | ((id & 0xCCCC) >>> 2));
            }
            case 270 -> {
                //0x1111: 0001 0001 0001 0001  0xEEEE: 1110 1110 1110 1110
                return new SimpleShape(((id & 0x1111) << 3) | ((id & 0xEEEE) >>> 1));
            }
            default -> throw new IllegalArgumentException("Invalid angle: " + angle);
        }
    }

    /**
     * 返回切割后的图形.
     *
     * @param quadrants 需要保留的象限
     * @return 切割后的图形
     */
    private SimpleShape cut(int... quadrants) {
        if (quadrants == null || quadrants.length == 0) {
            throw new IllegalArgumentException("象限未定义");
        }
        int filter = 0;
        for (var x : quadrants) {
            if (x < 1 || x > 4) {
                throw new IllegalArgumentException("象限不是1-4");
            }
            filter |= (0b0001 << (x - 1));
        }
        // AX的速度更快一些
        return cutAX(filter);
    }

    /**
     * BC提供的切割方法.
     *
     * @param filter 层筛选器
     * @return 根据层筛选器切割后的最简图形的id
     */
    private SimpleShape cutBC(int filter) {
        int id = this.id;
        int ret = 0;
        while (id != 0) {
            // 取出指定层
            int temp = id & filter;
            if (temp > 0) {
                // 该层非空，该层直接添加到ret里面，层筛选器上移
                ret |= temp;
                filter <<= 4;
            } else {
                // 该层空，原图形砍掉底层
                id >>>= 4;
            }
        }
        return new SimpleShape(ret);
    }

    /**
     * AX提供的切割方法，稍快一些.
     *
     * @param filter 层筛选器
     * @return 根据层筛选器切割后的最简图形的id
     */
    private SimpleShape cutAX(int filter) {
        int ret = 0;
        // 空层数目
        int q = 0;
        for (int i = 0; i < 4; i++) {
            // 取出指定层
            int temp = id & filter;
            if (temp > 0) {
                // 该层非空，该层右移空层数后添加到ret里面
                ret |= temp >>> q;
            } else {
                // 该层空，空层计数增加
                q += 4;
            }
            //层筛选器上移
            filter <<= 4;
        }
        return new SimpleShape(ret);
    }

    /**
     * 返回当前图形堆叠在传入参数对应图形上的图形.
     *
     * @param bottomShape 堆叠的底层图形
     * @return 堆叠后的图形
     */
    private SimpleShape stackOn(SimpleShape bottomShape) {
        // 上层图形放到高16位
        int top = this.id << 16;
        int bottom = bottomShape.id;
        // 将上层图形下移，直至碰到地板（次数到达4次），或者上下图形重合
        for (int i = 0; i < 4; i++) {
            // 必须用无符号右移>>>
            top >>>= 4;
            // 如果有重合部分，则将上层图形上移
            if ((bottom & top) != 0) {
                top <<= 4;
                break;
            }
        }
        // 上层图形除去高于四层的部分后，与下层图形合到一起
        return new SimpleShape(bottom | (top & 0xFFFF));
    }

    public String getIdStr() {
        String full = Integer.toBinaryString(id | 0x00010000).substring(1);
        return full.substring(0, 4) + " " + full.substring(4, 8) + " " + full.substring(8, 12) + " " + full.substring(12);
    }
}
