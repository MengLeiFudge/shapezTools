package test;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import org.apache.commons.io.FileUtils;
import shapez.puzzle.Puzzle;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class Main {
    public static void main(String[] args) throws IOException {
        File puzzleDir = FileUtils.getFile("puzzles", "common");
        for (var file : Objects.requireNonNull(puzzleDir.listFiles())) {
            String s1 = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            JSONObject obj1 = JSONObject.parseObject(s1);
            Puzzle puzzle1 = Puzzle.getPuzzleInstanceByJson(obj1);
            obj1 = puzzle1.toJSONObject();
            FileUtils.writeStringToFile(file, obj1.toString(JSONWriter.Feature.PrettyFormat), StandardCharsets.UTF_8);
        }
    }
}
