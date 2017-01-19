package com.biglynx.fulfiller.network;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.BuildConfig;
import android.support.v7.widget.helper.ItemTouchHelper.Callback;
import android.util.Log;


import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.protocol.HTTP;

public class NetworkOperation extends AsyncTask<String, Integer, NetworkResponse> {
    private static final String RESPONSE_CODE = "code";
    private static final String RESPONSE_MESSAGE = "responseMsg";
    private static final String RESPONSE_STATUS = "status";
    private static HttpClient httpclient;
    private static String sessionCookie;
    private String contentType;
    private NetworkOperationListener listener;
    private String method;
    private Object tag;
    int timeoutConnection;
    private String url;

    /* renamed from: com.getdoctor.rk.ui.network.NetworkOperation.1 */
    class C03911 implements Runnable {
        final /* synthetic */ Dialog val$d;

        C03911(Dialog dialog) {
            this.val$d = dialog;
        }

        public void run() {
            this.val$d.dismiss();
        }
    }

    public NetworkOperation(NetworkOperationListener listener, Object tag) {
        this.timeoutConnection = 50000;
        this.listener = listener;
        this.tag = tag;
        HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, this.timeoutConnection);
        HttpConnectionParams.setSoTimeout(httpParameters, this.timeoutConnection);
        httpclient = new DefaultHttpClient(httpParameters);
    }

    public void timerDelayRemoveDialog(long time, Dialog d) {
        new Handler().postDelayed(new C03911(d), time);
    }

    public static String getSessionCookie() {
        return sessionCookie;
    }

    private HttpRequestBase createRequest(String... params) throws UnsupportedEncodingException {
        HttpRequestBase request = null;
        String url = params[0];
        this.url = url;
        this.method = params[1];
        if (this.method == HttpAdapter.METHOD_GET) {
            request = new HttpGet(url);
        } else if (this.method == HttpAdapter.METHOD_POST) {
            HttpRequestBase post;
            if (params.length > 2) {
                post = new HttpPost(url);
                if (params[2] != null) {
                    System.out.println(params[2]);
                   // post.setEntity(new StringEntity(params[2]));
                }
                if (this.contentType == null) {
                    post.addHeader(HTTP.CONTENT_TYPE, HttpAdapter.CONTENT_TYPE_APPLICATION_JSON);
                } else {
                    post.addHeader(HTTP.CONTENT_TYPE, this.contentType);
                }
                request = post;
            }
            if (params.length == 2) {
                post = new HttpPost(url);
                if (this.contentType == null) {
                    post.addHeader(HTTP.CONTENT_TYPE, HttpAdapter.CONTENT_TYPE_APPLICATION_JSON);
                } else {
                    post.addHeader(HTTP.CONTENT_TYPE, this.contentType);
                }
                request = post;
            }
        } else if (this.method == HttpAdapter.METHOD_DELETE) {
            request = new HttpDelete(url);
        } else if (this.method == HttpAdapter.METHOD_UPDATE) {
            return null;
        } else {
            if (this.method == HttpAdapter.METHOD_PUT) {
                HttpRequestBase put = new HttpPut(url);
                if (this.contentType == null) {
                    put.addHeader(HTTP.CONTENT_TYPE, HttpAdapter.CONTENT_TYPE_APPLICATION_JSON);
                } else {
                    put.addHeader(HTTP.CONTENT_TYPE, this.contentType);
                }
                request = put;
            }
        }
        request.setHeader("Cookie", sessionCookie);
        System.out.println(sessionCookie + ":::::::::::");
        return request;
    }

    protected NetworkResponse doInBackground(String... params) {
        NetworkResponse nResponse = new NetworkResponse();
        nResponse.setTag(this.tag);
        try {
            HttpResponse response = httpclient.execute(createRequest(params));
            System.out.println("response:" + response);
            StatusLine statusLine = response.getStatusLine();
            System.out.println("statusLine:" + statusLine);
            System.out.println("getStatusCode:" + statusLine.getStatusCode());
            if (statusLine.getStatusCode() < Callback.DEFAULT_DRAG_ANIMATION_DURATION || statusLine.getStatusCode() > 202) {
                nResponse.setStatusCode(statusLine.getStatusCode());
                nResponse.setResponseString(extractResponseString(response.getEntity().getContent()));
                return nResponse;
            }
            Header header = response.getFirstHeader("Set-Cookie");
            if (header != null && this.url.endsWith("preAuth/accounts/verify")) {
                sessionCookie = header.getValue();
                Log.i("LOGIN_SESSION", sessionCookie);
            }
            HttpEntity entity = response.getEntity();
            System.out.println("entity size:" + entity.getContentLength());
            String json = extractResponseString(entity.getContent());
            System.out.println("json size:" + json.length());
            nResponse.setStatusCode(statusLine.getStatusCode());
            if (json != null && json.trim().length() > 0) {
                nResponse.setResponseObject(parseResponseString(json, params[0]));
            }
            nResponse.setResponseString(json);
            return nResponse;
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            nResponse.setResponseString("Unable to contact server, please try again later");
            nResponse.setResponseStatus("ERROR");
            return nResponse;
        } catch (IOException e2) {
            e2.printStackTrace();
            nResponse.setResponseString("Unable to contact server, please try again later");
            nResponse.setResponseStatus("ERROR");
            return nResponse;
        } catch (JSONException e3) {
            e3.printStackTrace();
            nResponse.setResponseString("Unable to contact server, please try again later");
            nResponse.setResponseStatus("ERROR");
            return nResponse;
        }
    }

    private String extractResponseString(InputStream content) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(content));
        String s = new String();
        StringBuffer stringBuffer = new StringBuffer();
        while (true) {
            s = reader.readLine();
            if (s != null) {
                stringBuffer.append(s);
            } else {
                String json = stringBuffer.toString();
                System.out.println(json);
                return json;
            }
        }
    }

    public Object parseResponseString(String jsonString, String call) throws JSONException {
        System.out.println("Response....." + jsonString);
        return null;
    }

    protected void onPostExecute(NetworkResponse result) {
        super.onPostExecute(result);
        this.listener.operationCompleted(result);
    }

    public String getContentType() {
        return this.contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public static void clearCookies() {
        sessionCookie = BuildConfig.FLAVOR;
    }

    public static void setSessionCookie(String cookie) {
        sessionCookie = cookie;
    }
}
