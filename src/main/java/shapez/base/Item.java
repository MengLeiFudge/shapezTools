package shapez.base;

import lombok.Data;

import static shapez.SettingsAndUtils.PATTERN_SHAPE;

/**
 * @author MengLeiFudge
 */
@Data
public abstract class Item {
    /**
     * 物品短代码.
     */
    protected String shortKey;

    public Item() {
    }

    public static Item getItemByShortKey(String shortKey) {
        if (PATTERN_SHAPE.matcher(shortKey).matches()) {
            return new Shape(shortKey);
        } else {
            return new Color(shortKey);
        }
    }
}
