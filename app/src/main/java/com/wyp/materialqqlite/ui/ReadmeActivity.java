package com.wyp.materialqqlite.ui;

import android.support.v4.view.ViewCompat;
import android.os.Bundle;
import android.webkit.WebView;


import com.wyp.materialqqlite.AbsActivity;
import com.wyp.materialqqlite.R;

public class ReadmeActivity extends AbsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_readme);
    }

    @Override
    public void setUpViews() {
        ViewCompat.setElevation(mToolbar, getResources().getDimension(R.dimen.toolbar_elevation));
        WebView webView= (WebView) findViewById(R.id.webview_readme);
        webView.loadUrl(" file:///android_asset/readme.html ");
    }



}
