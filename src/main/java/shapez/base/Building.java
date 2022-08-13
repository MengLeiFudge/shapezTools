package shapez.base;

import lombok.Data;

/**
 * @author MengLeiFudge
 */
@Data
public class Building {
    private final BuildingType type;
    /**
     * 如果建筑是生成器或接收器，则item表示生成的/需要的图形.
     */
    private final Shape item;
    /**
     * 角度，可能的值有0,90,180,-90.
     */
    private final int r;
    /**
     * 建筑r=0时左上角的格子所在的横坐标.
     */
    private final int x;
    /**
     * 建筑r=0时左上角的格子所在的纵坐标.
     */
    private final int y;

    /**
     * 从json读取信息时，使用该构造方法.
     * <p>
     * 官方谜题的原点在地图中心，程序中原点在左下角，传入之前需判断是否要转换为一象限坐标。
     *
     * @param type
     * @param item
     * @param r
     * @param x    左下角为原点的建筑位置
     * @param y
     */
    public Building(BuildingType type, Shape item, int r, int x, int y) {
        this.type = type;
        this.item = item;
        this.r = r;
        this.x = x;
        this.y = y;
    }

    public Building(BuildingType type, int r, int x, int y) {
        this(type, null, r, x, y);
    }

    /**
     * 谜题中涉及到的所有建筑类型.
     */
    public enum BuildingType {
        // 方块，r无意义，一直为0
        BLOCK,
        // 生成器，r=0表示生成的物品从上方输出
        EMITTER,
        // 接收器，r=0表示接收的物品从下方输入
        GOAL,
        // 分离器，r=0表示下方输入
        SPLITTER_LEFT,
        SPLITTER_RIGHT,
        // 切割机，r=0表示下方输入
        CUTTER,
        CUTTER_QUAD,
        // 旋转器，r=0表示下方输入
        ROTATER_CW,
        ROTATER_CCW,
        ROTATER_180,
        // 堆叠机，r=0表示下方输入
        STACKER,
        // 混色器，r=0表示下方输入
        MIXER,
        // 上色器，r=0表示左侧输入图形，右上输入染料
        PAINTER,
        // 上色器（镜像），r=0表示左侧输入图形，右下输入染料
        PAINTER_MIRROR,
        // 上色器（双面），r=0表示左侧输入图形，右上输入染料
        PAINTER_DOUBLE,
        // 垃圾桶，r无意义
        TRASH,
        // 传送带，r=0表示下方输入
        BELT_STRAIGHT,
        BELT_LEFT,
        BELT_RIGHT,
        // 隧道入口，r=0表示下方输入
        TUNNEL1_ENTRY,
        // 隧道出口，r=0表示上方输出
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
