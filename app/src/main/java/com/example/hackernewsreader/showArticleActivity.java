package com.example.hackernewsreader;

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
        WebView articelWebView = findViewById(R.id.articleWebView);
        articelWebView.getSettings().setJavaScriptEnabled(true);
        articelWebView.setWebViewClient(new WebViewClient());
        String url = getIntent().getStringExtra("url");
        articelWebView.loadUrl(url);

    }
}
