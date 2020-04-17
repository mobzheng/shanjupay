package com.shanjupay.paymentagent.api;

import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.paymentagent.api.conf.AliConfigParam;
import com.shanjupay.paymentagent.api.conf.WXConfigParam;
import com.shanjupay.paymentagent.api.dto.AlipayBean;
import com.shanjupay.paymentagent.api.dto.PaymentResponseDTO;
import com.shanjupay.paymentagent.api.dto.WeChatBean;

import java.util.Map;

/**
 * 与第三支付渠道进行交互
 * Created by Administrator.
 */
public interface PayChannelAgentService {


    /**
     * 调用支付宝的下单接口
     * @param aliConfigParam 支付渠道配置的参数（配置的支付宝的必要参数）
     * @param alipayBean 业务参数（商户订单号，订单标题，订单描述,,）
     * @return 统一返回PaymentResponseDTO
     */
    public PaymentResponseDTO createPayOrderByAliWAP(AliConfigParam aliConfigParam, AlipayBean alipayBean) throws BusinessException;

    /**
     * 查询支付宝订单状态
     * @param aliConfigParam 支付渠道参数
     * @param outTradeNo 闪聚平台的订单号
     * @return
     */
    public PaymentResponseDTO queryPayOrderByAli(AliConfigParam aliConfigParam,String outTradeNo) throws BusinessException;

    /**
     * 查询微信订单状态
     * @param wxConfigParam 支付渠道参数
     * @param outTradeNo 闪聚平台的订单号
     * @return
     * @throws BusinessException
     */
    public PaymentResponseDTO queryPayOrderByWeChat(WXConfigParam wxConfigParam,String outTradeNo)throws BusinessException;
    /**
     * 微信下单接口
     * @param wxConfigParam 微信支付渠道参数
     * @param weChatBean 订单业务数据
     * @return h5网页的数据
     */
    public Map<String, String> createPayOrderByWeChatJSAPI(WXConfigParam wxConfigParam, WeChatBean weChatBean);
}
