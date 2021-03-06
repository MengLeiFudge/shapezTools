package shapez.puzzle;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import shapez.puzzle.Building.BuildingType;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;
import static shapez.Utils.ALL_PUZZLES_DIR;
import static shapez.Utils.THREAD_NUM;
import static shapez.Utils.dfPercent;
import static shapez.puzzle.MyThreadPoolExecutor.showProcessState;

/**
 * @author MengLeiFudge
 */
public class GetAllPuzzles {
    public GetAllPuzzles() {
    }

    public void process(boolean getAllPuzzles) {
        new File(ALL_PUZZLES_DIR).mkdirs();
        if (getAllPuzzles) {
            getAllPuzzles();
        }
        createXlsx();
    }

    public void getAllPuzzles() {
        MyThreadPoolExecutor.init();
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
        showProcessState();
    }

    public static void createXlsx() {
        System.out.println("????????????csv");
        File tar = new File("????????????_AllPuzzles_" + (new SimpleDateFormat("yyyyMMdd").format(new Date())) + ".csv");
        tar.delete();
        try {
            tar.createNewFile();
            BufferedWriter bw = new BufferedWriter(new FileWriter(tar));
            bw.write("??????ID," +
                    "?????????ShortKey," +
                    "????????????Title," +
                    "??????Author," +
                    "????????????Downloads," +
                    "????????????Completions," +
                    "?????????CompletionRate," +
                    "????????????AverageTime," +
                    "????????????Likes," +
                    "???Width," +
                    "???Height," +
                    "??????Splitter," +
                    "??????Cutter," +
                    "??????Rotater," +
                    "??????Stacker," +
                    "??????Mixer," +
                    "??????Painter," +
                    "?????????Trash," +
                    "?????????Belt," +
                    "??????Tunnel,"
            );
            bw.newLine();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File[] files = new File(ALL_PUZZLES_DIR).listFiles();
        if (files == null) {
            System.out.println(ALL_PUZZLES_DIR + " ????????????");
            return;
        }
        ArrayList<File> list = new ArrayList<>(Arrays.asList(files));
        list.sort((o1, o2) -> {
            int i1 = Integer.parseInt(o1.getName().substring(0, o1.getName().indexOf(' ')));
            int i2 = Integer.parseInt(o2.getName().substring(0, o2.getName().indexOf(' ')));
            return i1 - i2;
        });
        for (File f : list) {
            String fileStr;
            try (FileInputStream in = new FileInputStream(f)) {
                byte[] bytes = new byte[(int) f.length()];
                in.read(bytes);
                fileStr = new String(bytes, StandardCharsets.UTF_8);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(tar, true))) {
                JSONObject obj = JSON.parseObject(fileStr);
                Puzzle puzzle = new Puzzle(obj);
                double completeRate = (double) puzzle.getCompletions() / puzzle.getDownloads();
                bw.write(puzzle.getId() + "," +
                        avoidCalculate(puzzle.getShortKey()) + "," +
                        avoidCalculate(puzzle.getTitle()) + "," +
                        avoidCalculate(puzzle.getAuthor()) + "," +
                        puzzle.getDownloads() + "," +
                        puzzle.getCompletions() + "," +
                        dfPercent.format(completeRate) + "," +
                        getAvgTime(puzzle.getAverageTime()) + "," +
                        puzzle.getLikes() + "," +
                        puzzle.getW() + "," +
                        puzzle.getH() + "," +
                        // ?????? excludedBuildings ???????????????????????????????????????????????
                        (puzzle.getExcludedBuildings().contains(BuildingType.SPLITTER) ? "??" : "") + "," +
                        (puzzle.getExcludedBuildings().contains(BuildingType.CUTTER) ? "??" : "") + "," +
                        (puzzle.getExcludedBuildings().contains(BuildingType.ROTATER) ? "??" : "") + "," +
                        (puzzle.getExcludedBuildings().contains(BuildingType.STACKER) ? "??" : "") + "," +
                        (puzzle.getExcludedBuildings().contains(BuildingType.MIXER) ? "??" : "") + "," +
                        (puzzle.getExcludedBuildings().contains(BuildingType.PAINTER) ? "??" : "") + "," +
                        (puzzle.getExcludedBuildings().contains(BuildingType.TRASH) ? "??" : "") + "," +
                        (puzzle.getExcludedBuildings().contains(BuildingType.BELT) ? "??" : "") + "," +
                        (puzzle.getExcludedBuildings().contains(BuildingType.TUNNEL) ? "??" : "")
                );
                bw.newLine();
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("?????????csv");
    }

    private static String avoidCalculate(String s) {
        // ?????????????????????-???????????????????????????'???????????????????????????
        if (s.startsWith("-") || s.startsWith("=")) {
            return "'" + s;
        } else {
            return s;
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
