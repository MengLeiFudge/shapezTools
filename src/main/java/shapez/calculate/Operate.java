package shapez.calculate;

/**
 * 指示图形通过何种处理方式得到.
 * <p>
 * 尽管有切割后取左侧部分的操作，但显然半破优于切割，所以不考虑切割。
 *
 * @author MengLeiFudge
 */
public enum Operate {
    /**
     * 图形左侧.
     */
    LEFT,
    /**
     * 图形右侧.
     */
    RIGHT,
    /**
     * 图形右上.
     */
    TOP_RIGHT,
    /**
     * 图形右下.
     */
    BOTTOM_RIGHT,
    /**
     * 图形左下.
     */
    BOTTOM_LEFT,
    /**
     * 图形左上.
     */
    TOP_LEFT,
    /**
     * 顺时针旋转90度.
     */
    R90,
    /**
     * 旋转180度.
     */
    R180,
    /**
     * 逆时针旋转90度.
     */
    R270,
    /**
     * 将两个图形堆叠.
     */
    STACK;

    public static Operate[] values2c1r() {
        return new Operate[]{
                LEFT,
                RIGHT,
                R90,
                STACK
        };
    }
}
