package shapez;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.HashMap;
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

    public static final int THREAD_NUM = Runtime.getRuntime().availableProcessors();

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
    public static final boolean UPDATE_LOCAL_PUZZLES = false;

    /**
     * 如果本地没有某个ID（小于本地最大ID）的谜题，是否获取该谜题数据.
     * <p>
     * 谜题未下载全的时候，应该将此项设为true。
     */
    public static final boolean GET_NON_EXISTS_PUZZLES = false;

    /**
     * Shapez Token，用于下载谜题.
     * <p>
     * Token 通过抓包方式获取，每次登录谜题都会回传一个 TOKEN，下次登录该 TOKEN 将会失效。
     */
    public static final String TOKEN = "7a11e5bc-2f69-4bbc-987e-f9f3f6de8c89";

    /**
     * Shapez UA，用于下载谜题.
     * <p>
     * UA 通过抓包方式获取，且随版本更新而变化。
     */
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) " +
            "shapez/1.5.5 Chrome/96.0.4664.174 Electron/16.2.8 Safari/537.36";

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
            //System.setProperty("http.proxyHost", "127.0.0.1");
            //System.setProperty("https.proxyHost", "127.0.0.1");
            //System.setProperty("http.proxyPort", "8888");// 8888 是 Charles 的默认端口号，请填写你使用的端口号
            //System.setProperty("https.proxyPort", "8888");
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
        return null;
        /*
        System.setProperty("http.proxyHost", "127.0.0.1");
        System.setProperty("https.proxyHost", "127.0.0.1");
        System.setProperty("http.proxyPort", "8888"); // 8888 是 Charles 的默认端口号，请填写你使用的端口号
        System.setProperty("https.proxyPort", "8888");
        RequestBuilder requestBuilder;
        try {
            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create()
                    //.setBoundary("------------------------" + getRandomHexStr(false, 16))
                    .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                    .setContentType(ContentType.APPLICATION_JSON);
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

         */
    }

    /**
     * 获取 Token.
     *
     * @return 动态 Token
     * @deprecated 该方法目前不能获取，传入的是steam:token，还要再研究一下
     */
    @Deprecated
    public static String getToken() {
        String url = "https://api.shapez.io/v1/public/login";
        HashMap<String, String> headerParams = new HashMap<>();
        headerParams.put("Content-Type", "application/json");
        headerParams.put("User-Agent", USER_AGENT);
        headerParams.put("x-api-key", "d5c54aaa491f200709afff082c153ef2");
        HashMap<String, String> bodyParams = new HashMap<>();
        bodyParams.put("token", "14000000468e1b196a4d2aa23e2048060100100139e5f9621800000001000000020000003c5899ac34342" +
                "466f70a031917000000b800000038000000040000003e20480601001001221f1400f91150750b0aa8c000000000bebced623e" +
                "6c09630100b3060700010038cd180000000000859ba8b5e9d6e792ed9ef56158281c46ddbbfea1c3f9f9eabbeedaf43137db0" +
                "79452c36f69356072bf164fa48020816729b4d782f8f61017dc8290e4bf8cf131f8a7e4d1da1245aa0593fd7cf57d982d0784" +
                "389ecb7269430f8639f55f42734d21b2daee6f9ddc9fa4b664bd0dc3640f0111aec0e7ee8cf74d9a4c6e74238af7");
        String s = postInfoToUrl(url, headerParams, bodyParams, null);
        try {
            JSONObject obj = JSON.parseObject(s);
            return obj.getString("token");
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 通过序号或短代码获取指定谜题的 json.
     * <p>
     * <ul>
     *     <li>如果官方谜题库有该谜题，将谜题保存到谜题文件夹并返回谜题数据；否则从本地查找谜题</li>
     *     <li>如果谜题文件夹内有该谜题，将其移动到已删除文件夹并返回谜题数据；否则从已删除文件夹查找谜题</li>
     *     <li>如果已删除文件夹内有该谜题，返回谜题数据；否则返回 null</li>
     * </ul>
     *
     * @param o 谜题序号({@link Integer})或短代码({@link String})
     * @return 获取到谜题时，返回谜题数据；否则返回 <code>null</code>
     */
    public static JSONObject getPuzzleJson(Object o, Location... locations) {
        // 1.检查输入
        int id = -1;
        String shortKey = null;
        if (o instanceof Integer) {
            id = (int) o;
            if (id <= 0) {
                throw new IllegalArgumentException("id 必须为正数！");
            }
        } else if (o instanceof String) {
            shortKey = (String) o;
            if (!shortKey.matches("([CRWS][rgbypcuw]|--){4}.*")) {
                throw new IllegalArgumentException("id 必须为正数！");
            }
        } else {
            throw new IllegalArgumentException("只能传入谜题序号或短代码！");
        }
        // 2.获取官方谜题json
        String url = "https://api.shapez.io/v1/puzzles/download/" + o;
        HashMap<String, String> headerParams = new HashMap<>(10);
        headerParams.put("Content-Type", "application/json");
        headerParams.put("User-Agent", USER_AGENT);
        headerParams.put("x-api-key", "d5c54aaa491f200709afff082c153ef2");
        headerParams.put("x-token", TOKEN);
        String data = getInfoFromUrl(url, null, headerParams);
        if (data == null) {
            // 获取谜题信息失败
            throw new IllegalStateException("未能获取到官方谜题json，请检查TOKEN！");
        }
        JSONObject obj = JSON.parseObject(data);
        // 3.
        // {error: "not-found"}
        if (obj.containsKey("error")) {
            // 谜题不存在或已删除，需要查看本地是否有该谜题
            if (PUZZLES_DIR.exists()) {
                // 如果获取到，将谜题移动至delete文件夹
                obj = getLocalPuzzleJson(o, false, true);
            }
            if (obj == null) {
                if (PUZZLES_DELETED_DIR.exists()) {
                    obj = getLocalPuzzleJson(o, true, false);
                }
            }
        }
        if (obj == null || obj.containsKey("error")) {
            // 官方谜题库、本地都没有该谜题
            return null;
        }
        // 4.保存至本地
        // 不对啊，如果是放在已删除里面的呢？
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
            bw.write(obj.toString(SerializerFeature.PrettyFormat));
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return obj;
    }

    private static JSONObject getLocalPuzzleJson(Object o, boolean fromDeletedDir, boolean moveToDeletedDir) {
        // 1.检查输入
        int id = -1;
        String shortKey = null;
        if (o instanceof Integer) {
            id = (int) o;
            if (id <= 0) {
                throw new IllegalArgumentException("id 必须为正数！");
            }
        } else if (o instanceof String) {
            shortKey = (String) o;
            if (!shortKey.matches("([CRWS][rgbypcuw]|--){4}.*")) {
                throw new IllegalArgumentException("id 必须为正数！");
            }
            shortKey = strFormat(shortKey);
        } else {
            throw new IllegalArgumentException("只能传入谜题序号或短代码！");
        }
        // 2.bianli
        File dir = fromDeletedDir ? PUZZLES_DELETED_DIR : PUZZLES_DIR;
        if (!dir.exists()) {
            return null;
        }
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            return null;
        }
        File puzzleFile = null;
        for (File file : files) {
            //8 [The first puzzle] [RuRuRuRu] by tobspr
            String name = file.getName();
            // 通过id寻找puzzle
            if (id != -1) {
                int index1 = name.indexOf(" ");
                String idStr = name.substring(0, index1);
                if ((id + "").equals(idStr)) {
                    puzzleFile = file;
                    break;
                } else {
                    continue;
                }
            }
            // 通过短代码寻找puzzle，谜题名称不能有[]
            int index2 = name.indexOf("[");
            String shortKeyStr = name.substring(index2 + 1);
            int index3 = shortKeyStr.indexOf("[");
            int index4 = shortKeyStr.indexOf("]");
            shortKeyStr = shortKeyStr.substring(index3 + 1, index4);
            if (shortKeyStr.equals(shortKey)) {
                puzzleFile = file;
                break;
            }
        }
        if (puzzleFile == null) {
            return null;
        }
        StringBuilder info = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(puzzleFile))) {
            String s;
            while ((s = br.readLine()) != null) {
                info.append(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!fromDeletedDir && moveToDeletedDir) {
            String name = puzzleFile.getName();
            File dest = new File(PUZZLES_DELETED_DIR, name);
            if (!PUZZLES_DELETED_DIR.exists()) {
                PUZZLES_DELETED_DIR.mkdirs();
            }
            puzzleFile.renameTo(dest);
        }
        return JSON.parseObject(info.toString());
    }

    /**
     * 去除 windows 禁止用于文件名的符号.
     *
     * @param s 要修改的字符串
     * @return 修改后的可用字符串
     */
    public static String strFormat(String s) {
        if (s != null && !"".equals(s)) {
            s = s.replaceAll("[\\/:\\*\\?\"<>\\|]", "_");
                    /*.replace("\\", "_")
                    .replace("/", "_")
                    .replace(":", "_")
                    .replace("*", "_")
                    .replace("?", "_")
                    .replace("\"", "_")
                    .replace("<", "_")
                    .replace(">", "_")
                    .replace("|", "_");*/
        }
        return s;
    }

}
