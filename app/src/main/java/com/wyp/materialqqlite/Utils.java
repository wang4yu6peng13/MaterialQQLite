package com.wyp.materialqqlite;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;


public class Utils {
	public static byte[] InputStreamToByte(InputStream is) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int ch;
		byte[] buf = new byte[1024 * 4];
		while ((ch = is.read(buf)) != -1) {
			out.write(buf, 0, ch);
		}
		byte data[] = out.toByteArray();
		out.close();
		return data;
	} 
	
	public static String GetBetweenString(String str, String strStart,
			String strEnd) {
		int nPos1 = str.indexOf(strStart);
		if (nPos1 != -1) {
			int nPos2 = str.indexOf(strEnd, nPos1+1);
			if (nPos2 != -1) {
				return str.substring(nPos1+1, nPos2);
			}
		}
		return str;
	}
	
	// 合并两个byte数组  
    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2){  
        byte[] byte_3 = new byte[byte_1.length+byte_2.length];  
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);  
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);  
        return byte_3;  
    }
    
    public static int addBytes(byte[] array, int pos, byte[] add) {
        System.arraycopy(add, 0, array, pos, add.length);
        return pos+add.length;
    }
    
	public static int addString(byte[] array, int pos, String add)
			throws UnsupportedEncodingException {
		return addString(array, pos, add, "UTF-8");
	}
    
	public static int addString(byte[] array, int pos, String add,
			String charsetName) throws UnsupportedEncodingException {
		return addBytes(array, pos, add.getBytes(charsetName));
	}
	
	// 写数据
	public static void writeFile(String fileName, byte[] bytData) {
		try {
			FileOutputStream fos = new FileOutputStream(fileName);
	        fos.write(bytData);  
	        fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static boolean isEmptyStr(String str) {
		if (null == str || str.length() <= 0)
			return true;
		else
			return false;
	}
	
	public static String unicodeToHexStr(String str, boolean bDblSlash) {
		String strRet = "";
		String strSlash = (bDblSlash ? "\\\\u" : "\\u");
		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);
			if (ch > 255)
				strRet += strSlash + Integer.toHexString(ch);
			else
				strRet += ch;
		}
		return strRet;
	}
		
	public static int HexStrToRGB(String str, int nDefColor) {
		try {
			if (isEmptyStr(str))
				return nDefColor;
			
			if (str.length() >= 2 && 
					str.substring(0, 2).equalsIgnoreCase("0x")) {
				str = str.substring(2);
				if (isEmptyStr(str))
					return nDefColor;
			}
			
			return (int)Long.parseLong(str, 16);	// Java大端字节序
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nDefColor;
	}

	public static int HexStrToRGB(String str) {
		return HexStrToRGB(str, 0);
	}
	
	public static String RGBToHexStr(int color) {
		return Integer.toHexString(color);
	}
	
	public static int HexStrToARGB(String str, int nDefColor) {
		try {
			if (Utils.isEmptyStr(str))
				return nDefColor;
			
			if (str.length() >= 2 && 
					str.substring(0, 2).equalsIgnoreCase("0x")) {
				str = str.substring(2);
				if (Utils.isEmptyStr(str))
					return nDefColor;
			}
			
			return (int)Long.parseLong(str, 16);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nDefColor;
	}

	public static int HexStrToARGB(String str) {
		return HexStrToARGB(str);
	}
	
	public static String getBetweenString(String str, 
			int nPos, String strStart, String strEnd) {
		if (isEmptyStr(str) || isEmptyStr(strStart) || isEmptyStr(strEnd))
			return "";

		int nStart = str.indexOf(strStart, nPos);
		if (nStart != -1) {
			nStart += strStart.length();
			int nEnd = str.indexOf(strEnd, nStart);
			if (nEnd != -1) {
				return str.substring(nStart, nEnd);
			}
		}
		
		return "";
	}

	public static int getBetweenInt(String str, int nPos,
			String strStart, String strEnd, int nDefValue) {
		String strText = getBetweenString(str, nPos, strStart, strEnd);
		if (!isEmptyStr(strText))
			return (int)Long.parseLong(strText);
		else
			return nDefValue;
	}
	
	public static int getUByte(byte data) {
		return data & 0x0FF;
	}

	public static int getUShort(short data){
		return data & 0x0FFFF;
	}
		
	public static long getUInt(int data) {
		return data & 0xFFFFFFFFL;
	}
	
	// 判断两个时间是否同一年
	public static boolean isSameYear(Calendar c1, Calendar c2) {
    	return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR);
	}

	// 判断两个时间是否同一天
	public static boolean isSameDay(Calendar c1, Calendar c2) {
    	return ((c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH))
    			&& (c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH))
    			&& (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)));
	}
	
	// 判断时间是否是今年
    public static boolean isThisYear(int nTime) {
    	Calendar c1 = Calendar.getInstance();
    	c1.setTimeInMillis(getUInt(nTime)*1000);
    	
    	Calendar c2 = Calendar.getInstance();
    	
    	return isSameYear(c1, c2);
    }
    
	// 判断时间是否是今天
    public static boolean isToday(int nTime) {
    	Calendar c1 = Calendar.getInstance();
    	c1.setTimeInMillis(getUInt(nTime)*1000);
    	
    	Calendar c2 = Calendar.getInstance();
    	
    	return isSameDay(c1, c2);
    }
    
    // 判断时间是否是昨天
    public static boolean isYesterday(int nTime) {
    	Calendar c1 = Calendar.getInstance();
    	c1.setTimeInMillis(getUInt(nTime)*1000);
    	
    	Calendar c2 = Calendar.getInstance();
    	c2.add(Calendar.DATE, -1);
    	
    	return isSameDay(c1, c2);
    }

    // 格式化时间
    public static String formatTime(int nTime) {
    	if (0 == nTime)
    		return "";
    	
    	if (isToday(nTime)) {	// 今天
    		Calendar time = Calendar.getInstance();
        	time.setTimeInMillis(getUInt(nTime)*1000);
        	SimpleDateFormat dateFmt = new SimpleDateFormat("HH:mm", Locale.getDefault());
        	return dateFmt.format(time.getTime());
    	} else if (isYesterday(nTime)) {	// 昨天
    		return "昨天";
    	} else {
    		Calendar time = Calendar.getInstance();
        	time.setTimeInMillis(getUInt(nTime)*1000);
    		SimpleDateFormat dateFmt = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        	return dateFmt.format(time.getTime());
    	}
    }
    

	
    public static boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = { 0, 0 };
            v.getLocationInWindow(leftTop);
            int left = leftTop[0], top = leftTop[1], 
            		bottom = top + v.getHeight(), right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }
    
    public static Boolean hideInputMethod(Context context, View v) {
    	InputMethodManager imm = (InputMethodManager)
    			context.getSystemService(Context.INPUT_METHOD_SERVICE);
    	if (imm != null) {
    		return imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    	}
    	return false;
    }

	public static SpannableString getSysFace(Context context, 
			FaceInfo faceInfo, int cx, int cy) {
		if (null == context || null == faceInfo)
			return null;
	
		if (faceInfo.m_nId < 0 || 0 == faceInfo.m_nResId)
			return new SpannableString(faceInfo.m_strTip);
		
		String str = "/f[\"";
		str += faceInfo.m_nId;
		str += "\"]";

		Drawable drawable = context.getResources().getDrawable(faceInfo.m_nResId);
		drawable.setBounds(0, 0, cx, cy);
		
		ImageSpan imgSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
		SpannableString spanStr = new SpannableString(str);
		spanStr.setSpan(imgSpan, 0, str.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		
		return spanStr;
	}

	public static SpannableString getCustomFace(Context context, 
			int nResId, String strFileName) {
		if (null == context || 0 == nResId || isEmptyStr(strFileName))
			return null;
		
		String str = "/c[\"";
		str += strFileName;
		str += "\"]";
				
		ImageSpan imgSpan = new ImageSpan(context, 
				nResId, ImageSpan.ALIGN_BASELINE);
		SpannableString spanStr = new SpannableString(str);
		spanStr.setSpan(imgSpan, 0, str.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		
		return spanStr;
	}
	
	public static SpannableString getCustomFace(final Context context, 
			Bitmap bmp, String strFileName, ClickableSpan clickSpan) {
		if (null == context || null == bmp || isEmptyStr(strFileName))
			return null;
		
		String str = "/c[\"";
		str += strFileName;
		str += "\"]";
		
//		int cx = bmp.getWidth();
//		int cy = bmp.getHeight();
//		
//		Drawable drawable = new BitmapDrawable(bmp);
//		drawable.setBounds(0, 0, cx, cy);
		
//		ImageSpan imgSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
		ImageSpan imgSpan = new ImageSpan(context, bmp, ImageSpan.ALIGN_BASELINE);
		SpannableString spanStr = new SpannableString(str);
		spanStr.setSpan(imgSpan, 0, str.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		if (clickSpan != null) {
//	        ClickableSpan[] click_spans = spanStr.getSpans(0, str.length(), ClickableSpan.class);
//	        if (click_spans.length != 0) {
//	        	for(ClickableSpan c_span : click_spans) {
//	            	spanStr.removeSpan(c_span);
//	            }
//	        }
			spanStr.setSpan(clickSpan, 0, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);			
		}
        
		return spanStr;
	}
	
	public static boolean ptInView(View view, int x, int y) {
		int[] location = new int[2];
		view.getLocationInWindow(location);
		
		if (x > location[0] && x < location[0] + view.getWidth() &&
				y > location[1] && y < location[1] + view.getHeight())
			return true;
		else
			return false;
	}
	
	public static Bitmap getRoundedCornerBitmap(Bitmap bmp, float roundPx){
		if (null == bmp || roundPx <= 0)
			return bmp;
		
		Bitmap output = Bitmap.createBitmap(bmp.getWidth(), 
				bmp.getHeight(), Config.ARGB_8888); 
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242; 
		final Paint paint = new Paint(); 
		final Rect rect = new Rect(0, 0, bmp.getWidth(), bmp.getHeight()); 
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true); 
		canvas.drawARGB(0, 0, 0, 0); 
		paint.setColor(color); 
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN)); 
		canvas.drawBitmap(bmp, rect, rect, paint);

		return output; 
	}

	public static Bitmap zoomImg(Bitmap bmp, int cx ,int cy) {
		if (null == bmp || cx <= 0 || cy <= 0)
			return null;
		
		int nWidth = bmp.getWidth();
		int nHeight = bmp.getHeight();
		if (cx == nWidth && cy == nHeight)
			return bmp;
		
		float scaleWidth = ((float)cx) / nWidth;
		float scaleHeight = ((float)cy) / nHeight;

		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);

		Bitmap newBmp = Bitmap.createBitmap(bmp, 0, 0, nWidth, nHeight, matrix, true);
		return newBmp;
	}
	
	public static Drawable bitmap2Drawable(Bitmap bitmap) {
		return new BitmapDrawable(bitmap);
    }
      
    public static Bitmap drawable2Bitmap(Drawable drawable) {
    	if (drawable instanceof BitmapDrawable){
    		return ((BitmapDrawable)drawable).getBitmap();
    	} else if(drawable instanceof NinePatchDrawable) {
    		Bitmap bitmap = Bitmap.createBitmap(
    				drawable.getIntrinsicWidth(), 
    				drawable.getIntrinsicHeight(),
    				drawable.getOpacity() != PixelFormat.OPAQUE ? 
    						Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);  
    		Canvas canvas = new Canvas(bitmap);
    		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), 
    				drawable.getIntrinsicHeight());  
    		drawable.draw(canvas);
            return bitmap;
        } else {
            return null;  
        }
    }
}
