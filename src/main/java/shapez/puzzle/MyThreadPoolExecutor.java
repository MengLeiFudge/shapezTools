package shapez.puzzle;

import com.alibaba.fastjson2.JSONObject;
import shapez.SettingsAndUtils;

import java.io.File;
import java.util.HashMap;

import static shapez.SettingsAndUtils.GET_NON_EXISTS_PUZZLES;
import static shapez.SettingsAndUtils.THREAD_NUM;
import static shapez.SettingsAndUtils.UPDATE_LOCAL_PUZZLES;
import static shapez.SettingsAndUtils.getPuzzleJson;
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
        File[] files = SettingsAndUtils.PuzzleSource.LOCAL_COMMON.getDir().listFiles();
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
        JSONObject obj;
        // 无限循环直到成功获取数据（只要 id > 0，必定能获得数据）
        int i = 0;
        while (true) {
            if (i >= 3) {
                System.out.println("Thread " + threadNo + ", ID " + id + " 开始第 " + (i + 1) + " 次捞取数据！");
            }
            // 获取谜题并保存至本地
            obj = getPuzzleJson(id, SettingsAndUtils.PuzzleSource.OFFICIAL);
            if (obj == null || obj.containsKey("error")) {
                sleep(3000);
            } else {
                break;
            }
            i++;
        }
        return true;
    }

    public static void showProcessState() {
        StringBuilder sb = new StringBuilder();
        sb.append("已处理 ").append(processedNum).append(" 个谜题：");
        for (int i = 0; i < THREAD_NUM; i++) {
            sb.append(i).append("_").append(processIndex[i]).append(" ");
        }
        System.out.println(sb);
    }
}