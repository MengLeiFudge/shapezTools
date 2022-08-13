package shapez;

import org.apache.http.Consts;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

/**
 * 设置/工具类.
 *
 * @author MengLeiFudge
 */
public class SettingsAndUtils {
    private SettingsAndUtils() {
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

    /**
     * 从网站获取字符串，通常是 json 格式数据.
     * <p>
     * 使用指定ua（如果未指定ua）以解除部分网站对java访问的403限制。
     *
     * @param httpUrl      要获取信息的网站
     * @param urlParams    网址附加参数
     * @param headerParams 请求头附加参数
     * @return 反馈的字符串
     */
    public static String getInfoFromUrl(String httpUrl, Map<String, String> urlParams, Map<String, String> headerParams) {
        try {
            StringBuilder urlSb = new StringBuilder(httpUrl);
            if (urlParams != null && !urlParams.isEmpty()) {
                boolean firstParam = true;
                for (var x : urlParams.entrySet()) {
                    urlSb.append(firstParam ? "?" : "&").append(x.getKey()).append("=").append(x.getValue());
                    if (firstParam) {
                        firstParam = false;
                    }
                }
            }
            HttpURLConnection conn = (HttpURLConnection) new URL(urlSb.toString()).openConnection();
            for (var x : headerParams.entrySet()) {
                conn.setRequestProperty(x.getKey(), x.getValue());
            }
            if (!headerParams.containsKey("User-Agent")) {
                // 该ua可以解除部分网站对java访问的403限制
                conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            }
            conn.connect();
            if (conn.getResponseCode() != 200) {
                return null;
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                result.append(line);
            }
            br.close();
            conn.disconnect();
            return result.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Random random = new Random();

    private static String getRandomHexStr(boolean upper, int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            String oneLetter = Integer.toHexString(random.nextInt(16));
            sb.append(upper ? oneLetter.toUpperCase(Locale.ROOT) : oneLetter.toLowerCase(Locale.ROOT));
        }
        return sb.toString();
    }

    /**
     * 上传内容给网站，并获取反馈的信息.
     *
     * @param httpUrl        要上传内容并获取信息的网站
     * @param headerParams   请求头附加参数
     * @param bodyParams     请求内容附加字符串参数
     * @param bodyFileParams 请求内容附加文件参数
     * @return 反馈的字符串
     */
    public static String postInfoToUrl(String httpUrl, Map<String, String> headerParams, Map<String, String> bodyParams, Map<String, File> bodyFileParams) {
        RequestBuilder requestBuilder;
        try {
            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create()
                    .setBoundary("------------------------" + getRandomHexStr(false, 16))
                    .setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            if (bodyParams != null && !bodyParams.isEmpty()) {
                for (Map.Entry<String, String> entry : bodyParams.entrySet()) {
                    if (entry.getValue() != null) {
                        multipartEntityBuilder.addTextBody(entry.getKey(), entry.getValue());
                    }
                }
            }
            if (bodyFileParams != null && !bodyFileParams.isEmpty()) {
                for (Map.Entry<String, File> entry : bodyFileParams.entrySet()) {
                    if (entry.getValue() != null) {
                        multipartEntityBuilder.addBinaryBody(entry.getKey(), entry.getValue());
                    }
                }
            }
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(60000)
                    .setConnectTimeout(60000)
                    .setConnectionRequestTimeout(10000)
                    .build();
            requestBuilder = RequestBuilder.post()
                    .setUri(new URI(httpUrl))
                    .setConfig(requestConfig)
                    .setEntity(multipartEntityBuilder.build());
            if (headerParams != null && !headerParams.isEmpty()) {
                for (Map.Entry<String, String> entry : headerParams.entrySet()) {
                    if (entry.getValue() != null) {
                        requestBuilder.addHeader(entry.getKey(), entry.getValue());
                    }
                }
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
        try (CloseableHttpResponse response = HttpClients.createDefault().execute(requestBuilder.build())) {
            return EntityUtils.toString(response.getEntity(), Consts.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
