package shapez.base;

/**
 * 指示图形通过何种处理方式得到.
 *
 * @author MengLeiFudge
 */
public class Operate {
    /**
     * 指示图形通过何种处理方式得到.
     */
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
