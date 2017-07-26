package www.gjw.http.demo;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

/**
 * HttpClient工具类
 * 
 * @return
 * @author SHANHY
 * @create 2015年12月18日
 */
public class HttpClientUtil {
	private static Logger logger = Logger.getLogger(HttpClientUtil.class.getName());
	static final int timeOut = 10 * 1000;//超时时间
	
    private static CloseableHttpClient httpClient = null;//发起请求的对象
    private final static Object syncLock = new Object();

    /**
     * <li>功能描述：获取HttpClient对象</li>
     * <li>其他说明：</li>
     * <li>创建时间：2017年7月14日 下午4:20:49</li>
     * @param url
     * @return CloseableHttpClient<br/>
     * @author gjw
     */
    public static CloseableHttpClient getHttpClient(String url) {
        String hostname = url.split("/")[2];
        int port = 80;
        if (hostname.contains(":")) {
            String[] arr = hostname.split(":");
            hostname = arr[0];
            port = Integer.parseInt(arr[1]);
        }
        if (httpClient == null) {
            synchronized (syncLock) {
                if (httpClient == null) {
                    httpClient = createHttpClient(200, 40, 100, hostname, port);
                }
            }
        }
        System.out.println(Thread.currentThread().getName()+"--->>>>>>"+httpClient);
        return httpClient;
    }

    /**
     * <li>功能描述：创建HttpClient对象</li>
     * <li>其他说明：</li>
     * <li>创建时间：2017年7月14日 下午4:21:03</li>
     * @param maxTotal
     * @param maxPerRoute
     * @param maxRoute
     * @param hostname
     * @param port
     * @return CloseableHttpClient<br/>
     * @author gjw
     */
    public static CloseableHttpClient createHttpClient(int maxTotal,int maxPerRoute,int maxRoute,String hostname,int port) {
        ConnectionSocketFactory plainsf = PlainConnectionSocketFactory.getSocketFactory();
        LayeredConnectionSocketFactory sslsf = SSLConnectionSocketFactory.getSocketFactory();
        //支持https和http协议
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory> create()
        		.register("http", plainsf)
                .register("https", sslsf).build();
        //http连接池
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
        //将最大连接数增加
        cm.setMaxTotal(maxTotal);
        //将每个路由基础的连接增加
        cm.setDefaultMaxPerRoute(maxPerRoute);
        //传递给此方法的非正数值禁用连接验证,
        cm.setValidateAfterInactivity(1*1000);

        // 请求重试处理
        HttpRequestRetryHandler httpRequestRetryHandler = new HttpRequestRetryHandler() {
            public boolean retryRequest(IOException exception,
                    int executionCount, HttpContext context) {
                if (executionCount >= 5) {// 如果已经重试了5次，就放弃
                	System.out.println("重试了："+executionCount);
                    return false;
                }
                if (exception instanceof NoHttpResponseException) {//如果服务器丢掉了连接，那么就重试
                	logger.error("如果服务器丢掉了连接，那么就重试");
                    return true;
                }
                if (exception instanceof SSLHandshakeException) {//不要重试SSL握手异常
                	logger.error("不要重试SSL握手异常");
                    return false;
                }
                if (exception instanceof InterruptedIOException) {//超时
                	logger.error("超时");
                    return false;
                }
                if (exception instanceof UnknownHostException) {//目标服务器不可达
                	logger.error("目标服务器不可达");
                    return false;
                }
                if (exception instanceof ConnectTimeoutException) {//连接被拒绝
                	logger.error("连接被拒绝");
                    return false;
                }
                if (exception instanceof SSLException) {//SSL握手异常
                	logger.error("SSL握手异常");
                    return false;
                }

                HttpClientContext clientContext = HttpClientContext
                        .adapt(context);
                HttpRequest request = clientContext.getRequest();
                // 如果请求是幂等的，就再次尝试
                if (!(request instanceof HttpEntityEnclosingRequest)) {
                    return true;
                }
                return false;
            }
        };

        CloseableHttpClient httpClient = HttpClients.custom()
        		.setConnectionManager(cm)
        		.setRetryHandler(httpRequestRetryHandler).build();
        return httpClient;
    }

    /**
     * <li>功能描述：设置请求头信息 及 连接超时设计</li>
     * <li>其他说明：</li>
     * <li>创建时间：2017年7月14日 下午4:07:00</li>
     * @param httpRequestBase void<br/>
     * @author gjw
     */
    private static void config(HttpRequestBase httpRequestBase){
        // 设置Header等
        httpRequestBase.setHeader("User-Agent", "Mozilla/5.0");
        // 配置请求的超时设置
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(timeOut)
                .setConnectTimeout(timeOut).setSocketTimeout(timeOut).build();
        httpRequestBase.setConfig(requestConfig);
    }
    
    /**
     * <li>功能描述：添加参数</li>
     * <li>其他说明：</li>
     * <li>创建时间：2017年7月14日 下午3:59:43</li>
     * @param httpost
     * @param params void<br/>
     * @author gjw
     */
    private static void setPostParams(HttpPost httpost,Map<String, Object> params) {
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
        	nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
        }
        try {
            httpost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * <li>功能描述：POST 请求URL获取内容</li>
     * <li>其他说明：</li>
     * <li>创建时间：2017年7月14日 下午4:03:39</li>
     * @param url 请求的地址
     * @param params 请求参数
     * @return
     * @throws IOException String<br/>
     * @author gjw
     */
    public static String post(String url, Map<String, Object> params) {
        HttpPost httppost = new HttpPost(url);
        config(httppost);//请求头设置
        setPostParams(httppost, params);//post请求参数设置
        CloseableHttpResponse response = null;
        String result_Str = null;
        try {
            response = getHttpClient(url).execute(httppost,HttpClientContext.create());
            HttpEntity entity = response.getEntity();
            result_Str = EntityUtils.toString(entity, "utf-8");
            EntityUtils.consume(entity);//这个方法也可以把底层的流给关闭了
            return result_Str;
        } catch (Exception e) {
        	logger.error(e.getMessage(), e);
        } finally {
            try {
                if (response != null)
                    response.close();
            } catch (IOException e) {
            	logger.error(e.getMessage(), e);
            }
        }
        return result_Str;
    }

    /**
     * GET请求URL获取内容
     * 
     * @param url
     * @return
     * @author SHANHY
     * @create 2015年12月18日
     */
    public static String get(String url) {
        HttpGet httpget = new HttpGet(url);
        config(httpget);//请求头设置
        CloseableHttpResponse response = null;
        String result_Str = null;
        try {
            response = getHttpClient(url).execute(httpget,HttpClientContext.create());//发送GET请求
            HttpEntity entity = response.getEntity();
            result_Str = EntityUtils.toString(entity, "utf-8");
            EntityUtils.consume(entity);
            return result_Str;
        } catch (IOException e) {
        	logger.error(e.getMessage(), e);
        } finally {
            try {
				if(response != null)
					response.close();
            } catch (IOException e) {
            	logger.error(e.getMessage(), e);
            }
        }
        return result_Str;
    }
}