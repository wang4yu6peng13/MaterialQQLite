package com.wyp.materialqqlite.ui;

import android.support.v4.view.ViewCompat;
import android.os.Bundle;
import android.widget.ListView;

import com.wyp.materialqqlite.AbsActivity;
import com.wyp.materialqqlite.R;

public class OpensourceActivity extends AbsActivity {

    private ListView openlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opensource);
    }

    @Override
    public void setUpViews() {
        ViewCompat.setElevation(mToolbar, getResources().getDimension(R.dimen.toolbar_elevation));
        openlist = (ListView) findViewById(R.id.open_list);
        OpenAdapter adapter = new OpenAdapter(this);
        openlist.setAdapter(adapter);
    }

}




