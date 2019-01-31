package com.example.hackernewsreader;

import android.opengl.Visibility;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class showArticleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_article);
        final ProgressBar progressBar = findViewById(R.id.progressBar);
        final WebView articelWebView = findViewById(R.id.articleWebView);
        articelWebView.setVisibility(View.GONE);
        articelWebView.getSettings().setJavaScriptEnabled(true);
        articelWebView.setWebViewClient(new WebViewClient());
        String url = getIntent().getStringExtra("url");
        articelWebView.setWebViewClient(new WebViewClient(){
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
                articelWebView.setVisibility(View.VISIBLE);
            }
        });
        articelWebView.loadUrl(url);


    }
}
