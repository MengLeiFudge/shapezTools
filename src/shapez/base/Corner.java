package shapez.base;

import lombok.Data;

/**
 * 表示图形的一个角.
 * <p>
 * 空角应该用无参构造函数生成的空对象表示，而非null。
 * 同样，空角需要用 {@link #isEmpty()} 判断。
 *
 * @author MengLeiFudge
 */
@Data
public class Corner {
    /**
     * 角的形状.
     */
    public enum CornerShape {
        CIRCLE("C"),
        RECTANGLE("R"),
        WINDMILL("W"),
        STAR("S"),
        NONE("-"),
        /**
         * 表示有形状但不知道具体是哪种形状.
         */
        NOT_NONE("X");

        private final String shortKey;

        CornerShape(String shortKey) {
            this.shortKey = shortKey;
        }

        public static CornerShape getShapeByStr(String s) {
            for (CornerShape shape : CornerShape.values()) {
                if (shape.toString().equals(s)) {
                    return shape;
                }
            }
            throw new IllegalArgumentException("未找到短代码 " + s + " 对应的形状类型！");
        }

        @Override
        public String toString() {
            return shortKey;
        }
    }

    /**
     * 角的颜色.
     */
    public enum CornerColor {
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

        private final String shortKey;

        CornerColor(String shortKey) {
            this.shortKey = shortKey;
        }

        public static CornerColor getColorByStr(String s) {
            for (CornerColor color : CornerColor.values()) {
                if (color.toString().equals(s)) {
                    return color;
                }
            }
            throw new IllegalArgumentException("未找到短代码 " + s + " 对应的颜色类型！");
        }

        @Override
        public String toString() {
            return shortKey;
        }
    }

    private CornerShape shape;
    private CornerColor color;

    /**
     * 构造一个空的角.
     */
    public Corner() {
        this.shape = CornerShape.NONE;
        this.color = CornerColor.NONE;
    }

    /**
     * 通过角的形状和颜色构造指定的角.
     *
     * @param shape 角的形状
     * @param color 角的颜色
     */
    public Corner(CornerShape shape, CornerColor color) {
        if (shape == null || shape == CornerShape.NONE || color == null || color == CornerColor.NONE) {
            this.shape = CornerShape.NONE;
            this.color = CornerColor.NONE;
            return;
        }
        this.shape = shape;
        this.color = color;
    }

    /**
     * 通过角的图形短代码构造指定的角.
     *
     * @param shortKey 角对应的短代码
     */
    public Corner(String shortKey) {
        if (!shortKey.matches("[CRWS][rgbypcuw]|--")) {
            throw new IllegalArgumentException("角形状或颜色错误：" + shortKey);
        }
        this.shape = CornerShape.getShapeByStr(shortKey.substring(0, 1));
        this.color = CornerColor.getColorByStr(shortKey.substring(1, 2));
    }

    public void setShape(CornerShape shape) {
        if (shape == null || shape == CornerShape.NONE) {
            this.shape = CornerShape.NONE;
            this.color = CornerColor.NONE;
            return;
        }
        this.shape = shape;
    }

    public void setColor(CornerColor color) {
        if (color == null || color == CornerColor.NONE) {
            this.shape = CornerShape.NONE;
            this.color = CornerColor.NONE;
            return;
        }
        this.color = color;
    }

    @Override
    public String toString() {
        return shape.toString() + color.toString();
    }

    public boolean equals(Corner corner) {
        return shape == corner.shape && color == corner.color;
    }

    public boolean isEmpty() {
        return shape == CornerShape.NONE;
    }
}
