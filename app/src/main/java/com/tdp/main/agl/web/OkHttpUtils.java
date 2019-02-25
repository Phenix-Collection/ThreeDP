package com.tdp.main.agl.web;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.Settings;
import android.util.Log;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by tujh on 2018/3/8.
 */

public class OkHttpUtils {
    private static final String TAG = OkHttpUtils.class.getSimpleName();
    public static final String P12_PATH = "";
    private volatile static OkHttpUtils sOkHttpUtils;

    private OkHttpClient mOkHttpClient = null;

    public static OkHttpClient initOkHttpClient(Context context) {
        SSLSocketFactory sslSocketFactory = null;
        try {
            InputStream p12 = context.getAssets().open(P12_PATH);
            byte[] p12Bytes = new byte[p12.available()];
            int len = p12.read(p12Bytes);
            p12.close();


            //p2a服务器需要的ca，手动传避免部分机型ca不全
            InputStream ca = context.getAssets().open("ca2.pem");
            byte[] caBytes = new byte[ca.available()];
            ca.read(caBytes);
            ca.close();

            TrustManagerFactory tmf = OkHttpUtils.getTrustManagerFactory(caBytes);
            sslSocketFactory = new CustomSslSocketFactory(OkHttpUtils.getKeyManagerFactory(p12Bytes).getKeyManagers(),
                    tmf == null ? null : tmf.getTrustManagers());
        } catch (Exception e) {
        }
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(600000L, TimeUnit.MILLISECONDS)
                .writeTimeout(600000L, TimeUnit.MILLISECONDS)
                .readTimeout(600000L, TimeUnit.MILLISECONDS);
        OkHttpClient okHttpClient;
        if (sslSocketFactory != null) {
            okHttpClient = builder.sslSocketFactory(sslSocketFactory).build();
        } else {
            okHttpClient = builder.build();
        }
        return okHttpClient;
    }

    public static OkHttpUtils initOkHttpUtils(OkHttpClient okHttpClient) {
        if (sOkHttpUtils == null) {
            synchronized (OkHttpUtils.class) {
                if (sOkHttpUtils == null) {
                    sOkHttpUtils = new OkHttpUtils(okHttpClient);
                }
            }
        }
        return sOkHttpUtils;
    }

    private OkHttpUtils(OkHttpClient okHttpClient) {
        if (okHttpClient == null) {
            mOkHttpClient = new OkHttpClient();
        } else {
            mOkHttpClient = okHttpClient;
        }
    }

    public static OkHttpUtils getInstance() {
        return initOkHttpUtils(null);
    }

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    public static KeyManagerFactory getKeyManagerFactory(byte[] p12) {
        KeyManagerFactory kmf = null;
        try {
            KeyStore p12KeyStore = KeyStore.getInstance("PKCS12");
            InputStream in = new ByteArrayInputStream(p12);
            p12KeyStore.load(in, "".toCharArray());
            kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(p12KeyStore, "".toCharArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return kmf;
    }

    /**
     * 这里配置初始化ca，拿到trustmanager
     */
    public static TrustManagerFactory getTrustManagerFactory(byte[] caBytes) {
        if (caBytes == null) return null;
        TrustManagerFactory tmf = null;
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream caInput = new ByteArrayInputStream(caBytes);
            Certificate ca = null;
            try {
                ca = cf.generateCertificate(caInput);
                System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                caInput.close();
            }
            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);
            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tmf;
    }

    public static void createAvatarRequest(final String uploadFile, int gender, boolean isQ, String paramJson, final Callback callback) {
//        String url = "http://116.62.247.68:20182" + "/upload_nama";
        String url = "http://116.62.247.68:20193" + "/upload_nama";
        Log.e(TAG, "createAvatarRequest url " + url);
        Log.e(TAG, "createAvatarRequest uploadFile " + uploadFile);
        final File reallyUploadFile = new File(uploadFile);
        RequestBody requestBody = (new okhttp3.MultipartBody.Builder())
                .setType(MultipartBody.FORM)
                .addFormDataPart("gender", String.valueOf(gender))
                .addFormDataPart("is_q", isQ ? "1" : "0")
//                .addFormDataPart("params", paramJson)
                .addFormDataPart("input", "filename", RequestBody.create(MediaType.parse("image/png"), reallyUploadFile))
                .build();
        getInstance().getOkHttpClient().newCall(new Request.Builder().url(url).post(requestBody).build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(call, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
               /* if (!reallyUploadFile.equals(uploadFile)) {
                    reallyUploadFile.delete();
                }*/
                callback.onResponse(call, response);
            }
        });
    }

    public static void cancelAll() {
        for (Call call : getInstance().getOkHttpClient().dispatcher().queuedCalls()) {
            call.cancel();
        }
        for (Call call : getInstance().getOkHttpClient().dispatcher().runningCalls()) {
            call.cancel();
        }
    }
}
