package shapez;

import java.text.DecimalFormat;
import java.util.Scanner;

/**
 * @author MengLeiFudge
 */
public class Utils {
    private Utils() {
    }

    public static final Scanner sc = new Scanner(System.in).useDelimiter("\n");
    public static final DecimalFormat dfNoPercent = new DecimalFormat("0.0");
    public static final DecimalFormat dfPercent = new DecimalFormat("00.00%");

    public static final String TOKEN = "9666d5f2-a357-4164-81da-2d1e0a44ed28";
    public static final int THREAD_NUM = Runtime.getRuntime().availableProcessors();
    public static final String ALL_PUZZLES_DIR = "all_puzzles";
    /**
     * 是否更新本地谜题.
     */
    public static final boolean UPDATE_LOCAL_PUZZLES = true;
    /**
     * 如果本地没有某个ID（小于最大ID）的谜题，是否获取该谜题数据.
     */
    public static final boolean GET_NON_EXISTS_PUZZLES = true;

    public static void sleep(long milli) {
        try {
            Thread.sleep(milli);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }
}
