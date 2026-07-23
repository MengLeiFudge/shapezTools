package shapez2.old;

public enum Color {
    UNCOLORED("u", 0b000),
    RED("r", 0b001),
    GREEN("g", 0b010),
    BLUE("b", 0b100),
    YELLOW("y", 0b011),
    MAGENTA("m", 0b101),
    CYAN("c", 0b110),
    WHITE("w", 0b111),
    //顶针专用
    NONE("-", 0b000);

    private final String shortCode;
    private final int id;

    Color(String shortCode, int id) {
        this.shortCode = shortCode;
        this.id = id;
    }

    public static Color getColorByStr(String s) {
        return switch (s) {
            case "u" -> UNCOLORED;
            case "r" -> RED;
            case "g" -> GREEN;
            case "b" -> BLUE;
            case "y" -> YELLOW;
            case "m" -> MAGENTA;
            case "c" -> CYAN;
            case "w" -> WHITE;
            default -> throw new IllegalArgumentException("未找到短代码 " + s + " 对应的颜色！");
        };
    }

    @Override
    public String toString() {
        return shortCode;
    }
}
