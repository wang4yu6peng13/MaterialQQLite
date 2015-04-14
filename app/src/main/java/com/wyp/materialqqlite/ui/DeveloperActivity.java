package com.wyp.materialqqlite.ui;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.TextView;

import com.wyp.materialqqlite.AbsActivity;
import com.wyp.materialqqlite.R;
import com.wyp.materialqqlite.Utility;

public class DeveloperActivity extends AbsActivity{
    private SharedPreferences sp;
    private int color_theme;
    public int statusBarHeight = 0;
    private TextView tv_MingQQ,tv_MQQL,tv_thanks;
    private View line_mingqq,line_mqql,line_thanks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /** 当SDK >= 19 且 不是Chrome浏览器 时启用透明状态栏 */
        if (Build.VERSION.SDK_INT >= 19 && !Utility.isChrome()) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            statusBarHeight = Utility.getStatusBarHeight(getApplicationContext());
        }

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer);



    }


    @Override
    public void setUpViews() {
        View statusHeaderView = findViewById(R.id.statusHeaderView);
        statusHeaderView.getLayoutParams().height = statusBarHeight;
        ViewCompat.setElevation(mToolbar, getResources().getDimension(R.dimen.toolbar_elevation));
        sp=getSharedPreferences("theme",MODE_PRIVATE);
        color_theme=sp.getInt("color",-12627531);

        tv_MingQQ= (TextView) findViewById(R.id.tv_MingQQ);
        tv_MQQL= (TextView) findViewById(R.id.tv_MQQL);
        tv_thanks= (TextView) findViewById(R.id.tv_thanks);

        tv_MingQQ.setTextColor(color_theme);
        tv_MQQL.setTextColor(color_theme);
        tv_thanks.setTextColor(color_theme);

        line_mingqq=findViewById(R.id.line_mingqq);
        line_mqql=findViewById(R.id.line_mqql);
        line_thanks=findViewById(R.id.line_thanks);

        line_mingqq.setBackgroundColor(color_theme);
        line_mqql.setBackgroundColor(color_theme);
        line_thanks.setBackgroundColor(color_theme);


    }

}
