package shapez.base;

import lombok.Data;
import shapez.base.Corner.CornerColor;

/**
 * @author MengLeiFudge
 */
@Data
public class Color extends Item {
    private final CornerColor cornerColor;

    public Color(String shortKey) {
        super();
        switch (shortKey) {
            case "uncolored" -> cornerColor = CornerColor.UNCOLORED;
            case "red" -> cornerColor = CornerColor.RED;
            case "green" -> cornerColor = CornerColor.GREEN;
            case "blue" -> cornerColor = CornerColor.BLUE;
            case "yellow" -> cornerColor = CornerColor.YELLOW;
            case "purple" -> cornerColor = CornerColor.PURPLE;
            case "cyan" -> cornerColor = CornerColor.CYAN;
            case "white" -> cornerColor = CornerColor.WHITE;
            default -> throw new IllegalArgumentException("错误的颜色短代码：" + shortKey);
        }
        this.shortKey = shortKey;
    }

/*    public Corner.CornerColor getCornerColor() {
        for (var cc : Corner.CornerColor.values()) {
            if (cc.toString().equals(shortKey)) {
                return cc;
            }
        }
        throw new IllegalArgumentException("错误的颜色短代码：" + shortKey);
        //return Corner.CornerColor.NONE;
    }*/
}
