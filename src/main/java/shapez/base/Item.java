package shapez.base;

import lombok.Data;

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
        if (shortKey.matches("([CRWS][rgbypcuw]|--){4}.*")) {
            return new Shape(shortKey);
        } else {
            return new Color(shortKey);
        }
    }
}
