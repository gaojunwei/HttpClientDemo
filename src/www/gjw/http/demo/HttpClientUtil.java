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
 * HttpClient������
 * 
 * @return
 * @author SHANHY
 * @create 2015��12��18��
 */
public class HttpClientUtil {
	private static Logger logger = Logger.getLogger(HttpClientUtil.class.getName());
	static final int timeOut = 10 * 1000;//��ʱʱ��
	
    private static CloseableHttpClient httpClient = null;//��������Ķ���
    private final static Object syncLock = new Object();

    /**
     * <li>������������ȡHttpClient����</li>
     * <li>����˵����</li>
     * <li>����ʱ�䣺2017��7��14�� ����4:20:49</li>
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
     * <li>��������������HttpClient����</li>
     * <li>����˵����</li>
     * <li>����ʱ�䣺2017��7��14�� ����4:21:03</li>
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
        //֧��https��httpЭ��
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory> create()
        		.register("http", plainsf)
                .register("https", sslsf).build();
        //http���ӳ�
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
        //���������������
        cm.setMaxTotal(maxTotal);
        //��ÿ��·�ɻ�������������
        cm.setDefaultMaxPerRoute(maxPerRoute);
        //���ݸ��˷����ķ�����ֵ����������֤,
        cm.setValidateAfterInactivity(1*1000);

        // �������Դ���
        HttpRequestRetryHandler httpRequestRetryHandler = new HttpRequestRetryHandler() {
            public boolean retryRequest(IOException exception,
                    int executionCount, HttpContext context) {
                if (executionCount >= 5) {// ����Ѿ�������5�Σ��ͷ���
                	System.out.println("�����ˣ�"+executionCount);
                    return false;
                }
                if (exception instanceof NoHttpResponseException) {//������������������ӣ���ô������
                	logger.error("������������������ӣ���ô������");
                    return true;
                }
                if (exception instanceof SSLHandshakeException) {//��Ҫ����SSL�����쳣
                	logger.error("��Ҫ����SSL�����쳣");
                    return false;
                }
                if (exception instanceof InterruptedIOException) {//��ʱ
                	logger.error("��ʱ");
                    return false;
                }
                if (exception instanceof UnknownHostException) {//Ŀ����������ɴ�
                	logger.error("Ŀ����������ɴ�");
                    return false;
                }
                if (exception instanceof ConnectTimeoutException) {//���ӱ��ܾ�
                	logger.error("���ӱ��ܾ�");
                    return false;
                }
                if (exception instanceof SSLException) {//SSL�����쳣
                	logger.error("SSL�����쳣");
                    return false;
                }

                HttpClientContext clientContext = HttpClientContext
                        .adapt(context);
                HttpRequest request = clientContext.getRequest();
                // ����������ݵȵģ����ٴγ���
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
     * <li>������������������ͷ��Ϣ �� ���ӳ�ʱ���</li>
     * <li>����˵����</li>
     * <li>����ʱ�䣺2017��7��14�� ����4:07:00</li>
     * @param httpRequestBase void<br/>
     * @author gjw
     */
    private static void config(HttpRequestBase httpRequestBase){
        // ����Header��
        httpRequestBase.setHeader("User-Agent", "Mozilla/5.0");
        // ��������ĳ�ʱ����
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(timeOut)
                .setConnectTimeout(timeOut).setSocketTimeout(timeOut).build();
        httpRequestBase.setConfig(requestConfig);
    }
    
    /**
     * <li>������������Ӳ���</li>
     * <li>����˵����</li>
     * <li>����ʱ�䣺2017��7��14�� ����3:59:43</li>
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
     * <li>����������POST ����URL��ȡ����</li>
     * <li>����˵����</li>
     * <li>����ʱ�䣺2017��7��14�� ����4:03:39</li>
     * @param url ����ĵ�ַ
     * @param params �������
     * @return
     * @throws IOException String<br/>
     * @author gjw
     */
    public static String post(String url, Map<String, Object> params) {
        HttpPost httppost = new HttpPost(url);
        config(httppost);//����ͷ����
        setPostParams(httppost, params);//post�����������
        CloseableHttpResponse response = null;
        String result_Str = null;
        try {
            response = getHttpClient(url).execute(httppost,HttpClientContext.create());
            HttpEntity entity = response.getEntity();
            result_Str = EntityUtils.toString(entity, "utf-8");
            EntityUtils.consume(entity);//�������Ҳ���԰ѵײ�������ر���
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
     * GET����URL��ȡ����
     * 
     * @param url
     * @return
     * @author SHANHY
     * @create 2015��12��18��
     */
    public static String get(String url) {
        HttpGet httpget = new HttpGet(url);
        config(httpget);//����ͷ����
        CloseableHttpResponse response = null;
        String result_Str = null;
        try {
            response = getHttpClient(url).execute(httpget,HttpClientContext.create());//����GET����
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