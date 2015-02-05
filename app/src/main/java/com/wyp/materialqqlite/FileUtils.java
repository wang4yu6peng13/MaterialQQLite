package com.wyp.materialqqlite;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.os.Environment;

public class FileUtils {
	public static boolean hasSDCard() {
		return Environment.getExternalStorageState()
				.equals(Environment.MEDIA_MOUNTED);
	}
	
	// "/storage/sdcard/"
	public static String getSDCardDir() {
		return Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
	}

	// "/data/data/包名/files/"
	public static String getAppFilesDir(Context context) {
		return context.getFilesDir().getAbsolutePath() + "/";
	}
	
	// "/data/data/包名/cache/"
	public static String getAppCacheDir(Context context) {
		return context.getCacheDir().getAbsoluteFile() + "/";
	}
	
	public static void deleteFile(String strFileName) {
		File file = new File(strFileName);
		deleteFile(file);
	}
	
	public static void deleteFile(File file) {
		if (!file.exists()) {
            return;
        } else {
            if (file.isFile()) {
                file.delete();
                return;
            }
            if (file.isDirectory()) {  
                File[] childFile = file.listFiles();  
                if (childFile == null || childFile.length == 0) {
                    file.delete();
                    return;
                }
                for (File f : childFile) {
                    deleteFile(f);
                }
                file.delete();
            }
        }
    }
	
	public static String readFromAssets(Context context, String fileName) {
		try {
			InputStream is = context.getResources().getAssets().open(fileName);
			InputStreamReader reader = new InputStreamReader(is); 
			BufferedReader bufReader = new BufferedReader(reader);
			String strLine = "";
			String Result = "";
            while((strLine = bufReader.readLine()) != null)
                Result += strLine;
            return Result;
        } catch (Exception e) { 
            e.printStackTrace();
        }
		return "";
	}	
}
