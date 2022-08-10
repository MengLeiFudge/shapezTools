package shapez.base;

import lombok.Data;
import shapez.base.Corner.Shape;
import shapez.base.Corner.Color;

/**
 * 表示一个图形.
 * <p>
 * 数据结构固定使用四层（无论图形实际有没有四层），但输出时只输出有效层，不输出全空层。
 * <p>
 * 模拟切割或模拟拆分信号输出为空时，使用“--------:--------:--------:--------”而非 null 代替。
 *
 * @author MengLeiFudge
 */
@Data
public class FullShape {
    /**
     * 表示该图形的最简数组，大小为 4 * 4.
     * <p>
     * simplestLayers[0]表示最底层；simplestLayers[3]表示最顶层。
     * <p>
     * simplestLayers[i][0]表示最底层右上角；顺时针方向继续，simplestLayers[i][1]表示最底层右下角，以此类推。
     */
    final Corner[][] simplestLayers;

    /**
     * 表示该图形实际的层数，范围为 0 - 4.
     */
    final int layerNum;

    public FullShape(String shapeStr) {
        String[] data = shapeStr.split(":");
        if (data.length > 4) {
            throw new IllegalStateException("图形至多四层：" + shapeStr);
        }
        Corner[][] layers = new Corner[data.length][4];
        for (int i = 0; i < data.length; i++) {
            String s = data[i];
            if ("--------".equals(s)) {
                throw new IllegalStateException("图形不能有全空层：" + shapeStr);
            }
            if (!s.matches("([CRWS][rgbypcuw]|--){4}")) {
                throw new IllegalStateException("图形形状或颜色错误：" + shapeStr);
            }
            for (int j = 0; j < 4; j++) {
                String shape = s.substring(j * 2, j * 2 + 1);
                String color = s.substring(j * 2 + 1, j * 2 + 2);
                for (Shape bs : Shape.values()) {
                    if (bs.toString().equals(shape)) {
                        for (Color bc : Color.values()) {
                            if (bc.toString().equals(color)) {
                                layers[i][j] = new Corner(bs, bc);
                                break;
                            }
                        }
                        break;
                    }
                }
            }
        }
        simplestLayers = simplifyLayers(layers);
        layerNum = getLayerNum();
    }

    private FullShape(Corner[][] layers) {
        simplestLayers = simplifyLayers(layers);
        layerNum = getLayerNum();
    }

    private Corner[][] simplifyLayers(Corner[][] layers) {
        // 最高为四层，且需要将空层去除
        int maxLayer = 0;
        for (Corner[] layer : layers) {
            if (!isEmptyLayer(layer)) {
                maxLayer++;
            }
        }
        maxLayer = Math.min(maxLayer, 4);
        Corner[][] ret = new Corner[4][4];
        int realI = 0;
        for (Corner[] level : layers) {
            if (realI < maxLayer && !isEmptyLayer(level)) {
                ret[realI] = new Corner[4];
                System.arraycopy(level, 0, ret[realI], 0, level.length);
                realI++;
            }
        }
        for (int i = realI; i < 4; i++) {
            ret[i] = new Corner[4];
            for (int j = 0; j < 4; j++) {
                ret[i][j] = new Corner();
            }
        }
        return ret;
    }

    private boolean isEmptyLayer(Corner[] layer) {
        return layer[0].getBs() == Shape.NONE && layer[1].getBs() == Shape.NONE
                && layer[2].getBs() == Shape.NONE && layer[3].getBs() == Shape.NONE;
    }

    private int getLayerNum() {
        for (int i = 0; i < 4; i++) {
            if (isEmptyLayer(simplestLayers[i])) {
                return i;
            }
        }
        return 4;
    }

    public boolean isEmpty() {
        return layerNum == 0;
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
            for (Corner sc : simplestLayers[i]) {
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
            for (Corner sc : simplestLayers[i]) {
                sb.append(sc.toString());
            }
        }
        return sb.toString();
    }

    public void show() {
        System.out.println("单层表达式：");
        System.out.println(toOneLine());
        System.out.println("多层表达式：");
        System.out.println(toMultipleLines());
    }

    /**
     * 忽略形状和颜色的类型，只关注有没有，返回该图形的ID.
     */
    public int getID() {
        int id = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (simplestLayers[i][j].getBs() != Shape.NONE) {
                    id |= 1 << ((i * 4) + j);
                }
            }
        }
        return id;
    }

    /**
     * 忽略形状和颜色的类型，只关注有没有，返回该图形的ID.
     */
    public FullShape(int id) {
        Corner[][] layers = new Corner[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if ((id & (1 << ((i * 4) + j))) > 0) {
                    layers[i][j] = new Corner(Shape.CIRCLE, Color.UNCOLORED);
                } else {
                    layers[i][j] = new Corner();
                }
            }
        }
        simplestLayers = simplifyLayers(layers);
        layerNum = getLayerNum();
    }

    private Corner[][] getSimplestLayersBackup() {
        Corner[][] ret = new Corner[4][];
        for (int i = 0; i < 4; i++) {
            ret[i] = new Corner[4];
            System.arraycopy(simplestLayers[i], 0, ret[i], 0, 4);
        }
        return ret;
    }


    /**
     * 切割后的左侧部分.
     *
     * @return 切割后的左侧部分
     */
    public FullShape leftSide() {
        Corner[][] ret = getSimplestLayersBackup();
        for (Corner[] sc : ret) {
            sc[0] = new Corner();
            sc[1] = new Corner();
        }
        return new FullShape(ret);
    }

    /**
     * 切割后的右侧部分.
     *
     * @return 切割后的右侧部分
     */
    public FullShape rightSide() {
        Corner[][] ret = getSimplestLayersBackup();
        for (Corner[] sc : ret) {
            sc[2] = new Corner();
            sc[3] = new Corner();
        }
        return new FullShape(ret);
    }

    /**
     * 顺时针旋转90度后的图形.
     *
     * @return 顺时针旋转90度后的图形
     */
    public FullShape rotate90() {
        Corner[][] ret = getSimplestLayersBackup();
        for (Corner[] layer : ret) {
            Corner sc = layer[0];
            layer[0] = layer[3];
            layer[3] = layer[2];
            layer[2] = layer[1];
            layer[1] = sc;
        }
        return new FullShape(ret);
    }

    /**
     * 顺时针旋转180度后的图形.
     *
     * @return 顺时针旋转180度后的图形
     */
    public FullShape rotate180() {
        Corner[][] ret = getSimplestLayersBackup();
        for (Corner[] layer : ret) {
            Corner sc = layer[0];
            layer[0] = layer[2];
            layer[2] = sc;
            sc = layer[1];
            layer[1] = layer[3];
            layer[3] = sc;
        }
        return new FullShape(ret);
    }

    /**
     * 顺时针旋转270度后的图形.
     *
     * @return 顺时针旋转270度后的图形
     */
    public FullShape rotate270() {
        Corner[][] ret = getSimplestLayersBackup();
        for (Corner[] layer : ret) {
            Corner sc = layer[0];
            layer[0] = layer[1];
            layer[1] = layer[2];
            layer[2] = layer[3];
            layer[3] = sc;
        }
        return new FullShape(ret);
    }

    /**
     * 堆叠到另一个图形后的图形.
     *
     * @param lowerFullShape 堆叠的底层图形
     * @return 堆叠到另一个图形后的图形
     */
    public FullShape stackOn(FullShape lowerFullShape) {
        Corner[][] lowerArr = lowerFullShape.simplestLayers;
        Corner[][] upperArr = this.simplestLayers;
        int minInterval = 8;
        for (int col = 0; col < 4; col++) {
            int belowDistance = 4;
            for (int i = 3; i >= 0; i--) {
                if (lowerArr[i][col].getBs() != Shape.NONE) {
                    belowDistance = lowerArr.length - i - 1;
                    break;
                }
            }
            int aboveDistance = 4;
            for (int i = 0; i < 4; i++) {
                if (upperArr[i][col].getBs() != Shape.NONE) {
                    aboveDistance = i;
                    break;
                }
            }
            minInterval = Math.min(minInterval, belowDistance + aboveDistance);
        }
        Corner[][] ret = new Corner[8 - minInterval][];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = new Corner[4];
            if (i < ret.length - upperArr.length) {
                System.arraycopy(lowerArr[i], 0, ret[i], 0, lowerArr[i].length);
            } else if (i >= lowerArr.length) {
                System.arraycopy(upperArr[upperArr.length - ret.length + i], 0, ret[i], 0, upperArr[upperArr.length - ret.length + i].length);
            } else {
                for (int col = 0; col < 4; col++) {
                    if (lowerArr[i][col].getBs() != Shape.NONE) {
                        ret[i][col] = lowerArr[i][col];
                    } else {
                        ret[i][col] = upperArr[upperArr.length - ret.length + i][col];
                    }
                }
            }
        }
        return new FullShape(ret);
    }

    /**
     * 上色后的图形.
     *
     * @param color 要染的颜色
     * @return 上色后的图形
     */
    public FullShape color(Color color) {
        Corner[][] ret = getSimplestLayersBackup();
        for (Corner[] layer : ret) {
            for (Corner sc : layer) {
                if (sc.getBs() != Shape.NONE) {
                    sc.setBc(color);
                }
            }
        }
        return new FullShape(ret);
    }

    /**
     * 拆分后的顶层图形.
     *
     * @return 拆分后的顶层图形
     */
    public FullShape topLayer() {
        Corner[][] ret = getSimplestLayersBackup();
        if (layerNum > 0) {
            for (int i = 0; i < 4; i++) {
                ret[0][i] = ret[layerNum - 1][i];
                ret[1][i] = new Corner();
                ret[2][i] = new Corner();
                ret[3][i] = new Corner();
            }
        }
        return new FullShape(ret);
    }

    /**
     * 拆分后除顶层图形外的图形.
     *
     * @return 拆分后除顶层图形外的图形
     */
    public FullShape otherLayers() {
        Corner[][] ret = getSimplestLayersBackup();
        if (layerNum > 0) {
            for (int i = 0; i < 4; i++) {
                ret[layerNum - 1][i] = new Corner();
            }
        }
        return new FullShape(ret);
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
            if (simplestLayers[layer][i].getBs() != Shape.NONE
                    && simplestLayers[layer + 1][i].getBs() != Shape.NONE) {
                return false;
            }
        }
        return true;
    }
}
