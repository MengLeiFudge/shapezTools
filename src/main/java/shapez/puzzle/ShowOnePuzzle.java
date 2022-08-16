package shapez.puzzle;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import shapez.SettingsAndUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static shapez.SettingsAndUtils.PUZZLES_DIR;
import static shapez.SettingsAndUtils.getPuzzleJson;
import static shapez.SettingsAndUtils.sc;

public class ShowOnePuzzle {
    public ShowOnePuzzle() {
    }

    public void process() {
        System.out.println("请输入谜题ID或短代码");
        String puzzleStr = sc.nextLine().trim();
        System.out.println("请稍等....");
        String info = SettingsAndUtils.getPuzzleJson(puzzleStr);
        if (info == null) {
            System.out.println("info is null!");
        } else if ("".equals(info)) {
            System.out.println("info is empty!");
        } else {
            try {
                JSONObject obj = JSON.parseObject(info);
                System.out.println(obj.toString(SerializerFeature.PrettyFormat));

                // 保存到本地
                if (obj.containsKey("error")) {
                    // 不存在的或已删除的谜题
                    System.out.println("谜题不存在或已删除！");
                    return;
                }
                JSONObject meta = obj.getJSONObject("meta");
                int id = meta.getInteger("id");
                String title = strFormat(meta.getString("title"));
                String shortKey = strFormat(meta.getString("shortKey"));
                String author = strFormat(meta.getString("author"));
                File f = new File(PUZZLES_DIR,
                        id + " [" + title + "] [" + shortKey + "] by " + author + ".json");
                if (!f.exists()) {
                    try {
                        f.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {
                    bw.write(obj.toString(SerializerFeature.PrettyFormat));
                    bw.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
