package com.shanjupay.paymentagent.service;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeWapPayResponse;
import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConstants;
import com.github.wxpay.sdk.WXPayUtil;
import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import com.shanjupay.paymentagent.api.PayChannelAgentService;
import com.shanjupay.paymentagent.api.conf.AliConfigParam;
import com.shanjupay.paymentagent.api.conf.WXConfigParam;
import com.shanjupay.paymentagent.api.dto.AlipayBean;
import com.shanjupay.paymentagent.api.dto.PaymentResponseDTO;
import com.shanjupay.paymentagent.api.dto.TradeStatus;
import com.shanjupay.paymentagent.api.dto.WeChatBean;
import com.shanjupay.paymentagent.common.constant.AliCodeConstants;
import com.shanjupay.paymentagent.config.WXSDKConfig;
import com.shanjupay.paymentagent.message.PayProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Administrator
 * @version 1.0
 **/
@Service
@Slf4j
public class PayChannelAgentServiceImpl implements PayChannelAgentService {

    @Autowired
    PayProducer payProducer;
    /**
     * 调用支付宝的下单接口
     *
     * @param aliConfigParam 支付渠道配置的参数（配置的支付宝的必要参数）
     * @param alipayBean     业务参数（商户订单号，订单标题，订单描述,,）
     * @return 统一返回PaymentResponseDTO
     */
    @Override
    public PaymentResponseDTO createPayOrderByAliWAP(AliConfigParam aliConfigParam, AlipayBean alipayBean) throws BusinessException {

        String url = aliConfigParam.getUrl();//支付宝接口网关地址
        String appId = aliConfigParam.getAppId();//支付宝应用id
        String rsaPrivateKey = aliConfigParam.getRsaPrivateKey();//应用私钥
        String format = aliConfigParam.getFormat();//json格式
        String charest = aliConfigParam.getCharest();//编码
        String alipayPublicKey = aliConfigParam.getAlipayPublicKey();//支付宝公钥
        String signtype = aliConfigParam.getSigntype();//签名算法
        String returnUrl = aliConfigParam.getReturnUrl();//支付成功跳转的url
        String notifyUrl = aliConfigParam.getNotifyUrl();//支付结果异步通知的url

        //构造sdk的客户端对象
        AlipayClient alipayClient = new DefaultAlipayClient(url, appId, rsaPrivateKey, format, charest, alipayPublicKey, signtype); //获得初始化的AlipayClient
        AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();//创建API对应的request
        AlipayTradeWapPayModel model  = new AlipayTradeWapPayModel();
        model.setOutTradeNo(alipayBean.getOutTradeNo());//商户的订单，就是闪聚平台的订单
        model.setTotalAmount(alipayBean.getTotalAmount());//订单金额（元）
        model.setSubject(alipayBean.getSubject());
        model.setBody(alipayBean.getBody());
        model.setProductCode("QUICK_WAP_PAY");//产品代码，固定QUICK_WAP_PAY
        model.setTimeoutExpress(alipayBean.getExpireTime());//订单过期时间
        alipayRequest.setBizModel(model);

        alipayRequest.setReturnUrl(returnUrl);
        alipayRequest.setNotifyUrl(notifyUrl);
        try {
            //请求支付宝下单接口,发起http请求
            AlipayTradeWapPayResponse response = alipayClient.pageExecute(alipayRequest);
            PaymentResponseDTO paymentResponseDTO = new PaymentResponseDTO();
            log.info("调用支付宝下单接口，响应内容:{}",response.getBody());
            paymentResponseDTO.setContent(response.getBody());//支付宝的响应结果

            //向MQ发一条延迟消息,支付结果查询
            PaymentResponseDTO<AliConfigParam> notice = new PaymentResponseDTO<AliConfigParam>();
            notice.setOutTradeNo(alipayBean.getOutTradeNo());//闪聚平台的订单
            notice.setContent(aliConfigParam);
            notice.setMsg("ALIPAY_WAP");//标识是查询支付宝的接口
            //发送消息
            payProducer.payOrderNotice(notice);


            return paymentResponseDTO;
        } catch (AlipayApiException e) {
            e.printStackTrace();
            throw new BusinessException(CommonErrorCode.E_400002);
        }

    }

    /**
     * 查询支付宝订单状态
     *
     * @param aliConfigParam 支付渠道参数
     * @param outTradeNo     闪聚平台的订单号
     * @return
     */
    @Override
    public PaymentResponseDTO queryPayOrderByAli(AliConfigParam aliConfigParam, String outTradeNo) throws BusinessException{
        String url = aliConfigParam.getUrl();//支付宝接口网关地址
        String appId = aliConfigParam.getAppId();//支付宝应用id
        String rsaPrivateKey = aliConfigParam.getRsaPrivateKey();//应用私钥
        String format = aliConfigParam.getFormat();//json格式
        String charest = aliConfigParam.getCharest();//编码
        String alipayPublicKey = aliConfigParam.getAlipayPublicKey();//支付宝公钥
        String signtype = aliConfigParam.getSigntype();//签名算法
        String returnUrl = aliConfigParam.getReturnUrl();//支付成功跳转的url
        String notifyUrl = aliConfigParam.getNotifyUrl();//支付结果异步通知的url

        //构造sdk的客户端对象
        AlipayClient alipayClient = new DefaultAlipayClient(url, appId, rsaPrivateKey, format, charest, alipayPublicKey, signtype); //获得初始化的AlipayClient
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        AlipayTradeWapPayModel model  = new AlipayTradeWapPayModel();
        model.setOutTradeNo(outTradeNo);//商户的订单，就是闪聚平台的订单
        request.setBizModel(model);

        AlipayTradeQueryResponse response = null;
        try {
            //请求支付宝订单状态查询接口
            response = alipayClient.execute(request);
            //支付宝响应的code，10000表示接口调用成功
            String code = response.getCode();
            if(AliCodeConstants.SUCCESSCODE.equals(code)){
                String tradeStatusString = response.getTradeStatus();
                //解析支付宝返回的状态，解析成闪聚平台的TradeStatus
                TradeStatus tradeStatus = covertAliTradeStatusToShanjuCode(tradeStatusString);
                //String tradeNo(支付宝订单号), String outTradeNo（闪聚平台的订单号）, TradeStatus tradeState（订单状态）, String msg（返回信息）
                return PaymentResponseDTO.success(response.getTradeNo(),response.getOutTradeNo(),tradeStatus,response.getMsg());
            }


        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
       //String msg, String outTradeNo, TradeStatus tradeState
        return PaymentResponseDTO.fail("支付宝订单状态查询失败",outTradeNo,TradeStatus.UNKNOWN);
    }

    /**
     * 查询微信订单状态
     *
     * @param wxConfigParam 支付渠道参数
     * @param outTradeNo    闪聚平台的订单号
     * @return
     * @throws BusinessException
     */
    @Override
    public PaymentResponseDTO queryPayOrderByWeChat(WXConfigParam wxConfigParam, String outTradeNo) throws BusinessException {
        WXSDKConfig config = new WXSDKConfig(wxConfigParam);

        Map<String, String> result = null;

        try {
            //创建sdk客户端
            WXPay wxPay = new WXPay(config);
            Map<String,String> map = new HashMap<>();
            map.put("out_trade_no",outTradeNo);//闪聚平台的订单号
            //调用微信的订单查询接口
            result = wxPay.orderQuery(map);
        } catch (Exception e) {
            e.printStackTrace();
            return PaymentResponseDTO.fail("调用微信订单查询接口失败",outTradeNo,TradeStatus.UNKNOWN);
        }

            String return_code = result.get("return_code");
            String return_msg = result.get("return_msg");
            String result_code = result.get("result_code");
            String trade_state = result.get("trade_state");//订单状态
            String transaction_id = result.get("transaction_id");//微信订单号

            if("SUCCESS".equals(return_code) && "SUCCESS".equals(result_code)){

                if("SUCCESS".equals(trade_state)){  //支付成功
                    return PaymentResponseDTO.success(transaction_id,outTradeNo,TradeStatus.SUCCESS,return_msg);
                }else if("CLOSED".equals(trade_state)){//交易关闭
                    return PaymentResponseDTO.success(transaction_id,outTradeNo,TradeStatus.REVOKED,return_msg);
                }else if("USERPAYING".equals(trade_state)){//支付中
                    return PaymentResponseDTO.success(transaction_id,outTradeNo,TradeStatus.USERPAYING,return_msg);
                }else if("PAYERROR".equals(trade_state)){//支付失败
                    return PaymentResponseDTO.success(transaction_id,outTradeNo,TradeStatus.FAILED,return_msg);

                }

            }

        return PaymentResponseDTO.success("不可识别的微信订单状态",transaction_id,outTradeNo,TradeStatus.UNKNOWN);
    }

    /**
     * 微信下单接口
     *
     * @param wxConfigParam 微信支付渠道参数
     * @param weChatBean    订单业务数据
     * @return h5网页的数据
     */
    @Override
    public Map<String, String> createPayOrderByWeChatJSAPI(WXConfigParam wxConfigParam, WeChatBean weChatBean) {
        WXSDKConfig config = new WXSDKConfig(wxConfigParam);
        Map<String,String> jsapiPayParam = null;
        try {
            //创建sdk客户端
            WXPay wxPay = new WXPay(config);
            //构造请求的参数
            Map<String,String> requestParam = new HashMap<>();
            requestParam.put("out_trade_no",weChatBean.getOutTradeNo());//订单号
            requestParam.put("body", weChatBean.getBody());//订单描述
            requestParam.put("fee_type", "CNY");//人民币
            requestParam.put("total_fee", String.valueOf(weChatBean.getTotalFee())); //金额
            requestParam.put("spbill_create_ip", weChatBean.getSpbillCreateIp());//客户端ip
            requestParam.put("notify_url", weChatBean.getNotifyUrl());//微信异步通知支付结果接口，暂时不用
            requestParam.put("trade_type", "JSAPI");
            //从请求中获取openid
            String openid = weChatBean.getOpenId();
            requestParam.put("openid",openid);
            //调用统一下单接口
            Map<String, String> resp = wxPay.unifiedOrder(requestParam);
            //=====向mq写入订单查询的消息=====
            PaymentResponseDTO paymentResponseDTO = new PaymentResponseDTO();
            //订单号
            paymentResponseDTO.setOutTradeNo(weChatBean.getOutTradeNo());
            //支付渠道参数
            paymentResponseDTO.setContent(wxConfigParam);
            //msg
            paymentResponseDTO.setMsg("WX_JSAPI");
            payProducer.payOrderNotice(paymentResponseDTO);

            //准备h5网页需要的数据
            jsapiPayParam = new HashMap<>();
            jsapiPayParam.put("appId",wxConfigParam.getAppId());
            jsapiPayParam.put("timeStamp",System.currentTimeMillis()/1000+"");
            jsapiPayParam.put("nonceStr", UUID.randomUUID().toString());//随机字符串
            jsapiPayParam.put("package","prepay_id="+resp.get("prepay_id"));
            jsapiPayParam.put("signType","HMAC-SHA256");
            //将h5网页响应给前端
            jsapiPayParam.put("paySign", WXPayUtil.generateSignature(jsapiPayParam,wxConfigParam.getKey(), WXPayConstants.SignType.HMACSHA256));
            return jsapiPayParam;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(CommonErrorCode.E_400001);
        }
    }

    //解析支付宝的订单状态为闪聚平台的状态
    private TradeStatus covertAliTradeStatusToShanjuCode(String aliTradeStatus){
            switch (aliTradeStatus){
                case AliCodeConstants.TRADE_FINISHED:
                case AliCodeConstants.TRADE_SUCCESS:
                    return TradeStatus.SUCCESS;//业务交易支付 明确成功
                case AliCodeConstants.TRADE_CLOSED:
                    return TradeStatus.REVOKED;//交易已撤销
                case    AliCodeConstants.WAIT_BUYER_PAY:
                    return TradeStatus.USERPAYING;//交易新建，等待支付
                default:
                    return TradeStatus.FAILED;//交易失败
            }
    }
}
