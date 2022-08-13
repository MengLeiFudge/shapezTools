package shapez.puzzle;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static shapez.Utils.PUZZLES_DIR;
import static shapez.Utils.sc;
import static shapez.puzzle.MyThreadPoolExecutor.getPuzzleStr;
import static shapez.puzzle.MyThreadPoolExecutor.strFormat;

public class ShowOnePuzzle {
    public ShowOnePuzzle() {
    }

    public void process() {
        System.out.println("请输入谜题ID或短代码");
        String puzzleStr = sc.nextLine().trim();
      /*  File dir = new File(PUZZLES_DIR,
                id + " [" + title + "] [" + shortKey + "] by " + author + ".json");
        if(){

        }

*/
        System.out.println("请稍等....");
        String info = getPuzzleStr(puzzleStr);
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
                    System.out.println(obj.toString(SerializerFeature.PrettyFormat));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
