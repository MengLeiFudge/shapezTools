package shapez.calculate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;

/**
 * 计算所有可合成的图形样式数目，并输出其合成路径.
 * <p>
 * 由于并不需要图形每个角的形状、颜色，只需要结构，
 * 程序中将使用 1-65535 的 id 表示结构，每个 bit 表示一个角是否存在。
 * 由于java进行位运算时，会先转换成int，所以id使用int而非。
 * <p>
 * 1.获取所有可合成图形的id
 * <p>
 * 2.根据第一步的id，获取任一一个id的所有“上一步”
 *
 * @author MengLeiFudge
 */
public class GetAllShapes {

    private static final Logger logger = LoggerFactory.getLogger(GetAllShapes.class);

    //private final

    public GetAllShapes() {
    }


    public void process() {
        long t1 = System.currentTimeMillis();

        getAllShapes();

        long t2 = System.currentTimeMillis();
        System.out.println("用时 " + (t2 - t1) / 1000.0 + " s");


    }

    /**
     * 获取所有的可合成图形id.
     */
    private void getAllShapes() {
        // 指示获取某个图形需要的最少步骤。索引为id的steps不为0，表示id存在。
        int[] steps = new int[0x10000];
        // 可合成图形总数
        int num = 0;
        for (int i = 0x0001; i <= 0x000f; i++) {
            steps[i] = 1;
            num++;
        }
        int currentStep = 1;
        while (true) {
            boolean updated = false;
            // todo: 加多线程处理
            for (int id = 0x0001; id <= 0xffff; id++) {
                if (steps[id] != currentStep) {
                    continue;
                }
                SimpleShape shape = new SimpleShape(id);
                for (var x : Operate.values()) {
                    if (x != Operate.STACK) {
                        SimpleShape shapeNew = shape.process(x);
                        int newId = shapeNew.getId();
                        if (newId != 0 && steps[newId] == 0) {
                            steps[newId] = currentStep + 1;
                            updated = true;
                            num++;
                            //logger.info(shape.getIdStr() + " -> " + x + " -> " + shapeNew.getIdStr());
                        }
                    } else {
                        for (int id2 = 0x0001; id2 <= 0xffff; id2++) {
                            if (steps[id2] == 0) {
                                continue;
                            }
                            SimpleShape shape2 = new SimpleShape(id2);
                            SimpleShape shapeNew1 = shape.process(Operate.STACK, shape2);
                            int stack1 = shapeNew1.getId();
                            if (steps[stack1] == 0) {
                                steps[stack1] = currentStep + 1;
                                updated = true;
                                num++;
                                //logger.info(shape.getIdStr() + " on " + shape2.getIdStr() + " -> " + shapeNew1.getIdStr());
                            }
                            SimpleShape shapeNew2 = shape2.process(Operate.STACK, shape);
                            int stack2 = shapeNew2.getId();
                            if (steps[stack2] == 0) {
                                steps[stack2] = currentStep + 1;
                                updated = true;
                                num++;
                                //logger.info(shape2.getIdStr() + " on " + shape.getIdStr() + " -> " + shapeNew2.getIdStr());
                            }
                        }
                    }
                }
            }
            if (updated) {
                System.out.println("Step" + currentStep + " End, num: " + num);
                currentStep++;
            } else {
                System.out.println("Finish, num: " + num);
                break;
            }
        }
    }


}
