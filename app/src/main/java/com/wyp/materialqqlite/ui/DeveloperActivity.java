package com.wyp.materialqqlite.ui;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.wyp.materialqqlite.R;

public class DeveloperActivity extends ActionBarActivity {
    private SharedPreferences sp;
    private int color_theme;
    private Toolbar toolbar;
    private TextView tv_MingQQ,tv_MQQL,tv_thanks;
    private View line_mingqq,line_mqql,line_thanks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer);
        sp=getSharedPreferences("theme",MODE_PRIVATE);
        color_theme=sp.getInt("color",-12627531);
        toolbar= (Toolbar) findViewById(R.id.toolbar_dev);
        toolbar.setBackgroundColor(color_theme);
        toolbar.setTitle(R.string.developers);

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintColor(color_theme);
    }
    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }


}
