package com.cvnavi.downloader.util;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.cookie.ClientCookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequestUtil {

    static CloseableHttpClient httpclient = null;
    static {

        SSLContext sslContext = null;
        try {
            sslContext = new SSLContextBuilder().loadTrustMaterial(null, (arg0, arg1) -> true).build();
        } catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
            e.printStackTrace();
        }
        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext,
                new NoopHostnameVerifier());
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("https", socketFactory).register("http", new PlainConnectionSocketFactory()).build();

        SocketConfig socketConfig= SocketConfig.custom().setSoTimeout(30000).build();
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(
                socketFactoryRegistry);
        connectionManager.setDefaultSocketConfig(socketConfig);
        connectionManager.setMaxTotal(200);
        connectionManager.setDefaultMaxPerRoute(20);

        httpclient = HttpClients.custom().setConnectionManager(connectionManager).build();

    }

    /**
     * 向指定URL发送GET方法的请求
     *
     * @param url
     *            发送请求的URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     */
    public static String sendGet(String url, String param) {
        String result = "";
        HttpGet httpGet = new HttpGet(url+"?"+param);
        return sendHttp(httpGet,null,null,10000);
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url
     *            发送请求的 URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param) {
        HttpPost httpPost = new HttpPost(url);
        if (param != null && param.length() > 0) {
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            for (String p : param.split("&")) {
                String pair[] = p.split("=");
                nvps.add(new BasicNameValuePair(pair[0], pair[1]));
                try {
                    httpPost.setEntity(new UrlEncodedFormEntity(nvps));
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                }
            }
        }

        return sendHttp(httpPost,null,null,10000);
    }


    private static String sendHttp(HttpRequestBase requestMethod, HashMap<String, String> header,
                                                 HashMap<String, String> cookie, int timeout) {

        CloseableHttpResponse response1 = null;

        if (header != null && header.size() > 0) {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                requestMethod.setHeader(entry.getKey(), entry.getValue());
            }
        }

        HttpClientContext context = HttpClientContext.create();
        RequestConfig.Builder configBuilder = RequestConfig.custom();
        configBuilder.setConnectionRequestTimeout(timeout);
        configBuilder.setConnectTimeout(timeout);
        configBuilder.setSocketTimeout(timeout);
        context.setRequestConfig(configBuilder.build());

        if (cookie != null && cookie.size() > 0) {
            CookieStore cookieStore = new BasicCookieStore();
            String domain = requestMethod.getURI().getAuthority();

            if (domain.substring(domain.indexOf('.') + 1).contains(".")) {// 如果域名中只有一个点，就不要取子域名了。
                domain = domain.substring(domain.indexOf('.'));
            }

            for (Map.Entry<String, String> entry : cookie.entrySet()) {
                BasicClientCookie c = new BasicClientCookie(entry.getKey(), entry.getValue());
                c.setPath("/");
                c.setDomain(domain);
                c.setAttribute(ClientCookie.DOMAIN_ATTR, "true");
                cookieStore.addCookie(c);
            }
            context.setCookieStore(cookieStore);
        }

        try {
            response1 = httpclient.execute(requestMethod, context);
            HttpEntity entity = response1.getEntity();
            String result = EntityUtils.toString(entity);
            EntityUtils.consume(entity);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}