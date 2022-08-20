package shapez;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.commons.io.FileUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.net.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 设置/工具类.
 *
 * @author MengLeiFudge
 */
public class SettingsAndUtils {
    private SettingsAndUtils() {
    }

    private static final Logger logger = LoggerFactory.getLogger(SettingsAndUtils.class);

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
    private static final String TOKEN = "cee4a637-cc15-4e7b-bce9-bc0999c3318b";

    /**
     * Shapez UA，用于下载谜题.
     * <p>
     * UA 通过抓包方式获取，且随版本更新而变化。
     */
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) " +
            "shapez/1.5.5 Chrome/96.0.4664.174 Electron/16.2.8 Safari/537.36";

    /**
     * 是否使用 Charles 代理.
     * <p>
     * 该功能用于检查程序模拟的请求是否与游戏操作时发送的请求一致。
     */
    private static final boolean USE_CHARLES_PROXY = false;

    /**
     * Charles 代理使用的端口号.
     * <p>
     * 填入 Charles 中 Proxy - Proxy Settings - HTTP Proxy - Port 值，默认值 8888。
     */
    private static final int CHARLES_PROXY_PORT = 8888;

    /**
     * 设置 Charles 代理.
     * <p>
     * 注意，如果要监控 https 链接，必须先将 Charles 证书安装至 JDK 内！
     * 具体方法见<a href="https://blog.csdn.net/u013019701/article/details/95326460">使用Charles 抓取Java程序的请求</a>。
     */
    private static void setCharlesProxy() {
        System.setProperty("http.proxyHost", "127.0.0.1");
        System.setProperty("https.proxyHost", "127.0.0.1");
        System.setProperty("http.proxyPort", CHARLES_PROXY_PORT + "");
        System.setProperty("https.proxyPort", CHARLES_PROXY_PORT + "");
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
        if (USE_CHARLES_PROXY) {
            setCharlesProxy();
        }
        // 1.构建uri
        URI uri;
        try {
            URIBuilder uriBuilder = new URIBuilder(httpUrl);
            if (urlParams != null && !urlParams.isEmpty()) {
                for (var x : urlParams.entrySet()) {
                    uriBuilder.setParameter(x.getKey(), x.getValue());
                }
            }
            uri = uriBuilder.build();
        } catch (URISyntaxException e) {
            logger.error(null, e);
            return null;
        }
        // 2.构建httpGet，添加header，设置超时
        HttpGet httpGet = new HttpGet(uri);
        if (headerParams != null && !headerParams.isEmpty()) {
            for (var x : headerParams.entrySet()) {
                httpGet.addHeader(x.getKey(), x.getValue());
            }
        }
        if (headerParams == null || !headerParams.containsKey("User-Agent")) {
            // 该ua可以解除部分网站对java访问的403限制
            httpGet.addHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        }
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(10, TimeUnit.SECONDS)
                .setResponseTimeout(10, TimeUnit.SECONDS)
                .build();
        httpGet.setConfig(requestConfig);
        // 3.获取信息
        try (CloseableHttpClient httpclient = HttpClients.createDefault();
             CloseableHttpResponse response = httpclient.execute(httpGet)) {
            if (response.getCode() == 200) {
                return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            }
            return null;
        } catch (ParseException | IOException e) {
            logger.error(null, e);
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
    public static String postInfoToUrl(String httpUrl, Map<String, String> headerParams,
                                       Map<String, String> bodyParams, Map<String, File> bodyFileParams) {
        if (USE_CHARLES_PROXY) {
            setCharlesProxy();
        }
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
     * 指示从哪里获取谜题.
     */
    public enum PuzzleSource {
        OFFICIAL(null),
        /**
         * 存放官方谜题的路径.
         */
        LOCAL_COMMON(new File("puzzles", "common")),
        /**
         * 存放已删除的官方谜题的路径.
         */
        LOCAL_DELETED(new File("puzzles", "deleted")),
        /**
         * 存放谜题解的路径.
         */
        LOCAL_SOLUTION(new File("puzzles", "solutions"));

        private final File dir;

        PuzzleSource(File dir) {
            this.dir = dir;
        }

        public File getDir() {
            try {
                FileUtils.forceMkdir(dir);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return dir;
        }
    }

    private static String getPuzzleJsonName(JSONObject obj) {
        try {
            JSONObject meta = obj.getJSONObject("meta");
            int id = meta.getInteger("id");
            String title = meta.getString("title");
            String shortKey = meta.getString("shortKey");
            String author = meta.getString("author");
            String name = id + " [" + title + "] [" + shortKey + "] by " + author + ".json";
            // 去除 windows 禁止用于文件名的符号
            return name.replaceAll("[\\\\/:*?\"<>|]", "_");
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("传入的参数不是常规谜题数据！");
        }
    }

    /**
     * 通过序号或短代码获取指定谜题的 json.
     * <ul>
     *     <li>如果官方谜题库有该谜题，将谜题保存到谜题文件夹并返回谜题数据；否则从本地查找谜题</li>
     *     <li>如果谜题文件夹内有该谜题，将其移动到已删除文件夹并返回谜题数据；否则从已删除文件夹查找谜题</li>
     *     <li>如果已删除文件夹内有该谜题，返回谜题数据；否则返回 null</li>
     * </ul>
     *
     * @param o 谜题序号({@link Integer})或短代码({@link String})
     * @return 获取到谜题时，返回谜题数据；否则返回 <code>null</code>
     */
    public static JSONObject getPuzzleJson(Object o, PuzzleSource... sources) {
        // 1.检查输入
        int id = -1;
        String shortKey = null;
        try {
            id = Integer.parseInt(o.toString());
        } catch (NumberFormatException e) {
            shortKey = o.toString();
            if (!PATTERN_SHAPE.matcher(shortKey).matches()) {
                throw new IllegalArgumentException("短代码不合规！");
            }
        }
        // 2.获取谜题json
        List<PuzzleSource> sourceList = Arrays.stream(sources).toList();
        JSONObject puzzleJson = null;
        try {
            // 2.1 OFFICIAL
            if (sourceList.contains(PuzzleSource.OFFICIAL)) {
                String url = "https://api.shapez.io/v1/puzzles/download/" + o;
                HashMap<String, String> headerParams = new HashMap<>(10);
                headerParams.put("Content-Type", "application/json");
                headerParams.put("User-Agent", USER_AGENT);
                headerParams.put("x-api-key", "d5c54aaa491f200709afff082c153ef2");
                headerParams.put("x-token", TOKEN);
                String puzzleStr = getInfoFromUrl(url, null, headerParams);
                if (puzzleStr == null) {
                    throw new IllegalStateException("未能获取到官方谜题json，请检查TOKEN！");
                }
                JSONObject obj = JSON.parseObject(puzzleStr);
                if (!obj.containsKey("error")) {
                    puzzleJson = obj;
                    // 将谜题存入本地
                    if (UPDATE_LOCAL_PUZZLES) {
                        FileUtils.writeStringToFile(new File(PuzzleSource.LOCAL_COMMON.getDir(), getPuzzleJsonName(puzzleJson)),
                                puzzleJson.toString(SerializerFeature.PrettyFormat), StandardCharsets.UTF_8);
                    }
                }
            }
            // 2.2 LOCAL
            PuzzleSource[] localSourcesOrder = {PuzzleSource.LOCAL_COMMON, PuzzleSource.LOCAL_DELETED, PuzzleSource.LOCAL_SOLUTION};
            for (PuzzleSource puzzleSource : localSourcesOrder) {
                if (puzzleJson == null && sourceList.contains(puzzleSource)) {
                    String param1 = id == -1 ? shortKey : id + " ";
                    File puzzleFile = getLocalPuzzleFile(param1, id != -1, puzzleSource);
                    if (puzzleFile != null) {
                        String puzzleStr = FileUtils.readFileToString(puzzleFile, StandardCharsets.UTF_8);
                        JSONObject obj = JSON.parseObject(puzzleStr);
                        if (!obj.containsKey("error")) {
                            puzzleJson = obj;
                        }
                        // 如果官方已删除该谜题，移动至已删除文件夹
                        if (puzzleSource == PuzzleSource.LOCAL_COMMON && sourceList.contains(PuzzleSource.OFFICIAL)) {
                            FileUtils.moveToDirectory(puzzleFile, PuzzleSource.LOCAL_DELETED.getDir(), true);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return puzzleJson;
    }

    public static final Pattern PATTERN_CORNER = Pattern.compile("[CRWS][rgbypcuw]|--");
    public static final Pattern PATTERN_LAYER = Pattern.compile("([CRWS][rgbypcuw]|--){4}");
    public static final Pattern PATTERN_SHAPE = Pattern.compile("([CRWS][rgbypcuw]|--){4}(:([CRWS][rgbypcuw]|--){4}){0,3}");

    private static final Pattern PATTERN_PUZZLE_FILE = Pattern.compile("\\[.+]");

    /**
     * 在指定文件夹内寻找谜题文件.
     *
     * @param s      谜题 id 加空格（如"8 "），或短代码（如"RuRuRuRu"）
     * @param byId   参数 s 是否表示 id
     * @param source 要在哪个文件夹内寻找该谜题
     * @return 如果找到对应谜题，返回谜题文件；否则返回 {@code null}
     */
    private static File getLocalPuzzleFile(String s, boolean byId, PuzzleSource source) {
        File[] files = PuzzleSource.LOCAL_COMMON.getDir().listFiles();
        if (files == null) {
            return null;
        }
        File puzzleFile = null;
        for (File file : files) {
            //8 [The first puzzle] [RuRuRuRu] by tobspr
            String name = file.getName();
            if (byId) {
                // 通过id寻找puzzle，谜题最前面是id加空格
                if (name.startsWith(s)) {
                    puzzleFile = file;
                    break;
                }
            } else {
                // 通过短代码寻找puzzle，谜题名称必定没有[]
                Matcher matcher = PATTERN_PUZZLE_FILE.matcher(name);
                String shortKeyStr = matcher.group(2);
                if (s.equals(shortKeyStr)) {
                    puzzleFile = file;
                    break;
                }
            }
        }
        return puzzleFile;
    }

    public static JSONObject getLocalPuzzleJsonByFile(File file) {
        try {
            String puzzleStr = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            JSONObject obj = JSON.parseObject(puzzleStr);
            if (!obj.containsKey("error")) {
                return obj;
            }
            return null;
        } catch (IOException e) {
            return null;
        }
    }
}
