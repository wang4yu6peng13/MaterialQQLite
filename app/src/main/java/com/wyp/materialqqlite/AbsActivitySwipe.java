package com.wyp.materialqqlite;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import me.imid.swipebacklayout.lib.SwipeBackLayout;

public abstract class AbsActivitySwipe extends MySwipeBackActivity {

    protected Toolbar toolbar;
    protected ActionBar mActionBar;

    private SwipeBackLayout mSwipeBackLayout;
    protected SharedPreferences sp;
    protected int color_theme;
    protected int statusBarHeight = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /** Set up translucent status bar */
        if (Build.VERSION.SDK_INT >= 19 && !Utility.isChrome()) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            statusBarHeight = Utility.getStatusBarHeight(getApplicationContext());
        }

        if (Build.VERSION.SDK_INT >= 21) {
	        // 如果启用此选项在 Lollipop 下状态栏将完全透明
	        // getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        super.onCreate(savedInstanceState);
    }

    protected abstract void setUpViews();

    @Override
    public void setContentView(@LayoutRes int layoutResId) {
        super.setContentView(layoutResId);
        sp = getSharedPreferences("theme",MODE_PRIVATE);
        color_theme = sp.getInt("color", -12627531);
        try {
            View statusHeaderView = findViewById(R.id.statusHeaderView);
            statusHeaderView.setBackgroundColor(color_theme);
            statusHeaderView.getLayoutParams().height = statusBarHeight;
        } catch (NullPointerException e) {

        }

        try {
            toolbar = (Toolbar) findViewById(R.id.toolbar_chat);
            setSupportActionBar(toolbar);
        } catch (Exception e) {

        }
        mActionBar = getSupportActionBar();

        try {
            mSwipeBackLayout = getSwipeBackLayout();
            mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT );
            mSwipeBackLayout.setEnableGesture( true);
        } catch (Exception e) {

        }

        setUpViews();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mSwipeBackLayout != null) {
            scrollToFinishActivity();
        } else {
            super.onBackPressed();
        }
    }

}
