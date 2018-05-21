package com.springrain.erp.modules.sys.security;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;

public class HashedCredentialsMatcher extends org.apache.shiro.authc.credential.HashedCredentialsMatcher{

	@Override
	public boolean doCredentialsMatch(AuthenticationToken token,
			AuthenticationInfo info) {
		UsernamePasswordToken authcToken = (UsernamePasswordToken) token;
		if ("1".equals(authcToken.getLoginType())) {
			return true;
		}
		return super.doCredentialsMatch(token, info);
	}
}
