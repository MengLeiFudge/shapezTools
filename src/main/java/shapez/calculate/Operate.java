package shapez.calculate;

/**
 * 指示图形通过何种处理方式得到.
 *
 * @author MengLeiFudge
 */
public enum Operate {
    /**
     * 切割后取左侧.
     */
    LEFT,
    /**
     * 切割后取右侧.
     */
    RIGHT,
    /**
     * 切割后取右上.
     */
    TOP_RIGHT,
    /**
     * 切割后取右下.
     */
    BOTTOM_RIGHT,
    /**
     * 切割后取左下.
     */
    BOTTOM_LEFT,
    /**
     * 切割后取左上.
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

    public static Operate[] values2c1r(){
        return new Operate[]{
                LEFT,
                RIGHT,
                R90,
                STACK
        };
    }
}
