package com.wyp.materialqqlite.ui;

import android.content.SharedPreferences;
import android.support.v4.view.ViewCompat;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.wyp.materialqqlite.AbsActivity;
import com.wyp.materialqqlite.R;

public class DeveloperActivity extends AbsActivity{

    private SharedPreferences sp;
    private int color_theme;
    private TextView tv_MingQQ,tv_MQQL,tv_thanks;
    private View line_mingqq,line_mqql,line_thanks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer);
    }


    @Override
    public void setUpViews() {
        ViewCompat.setElevation(mToolbar, getResources().getDimension(R.dimen.toolbar_elevation));

        sp = getSharedPreferences("theme", MODE_PRIVATE);
        color_theme = sp.getInt("color", -12627531);

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
