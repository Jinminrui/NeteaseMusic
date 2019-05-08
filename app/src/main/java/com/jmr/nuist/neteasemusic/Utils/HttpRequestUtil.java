package com.jmr.nuist.neteasemusic.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpRequestUtil {
    private static final byte[] LOCKER = new byte[0];
    private static HttpRequestUtil mInstance;
    private OkHttpClient okHttpClient;
    private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();


    private HttpRequestUtil() {
        okhttp3.OkHttpClient.Builder ClientBuilder = new okhttp3.OkHttpClient.Builder();
        ClientBuilder.connectTimeout(10, TimeUnit.SECONDS);//连接超时

//        try {
//            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
//            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
//            keyStore.load(null);
//            String certificateAlias = Integer.toString(0);
//            keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(getClass()
//                    .getClassLoader()
//                    .getResourceAsStream("assets/ca.cer")));
//            SSLContext sslContext = SSLContext.getInstance("TLS");
//            TrustManagerFactory trustManagerFactor =TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
//            trustManagerFactor.init(keyStore);
//            sslContext.init(null,trustManagerFactor.getTrustManagers(),new SecureRandom());
//            ClientBuilder.sslSocketFactory(sslContext.getSocketFactory());
//            ClientBuilder.hostnameVerifier(new HostnameVerifier() {
//                @Override
//                public boolean verify(String hostname, SSLSession session) {
//                    return true;
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        // 处理cookies
        ClientBuilder.cookieJar(new CookieJar() {
            @Override
            public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                cookieStore.put(url.host(), cookies);
            }

            @Override
            public List<Cookie> loadForRequest(HttpUrl url) {
                List<Cookie> cookies = cookieStore.get(url.host());
                return cookies != null ? cookies : new ArrayList<Cookie>();
            }
        });

        okHttpClient = ClientBuilder.build();
    }

    /**
     * 单例模式获取http请求工具类实例
     *
     * @return
     */
    public static HttpRequestUtil getInstance() {
        if (mInstance == null) {
            synchronized (LOCKER) {
                if (mInstance == null) {
                    mInstance = new HttpRequestUtil();
                }
            }
        }
        return mInstance;
    }

    /**
     * 自定义网络回调接口
     */
    public interface MyCallback {
        void success(Call call, Response response) throws IOException;

        void failed(Call call, IOException e);
    }

    /**
     * get请求，异步方式，在子线程中执行网络数据，更新UI需要切换到主线程
     *
     * @param url
     * @param myCallback
     */
    public void getDataAsyn(String url, final MyCallback myCallback) {
        Request.Builder builder = new Request.Builder();
        Request request = builder.get().url(url).build();
        //2 将Request封装为Call
        Call call = okHttpClient.newCall(request);
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                myCallback.failed(call, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                myCallback.success(call, response);
            }
        });
    }


    /**
     * 判断网络是否可用
     *
     * @param context
     * @return
     */

    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }


    /**
     * 生成安全套接字工厂，用于https请求的证书跳过
     *
     * @return
     */
    public SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, new TrustManager[]{new TrustAllCerts()}, new SecureRandom());
            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
        }
        return ssfFactory;
    }

    /**
     * 用于信任所有证书
     */
    class TrustAllCerts implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

}
