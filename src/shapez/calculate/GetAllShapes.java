package shapez.calculate;

import shapez.base.Operate.BaseOperate;

import java.util.Arrays;

/**
 * 计算所有可合成的图形样式数目，并输出其合成路径.
 * <p>
 * <ul>
 *     <li>图形样式将忽略角的形状、颜色的影响，全部使用 Cu 表示</li>
 *     <li>为加快计算速度，图形使用 1-65535 的 id 表示，每个 bit 表示一个角是否存在</li>
 * </ul>
 * <p>
 * 由于java进行位运算时，会先转换成int，所以id使用int而非。
 */
public class GetAllShapes {
    /**
     * steps不为-1，表示id存在
     */
    int[] steps;
    BaseOperate[] operates;
    int[] parent1;
    int[] parent2;

    public GetAllShapes() {
        steps = new int[65536];
        Arrays.fill(steps, -1);
        operates = new BaseOperate[65536];
        Arrays.fill(operates, BaseOperate.BASE);
        parent1 = new int[65536];
        parent2 = new int[65536];
    }

    private int rotate90(int id) {
        //0x7777: 0111 0111 0111 0111  0x8888: 1000 1000 1000 1000
        return ((id & 0x7777) << 1) | ((id & 0x8888) >> 3);
    }

    private int rotate180(int id) {
        //0x3333: 0011 0011 0011 0011  0xCCCC: 1100 1100 1100 1100
        return ((id & 0x3333) << 2) | ((id & 0xCCCC) >> 2);
    }

    private int rotate270(int id) {
        //0x1111: 0001 0001 0001 0001  0xEEEE: 1110 1110 1110 1110
        return ((id & 0x1111) << 3) | ((id & 0xEEEE) >> 1);
    }

    private int leftSideBC(int id) {
        id &= 0x3333;
        int ret = 0;
        int filter = 0xF;
        for (int i = 0; i < 4; i++) {
            if (id == 0) {
                break;
            }
            int temp = id & filter;
            if (temp > 0) {
                ret |= temp;
                filter <<= 4;
            } else {
                id >>= 4;
            }
        }
        return ret;
    }

    private int leftSideAX(int id) {
        int ret = 0;
        int filter = 0x3;
        int q = 0;
        for (int i = 0; i < 4; i++) {
            if ((id & filter) > 0) {
                ret |= (id & filter) >> q;
            } else {
                q += 4;
            }
            filter <<= 4;
        }
        return ret;
    }

    private int rightSideBC(int id) {
        id &= 0xCCCC;
        int ret = 0;
        int filter = 0xF;
        for (int i = 0; i < 4; i++) {
            if (id == 0) {
                break;
            }
            int temp = id & filter;
            if (temp > 0) {
                ret |= temp;
                filter <<= 4;
            } else {
                id >>= 4;
            }
        }
        return ret;
    }

    private int rightSideAX(int id) {
        int ret = 0;
        int filter = 0xC;
        int q = 0;
        for (int i = 0; i < 4; i++) {
            if ((id & filter) > 0) {
                ret |= (id & filter) >> q;
            } else {
                q += 4;
            }
            filter <<= 4;
        }
        return ret;
    }

    /**
     * only for mod.
     *
     * @param id
     * @return
     */
    private int cutBlac(int id) {
        int ret = 0;
        int filter = 0xE;
        int q = 0;
        for (int i = 0; i < 4; i++) {
            if ((id & filter) > 0) {
                ret |= (id & filter) >> q;
            } else {
                q += 4;
            }
            filter <<= 4;
        }
        return ret;
    }

    private int stack(int id1, int id2) {
        //右输入图形放到高16位，表示在上面
        id2 <<= 16;
        //如果未重合，就一直向下移，直到id2碰到地板，或者id2与id1重合
        int i = 0;
        while ((id1 & id2) == 0 && i < 4) {
            id2 >>= 4;
            i++;
        }
        //如果重合，把id2上移一层
        if ((id1 & id2) > 0) {
            id2 <<= 4;
        }
        //除去高于四层的部分
        return id1 | (id2 & 0xFFFF);
    }

    public void process() {
        long t1 = System.currentTimeMillis();
        for (int i = 1; i <= 15; i++) {
            steps[i] = 0;
            operates[i] = BaseOperate.BASE;
        }
        int step = 0;
        int num = 15;
        while (true) {
            boolean updated = false;
            for (int id = 1; id <= 65535; id++) {
                if (steps[id] != step) {
                    continue;
                }
                int r90 = rotate90(id);
                if (steps[r90] == -1) {
                    steps[r90] = step + 1;
                    operates[r90] = BaseOperate.R90;
                    parent1[r90] = id;
                    num++;
                    show(r90);
                    updated = true;
                }
                int r180 = rotate180(id);
                if (steps[r180] == -1) {
                    steps[r180] = step + 1;
                    operates[r180] = BaseOperate.R180;
                    parent1[r180] = id;
                    num++;
                    show(r180);
                    updated = true;
                }
                int r270 = rotate270(id);
                if (steps[r270] == -1) {
                    steps[r270] = step + 1;
                    operates[r270] = BaseOperate.R270;
                    parent1[r270] = id;
                    num++;
                    show(r270);
                    updated = true;
                }
                int left = leftSideAX(id);
                if (left != 0 && steps[left] == -1) {
                    steps[left] = step + 1;
                    operates[left] = BaseOperate.LEFT;
                    parent1[left] = id;
                    num++;
                    show(left);
                    updated = true;
                }
                int right = rightSideAX(id);
                if (right != 0 && steps[right] == -1) {
                    steps[right] = step + 1;
                    operates[right] = BaseOperate.RIGHT;
                    parent1[right] = id;
                    num++;
                    show(right);
                    updated = true;
                }
                for (int id2 = 1; id2 <= 65535; id2++) {
                    if (steps[id2] == -1) {
                        continue;
                    }
                    int stack1 = stack(id, id2);
                    if (steps[stack1] == -1) {
                        steps[stack1] = step + 1;
                        operates[stack1] = BaseOperate.ADD;
                        parent1[stack1] = id;
                        parent2[stack1] = id2;
                        num++;
                        show(stack1);
                        updated = true;
                    }
                    int stack2 = stack(id2, id);
                    if (steps[stack2] == -1) {
                        steps[stack2] = step + 1;
                        operates[stack2] = BaseOperate.ADD;
                        parent1[stack2] = id2;
                        parent2[stack2] = id;
                        num++;
                        show(stack2);
                        updated = true;
                    }
                }
            }

            if (updated) {
                System.out.println("Step" + step + " End, num: " + num);
                step++;
            } else {
                System.out.println("Fin, num: " + num);
                break;
            }
        }
        long t2 = System.currentTimeMillis();
        System.out.println("用时" + (t2 - t1) / 1000 + "s" + (t2 - t1) % 1000 + "ms");
    }

    private void show(int id) {
        System.out.println(Integer.toBinaryString(id));
    }

}
