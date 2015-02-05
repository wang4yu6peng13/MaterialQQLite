package com.wyp.materialqqlite.qqclient.protocol.protocoldata;

public class Content {
	public ContentType m_nType;			// 内容类型
	public FontInfo m_FontInfo;			// 字体信息
	public String m_strText;			// 文本信息
	public int m_nFaceId;				// 系统表情Id
	public CustomFaceInfo m_CFaceInfo;	// 自定义表情信息
	
	public Content() {
		m_nType = ContentType.CONTENT_TYPE_UNKNOWN;
		m_FontInfo = new FontInfo();
		m_CFaceInfo = new CustomFaceInfo();
	}
}
