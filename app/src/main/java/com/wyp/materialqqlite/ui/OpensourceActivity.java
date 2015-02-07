package com.wyp.materialqqlite.ui;

import android.annotation.TargetApi;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.wyp.materialqqlite.R;

public class OpensourceActivity extends ActionBarActivity {
    private SharedPreferences sp;
    private int color_theme;
    private Toolbar toolbar;
    private ListView openlist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opensource);

        sp=getSharedPreferences("theme",MODE_PRIVATE);
        color_theme=sp.getInt("color",-12627531);

        openlist= (ListView) findViewById(R.id.open_list);
        toolbar= (Toolbar) findViewById(R.id.toolbar_open);
        toolbar.setBackgroundColor(color_theme);
        toolbar.setTitle(R.string.opensource);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintColor(color_theme);

        OpenAdapter adapter = new OpenAdapter(this);
        openlist.setAdapter(adapter);

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




