package com.wyp.materialqqlite.qqclient.protocol.protocoldata;

public class QQLoginResultCode {
	public final static int SUCCESS = 0;				// 登录成功
	public final static int FAILED = 1;				// 登录失败
	public final static int PASSWORD_ERROR = 2;		// 密码错误
	public final static int NEED_VERIFY_CODE = 3;		// 需要输入验证码
	public final static int VERIFY_CODE_ERROR = 4;	// 验证码错误
	public final static int USER_CANCEL_LOGIN = 5;	// 用户取消登录
}
