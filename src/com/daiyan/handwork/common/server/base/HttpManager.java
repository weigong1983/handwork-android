package com.daiyan.handwork.common.server.base;

import org.apache.http.params.HttpParams;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.HttpVersion;
import org.apache.http.HttpResponse;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ClientConnectionManager;

import java.io.IOException;

public class HttpManager {
    public static final DefaultHttpClient httpClient;
    static {
        final HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

        HttpConnectionParams.setStaleCheckingEnabled(params, false);
        HttpConnectionParams.setConnectionTimeout(params, 20 * 1000);
        HttpConnectionParams.setSoTimeout(params, 20 * 1000);
        HttpConnectionParams.setSocketBufferSize(params, 8192);
        
        HttpClientParams.setRedirecting(params, false);
        HttpProtocolParams.setUserAgent(params, "Mozilla/5 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.15) Gecko/2009101601 Firefox/3.15 (.NET CLR 3.5.30729)");        
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

        ClientConnectionManager manager = new ThreadSafeClientConnManager(params, schemeRegistry);
        httpClient = new DefaultHttpClient(manager, params);
        
    }

    private HttpManager() {
    }
    
    public static void setUserAgent(String userAgent) {
        HttpProtocolParams.setUserAgent(httpClient.getParams(), userAgent);
    }

    public static HttpResponse execute(HttpHead head) throws IOException {
        return httpClient.execute(head);
    }

    public static HttpResponse execute(HttpHost host, HttpGet get) throws IOException {
    	
        return httpClient.execute(host, get);
    }

    public static HttpResponse execute(HttpGet get) throws IOException {
        return httpClient.execute(get);
    }
    
    public static HttpResponse execute(HttpPost post) throws IOException {
        return httpClient.execute(post);
    }
    
    public static HttpResponse execute(HttpHost host, HttpPost post) throws IOException {
        return httpClient.execute(host, post);
    }

}
