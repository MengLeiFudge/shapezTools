package shapez.base;

import lombok.Data;
import shapez.base.Corner.CornerColor;

/**
 * 表示一个染料，包含染料的颜色信息.
 *
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
}
