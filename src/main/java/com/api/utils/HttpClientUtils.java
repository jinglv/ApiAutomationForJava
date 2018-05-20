package com.api.utils;

import com.api.exception.HttpClientException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpClientUtils {
    //声明httpClient对象
    private static CloseableHttpClient httpclient;

    static {
        PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager();
        manager.setMaxTotal( 200 ); //连接池最大并发连接数
        manager.setDefaultMaxPerRoute( 200 );//单路由最大并发数,路由是对maxTotal的细分
        httpclient = HttpClients.custom().setConnectionManager( manager ).build();
    }

    /* ConnectionRequestTimeout httpclient使用连接池来管理连接，这个时间就是从连接池获取连接的超时时间，可以想象下数据库连接池
       ConnectTimeout 建立连接最大时间
       SocketTimeout 数据传输过程中数据包之间间隔的最大时间
       HttpHost 代理
     */
    private static RequestConfig config = RequestConfig.copy( RequestConfig.DEFAULT )
            .setSocketTimeout( 10000 )
            .setConnectTimeout( 5000 )
            .setConnectionRequestTimeout( 100 ).build();
            //.setProxy(new HttpHost("127.0.0.1", 8888, "http")).build(); //结合抓包工具进行数据拦截,IP替换可进行IP欺骗

    /**
     * Get 请求
     * @param url
     * @param header
     * @param params
     * @return
     * @throws HttpClientException
     */
    public static String doGet(String url, Map<String, Object> header, Map<String, Object> params) throws HttpClientException {
        String result = ""; //初始化保存结果
        CloseableHttpResponse closeableHttpResponse = null;
        try {

            URIBuilder builder = new URIBuilder(url);
            if (params != null) {
                for (String key : params.keySet()) {
                    builder.addParameter(key, params.get(key).toString());
                }
            }

            URI uri = builder.build();

            //创建Http Get请求
            HttpGet get = new HttpGet( uri );
            get.setConfig( config );
            get.addHeader( HTTP.CONTENT_ENCODING, "utf-8" );

            // 请求头信息
            if (header != null) {
                for (Map.Entry<String, Object> entry : header.entrySet()) {
                    get.setHeader( entry.getKey(), entry.getValue().toString() );
                }
            }

            closeableHttpResponse = httpclient.execute( get );
            if (closeableHttpResponse.getStatusLine().getStatusCode() == 200) {
                result = EntityUtils.toString( closeableHttpResponse.getEntity(), "UTF-8" );
            } else {
                throw new HttpClientException(
                        "System level error, Code=[" + closeableHttpResponse.getStatusLine().getStatusCode() + "]" );
            }
        } catch (ClientProtocolException e) {
            throw new HttpClientException( "HttpClient error," + e.getMessage() );
        } catch (IOException e) {
            throw new HttpClientException( "IO error," + e.getMessage() );
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } finally {
            // 关闭连接,释放资源
            if (closeableHttpResponse != null) {
                try {
                    closeableHttpResponse.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * 重载Get请求方法，无请求头
     * @param url
     * @return
     * @throws HttpClientException
     */
    public static String doGet(Map<String, Object> params, String url) throws HttpClientException {
        String result = doGet(url, null, params);
        return result;
    }

    /**
     * 重载Get请求方法，无请求头和参数
     * @param url
     * @return
     * @throws HttpClientException
     */
    public static String doGet(String url) throws HttpClientException {
        String result = doGet(url, null);
        return result;
    }

    /**
     * 重载Get请求方法，有请求头, 无参数
     * @param url
     * @return
     * @throws HttpClientException
     */
    public static String doGet(String url, Map<String, Object> headers) throws HttpClientException {
        String result = doGet(url, headers, null);
        return result;
    }

    /**
     * Post请求
     * @param url
     * @param params
     * @param header
     * @return
     * @throws HttpClientException
     */
    public static String doPost(String url, Map<String, Object> params, Map<String, Object> header) throws HttpClientException {
        String result = null;
        HttpPost post = new HttpPost(url);
        post.setConfig(config);
        post.addHeader(HTTP.CONTENT_ENCODING, "UTF-8");
        CloseableHttpResponse closeableHttpResponse = null;
        // 获取响应实体
        HttpEntity postEntity = null;
        try {
            if (params != null) {
                List<NameValuePair> list = new ArrayList<NameValuePair>();
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    list.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
                }
                postEntity = new UrlEncodedFormEntity(list, "UTF-8"); //设置编码格式以防参数的中文乱码
                post.setEntity(postEntity);
            }

            if (header != null) {
                for (Map.Entry<String, Object> entry : header.entrySet()) {
                    post.setHeader(entry.getKey(), entry.getValue().toString());
                }
            }
            closeableHttpResponse = httpclient.execute(post);
            // 获取响应状态码为200，否则抛出异常
            if (closeableHttpResponse.getStatusLine().getStatusCode() == 200) {
                result = EntityUtils.toString(closeableHttpResponse.getEntity(), "UTF-8"); //设置编码格式以防中文乱码
            } else {
                throw new HttpClientException(
                        "System level error, Code=[" + closeableHttpResponse.getStatusLine().getStatusCode() + "]");
            }
        } catch (IOException e) {
            throw new HttpClientException("IO error," + e.getMessage());
        } finally {
            // 关闭连接,释放资源
            if (postEntity != null) {
                try {
                    EntityUtils.consume(postEntity);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (closeableHttpResponse != null) {
                try {
                    closeableHttpResponse.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * 重载Post方法，无请求头
     * @param url
     * @param params
     * @return
     * @throws HttpClientException
     */
    public static String doPost(String url, Map<String, Object> params) throws HttpClientException {
        String result = doPost(url, params, null);
        return result;
    }

    /**
     * 传入的参数是json格式
     * @param url
     * @param jsonParams
     * @param header
     * @return
     * @throws HttpClientException
     */
    public static String doPostJson(String url, String jsonParams, Map<String, Object> header)
            throws HttpClientException {
        String result = null;
        HttpPost post = new HttpPost(url);
        post.setConfig(config);
        post.addHeader(HTTP.CONTENT_ENCODING, "UTF-8");
        CloseableHttpResponse closeableHttpResponse = null;
        StringEntity postEntity = null;
        try {
            if (jsonParams != null) {
                postEntity = new StringEntity(jsonParams, "utf-8");
                postEntity.setContentEncoding("utf-8");
                postEntity.setContentType("application/json");
                post.setEntity(postEntity);
            }

            if (header != null) {
                for (Map.Entry<String, Object> entry : header.entrySet()) {
                    post.setHeader(entry.getKey(), entry.getValue().toString());
                }
            }
            closeableHttpResponse = httpclient.execute(post);
            if (closeableHttpResponse.getStatusLine().getStatusCode() == 200) {
                result = EntityUtils.toString(closeableHttpResponse.getEntity(), "UTF-8");
            } else {
                throw new HttpClientException(
                        "System level error, Code=[" + closeableHttpResponse.getStatusLine().getStatusCode() + "]");
            }
        } catch (IOException e) {
            throw new HttpClientException("IO error," + e.getMessage());
        } finally {
            if (postEntity != null) {
                try {
                    EntityUtils.consume(postEntity);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (closeableHttpResponse != null) {
                try {
                    closeableHttpResponse.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
}
