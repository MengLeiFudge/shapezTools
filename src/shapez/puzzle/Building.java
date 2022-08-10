package shapez.puzzle;

import lombok.Data;
import shapez.base.FullShape;

/**
 * @author MengLeiFudge
 */
@Data
public class Building {
    private BuildingType type;
    /**
     * 如果建筑是生成器或接收器，则item表示生成的/需要的图形.
     */
    private FullShape item;
    /**
     * 角度，可能的值有0,90,180,-90.
     */
    private int r;
    private int x;
    private int y;

    /**
     * 从json读取信息时，使用该构造方法.
     * <p>
     * 官方谜题的原点在地图中心，程序中原点在左下角，传入之前需判断是否要转换为一象限坐标。
     *
     * @param type
     * @param item
     * @param r
     * @param x 左下角为原点的建筑位置
     * @param y
     */
    Building(BuildingType type, FullShape item, int r, int x, int y) {
        this.type = type;
        this.item = item;
        this.r = r;
        this.x = x;
        this.y = y;
    }

    Building(BuildingType type, int r, int x, int y) {
        this(type, null, r, x, y);
    }


    enum BuildingType {
        // 方块
        BLOCK,
        // 生成器
        EMITTER,
        // 接收器
        GOAL,
        // 分离器
        SPLITTER_LEFT,
        SPLITTER_RIGHT,
        // 切割机
        CUTTER,
        CUTTER_QUAD,
        // 旋转器
        ROTATER_CW,
        ROTATER_CCW,
        ROTATER_180,
        // 堆叠机
        STACKER,
        // 混色器
        MIXER,
        // 上色器
        PAINTER,
        PAINTER_MIRROR,
        PAINTER_DOUBLE,
        // 垃圾桶
        TRASH,
        // 传送带
        BELT_STRAIGHT,
        BELT_LEFT,
        BELT_RIGHT,
        // 隧道
        TUNNEL1_ENTRY,
        TUNNEL1_EXIT,
        TUNNEL2_ENTRY,
        TUNNEL2_EXIT;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }

        public static BuildingType getBuildingTypeByStr(String str) {
            for (var x : BuildingType.values()) {
                if (x.toString().equals(str)) {
                    return x;
                }
            }
            return null;
        }
    }
}
