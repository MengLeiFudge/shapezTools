package shapez.calculate;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

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
    private static final File SHAPES_FILE = FileUtils.getFile("shape database", "shapes.json");
    private List<Integer> allShapes;

    public GetAllShapes() {
    }

    public void process() {
        if (getAllShapesByFile()) {
            return;
        }
        getAllShapesByCalculate();
    }

    private boolean getAllShapesByFile() {
        if (SHAPES_FILE.exists()) {
            try {
                String allShapesStr = FileUtils.readFileToString(SHAPES_FILE, StandardCharsets.UTF_8);
                allShapes = JSON.parseArray(allShapesStr, Integer.class);
                logger.info("num: " + allShapes.size());
                return true;
            } catch (IOException e) {
                logger.error("", e);
            }
        }
        return false;
    }

    /**
     * 获取所有的可合成图形id.
     */
    private void getAllShapesByCalculate() {
        long t1 = System.currentTimeMillis();
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
            for (int id = 0x0001; id <= 0xffff; id++) {
                if (steps[id] != currentStep) {
                    continue;
                }
                SimpleShape shape = new SimpleShape(id);
                for (var x : Operate.values()) {
                    if (x != Operate.STACK) {
                        SimpleShape shapeNew = shape.process(x);
                        int newId = shapeNew.id();
                        if (newId != 0 && steps[newId] == 0) {
                            steps[newId] = currentStep + 1;
                            updated = true;
                            num++;
                        }
                    } else {
                        for (int id2 = 0x0001; id2 <= 0xffff; id2++) {
                            if (steps[id2] == 0) {
                                continue;
                            }
                            SimpleShape shape2 = new SimpleShape(id2);
                            SimpleShape shapeNew1 = shape.process(Operate.STACK, shape2);
                            int stack1 = shapeNew1.id();
                            if (steps[stack1] == 0) {
                                steps[stack1] = currentStep + 1;
                                updated = true;
                                num++;
                            }
                            SimpleShape shapeNew2 = shape2.process(Operate.STACK, shape);
                            int stack2 = shapeNew2.id();
                            if (steps[stack2] == 0) {
                                steps[stack2] = currentStep + 1;
                                updated = true;
                                num++;
                            }
                        }
                    }
                }
            }
            if (updated) {
                logger.info("Step" + currentStep + " End, num: " + num);
                currentStep++;
            } else {
                logger.info("Finish, num: " + num);
                break;
            }
        }
        long t2 = System.currentTimeMillis();
        logger.info("计算共用时 " + (t2 - t1) / 1000.0 + " s");
        allShapes = new ArrayList<>();
        for (int id = 0x0001; id <= 0xffff; id++) {
            if (steps[id] != 0) {
                allShapes.add(id);
            }
        }
        try {
            String allShapesStr = JSON.toJSONString(allShapes, JSONWriter.Feature.PrettyFormat);
            FileUtils.writeStringToFile(SHAPES_FILE, allShapesStr, StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.error("", e);
        }
    }
}
