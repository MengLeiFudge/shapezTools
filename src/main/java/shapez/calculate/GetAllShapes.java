package shapez.calculate;

import java.util.HashMap;
import java.util.HashSet;

/**
 * 计算所有可合成的图形样式数目，并输出其合成路径.
 * <p>
 * 由于并不需要图形每个角的形状、颜色，只需要结构，
 * 程序中将使用 1-65535 的 id 表示结构，每个 bit 表示一个角是否存在。
 * 由于java进行位运算时，会先转换成int，所以id使用int而非。
 *
 * @author MengLeiFudge
 */
public class GetAllShapes {
    /**
     * 指示某个id对应的图形是否存在.
     */
    private final boolean[] exists = new boolean[65536];


    /**
     * steps不为-1，表示id存在
     */
    private int[] steps;
    Operate[] operates;
    int[] parent1;
    int[] parent2;


    /**
     * 指示图形合成所需最少步骤的 map.
     * <p>
     * key 表示该图形合成所需最少步骤，value 表示该图形。
     */
    private final HashMap<Integer, SimpleShape> map = new HashMap<>();


    private final HashSet<Integer> set = new HashSet<>();


    public GetAllShapes() {
        for (int i = 1; i < 0b1111; i++) {
           // map.put(1, i);
            set.add(i);
        }
    }


    public void process() {
        long t1 = System.currentTimeMillis();
        int step = 1;
        while (true) {
            boolean updated = false;
            for (int id = 1; id <= 65535; id++) {
                if (steps[id] != step) {
                    continue;
                }
                SimpleShape shape = new SimpleShape(id);
                int r90 = shape.process(Operate.R90).getId();
                if (!exists[r90]) {
                    steps[r90] = step + 1;
                    operates[r90] = Operate.R90;
                    parent1[r90] = id;
                    updated = true;
                }
                int left = shape.process(Operate.LEFT).getId();
                if (left != 0 && !exists[left]) {
                    steps[left] = step + 1;
                    operates[left] = Operate.LEFT;
                    parent1[left] = id;
                    updated = true;
                }
                int right = shape.process(Operate.RIGHT).getId();
                if (right != 0 && !exists[right]) {
                    steps[right] = step + 1;
                    operates[right] = Operate.RIGHT;
                    parent1[right] = id;
                    updated = true;
                }
                for (int id2 = 1; id2 <= 65535; id2++) {
                    if (!exists[id2]) {
                        continue;
                    }
 /*                   int stack1 = stack(id, id2);
                    if (!exists[stack1]) {
                        steps[stack1] = step + 1;
                        operates[stack1] = Operate.ADD;
                        parent1[stack1] = id;
                        parent2[stack1] = id2;
                        num++;
                        show(stack1);
                        updated = true;
                    }
                    int stack2 = stack(id2, id);
                    if (!exists[stack2]) {
                        steps[stack2] = step + 1;
                        operates[stack2] = Operate.ADD;
                        parent1[stack2] = id2;
                        parent2[stack2] = id;
                        num++;
                        show(stack2);
                        updated = true;
                    }*/
                }
            }
            if (updated) {
                //System.out.println("Step" + step + " End, num: " + num);
                step++;
            } else {
                //System.out.println("Finish, num: " + num);
                break;
            }
        }
        long t2 = System.currentTimeMillis();
        System.out.println("用时 " + (t2 - t1) / 1000.0 + " s");
    }


}
