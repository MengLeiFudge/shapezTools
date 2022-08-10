package shapez;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Scanner;

/**
 * 设置/工具类.
 *
 * @author MengLeiFudge
 */
public class Utils {
    private Utils() {
    }

    public static final Scanner sc = new Scanner(System.in).useDelimiter("\n");

    public static final DecimalFormat dfNoPercent = new DecimalFormat("0.0");
    public static final DecimalFormat dfP = new DecimalFormat("00.00%");

    /**
     * 萌泪的Token，不要滥用哦.
     */
    public static final String TOKEN = "9666d5f2-a357-4164-81da-2d1e0a44ed28";

    public static final int THREAD_NUM = Runtime.getRuntime().availableProcessors();

    /**
     * 存放官方谜题的路径.
     */
    public static final File PUZZLES_DIR = new File("puzzles");

    /**
     * 存放已删除的官方谜题的路径.
     */
    public static final File PUZZLES_DELETED_DIR = new File("puzzles_deleted");

    /**
     * 存放谜题解的路径.
     */
    public static final File SOLUTIONS_DIR = new File("solutions");

    /**
     * 是否更新本地谜题.
     * <p>
     * 谜题有可能会被删除，更新本地谜题可以将被删除的谜题移动到其他位置。
     */
    public static final boolean UPDATE_LOCAL_PUZZLES = true;

    /**
     * 如果本地没有某个ID（小于最大ID）的谜题，是否获取该谜题数据.
     * <p>
     * 谜题未下载全的时候，应该将此项设为true，
     */
    public static final boolean GET_NON_EXISTS_PUZZLES = false;

    /**
     * 休眠指定时间，单位ms.
     *
     * @param milliseconds 要休眠的时间
     */
    public static void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }
}
