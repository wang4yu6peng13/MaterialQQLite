package com.wyp.materialqqlite;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import android.graphics.Bitmap;

public class ImageCache {
	private ReferenceQueue<Bitmap> m_queRef;
	private Map<String, SoftRef> m_mapImage;
	private Queue<String> m_queImage;
	private int m_nMaxSize = 80;
	private int m_nCleanSize = 20;
    
	private class SoftRef extends SoftReference<Bitmap> {
        private String m_strKey = "";

        public SoftRef(String strKey, Bitmap bmp, ReferenceQueue<Bitmap> queRef) {
            super(bmp, queRef);
            m_strKey = strKey;
        }
    }
	
	public ImageCache() {
		m_queRef = new ReferenceQueue<Bitmap>();
		m_mapImage = new HashMap<String, SoftRef>();
		m_queImage = new LinkedList<String>();
	}
	
	public int getMaxSize() {
		return m_nMaxSize;
	}
	
	public void setMaxSize(int nSize) {
		m_nMaxSize = nSize;
	}
	
	public int getCleanSize() {
		return m_nCleanSize;
	}
	
	public void setCleanSize(int nSize) {
		m_nCleanSize = nSize;
	}
	
	public Bitmap get(int nKey) {
		return get(String.valueOf(nKey));
	}
	
	public Bitmap get(String strKey) {
		Bitmap bmp = null;
		if (m_mapImage.containsKey(strKey)) {
			SoftRef ref = (SoftRef)m_mapImage.get(strKey);
			bmp = (Bitmap)ref.get();
		}
		return bmp;
	}
	
	public void put(int nKey, Bitmap bmp) {
		put(String.valueOf(nKey), bmp);
	}
	
	public void put(String strKey, Bitmap bmp) {
		if (null == strKey || strKey.length() <= 0 || null == bmp)
			return;
		
		cleanCache();
		if (m_mapImage.size() >= m_nMaxSize) {
			forceClean(m_nCleanSize);
		}
        SoftRef ref = new SoftRef(strKey, bmp, m_queRef);
        m_mapImage.put(strKey, ref);
        m_queImage.offer(strKey);
	}
	
    public void clear() {
    	cleanCache();
        m_mapImage.clear();
        System.gc();
        System.runFinalization();
    }
    
	private void cleanCache() {
        SoftRef ref = null;
        while ((ref = (SoftRef)m_queRef.poll()) != null) {
            m_mapImage.remove(ref.m_strKey);
        }
    }
	
	private void forceClean(int nSize) {
		if (nSize >= m_queImage.size()) {
			m_queImage.clear();
			m_mapImage.clear();
			return;
		}
		
		for (int i = 0; i < nSize; i++) {
			String strKey = m_queImage.poll();
			m_mapImage.remove(strKey);
		}
	}
}
