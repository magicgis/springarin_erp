package com.springrain.erp.modules.sys.security;

import org.apache.shiro.authc.AuthenticationException;

/**
 * 动态密码异常处理类
 */
public class PasswordException extends AuthenticationException {

	private static final long serialVersionUID = 1L;

	public PasswordException() {
		super();
	}

	public PasswordException(String message, Throwable cause) {
		super(message, cause);
	}

	public PasswordException(String message) {
		super(message);
	}

	public PasswordException(Throwable cause) {
		super(cause);
	}

}
