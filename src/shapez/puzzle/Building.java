package shapez.puzzle;

import lombok.Data;

/**
 * @author MengLeiFudge
 */
@Data
public class Building {
    Building(String type, String item, int r, int x, int y) {
        switch (type) {// block, emitter, goal
            case "block" -> {
                this.type = BuildingType.BLOCK;
                // 方块没有图形或颜色
                //this.item = null;
            }
            case "emitter" -> {
                this.type = BuildingType.EMITTER;
                // 生成器可能是图形，也可能是颜色
                //this.item = new FullShape(item);
            }
            case "goal" -> {
                this.type = BuildingType.GOAL;
                // 接收器只能是图形，不能是颜色
                //this.item = new FullShape(item);
            }
            default -> throw new IllegalArgumentException("错误的建筑类型：" + type);
        }
        this.item = item;
        this.r = r;// 0,90,180,-90
        this.x = x;
        this.y = y;
    }

    private BuildingType type;
    //private FullShape item;
    private String item;
    private int r;
    private int x;
    private int y;

    enum BuildingType {
        // 方块
        BLOCK,
        // 生成器
        EMITTER,
        // 接收器
        GOAL,

        // 分离器
        SPLITTER,
        // 切割机
        CUTTER,
        // 旋转器
        ROTATER,
        // 堆叠机
        STACKER,
        // 混色器
        MIXER,
        // 上色器
        PAINTER,
        // 垃圾桶
        TRASH,
        // 传送带
        BELT,
        // 隧道
        TUNNEL
    }
}
