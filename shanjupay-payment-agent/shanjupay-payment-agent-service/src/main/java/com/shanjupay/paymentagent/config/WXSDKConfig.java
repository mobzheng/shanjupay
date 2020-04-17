package com.shanjupay.paymentagent.config;

import com.github.wxpay.sdk.IWXPayDomain;
import com.github.wxpay.sdk.WXPayConfig;
import com.github.wxpay.sdk.WXPayConstants;
import com.shanjupay.paymentagent.api.conf.WXConfigParam;
import org.springframework.util.Assert;

import java.io.InputStream;

/**
 * 微信支付参数
 */
public class WXSDKConfig extends WXPayConfig {

	private WXConfigParam param;

	public WXSDKConfig(WXConfigParam param) {
		Assert.notNull(param, "微信支付参数不能为空");
		this.param = param;
	}

	public WXConfigParam getParam() {
		return param;
	}

	public String getAppID() {
		return param.getAppId();
	}

	@Override
	protected String getMchID() {
		return param.getMchId();
	}

	@Override
	protected String getKey() {
		return param.getKey();
	}


	public String getAppSecret() {
		return param.getAppSecret();
	}

	public InputStream getCertStream() {
		return null;
	}

	public int getHttpConnectTimeoutMs() {
		return 8000;
	}

	public int getHttpReadTimeoutMs() {
		return 10000;
	}

	@Override
	protected IWXPayDomain getWXPayDomain() {
		return new IWXPayDomain() {
			@Override
			public void report(String s, long l, Exception e) {

			}

			@Override
			public DomainInfo getDomain(WXPayConfig wxPayConfig) {//api.mch.weixin.qq.com
				return new DomainInfo(WXPayConstants.DOMAIN_API, true);
			}
		};
	}
}
