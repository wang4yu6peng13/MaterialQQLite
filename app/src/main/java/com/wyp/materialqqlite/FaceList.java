package com.wyp.materialqqlite;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

public class FaceList {
	private List<FaceInfo> m_arrFaceInfo;
	private List<List<FaceInfo>> m_arrFacePage;
	private int m_nFaceCntOfOnePage;		// 一页显示的表情数
	private int m_nPageCnt;				// 表情页数
	private int m_nDelBtnPicResId;			// 删除按钮图片资源ID
	
	public FaceList() {
		m_arrFaceInfo = new ArrayList<FaceInfo>();
		m_arrFacePage = new ArrayList<List<FaceInfo>>();
		m_nFaceCntOfOnePage = 21;
		m_nDelBtnPicResId = 0;
	}
	
	public void reset() {
		m_arrFaceInfo.clear();
		for (List<FaceInfo> facePage : m_arrFacePage) {
			facePage.clear();
		}
		m_arrFacePage.clear();
	}
	
	public void setDelBtnPicResId(int nResId) {
		m_nDelBtnPicResId = nResId;
	}
	
	public boolean loadConfigFile(Context context, String strFileName) {
		try {
			reset();
			
			InputStream in = context.getResources().getAssets().open(strFileName);
			BufferedReader buf = new BufferedReader(
					new InputStreamReader(in, "UTF-8"));
			String str = null;
			while ((str = buf.readLine()) != null) {
				String[] text = str.split(",");
				
				if (text.length != 3)
					continue;
				
				FaceInfo faceInfo = new FaceInfo();
				faceInfo.m_nId = Integer.parseInt(text[0]);		// 表情ID
				faceInfo.m_strFileName = text[1];				// 表情文件名
				faceInfo.m_strTip = text[2];					// 表情提示文字
				String fileName = text[1].substring(0, text[1].lastIndexOf("."));
				faceInfo.m_nResId = context.getResources().getIdentifier(
						fileName, "drawable", context.getPackageName());

				m_arrFaceInfo.add(faceInfo);
			}

			int nFaceCntOfOnePage = m_nFaceCntOfOnePage - 1;
			if (m_arrFaceInfo.size() % nFaceCntOfOnePage == 0)
				m_nPageCnt = m_arrFaceInfo.size() / nFaceCntOfOnePage;
			else
				m_nPageCnt = m_arrFaceInfo.size() / nFaceCntOfOnePage + 1;
			
			FaceInfo faceInfo = new FaceInfo();		// 删除按钮
			faceInfo.m_nId = -1;					// 表情ID
			faceInfo.m_strFileName = "";			// 表情文件名
			faceInfo.m_strTip = "";					// 表情提示文字
			faceInfo.m_nResId = m_nDelBtnPicResId;	// 表情资源ID
			
			for (int i = 0; i < m_nPageCnt; i++) {
				List<FaceInfo> facePage = new ArrayList<FaceInfo>(); 
				for (int j = 0; j < nFaceCntOfOnePage; j++) {
					int nPos = i * nFaceCntOfOnePage + j;
					if (nPos >= m_arrFaceInfo.size())
						break;
					
					facePage.add(m_arrFaceInfo.get(nPos));
				}
				facePage.add(faceInfo);
				m_arrFacePage.add(facePage);
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public int getCount() {
		return m_arrFaceInfo.size();
	}
	
	public FaceInfo getFaceInfo(int nIndex) {
		if (nIndex >= 0 && nIndex < m_arrFaceInfo.size())
			return m_arrFaceInfo.get(nIndex);
		else
			return null;
	}
	
	public FaceInfo getFaceInfoById(int nFaceId) {
		for (int i = 0; i < m_arrFaceInfo.size(); i++) {
			FaceInfo faceInfo = m_arrFaceInfo.get(i);
			if (faceInfo != null && faceInfo.m_nId == nFaceId)
				return faceInfo;
		}
		return null;
	}

	public int getFaceCountOfOnePage() {
		return m_nFaceCntOfOnePage;
	}
	
	public void setFaceCountOfOnePage(int nFaceCnt) {
		m_nFaceCntOfOnePage = nFaceCnt;
	}
	
	public int getPageCount() {
		return m_nPageCnt;
	}
	
	public int getFaceCount(int nPage) {
		if (nPage >= 0 && nPage < m_arrFaceInfo.size()-1) {
			return m_nFaceCntOfOnePage;
		}
		else if (nPage == m_arrFaceInfo.size()-1) {
			int nRemainder = m_arrFaceInfo.size() % m_nFaceCntOfOnePage;
			if (0 == nRemainder)
				return m_nFaceCntOfOnePage;
			else
				return nRemainder;
		} else {
			return 0;
		}
	}
	
	public FaceInfo getFaceInfo(int nPage, int nIndex) {
		int nPos = nPage * m_nFaceCntOfOnePage + nIndex;
		if (nPos >= 0 && nPos < m_arrFaceInfo.size())
			return m_arrFaceInfo.get(nPos);
		else
			return null;
	}
	
	public List<FaceInfo> getFacePage(int nPage) {
		return m_arrFacePage.get(nPage);
	}
}
