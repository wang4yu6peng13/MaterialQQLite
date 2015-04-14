package com.wyp.materialqqlite;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


/* 
使用注意：
1：由于WebView只能在UI主线程调用，所以必须在UI主线程创建该类对象。
2：当在后台线程想通过该类运行JS脚本的时候，需要使用sendMsg_RunJs，而不能使用runJs。
3：目前的JS返回值只支持返回单一String类型。
4：混淆代码的时候需要在proguard-project.txt里面添加以下代码。
proguard-project.txt：
-keepclassmembers class com.zym.mingqq.JsEngine$JsObject {
	public *;
}
-keepattributes *Annotation*
-keepattributes *JavascriptInterface*
android webview中调用了js的时候混淆注意事项
http://blog.csdn.net/minenamewj/article/details/40112335
*/

// 使用WebView封装的JS脚本引擎
public class JsEngine {
	private Context m_context;
	private WebView m_webView;
	private Handler m_hCallBack;
	private JsObject m_jsObject;
	
	private Handler m_hRunJs = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:		// 调用JS代码计算密码Hash
				JsObject jsObj = (JsObject)msg.obj;
				runJs(jsObj.getUrl(), jsObj.getFuncName(), jsObj.getArg());				
				break;
			
			default:
				break;
			}
		}
	};
	
	public JsEngine(Context context) {
		m_context = context;
		m_webView = new WebView(m_context);
		m_jsObject = new JsObject();
		
		WebSettings setting = m_webView.getSettings();
		setting.setJavaScriptEnabled(true);
		
		m_webView.addJavascriptInterface(m_jsObject, "JsObject");
	}
	
	// 设置回调通知句柄
	public void setCallBackHandler(Handler handler) {
		m_hCallBack = handler;
	}
	
	public void sendMsg_RunJs(String strUrl, String strFuncName, String strArg) {
		m_jsObject.setResult("");	// 清空上一次执行的返回值
		
		if (m_hRunJs != null) {
			JsObject jsObj = new JsObject();
			jsObj.setUrl(strUrl);
			jsObj.setFuncName(strFuncName);
			jsObj.setArg(strArg);
			
			Message msg = m_hRunJs.obtainMessage();
			msg.what = 0;
			msg.arg1 = 0;
			msg.arg2 = 0;
			msg.obj = jsObj;
			m_hRunJs.sendMessage(msg);
		}
	}
	
	// 运行JS脚本代码
    public void runJs(String strUrl, String strFuncName, String strArg) {
		try {
			m_jsObject.setUrl(strUrl);
			m_jsObject.setFuncName(strFuncName);
			m_jsObject.setArg(strArg);
			m_jsObject.setResult("");	// 清空上一次执行的返回值
			
			m_webView.setWebViewClient(new WebViewClient() {
				@Override
				public void onPageFinished(WebView view, String url) {
					String strUrl = "javascript:" + m_jsObject.getFuncName() + "(";
					String strArg = m_jsObject.getArg();
					if (strArg != null && strArg.length() > 0)
						strUrl += strArg;
					strUrl += ")";
					m_webView.loadUrl(strUrl);
					super.onPageFinished(view, url);
				}
			});

			m_webView.loadUrl(strUrl);
		} catch (Exception e) {
			e.printStackTrace();
		} 
    }
    
    // 获取JS返回值
    public String getJsResult() {
    	return m_jsObject.getResult();
    }
	
    // 设置JS返回值
    public void setJsResult(String strResult) {
    	m_jsObject.setResult(strResult);
    }
    
	public class JsObject {
		private String m_strUrl;
		private String m_strFuncName;
		private String m_strArg;
		private String m_strResult;

	    public String getUrl() {
	    	return m_strUrl;
	    }
	    
	    public void setUrl(String strUrl) {
	    	m_strUrl = strUrl;
	    }

	    public String getFuncName() {
	    	return m_strFuncName;
	    }
	    
	    public void setFuncName(String strFuncName) {
	    	m_strFuncName = strFuncName;
	    }

	    public String getArg() {
	    	return m_strArg;
	    }
	    
	    public void setArg(String strArg) {
	    	m_strArg = strArg;
	    }

	    public String getResult() {
	    	return m_strResult;
	    }
	    
	    public void setResult(String strResult) {
	    	m_strResult = strResult;
	    }
	    
		@JavascriptInterface
		public void returnResult(String strResult) {
			System.out.println(strResult);
			setResult(strResult);
			
			// 通知回调接口获取JS的返回值
			if (m_hCallBack != null) {
				m_hCallBack.sendEmptyMessage(0);
			}
		}
	}
}