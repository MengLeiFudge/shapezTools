package shapez.base;

public class Operate {
    public enum BaseOperate {
        // 基类
        BASE,
        // 切割后的左边
        LEFT,
        // 切割后的右边
        RIGHT,
        // 顺时针90度
        R90,
        // 不需要的操作
        R180,
        // 不需要的操作
        R270,
        // 堆叠
        ADD
    }
}
