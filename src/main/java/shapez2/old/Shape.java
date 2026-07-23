package shapez2.old;

import lombok.Data;

import static spztool.SettingsAndUtils.PATTERN_CORNER;

public enum Shape {
    CIRCLE("C", 0b01),
    RECTANGLE("R", 0b01),
    WINDMILL("W", 0b01),
    STAR("S"),
    CIRCLE("C", 0b01),
    RECTANGLE("R", 0b01),
    WINDMILL("W", 0b01),
    STAR("S", 0b01),
    PIN("P", 0b01),
    CRYSTAL("c", 0b01);

    private final String shortKey;

    Shape(String shortKey) {
        this.shortKey = shortKey;
    }

    public static Shape getShapeByStr(String s) {
        for (Shape shape : Shape.values()) {
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


@Data
public class Shape {

    /**
     * 角的颜色.
     */

    private Shape shape;
    private CornerColor color;

    /**
     * 构造一个空的角.
     */
    public Shape() {
        this.shape = Shape.NONE;
        this.color = CornerColor.NONE;
    }

    /**
     * 通过角的形状和颜色构造指定的角.
     *
     * @param shape 角的形状
     * @param color 角的颜色
     */
    public Shape(Shape shape, CornerColor color) {
        if (shape == null || shape == Shape.NONE || color == null || color == CornerColor.NONE) {
            this.shape = Shape.NONE;
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
    public Shape(String shortKey) {
        if (!PATTERN_CORNER.matcher(shortKey).matches()) {
            throw new IllegalArgumentException("角形状或颜色错误：" + shortKey);
        }
        this.shape = Shape.getShapeByStr(shortKey.substring(0, 1));
        this.color = CornerColor.getColorByStr(shortKey.substring(1, 2));
    }

    public void setShape(Shape shape) {
        if (shape == null || shape == Shape.NONE) {
            this.shape = Shape.NONE;
            this.color = CornerColor.NONE;
            return;
        }
        this.shape = shape;
    }

    public void setColor(CornerColor color) {
        if (color == null || color == CornerColor.NONE) {
            this.shape = Shape.NONE;
            this.color = CornerColor.NONE;
            return;
        }
        this.color = color;
    }

    @Override
    public String toString() {
        return shape.toString() + color.toString();
    }

    public boolean equals(Shape shape) {
        return this.shape == shape.shape && color == shape.color;
    }

    public boolean isEmpty() {
        return shape == Shape.NONE;
    }
}
