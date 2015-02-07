package com.wyp.materialqqlite.ui;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;

public class MyDialog extends Dialog {    
    public MyDialog(Context context, int style, int layout) {
        super(context, style);
        setContentView(layout);
    }
        
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU)
			this.dismiss();
		return super.onKeyDown(keyCode, event);
	}
}
