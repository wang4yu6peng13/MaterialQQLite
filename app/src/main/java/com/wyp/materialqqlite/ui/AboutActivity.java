package com.wyp.materialqqlite.ui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wyp.materialqqlite.AbsActivity;
import com.wyp.materialqqlite.R;
import com.wyp.materialqqlite.Utility;

public class AboutActivity extends AbsActivity {

    public int statusBarHeight = 0;
    private ListView aboutlist;

    private TextView tv;
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
        setContentView(R.layout.activity_about);

    }


    @Override
    public void setUpViews() {
        View statusHeaderView = findViewById(R.id.statusHeaderView);
        statusHeaderView.getLayoutParams().height = statusBarHeight;
        ViewCompat.setElevation(mToolbar, getResources().getDimension(R.dimen.toolbar_elevation));
        aboutlist= (ListView) findViewById(R.id.about_list);



        tv= (TextView) findViewById(R.id.appname_version);
        tv.setText(getString(R.string.app_name)+" "+getVersionName());


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




