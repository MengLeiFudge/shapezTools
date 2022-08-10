package shapez.base;

import lombok.Data;

/**
 * 表示图形的一个角.
 * <p>
 * 空角应该用无参的构造函数，而非null。同样，判断是否为空
 *
 * @author MengLeiFudge
 */
@Data
public class Corner {
    /**
     * 角的形状.
     */
    public enum Shape {
        CIRCLE("C"),
        RECTANGLE("R"),
        WINDMILL("W"),
        STAR("S"),
        NONE("-"),
        /**
         * 表示有形状但不知道具体是哪种形状.
         */
        NOT_NONE("X");

        private final String s;

        Shape(String s) {
            this.s = s;
        }

        @Override
        public String toString() {
            return s;
        }
    }

    /**
     * 角的颜色.
     */
    public enum Color {
        UNCOLORED("u"),
        RED("r"),
        GREEN("g"),
        BLUE("b"),
        YELLOW("y"),
        PURPLE("p"),
        CYAN("c"),
        WHITE("w"),
        NONE("-"),
        /**
         * 表示有颜色（等价于有形状）但不知道具体是哪种颜色.
         */
        NOT_NONE("x");

        private final String s;

        Color(String s) {
            this.s = s;
        }

        @Override
        public String toString() {
            return s;
        }
    }

    private Shape bs;
    private Color bc;

    public Corner() {
        this.bs = Shape.NONE;
        this.bc = Color.NONE;
    }

    public Corner(Shape bs, Color bc) {
        if (bs == Shape.NONE || bc == Color.NONE) {
            throw new IllegalArgumentException("空角请使用无参的构造方法！");
        }
        this.bs = bs;
        this.bc = bc;
    }

    public void setBs(Shape bs) {
        if (bs == null || bs == Shape.NONE) {
            this.bc = Color.NONE;
        }
        this.bs = bs;
    }

    public void setBc(Color bc) {
        if (bc == null || bc == Color.NONE) {
            this.bs = Shape.NONE;
        }
        this.bc = bc;
    }

    @Override
    public String toString() {
        return bs.toString() + bc.toString();
    }

    public boolean equals(Corner corner) {
        return bs == corner.bs && bc == corner.bc;
    }

    public boolean isEmpty() {
        return bs == Shape.NONE;
    }
}
