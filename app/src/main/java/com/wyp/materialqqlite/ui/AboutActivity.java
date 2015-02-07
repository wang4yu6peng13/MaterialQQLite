package com.wyp.materialqqlite.ui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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
import android.widget.TextView;
import android.widget.Toast;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.wyp.materialqqlite.R;

public class AboutActivity extends ActionBarActivity {
    private ListView aboutlist;
    private Toolbar toolbar;
    private SharedPreferences sp;
    private int color_theme;
    private TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        aboutlist= (ListView) findViewById(R.id.about_list);
        toolbar= (Toolbar) findViewById(R.id.toolbar_about);
        sp=getSharedPreferences("theme",MODE_PRIVATE);
        color_theme=sp.getInt("color",-12627531);
        toolbar.setBackgroundColor(color_theme);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintColor(color_theme);

        tv= (TextView) findViewById(R.id.appname_version);
        tv.setText(getString(R.string.app_name)+" "+getVersionName());

        toolbar.setTitle(R.string.aboutandfeedback);
        AboutAdapter adapter = new AboutAdapter(this);
       aboutlist.setAdapter(adapter);
        aboutlist.setOnItemClickListener(new AdapterView.OnItemClickListener() { //为每一个item设置相应的响应

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                switch(position){
                    case 0:
                        startActivity(ReadmeActivity.class);
                        break;
                    case 1:
                        startActivity(OpensourceActivity.class);
                        break;
                    case 2:
                        startActivity(DeveloperActivity.class);
                        break;
                    case 3:
                        Toast.makeText(AboutActivity.this, "崩溃日志在MaterialQQLite/目录下，请将crash开头的文件发给开发者以解决。", Toast.LENGTH_LONG).show();
                        break;

                }
            }
        });
    }
    private void startActivity(Class<?>cls){
        Intent intent = new Intent(this, cls);
        this.startActivity(intent);
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

    private String getVersionName() {
        try {
            // 获取packagemanager的实例
            PackageManager packageManager = getPackageManager();
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo = null;

            packInfo = packageManager.getPackageInfo(getPackageName(), 0);
            String version = packInfo.versionName;
            return version;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }

    }
}




