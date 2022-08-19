package shapez.puzzle;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import shapez.SettingsAndUtils;

import static shapez.SettingsAndUtils.getPuzzleJson;
import static shapez.SettingsAndUtils.sc;

/**
 * @author MengLeiFudge
 */
public class ShowOnePuzzle {
    public ShowOnePuzzle() {
    }

    public void process() {
        System.out.println("请输入谜题ID或短代码");
        String puzzleStr = sc.nextLine().trim();
        System.out.println("请稍等....");
        JSONObject obj = getPuzzleJson(puzzleStr, SettingsAndUtils.PuzzleSource.values());
        if (obj == null) {
            System.out.println("未找到该谜题的数据！");
            return;
        }
        System.out.println(obj.toString(SerializerFeature.PrettyFormat));
    }
}
