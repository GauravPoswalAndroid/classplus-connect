package com.classplus.connect.views;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.classplus.connect.R;
import com.classplus.connect.webview.VideoEnabledWebChromeClient;
import com.classplus.connect.webview.VideoEnabledWebView;

public class WebViewActivity extends AppCompatActivity {

    public static final String PARAM_URL = "PARAM_URL";
    private ProgressBar progressBar;
    private VideoEnabledWebView webView;
    private VideoEnabledWebChromeClient webChromeClient;

    public static void startActivity(Context context, String url) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(PARAM_URL, url);
        context.startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        View non_video_view = findViewById(R.id.non_video_view);
        ViewGroup video_view = findViewById(R.id.video_view);
        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);

        webChromeClient = new VideoEnabledWebChromeClient(non_video_view, video_view, progressBar, webView);

        webChromeClient.setFileSelectionCallback(new VideoEnabledWebChromeClient.FileSelectionCallback() {
            @Override
            public void openFileSelectionActivity() {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                startActivityForResult(
                        Intent.createChooser(intent, getString(R.string.select_file)),
                        VideoEnabledWebChromeClient.FILE_CHOOSER_RESULT_CODE);
            }

            @Override
            public void openFileSelectionActivity(WebChromeClient.FileChooserParams fileChooserParams) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Intent intent = fileChooserParams.createIntent();
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,
                            fileChooserParams.getMode() == WebChromeClient.FileChooserParams.MODE_OPEN_MULTIPLE);
                    startActivityForResult(
                            Intent.createChooser(intent, getString(R.string.select_file)),
                            VideoEnabledWebChromeClient.FILE_CHOOSER_RESULT_CODE);
                }
            }
        });

        webView.setWebChromeClient(webChromeClient);

        WebSettings webSettings = webView.getSettings();
//        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        //webSettings.setBuiltInZoomControls(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadsImagesAutomatically(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webView.setWebViewClient(new ViewEnabledClient());

        String url = getIntent().getStringExtra(PARAM_URL);
        webView.loadUrl(url);
        webView.setLongClickable(false);

        // Set JavaScript Interface
//        webView.addJavascriptInterface(new JSInterface(), "mobile");
    }

    /**
     * Patch fix for webview crash on lollipop
     * See this: https://stackoverflow.com/a/58812125
     *
     * @param overrideConfiguration
     */
    @Override
    public void applyOverrideConfiguration(Configuration overrideConfiguration) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP
                || Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP_MR1) {
            return;
        } else
            super.applyOverrideConfiguration(overrideConfiguration);
    }

    @Override
    protected void onDestroy() {
        destroyWebView();
        super.onDestroy();
    }

    private void destroyWebView() {
        webView.clearHistory();
        webView.clearCache(false);
        webView.loadUrl("about:blank");
        webView.onPause();
        webView.removeAllViews();
        webView.destroyDrawingCache();
        webView.destroy();
        webView = null;
    }

    @Override
    public void onBackPressed() {
        if (!webChromeClient.onBackPressed()) {
            if (webView.canGoBack()) {
                webView.goBack();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VideoEnabledWebChromeClient.FILE_CHOOSER_RESULT_CODE) {
//            if (null == uploadMessage && null == uploadMessageAboveL) return;
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (webChromeClient.fileReceivedMessageAboveL != null) {
                onActivityResultAboveL(requestCode, resultCode, data);
            } else if (webChromeClient.fileReceivedMessage != null) {
                webChromeClient.fileReceivedMessage.onReceiveValue(result);
                webChromeClient.fileReceivedMessage = null;
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent intent) {
        if (requestCode != VideoEnabledWebChromeClient.FILE_CHOOSER_RESULT_CODE || webChromeClient.fileReceivedMessageAboveL == null)
            return;
        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
            if (intent != null) {
                String dataString = intent.getDataString();
                ClipData clipData = intent.getClipData();
                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                    }
                } else if (dataString != null)
                    results = new Uri[]{Uri.parse(dataString)};
            }
        }
        webChromeClient.fileReceivedMessageAboveL.onReceiveValue(results);
        webChromeClient.fileReceivedMessageAboveL = null;
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public class ViewEnabledClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            if (!isNetworkConnected()) {
                view.setVisibility(View.INVISIBLE);
                Toast.makeText(view.getContext(), "No internet connection", Toast.LENGTH_SHORT)
                        .show();
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (URLUtil.isNetworkUrl(url)) {
                view.loadUrl(url);
            } else {
                Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                if (webIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(webIntent);
                }
            }
            return true;
        }
    }
}
