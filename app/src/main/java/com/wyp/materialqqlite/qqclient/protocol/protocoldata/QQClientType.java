package com.wyp.materialqqlite.qqclient.protocol.protocoldata;

public enum QQClientType {
	QQ_CLIENT_TYPE_PC(1),
	QQ_CLIENT_TYPE_MOBILE(21),
	QQ_CLIENT_TYPE_IPHONE(24),
	QQ_CLIENT_TYPE_WEBQQ(41),
	QQ_CLIENT_TYPE_PAD(42);
	
	private final int m_nValue;

	public int getValue() {
		return m_nValue;
	}

	// 构造器默认也只能是private, 从而保证构造函数只能在内部使用
	QQClientType(int nValue) {
		this.m_nValue = nValue;
	}
}
