package com.shanjupay.transaction.api;

import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.paymentagent.api.dto.PaymentResponseDTO;
import com.shanjupay.transaction.api.dto.PayOrderDTO;
import com.shanjupay.transaction.api.dto.QRCodeDto;

import java.util.Map;

/**
 * 交易相关的服务接口
 * Created by Administrator.
 */
public interface TransactionService {

    /**
     * 生成门店二维码的url
     * @param qrCodeDto,传入merchantId,appId、storeid、channel、subject、body
     * @return 支付入口（url），要携带参数（将传入的参数转成json，用base64编码）
     * @throws BusinessException
     */
    String createStoreQRCode(QRCodeDto qrCodeDto)throws BusinessException;

    /**
     * 保存支付宝订单，1、保存订单到闪聚平台，2、调用支付渠道代理服务调用支付宝的接口
     * @param payOrderDTO
     * @return
     * @throws BusinessException
     */
    public PaymentResponseDTO submitOrderByAli(PayOrderDTO payOrderDTO) throws BusinessException;

    /**
     * 根据订单号查询订单号
     * @param tradeNo
     * @return
     */
    public PayOrderDTO queryPayOrder(String tradeNo);

    /**
     * 更新订单支付状态
     *
     * @param tradeNo           闪聚平台订单号
     * @param payChannelTradeNo 支付宝或微信的交易流水号(第三方支付系统的订单)
     * @param state             订单状态  交易状态支付状态,0-订单生成,1-支付中(目前未使用),2-支付成功,4-关闭 5--失败
     */
    public void updateOrderTradeNoAndTradeState(String tradeNo, String payChannelTradeNo, String state) throws BusinessException;

    /**
     * 申请微信授权码
     * @param payOrderDTO
     * @return 申请授权码的地址
     */
    public String getWXOAuth2Code(PayOrderDTO payOrderDTO);

    /**
     * 申请openid
     * @param code 授权码
     * @param appId 闪聚平台的应用id，为了获取该应用的微信支付渠道参数
     * @return
     */
    public String getWXOAuthOpenId(String code,String appId);

    /**
     * 1、保存订单到闪聚平台，2、调用支付渠道代理服务调用微信的接口
     * @param payOrderDTO
     * @return h5页面所需要的数据
     */
    Map<String, String> submitOrderByWechat(PayOrderDTO payOrderDTO) throws BusinessException;
}
