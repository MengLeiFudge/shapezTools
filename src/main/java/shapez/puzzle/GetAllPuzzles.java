package shapez.puzzle;

import com.alibaba.fastjson.JSONObject;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.commons.io.FileUtils;
import shapez.SettingsAndUtils;
import shapez.base.Building.BuildingType;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static shapez.SettingsAndUtils.THREAD_NUM;
import static shapez.SettingsAndUtils.dfP;
import static shapez.SettingsAndUtils.getLocalPuzzleJsonByFile;

/**
 * @author MengLeiFudge
 */
public class GetAllPuzzles {
    public GetAllPuzzles() {
    }

    public void process(boolean getAllPuzzles) {
        if (getAllPuzzles) {
            getAllPuzzles();
        }
        writePuzzleCsv();
    }

    public void getAllPuzzles() {
        ThreadFactory factory = new ThreadFactoryBuilder().setNameFormat("puzzle-%d").build();

        //Common Thread Pool
        ExecutorService pool = new ThreadPoolExecutor(
                THREAD_NUM, THREAD_NUM,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(1024),
                factory, new ThreadPoolExecutor.AbortPolicy());

        pool.execute(() -> System.out.println(Thread.currentThread().getName()));
        pool.shutdown();


/*        MyThreadPoolExecutor.init();
        ExecutorService pool = new ThreadPoolExecutor(
                THREAD_NUM, THREAD_NUM,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(THREAD_NUM),
                Executors.defaultThreadFactory());
        for (int i = 0; i < THREAD_NUM; i++) {
            pool.execute(new MyThreadPoolExecutor(i));
        }
        pool.shutdown();
        try {
            while (!pool.isTerminated()) {
                showProcessState();
                sleep(3000);
            }
            //pool.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        showProcessState();*/
    }

    public static void writePuzzleCsv() {
        try {
            File dir = SettingsAndUtils.PuzzleSource.LOCAL_COMMON.getDir();
            File[] files = dir.listFiles();
            if (files == null) {
                System.out.println(dir + " 不存在！");
                return;
            }
            System.out.println("开始生成csv");
            // 写入首行
            File csv = new File("谜题合集_AllPuzzles_" + (new SimpleDateFormat("yyyyMMdd").format(new Date())) + ".csv");
            String firstLine = "序号ID,短代码ShortKey,谜题名称Title,作者Author,查看次数Downloads,完成次数Completions," +
                    "完成率CompletionRate,平均时间AverageTime,喜欢个数Likes,宽Width,高Height," +
                    "分离Splitter,切割Cutter,旋转Rotater,堆叠Stacker,混色Mixer,上色Painter,垃圾桶Trash,传送带Belt,隧道Tunnel\n";
            FileUtils.writeStringToFile(csv, firstLine, StandardCharsets.UTF_8);
            // 按谜题序号排序
            ArrayList<File> puzzlesFileList = new ArrayList<>(Arrays.asList(files));
            puzzlesFileList.sort((o1, o2) -> {
                int i1 = Integer.parseInt(o1.getName().substring(0, o1.getName().indexOf(' ')));
                int i2 = Integer.parseInt(o2.getName().substring(0, o2.getName().indexOf(' ')));
                return i1 - i2;
            });
            for (File f : puzzlesFileList) {
                JSONObject obj = getLocalPuzzleJsonByFile(f);
                if (obj == null) {
                    System.out.println(f + " 不是谜题文件！");
                    continue;
                }
                System.out.println(f.getName());
                Puzzle puzzle = new Puzzle(obj);
                double completeRate = (double) puzzle.getCompletions() / puzzle.getDownloads();
                String infoLine = puzzle.getId() + "," +
                        // 加引号表示是字符串，防止表格识别错误
                        "\"" + puzzle.getShortKey() + "\"," +
                        "\"" + puzzle.getTitle() + "\"," +
                        "\"" + puzzle.getAuthor() + "\"," +
                        puzzle.getDownloads() + "," +
                        puzzle.getCompletions() + "," +
                        dfP.format(completeRate) + "," +
                        getAvgTime(puzzle.getAverageTime()) + "," +
                        puzzle.getLikes() + "," +
                        puzzle.getW() + "," +
                        puzzle.getH() + "," +
                        // 如果 excludedBuildings 含有建筑，则该建筑为×，即被禁用
                        (puzzle.getExcludedBuildings().contains(BuildingType.SPLITTER_LEFT) ? "×" : "") + "," +
                        (puzzle.getExcludedBuildings().contains(BuildingType.CUTTER) ? "×" : "") + "," +
                        (puzzle.getExcludedBuildings().contains(BuildingType.ROTATER_CW) ? "×" : "") + "," +
                        (puzzle.getExcludedBuildings().contains(BuildingType.STACKER) ? "×" : "") + "," +
                        (puzzle.getExcludedBuildings().contains(BuildingType.MIXER) ? "×" : "") + "," +
                        (puzzle.getExcludedBuildings().contains(BuildingType.PAINTER) ? "×" : "") + "," +
                        (puzzle.getExcludedBuildings().contains(BuildingType.TRASH) ? "×" : "") + "," +
                        (puzzle.getExcludedBuildings().contains(BuildingType.BELT_STRAIGHT) ? "×" : "") + "," +
                        (puzzle.getExcludedBuildings().contains(BuildingType.TUNNEL1_ENTRY) ? "×" : "") + "\n";
                FileUtils.writeStringToFile(csv, infoLine, StandardCharsets.UTF_8, true);
            }
            System.out.println("已生成csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getAvgTime(double avgTime) {
        StringBuilder sb = new StringBuilder();
        if (avgTime > 3600) {
            int h = (int) (avgTime / 3600);
            sb.append(h).append("h");
            avgTime -= h * 3600;
        }
        if (avgTime > 60) {
            int m = (int) (avgTime / 60);
            sb.append(m).append("m");
            avgTime -= m * 60;
        }
        int s = (int) avgTime;
        sb.append(s).append("s");
        return sb.toString();
    }
}
