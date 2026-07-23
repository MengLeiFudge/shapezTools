package shapez2.old;

import lombok.Data;
import shapez.base.Corner.CornerColor;
import shapez.base.Corner.CornerShape;

import java.util.Arrays;

/**
 * 表示一个图形，包含所有角的形状和颜色信息.
 *
 * @author MengLeiFudge
 */
@Data
public class Shape111 {
    /**
     * 指示当前图形所有角的形状和颜色信息.
     * <p>
     * 无论图形实际有几层，该数组的大小都是 4 * 4。
     * <p>
     * corners[0]表示最底层；corners[3]表示最顶层。
     * <p>
     * corners[0][0]表示最底层右上角；corners[0][1]表示最底层右下角；顺时针方向以此类推。
     */
    final Shape[][] shapes;

    /**
     * 返回当前图形 {@link #shapes} 的深拷贝.
     *
     * @return 当前图形 {@link #shapes} 的深拷贝
     */
    private Shape[][] getCornersClone() {
        Shape[][] clone = new Shape[4][4];
        for (int i = 0; i < 4; i++) {
            System.arraycopy(shapes[i], 0, clone[i], 0, 4);
        }
        return clone;
    }

    /**
     * 表示当前图形层数，范围为 0 - 4.
     */
    final int layerNum;

    /**
     * 返回当前图形的层数.
     * <p>
     * 调用构造函数时，在 {@link #shapes} 赋值后，应该使用该方法获取图形层数，并将返回值赋值给 {@link #layerNum}。
     *
     * @return 当前图形的层数
     */
    private int getLayerNum() {
        assert shapes != null;
        for (int i = 0; i < 4; i++) {
            if (isEmptyLayer(shapes[i])) {
                return i;
            }
        }
        return 4;
    }

    /**
     * 通过图形短代码构建图形.
     *
     * @param shortKey 图形短代码
     * @throws IllegalArgumentException 如果图形短代码不合规
     */
    protected Shape111(String shortKey) {
        super();
        String[] layers = shortKey.split(":");
        Shape[][] shapes = new Shape[4][4];
        for (int i = 0; i < 4; i++) {
            // 超过的层数使用空角填充
            if (i >= layers.length) {
                Arrays.fill(shapes[i], new Shape());
                continue;
            }
            // 未超过的层数使用单层数据填充
            String layer = layers[i];
            if ("--------".equals(layer)) {
                throw new IllegalArgumentException("图形不能有全空层：" + shortKey);
            }
            for (int j = 0; j < 4; j++) {
                shapes[i][j] = new Shape(layer.substring(j * 2, j * 2 + 2));
            }
        }
        this.shapes = shapes;
        this.layerNum = getLayerNum();
        this.shortKey = shortKey;
    }

    /**
     * 通过角的信息数组构建图形.
     *
     * @param shapes 角的信息数组，必须已经去除全空层
     */
    private Shape111(Shape[][] shapes) {
        super();
        this.shapes = shapes;
        this.layerNum = getLayerNum();
        this.shortKey = toOneLine();
    }

    /**
     * 通过图形 id 构建图形.
     *
     * @param id 图形 id，范围 0 - 65535，不能存在全空层
     * @throws IllegalArgumentException 如果 id 对应的图形存在全空层
     */
    public Shape111(int id) {
        Shape[][] layers = new Shape[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if ((id & (1 << ((i * 4) + j))) > 0) {
                    layers[i][j] = new Shape(CornerShape.NOT_NONE, CornerColor.NOT_NONE);
                } else {
                    layers[i][j] = new Shape();
                }
            }
        }
        simplifyCorners(layers);
        this.shapes = layers;
        this.layerNum = getLayerNum();
        if (getId() != id) {
            throw new IllegalArgumentException("id 为 " + id + " 的图形存在全空层！");
        }
        this.shortKey = toOneLine();
    }

    /**
     * 将传入的角的信息数组处理为标准数组.
     * <p>
     * 处理后，数组中的全空层会被移除，顶部使用空角填充。
     *
     * @param shapes 角的信息数组
     */
    private void simplifyCorners(Shape[][] shapes) {
        assert shapes.length == 4;
        // 从下往上遍历，移动所有非空层
        int newLayer = 0;
        for (int i = 0; i < 4; i++) {
            if (!isEmptyLayer(shapes[i])) {
                // 将这一层放在第 newLayer 层（如果层数相同就不用放了）
                if (i != newLayer) {
                    System.arraycopy(shapes[i], 0, shapes[newLayer], 0, shapes[i].length);
                }
                newLayer++;
            }
        }
        // 未处理的层使用空角覆盖
        for (; newLayer < 4; newLayer++) {
            for (int j = 0; j < 4; j++) {
                shapes[newLayer][j] = new Shape();
            }
        }
    }

    /**
     * 返回该图形是否为空图形.
     *
     * @return 如果该图形是空图形，返回 true；否则返回 false
     */
    public boolean isEmpty() {
        return layerNum == 0;
    }

    /**
     * 返回传入的层是否为全空层.
     *
     * @param layer 需要进行判断的层
     * @return 如果传入的层的每个角都是空角，返回 true；否则返回 false.
     */
    public static boolean isEmptyLayer(Shape[] layer) {
        return layer[0].isEmpty() && layer[1].isEmpty() && layer[2].isEmpty() && layer[3].isEmpty();
    }

    /**
     * 返回当前图形传入层数对应的层是否为全空层.
     *
     * @param layer 需要进行判断的层，0最低，3最高
     * @return 如果传入的层的每个角都是空角，返回 true；否则返回 false.
     * @throws IllegalArgumentException 如果传入的层数不合规
     */
    public boolean isEmptyLayer(int layer) {
        if (layer < 0 || layer > 3) {
            throw new IllegalArgumentException("传入的层数只能是 0-3！");
        }
        return isEmptyLayer(shapes[layer]);
    }

    /**
     * 忽略形状和颜色的类型，只关注有没有，返回该图形的ID.
     */
    public int getId() {
        assert shapes != null && shapes.length == 4;
        return getId(shapes);
    }

    /**
     * 忽略形状和颜色的类型，只关注有没有，返回该图形的ID.
     */
    public static int getId(Shape[][] shapes) {
        assert shapes != null && shapes.length == 4;
        int id = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (shapes[i][j].getShape() != CornerShape.NONE) {
                    id |= 1 << ((i * 4) + j);
                }
            }
        }
        return id;
    }

    /**
     * 以表达式的形式显示该图形.
     */
    public String toOneLine() {
        if (layerNum == 0) {
            return "空";
        }
        boolean isFirst = true;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < layerNum; i++) {
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(":");
            }
            for (Shape sc : shapes[i]) {
                sb.append(sc.toString());
            }
        }
        return sb.toString();
    }

    /**
     * 以一行表示一层的形式显示该图形.
     */
    public String toMultipleLines() {
        if (layerNum == 0) {
            return "空";
        }
        boolean isFirst = true;
        StringBuilder sb = new StringBuilder();
        for (int i = layerNum - 1; i >= 0; i--) {
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append("\n");
            }
            for (Shape sc : shapes[i]) {
                sb.append(sc.toString());
            }
        }
        return sb.toString();
    }

    /**
     * 以单层表达式和多层表达式输出该图形.
     */
    public void show() {
        System.out.println("单层表达式：");
        System.out.println(toOneLine());
        System.out.println("多层表达式：");
        System.out.println(toMultipleLines());
    }

    /**
     * 切割后的图形.
     *
     * @param leftSide 如果为 true，返回切割后的左侧部分；否则返回右侧部分
     * @return 切割后的图形
     */
    public Shape111 cut(boolean leftSide) {
        Shape[][] clone = getCornersClone();
        for (Shape[] shapes : clone) {
            if (leftSide) {
                shapes[0] = new Shape();
                shapes[1] = new Shape();
            } else {
                shapes[2] = new Shape();
                shapes[3] = new Shape();
            }
        }
        simplifyCorners(clone);
        return new Shape111(clone);
    }

    /**
     * 四切后的图形.
     *
     * @param index 位置索引，0表示右上，1表示右下，2表示左下，3表示左上
     * @return 切割后的左侧部分
     * @throws IllegalArgumentException 如果传入的位置索引不合规
     */
    public Shape111 cutQuad(int index) {
        if (index < 0 || index > 3) {
            throw new IllegalArgumentException("传入的层数只能是 0-3！");
        }
        Shape[][] clone = getCornersClone();
        for (Shape[] shapes : clone) {
            for (int i = 0; i < 4; i++) {
                if (i != index) {
                    shapes[i] = new Shape();
                }
            }
        }
        simplifyCorners(clone);
        return new Shape111(clone);
    }

    /**
     * 旋转后的图形.
     *
     * @param angle 旋转角度，正负都可以，必须是90的倍数
     * @return 旋转后的图形
     * @throws IllegalArgumentException 如果传入的角度不是90的倍数
     */
    public Shape111 rotate(int angle) {
        // java 取余是先对绝对值取余，再加原数的正负号
        if (angle % 90 != 0) {
            throw new IllegalArgumentException("传入的角度只能是 90 的倍数（正负不限）！");
        }
        // 负数角度处理至正数
        if (angle < 0) {
            angle = angle + (-angle / 360 + 1) * 360;
        }
        // 正数角度处理至 0 至 270
        angle = angle % 360;
        assert angle == 0 || angle == 90 || angle == 180 || angle == 270;
        Shape[][] ret = getCornersClone();
        for (Shape[] layer : ret) {
            Shape sc = layer[0];
            switch (angle) {
                case 90 -> {
                    layer[0] = layer[3];
                    layer[3] = layer[2];
                    layer[2] = layer[1];
                    layer[1] = sc;
                }
                case 180 -> {
                    layer[0] = layer[2];
                    layer[2] = sc;
                    sc = layer[1];
                    layer[1] = layer[3];
                    layer[3] = sc;
                }
                case 270 -> {
                    layer[0] = layer[1];
                    layer[1] = layer[2];
                    layer[2] = layer[3];
                    layer[3] = sc;
                }
            }
        }
        return new Shape111(ret);
    }

    /**
     * 堆叠到另一个图形后的图形.
     *
     * @param lowerShape 堆叠的底层图形
     * @return 堆叠到另一个图形后的图形
     */
    public Shape111 stackOn(Shape111 lowerShape) {
        Shape[][] lowerShapes = lowerShape.getCornersClone();
        // 先获取两个图形的最小间距，即上方图形可以下移的格数。该间距至多为4
        int minInterval = 4;
        for (int col = 0; col < 4; col++) {
            // belowDistance 指下方图形在 col 列的
            int belowDistance = 4;
            for (int i = 3; i >= 0; i--) {
                if (!lowerShapes[i][col].isEmpty()) {
                    belowDistance = lowerShapes.length - i - 1;
                    break;
                }
            }
            int aboveDistance = 4;
            for (int i = 0; i < 4; i++) {
                if (!shapes[i][col].isEmpty()) {
                    aboveDistance = i;
                    break;
                }
            }
            minInterval = Math.min(minInterval, belowDistance + aboveDistance);
        }
        for (int i = 0; i < lowerShapes.length; i++) {
            if (i < lowerShapes.length - shapes.length) {
                System.arraycopy(lowerShapes[i], 0, lowerShapes[i], 0, lowerShapes[i].length);
            } else if (i >= lowerShapes.length) {
                System.arraycopy(shapes[shapes.length - lowerShapes.length + i], 0, lowerShapes[i], 0, shapes[shapes.length - lowerShapes.length + i].length);
            } else {
                for (int col = 0; col < 4; col++) {
                    if (lowerShapes[i][col].getShape() != CornerShape.NONE) {
                        lowerShapes[i][col] = lowerShapes[i][col];
                    } else {
                        lowerShapes[i][col] = shapes[shapes.length - lowerShapes.length + i][col];
                    }
                }
            }
        }
        return new Shape111(lowerShapes);
    }

    /**
     * 上色后的图形.
     *
     * @param cornerColor 要染的颜色
     * @return 上色后的图形
     */
    public Shape111 color(CornerColor cornerColor) {
        Shape[][] clone = getCornersClone();
        for (Shape[] layer : clone) {
            for (Shape shape : layer) {
                if (!shape.isEmpty()) {
                    shape.setColor(cornerColor);
                }
            }
        }
        return new Shape111(clone);
    }

    /**
     * 拆分后的顶层图形.
     *
     * @return 拆分后的顶层图形
     */
    public Shape111 topLayer() {
        Shape[][] clone = getCornersClone();
        if (layerNum > 0) {
            for (int i = 0; i < 4; i++) {
                clone[0][i] = clone[layerNum - 1][i];
                clone[1][i] = new Shape();
                clone[2][i] = new Shape();
                clone[3][i] = new Shape();
            }
        }
        return new Shape111(clone);
    }

    /**
     * 拆分后除顶层图形外的图形.
     *
     * @return 拆分后除顶层图形外的图形
     */
    public Shape111 otherLayers() {
        Shape[][] clone = getCornersClone();
        if (layerNum > 0) {
            for (int i = 0; i < 4; i++) {
                clone[layerNum - 1][i] = new Shape();
            }
        }
        return new Shape111(clone);
    }

    /**
     * 返回该图形是否为悬空图形.
     *
     * @return 该图形是否为悬空图形
     */
    public boolean isSuspend() {
        if (layerNum < 2) {
            return false;
        }
        for (int i = 0; i < layerNum - 1; i++) {
            if (isSuspend(i)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 返回第 layer 层与第 layer + 1 层是否为悬空.
     *
     * @param layer 要判断悬空的层，0123
     * @return 第 layer 层与第 layer + 1 层是否为悬空
     */
    public boolean isSuspend(int layer) {
        if (layer > layerNum - 2) {
            return false;
        }
        for (int i = 0; i < 4; i++) {
            if (!shapes[layer][i].isEmpty() && !shapes[layer + 1][i].isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
