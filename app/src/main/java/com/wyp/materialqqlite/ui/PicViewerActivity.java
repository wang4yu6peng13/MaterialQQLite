package com.wyp.materialqqlite.ui;

import java.io.File;
import java.util.List;

import com.wyp.materialqqlite.AppData;
import com.wyp.materialqqlite.HomeWatcher;
import com.wyp.materialqqlite.HomeWatcher.OnHomePressedListener;
import com.wyp.materialqqlite.R;
import com.wyp.materialqqlite.qqclient.QQClient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class PicViewerActivity extends Activity 
	implements OnHomePressedListener {
	
	private ViewPager mViewPager;
	private TestAdapter mAdapter;
	private String m_strPath;
	private List<String> m_arrFileName;
	private int m_nCurIndex = 0;
	private HomeWatcher mHomeWatcher;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_picviewer);
		
		QQClient client = AppData.getAppData().getQQClient();
		m_strPath = client.getChatPicFolder(0);
				
		Intent intent = getIntent();
		m_arrFileName = intent.getExtras().getStringArrayList("urls");
    	m_nCurIndex = intent.getIntExtra("curindex", 0);
    	
		mViewPager = (ViewPager)findViewById(R.id.pager);  
	    mAdapter = new TestAdapter(this, m_arrFileName);
	    mViewPager.setAdapter(mAdapter);
	    
	    mViewPager.setCurrentItem(m_nCurIndex);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		AppData.getAppData().cancelNotify(1);
		mHomeWatcher = new HomeWatcher(this);
		mHomeWatcher.setOnHomePressedListener(this);
		mHomeWatcher.startWatch();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		mHomeWatcher.setOnHomePressedListener(null);
		mHomeWatcher.stopWatch();
	}
	
    @Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onHomePressed() {
		String strTicker = getString(R.string.bgrun);
		String strTitle = getString(R.string.app_name);
		String strText = getString(R.string.nonewmsg);
		AppData.getAppData().showNotify(1, this, 
				strTicker, strTitle, strText);
	}

	@Override
	public void onHomeLongPressed() {
		// do nothing
	}

    public class TestAdapter extends PagerAdapter {
    	  
        private List<String> mPaths;  
          
        private Context mContext;  
          
        public TestAdapter(Context cx, List<String> paths) {  
            mContext = cx.getApplicationContext();
            mPaths = paths;
        }
        
        @Override  
        public int getCount() {  
            // TODO Auto-generated method stub  
            return mPaths.size();
        }  
      
        @Override  
        public boolean isViewFromObject(View view, Object obj) {  
            // TODO Auto-generated method stub  
            return view == (View)obj;
        }  
      
        @Override  
        public Object instantiateItem (ViewGroup container, int position) {  
            ImageView iv = new ImageView(mContext);  
            try {
            	iv.setBackgroundColor(Color.BLACK);
            	iv.setScaleType(ScaleType.CENTER_INSIDE);
            	String strFileName = m_strPath+mPaths.get(position);
            	File file = new File(strFileName);
            	if (file.exists()) {
            		Bitmap bm = BitmapFactory.decodeFile(strFileName);
                    if (bm != null) {
                    	iv.setImageBitmap(bm);                	
                    } else {
                    	iv.setImageResource(R.drawable.aio_image_default_round);
                    }	
            	} else {
            		iv.setImageResource(R.drawable.aio_image_default_round);
            	}
            } catch (OutOfMemoryError e) {  
                // TODO Auto-generated catch block  
                e.printStackTrace();  
            }  
            ((ViewPager)container).addView(iv, 0);
            return iv;  
        }  
          
        @Override  
        public void destroyItem (ViewGroup container, int position, Object object) {  
            container.removeView((View)object);  
        }  
    }
}
