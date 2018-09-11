package com.shijiaoji;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class MainActivity extends Activity {

    private String TAG = "MainActivity";
    private WebView webView;
    SharedPreferences sp;
    String url="http://www.lanou3g.com/";
//    String url="file:///android_asset/bgline.html";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //创建一个sharedpreferences
        sp = getSharedPreferences("aaa", MODE_PRIVATE);
        webView = (WebView) findViewById(R.id.main_webview);
        // 触摸焦点起作用.如果不设置，则在点击网页文本输入框时，不能弹出软键盘及不响应其他的一些事件。
        webView.requestFocus();
        // 设置WebView的客户端
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // 在开始加载网页时会回调
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 拦截 url 跳转,在里边添加点击链接跳转或者操作
                CookieManager cookieManager=CookieManager.getInstance();
                String cookie=cookieManager.getCookie(url);
                sp.edit().putString("cook", cookie).apply();
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
             /*   // 在结束加载网页时会回调

                // 获取页面内容
                view.loadUrl("javascript:window.java_obj.showSource("
                        + "document.getElementsByTagName('html')[0].innerHTML);");

                // 获取解析<meta name="share-description" content="获取到的值">
                view.loadUrl("javascript:window.java_obj.showDescription("
                        + "document.querySelector('meta[name=\"share-description\"]').getAttribute('content')"
                        + ");");*/
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                // 加载错误的时候会回调，在其中可做错误处理，比如再请求加载一次，或者提示404的错误页面
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view,
                                                              WebResourceRequest request) {
                // 在每一次请求资源时，都会通过这个函数来回调
                return super.shouldInterceptRequest(view, request);
            }

        });
        webView.addJavascriptInterface(new InJavaScriptLocalObj(), "java_obj");
        setParamera();
        synCookies(this, url,sp.getString("cook", ""));
        webView.loadUrl(url);

    }


    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        Log.w(TAG, "======onKeyLongPress==========");
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // 判断普通按键
        int keyCode = event.getKeyCode();
        int keyAction = event.getAction();
        if (keyCode == KeyEvent.KEYCODE_BACK && keyAction == KeyEvent.ACTION_UP) {
            if (webView.canGoBack()) {
                webView.goBack();
                return true;
            }

        }

        Log.w(TAG, "keyCode==" + keyCode);
        if (keyAction == KeyEvent.ACTION_DOWN) {
            Log.w(TAG, "keyAction==KeyEvent.ACTION_DOWN");
        } else if (keyAction == KeyEvent.ACTION_UP) {
            Log.w(TAG, "keyAction==KeyEvent.ACTION_UP");
        } else {
            Log.w(TAG, "keyAction==" + keyAction);
        }
        return super.dispatchKeyEvent(event);
    }

    private void setParamera() {
        WebSettings webSettings = webView.getSettings();
        // 让WebView能够执行javaScript
        webSettings.setJavaScriptEnabled(true);
        // 让JavaScript可以自动打开windows
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        // 设置缓存
        webSettings.setAppCacheEnabled(true);
        // 设置缓存模式,一共有四种模式
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        // 设置缓存路径
//        webSettings.setAppCachePath("");
        // 支持缩放(适配到当前屏幕)
        webSettings.setSupportZoom(true);
        // 将图片调整到合适的大小
        webSettings.setUseWideViewPort(true);
        // 支持内容重新布局,一共有四种方式
        // 默认的是NARROW_COLUMNS
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        // 设置可以被显示的屏幕控制
        webSettings.setDisplayZoomControls(true);
        // 设置默认字体大小
        webSettings.setDefaultFontSize(12);
        // 设置webview加载的页面的模式,缩放至屏幕的大小
        webSettings.setLoadWithOverviewMode(true);
    }


    public final class InJavaScriptLocalObj {
        @JavascriptInterface
        public void showSource(String html) {
            Log.d(TAG,"====>html=" + html);
        }

        @JavascriptInterface
        public void showDescription(String str) {
            Log.d(TAG,"====>html=" + str);
        }
    }

    /**
     * 同步一下cookie
     * 在mWebView.loadUrl(url);之前设置一下cookies
     */
    public void synCookies(Context context, String url,String cookies) {
        CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
//        cookieManager.removeSessionCookie();//移除
        cookieManager.setCookie(url, cookies);
        CookieSyncManager.getInstance().sync();
    }

}
