package shapez.base;

import lombok.Data;

@Data
public class Corner {
    public enum Shape {
        //圆
        CIRCLE("C"),
        //方
        RECTANGLE("R"),
        //风车
        WINDMILL("W"),
        //星
        STAR("S"),
        //空
        NONE("-"),
        //任意非空
        NOT_NONE("X");

        String s;

        Shape(String s) {
            this.s = s;
        }

        @Override
        public String toString() {
            return s;
        }
    }

    public enum Color {
        //颜色
        RED("r"),
        GREEN("g"),
        BLUE("b"),
        YELLOW("y"),
        PURPLE("p"),
        CYAN("c"),
        UNCOLORED("u"),
        WHITE("w"),
        //空
        NONE("-"),
        //任意颜色
        NOT_NONE("x");

        String s;

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

    public Corner(Shape bs, Color bc) {
        this.bs = bs;
        this.bc = bc;
    }

    @Override
    public String toString() {
        return bs.toString() + bc.toString();
    }
}
