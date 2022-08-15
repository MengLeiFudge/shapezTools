package shapez.puzzle;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import static shapez.SettingsAndUtils.GET_NON_EXISTS_PUZZLES;
import static shapez.SettingsAndUtils.PUZZLES_DELETED_DIR;
import static shapez.SettingsAndUtils.PUZZLES_DIR;
import static shapez.SettingsAndUtils.THREAD_NUM;
import static shapez.SettingsAndUtils.TOKEN;
import static shapez.SettingsAndUtils.UPDATE_LOCAL_PUZZLES;
import static shapez.SettingsAndUtils.USER_AGENT;
import static shapez.SettingsAndUtils.getInfoFromUrl;
import static shapez.SettingsAndUtils.postInfoToUrl;
import static shapez.SettingsAndUtils.sleep;

/**
 * 用于分流处理的线程.
 *
 * @author MengLeiFudge
 */
public record MyThreadPoolExecutor(int threadNo) implements Runnable {

    /**
     * 已经从 API 获取信息的谜题个数.
     */
    private static int processedNum;
    /**
     * 当前每个线程处理的进度.
     */
    private static int[] processIndex;
    /**
     * 指示某个id对应的谜题是否存在.
     */
    private static final HashMap<Integer, File> map = new HashMap<>();
    /**
     * 本地谜题最大ID.
     */
    private static int localMaxID = -1;

    public static void init() {
        processedNum = 0;
        processIndex = new int[THREAD_NUM];
        PUZZLES_DIR.mkdirs();
        File[] files = PUZZLES_DIR.listFiles();
        if (files == null) {
            return;
        }
        for (File f : files) {
            int id = Integer.parseInt(f.getName().substring(0, f.getName().indexOf(' ')));
            map.put(id, f);
            localMaxID = Math.max(localMaxID, id);
        }
    }

    @Override
    public void run() {
        // 指示连续遇到多少个 false
        int falseNum = 0;
        //int id = 1;
        int id = 1;
        while (true) {
            // 实时更新 processIndex
            synchronized (MyThreadPoolExecutor.class) {
                processIndex[threadNo] = id;
            }
            // 判断是否终止线程
            if (falseNum >= 20) {
                System.out.println("——————终止线程 " + threadNo + "，当前 ID " + id + "——————");
                return;
            }
            if (id % THREAD_NUM == threadNo) {
                boolean getPuzzle = false;
                // 判断 id 是否超过本地最大 id
                if (id <= localMaxID) {
                    // 本地可能有该谜题，判断本地是否有该谜题
                    if (map.containsKey(id)) {
                        // 本地有，判断是否需要更新
                        if (UPDATE_LOCAL_PUZZLES) {
                            getPuzzle = true;
                        }
                    } else {
                        // 本地没有，说明该谜题发布过且已删除，不必再获取，判断是否需要获取
                        if (GET_NON_EXISTS_PUZZLES) {
                            getPuzzle = true;
                        }
                    }
                } else {
                    getPuzzle = true;
                }
                if (!getPuzzle) {
                    id++;
                    continue;
                }
                try {
                    if (processOne(id)) {
                        // 实时更新 localMaxID
                        synchronized (MyThreadPoolExecutor.class) {
                            localMaxID = Math.max(localMaxID, id);
                        }
                        falseNum = 0;
                    } else {
                        // id 超过本地最大 id 时，开始计算连续 error 个数
                        if (id > localMaxID) {
                            falseNum++;
                        }
                    }
                } finally {
                    synchronized (MyThreadPoolExecutor.class) {
                        processedNum++;
                    }
                }
            }
            id++;
        }
    }

    /**
     * 处理某个 id 对应的谜题.
     *
     * @param id 谜题 id
     * @return 捞到 error 时返回 false，否则返回 true
     */
    private boolean processOne(int id) {
        String s;
        // 无限循环直到成功获取数据（只要 id > 0，必定能获得数据）
        int i = 0;
        while (true) {
            if (i >= 3) {
                System.out.println("Thread " + threadNo + ", ID " + id + " 开始第 " + (i + 1) + " 次捞取数据！");
            }
            s = getPuzzleStr(id);
            if (s == null || "".equals(s)) {
                sleep(3000);
            } else {
                break;
            }
            i++;
        }
        // 检查本地该 ID 对应谜题是否已经删除
        JSONObject obj = JSON.parseObject(s);
        if (obj.containsKey("error")) {
            if (map.containsKey(id)) {
                // 将该谜题移动至“已删除”文件夹
                File src = map.get(id);
                File dest = new File(PUZZLES_DELETED_DIR, src.getName());
                if(!PUZZLES_DELETED_DIR.exists()){
                    PUZZLES_DELETED_DIR.mkdirs();
                }
                src.renameTo(dest);
            }
            return false;
        }
        // 将谜题保存至本地。注意，不要用 Puzzle 类转换再获取 title 等数据，太慢
        JSONObject meta = obj.getJSONObject("meta");
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
        return true;
    }

    public static String getPuzzleStr(int id) {
        return getPuzzleStr(id + "");
    }

    public static String getToken() {
        String url = "https://api.shapez.io/v1/public/login";
        HashMap<String, String> headerParams = new HashMap<>();
        headerParams.put("Content-Type", "application/json");
        headerParams.put("User-Agent", USER_AGENT);
        headerParams.put("x-api-key", "d5c54aaa491f200709afff082c153ef2");
        HashMap<String, String> bodyParams = new HashMap<>();
        bodyParams.put("token", "14000000468e1b196a4d2aa23e2048060100100139e5f9621800000001000000020000003c5899ac34342466f70a031917000000b800000038000000040000003e20480601001001221f1400f91150750b0aa8c000000000bebced623e6c09630100b3060700010038cd180000000000859ba8b5e9d6e792ed9ef56158281c46ddbbfea1c3f9f9eabbeedaf43137db079452c36f69356072bf164fa48020816729b4d782f8f61017dc8290e4bf8cf131f8a7e4d1da1245aa0593fd7cf57d982d0784389ecb7269430f8639f55f42734d21b2daee6f9ddc9fa4b664bd0dc3640f0111aec0e7ee8cf74d9a4c6e74238af7");
        String s = postInfoToUrl(url, headerParams, bodyParams, null);
        try {
            JSONObject obj = JSON.parseObject(s);
            return obj.getString("token");
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 下载一个puzzle.
     *
     * @param shortKey
     * @return
     */
    public static String getPuzzleStr(String shortKey) {
        String url = "https://api.shapez.io/v1/puzzles/download/" + shortKey;
        HashMap<String, String> headerParams = new HashMap<>();
        headerParams.put("Content-Type", "application/json");
        headerParams.put("User-Agent", USER_AGENT);
        headerParams.put("x-api-key", "d5c54aaa491f200709afff082c153ef2");
        headerParams.put("x-token", TOKEN);
        //String uuid = UUID.randomUUID().toString();
        //headerParams.put("x-token", uuid);
        //headerParams.put("x-token", getToken());
        String data = getInfoFromUrl(url, null, headerParams);
        return data;
   /*     if (data != null && !"{\"error\":\"not-found\"}".equals(data)) {
            if (!data.startsWith("{\"meta\"")) {
                System.out.println("!data.startsWith(meta)");
            }
            return data;
        } else {
            return null;
        }*/
    }

    /**
     * 去除 windows 禁止用于文件名的符号.
     *
     * @param s 要修改的字符串
     * @return 修改后的可用字符串
     */
    public static String strFormat(String s) {
        if (s != null && !"".equals(s)) {
            s = s.replace("\\", "_")
                    .replace("/", "_")
                    .replace(":", "_")
                    .replace("*", "_")
                    .replace("?", "_")
                    .replace("\"", "_")
                    .replace("<", "_")
                    .replace(">", "_")
                    .replace("|", "_");
        }
        return s;
    }

    public static void showProcessState() {
        StringBuilder sb = new StringBuilder();
        sb.append("已处理 ").append(processedNum).append(" 个谜题：");
        for (int i = 0; i < THREAD_NUM; i++) {
            sb/*.append("th")*/.append(i).append("_").append(processIndex[i]).append(" ");
        }
        System.out.println(sb);
    }
}