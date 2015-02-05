package com.wyp.materialqqlite.qqclient.protocol.protocoldata;

public class FontInfo {
	public int m_nSize;				// 字体大小
	public int m_clrText;			// 字体颜色
	public String m_strName;		// 字体名称
	public boolean m_bBold;		// 是否加粗
	public boolean m_bItalic;		// 是否倾斜
	public boolean m_bUnderLine;	// 是否带下划线
	
	FontInfo() {
		m_nSize = 9;
		m_clrText = 0;
		m_strName = "宋体";
		m_bBold = m_bItalic = m_bUnderLine = false;
	}
}
