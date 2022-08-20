package shapez.calculate;

import lombok.Data;

/**
 * 表示一个没有角的形状、颜色信息，只有角存在与否信息的图形.
 *
 * @author MengLeiFudge
 */
@Data
public class SimpleShape {
    /**
     * 图形 id.
     * <p>
     * 由于 java 的位运算操作会返回 int，故此处使用 int 而非 short。
     * <p>
     * 可将该值视为一个 16 位的数据，每 4 位表示一层，低位表示低层；
     * 每层从低位到高位依次表示右上、右下、左下、左上。
     */
    private final int id;

    public SimpleShape(int id) {
        this.id = id;
    }

    private static final Operate[] db2C1R = Operate.values2c1r();
    private static final Operate[] dbFull = Operate.values();


    //private ConcurrentHashMap<Operate, ArrayList<SimpleShape>> operateMap = new ConcurrentHashMap<>();

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
                return new SimpleShape(((id & 0x7777) << 1) | ((id & 0x8888) >> 3));
            }
            case 180 -> {
                //0x3333: 0011 0011 0011 0011  0xCCCC: 1100 1100 1100 1100
                return new SimpleShape(((id & 0x3333) << 2) | ((id & 0xCCCC) >> 2));
            }
            case 270 -> {
                //0x1111: 0001 0001 0001 0001  0xEEEE: 1110 1110 1110 1110
                return new SimpleShape(((id & 0x1111) << 1) | ((id & 0xEEEE) >> 3));
            }
            default -> throw new IllegalArgumentException("Invalid angle: " + angle);
        }
    }

    private SimpleShape cut(int... quadrants) {
        if (quadrants == null || quadrants.length == 0) {
            throw new IllegalArgumentException("No quadrants specified");
        }
        int[] quadrant = new int[quadrants.length];
        //for

        return new SimpleShape(0);
    }

    private SimpleShape leftSideBC() {
        //0xCCCC: 1100 1100 1100 1100，取出左半部分
        int id = this.id;
        int ret = 0;
        //层筛选器
        int filter = 0xC;
        while (id != 0) {
            //取出指定层
            int temp = id & filter;
            if (temp > 0) {
                //该层非空，该层直接添加到ret里面，层筛选器上移
                ret |= temp;
                filter <<= 4;
            } else {
                //该层空，原图形砍掉底层
                id >>= 4;
            }
        }
        return new SimpleShape(ret);
    }

    /**
     * 最好使用该方法，因为它稍快一些.
     *
     * @return
     */
    private SimpleShape leftSideAX() {
        int ret = 0;
        //层筛选器
        int filter = 0xC;
        //空层数目
        int q = 0;
        for (int i = 0; i < 4; i++) {
            //取出指定层
            int temp = id & filter;
            if (temp > 0) {
                //该层非空，该层右移空层数后添加到ret里面
                ret |= temp >> q;
            } else {
                //该层空，空层计数增加
                q += 4;
            }
            //层筛选器上移
            filter <<= 4;
        }
        return new SimpleShape(ret);
    }

    /**
     * 三切（去掉右上角），only for mod.
     *
     * @param id
     * @return
     */
    private SimpleShape cutBlac() {
        int ret = 0;
        int filter = 0xE;
        int q = 0;
        for (int i = 0; i < 4; i++) {
            if ((id & filter) > 0) {
                ret |= (id & filter) >> q;
            } else {
                q += 4;
            }
            filter <<= 4;
        }
        return new SimpleShape(ret);
    }

    private SimpleShape stackOn(SimpleShape shape) {
   /*     int id1; int id2;
        //右输入图形放到高16位，表示在上面
        id2 <<= 16;
        //如果未重合，就一直向下移，直到id2碰到地板，或者id2与id1重合
        int i = 0;
        while ((id1 & id2) == 0 && i < 4) {
            id2 >>= 4;
            i++;
        }
        //如果重合，把id2上移一层
        if ((id1 & id2) > 0) {
            id2 <<= 4;
        }
        //除去高于四层的部分
        return id1 | (id2 & 0xFFFF);*/
        return new SimpleShape(0);
    }


    private void show() {
        //System.out.println(Integer.toBinaryString(id));
    }

}
