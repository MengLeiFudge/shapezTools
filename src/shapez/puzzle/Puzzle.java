package shapez.puzzle;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.Data;
import shapez.puzzle.Building.BuildingType;

import java.util.ArrayList;

/**
 * @author MengLeiFudge
 */
@Data
public class Puzzle {

    private JSONObject obj;
    private int w;
    private int h;
    private Building[] buildings = new Building[0];
    private int version;
    private ArrayList<BuildingType> excludedBuildings = new ArrayList<>();
    private double difficulty;
    private double averageTime;
    private int downloads;
    private String shortKey;
    private String author;
    private int completions;
    private int id;
    private boolean completed;
    private String title;
    private int likes;

    Puzzle(JSONObject obj) {
        this.obj = obj;
        if (obj.containsKey("error")) {
            return;
        }

        JSONObject game = obj.getJSONObject("game");
        //part1: game - bounds
        JSONObject bounds = game.getJSONObject("bounds");
        w = bounds.getInteger("w");
        h = bounds.getInteger("h");
        //part2: game - buildings
        JSONArray buildings = game.getJSONArray("buildings");
        this.buildings = new Building[buildings.size()];
        for (int i = 0; i < buildings.size(); i++) {
            JSONObject building = buildings.getJSONObject(i);
            String type = building.getString("type");// block, emitter, goal
            String item = null;
            if (!"block".equals(type)) {
                item = building.getString("item");
            }
            JSONObject pos = building.getJSONObject("pos");
            int r = pos.getInteger("r");// 0,90,180,-90
            int x = pos.getInteger("x");
            int y = pos.getInteger("y");
            this.buildings[i] = new Building(type, item, r, x, y);
        }
        //part3: game - version
        version = game.getInteger("version");
        //part4: game - version
        if (game.containsKey("excludedBuildings")) {
            JSONArray excludedBuildings = game.getJSONArray("excludedBuildings");
            for (int i = 0; i < excludedBuildings.size(); i++) {
                String excludedBuilding = excludedBuildings.getString(i);
                switch (excludedBuilding) {
                    case "balancer" -> this.excludedBuildings.add(BuildingType.SPLITTER);
                    case "cutter" -> this.excludedBuildings.add(BuildingType.CUTTER);
                    case "rotater" -> this.excludedBuildings.add(BuildingType.ROTATER);
                    case "stacker" -> this.excludedBuildings.add(BuildingType.STACKER);
                    case "mixer" -> this.excludedBuildings.add(BuildingType.MIXER);
                    case "painter" -> this.excludedBuildings.add(BuildingType.PAINTER);
                    case "trash" -> this.excludedBuildings.add(BuildingType.TRASH);
                    case "belt" -> this.excludedBuildings.add(BuildingType.BELT);
                    case "underground_belt" -> this.excludedBuildings.add(BuildingType.TUNNEL);
                    default -> throw new IllegalArgumentException("错误的建筑类型：" + excludedBuilding);
                }
            }
        }
        //part5: meta
        JSONObject meta = obj.getJSONObject("meta");
        difficulty = meta.get("difficulty") == null ? -1 : meta.getDouble("difficulty");
        averageTime = meta.get("averageTime") == null ? -1 : meta.getDouble("averageTime");
        downloads = meta.getInteger("downloads");
        shortKey = meta.getString("shortKey");
        author = meta.getString("author");
        completions = meta.getInteger("completions");
        id = meta.getInteger("id");
        completed = meta.getBoolean("completed");
        title = meta.getString("title");
        likes = meta.getInteger("likes");
    }

    public String getFormatStr() {
        return obj.toString(SerializerFeature.PrettyFormat);
    }

    /*
    static int getOriginX(int w, int h) {
        return 1;
    }

    static int getOriginY(int w, int h) {
        return 1;
    }

    static int convertToX(int originX, int originY, int x) {
        return 1;
    }

    static int convertToY(int originX, int originY, int y) {
        return 1;
    }*/
}
