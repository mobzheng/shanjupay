package com.shanjupay.transaction.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import com.shanjupay.common.util.AmountUtil;
import com.shanjupay.common.util.EncryptUtil;
import com.shanjupay.common.util.PaymentUtil;
import com.shanjupay.merchant.api.AppService;
import com.shanjupay.merchant.api.MerchantService;
import com.shanjupay.paymentagent.api.PayChannelAgentService;
import com.shanjupay.paymentagent.api.conf.AliConfigParam;
import com.shanjupay.paymentagent.api.conf.WXConfigParam;
import com.shanjupay.paymentagent.api.dto.AlipayBean;
import com.shanjupay.paymentagent.api.dto.PaymentResponseDTO;
import com.shanjupay.paymentagent.api.dto.WeChatBean;
import com.shanjupay.transaction.api.PayChannelService;
import com.shanjupay.transaction.api.TransactionService;
import com.shanjupay.transaction.api.dto.PayChannelParamDTO;
import com.shanjupay.transaction.api.dto.PayOrderDTO;
import com.shanjupay.transaction.api.dto.QRCodeDto;
import com.shanjupay.transaction.convert.PayOrderConvert;
import com.shanjupay.transaction.entity.PayOrder;
import com.shanjupay.transaction.mapper.PayOrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;

/**
 * @author Administrator
 * @version 1.0
 **/
@Service
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    //从配置文件读取支付入口地址
    @Value("${shanjupay.payurl}")
    String payurl;

    @Value("${weixin.oauth2RequestUrl}")
    String oauth2RequestUrl;
    @Value("${weixin.oauth2CodeReturnUrl}")
    String oauth2CodeReturnUrl;
    @Value("${weixin.oauth2Token}")
    String oauth2Token;


    @Reference
    AppService appService;

    @Reference
    MerchantService merchantService;

    @Autowired
    PayOrderMapper payOrderMapper;

    @Reference
    PayChannelAgentService payChannelAgentService;

    @Autowired
    PayChannelService payChannelService;


    /**
     * 生成门店二维码的url
     *
     * @param qrCodeDto@return 支付入口（url），要携带参数（将传入的参数转成json，用base64编码）
     * @throws BusinessException
     */
    @Override
    public String createStoreQRCode(QRCodeDto qrCodeDto) throws BusinessException {

        //校验商户id和应用id和门店id的合法性
        verifyAppAndStore(qrCodeDto.getMerchantId(),qrCodeDto.getAppId(),qrCodeDto.getStoreId());

        //组装url所需要的数据
        PayOrderDTO payOrderDTO = new PayOrderDTO();
        payOrderDTO.setMerchantId(qrCodeDto.getMerchantId());
        payOrderDTO.setAppId(qrCodeDto.getAppId());
        payOrderDTO.setStoreId(qrCodeDto.getStoreId());
        payOrderDTO.setSubject(qrCodeDto.getSubject());//显示订单标题
        payOrderDTO.setChannel("shanju_c2b");//服务类型，要写为c扫b的服务类型
        payOrderDTO.setBody(qrCodeDto.getBody());//订单内容
        //转成json
        String jsonString = JSON.toJSONString(payOrderDTO);
        //base64编码
        String ticket = EncryptUtil.encodeUTF8StringBase64(jsonString);

        //目标是生成一个支付入口 的url，需要携带参数将传入的参数转成json，用base64编码
        String url=payurl+ticket;
        return url;
    }

    /**
     * 保存支付宝订单，1、保存订单到闪聚平台，2、调用支付渠道代理服务调用支付宝的接口
     *
     * @param payOrderDTO
     * @return
     * @throws BusinessException
     */
    @Override
    public PaymentResponseDTO submitOrderByAli(PayOrderDTO payOrderDTO) throws BusinessException {

        payOrderDTO.setChannel("ALIPAY_WAP");//支付渠道
        //保存订单到闪聚平台数据库
        PayOrderDTO save = save(payOrderDTO);

        //调用支付渠道代理服务支付宝下单接口
        PaymentResponseDTO paymentResponseDTO = alipayH5(save.getTradeNo());
        return paymentResponseDTO;
    }

    //调用支付渠道代理服务的支付宝下单接口
    private  PaymentResponseDTO alipayH5(String tradeNo){
        //订单信息，从数据库查询订单
        PayOrderDTO payOrderDTO = queryPayOrder(tradeNo);
        //组装alipayBean
        AlipayBean alipayBean = new AlipayBean();
        alipayBean.setOutTradeNo(payOrderDTO.getTradeNo());//订单号
        try {
            alipayBean.setTotalAmount(AmountUtil.changeF2Y(payOrderDTO.getTotalAmount().toString()));
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(CommonErrorCode.E_300006);
        }
        alipayBean.setSubject(payOrderDTO.getSubject());
        alipayBean.setBody(payOrderDTO.getBody());
        alipayBean.setExpireTime("30m");

        //支付渠道配置参数，从数据库查询
        //String appId,String platformChannel,String payChannel
        PayChannelParamDTO payChannelParamDTO = payChannelService.queryParamByAppPlatformAndPayChannel(payOrderDTO.getAppId(), "shanju_c2b", "ALIPAY_WAP");
        String paramJson = payChannelParamDTO.getParam();
        //支付渠道参数
        AliConfigParam aliConfigParam = JSON.parseObject(paramJson, AliConfigParam.class);
        //字符编码
        aliConfigParam.setCharest("utf-8");
        //AliConfigParam aliConfigParam, AlipayBean alipayBean
        PaymentResponseDTO payOrderByAliWAP = payChannelAgentService.createPayOrderByAliWAP(aliConfigParam, alipayBean);
        return payOrderByAliWAP;
    }

    /**
     * 根据订单号查询订单信息
     * @param tradeNo
     * @return
     */
    public PayOrderDTO queryPayOrder(String tradeNo){
        PayOrder payOrder = payOrderMapper.selectOne(new LambdaQueryWrapper<PayOrder>().eq(PayOrder::getTradeNo, tradeNo));
        return PayOrderConvert.INSTANCE.entity2dto(payOrder);
    }

    /**
     * 更新订单支付状态
     *
     * @param tradeNo           闪聚平台订单号
     * @param payChannelTradeNo 支付宝或微信的交易流水号(第三方支付系统的订单)
     * @param state             订单状态  交易状态支付状态,0-订单生成,1-支付中(目前未使用),2-支付成功,4-关闭 5--失败
     */
    @Override
    public void updateOrderTradeNoAndTradeState(String tradeNo, String payChannelTradeNo, String state) throws BusinessException {
        LambdaUpdateWrapper<PayOrder> payOrderLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        payOrderLambdaUpdateWrapper.eq(PayOrder::getTradeNo,tradeNo)
                .set(PayOrder::getTradeState,state)
                .set(PayOrder::getPayChannelTradeNo,payChannelTradeNo);
        if(state!=null && state.equals("2")){
            payOrderLambdaUpdateWrapper.set(PayOrder::getPaySuccessTime,LocalDateTime.now());
        }
        payOrderMapper.update(null,payOrderLambdaUpdateWrapper);
    }

    /**
     * 申请微信授权码
     *
     * @param payOrderDTO
     * @return 申请授权码的地址
     */
    @Override
    public String getWXOAuth2Code(PayOrderDTO payOrderDTO) {

        //闪聚平台的应用id
        String appId = payOrderDTO.getAppId();
        //获取微信支付渠道参数
        //String appId,String platformChannel,String payChannel
        PayChannelParamDTO payChannelParamDTO = payChannelService.queryParamByAppPlatformAndPayChannel(appId, "shanju_c2b", "WX_JSAPI");
        String param = payChannelParamDTO.getParam();
        //微信支付渠道参数
        WXConfigParam wxConfigParam = JSON.parseObject(param, WXConfigParam.class);
       //state是一个原样返回的参数
        String jsonString = JSON.toJSONString(payOrderDTO);
        String state = EncryptUtil.encodeUTF8StringBase64(jsonString);
        //https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect
        try {
            String url = String.format("%s?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_base&state=%s#wechat_redirect",
                    oauth2RequestUrl,wxConfigParam.getAppId(), oauth2CodeReturnUrl,state
            );
            return "redirect:"+url;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "forward:/pay-page-error";
    }

    /**
     * 申请openid
     *
     * @param code  授权码
     * @param appId 闪聚平台的应用id，为了获取该应用的微信支付渠道参数
     * @return
     */
    @Override
    public String getWXOAuthOpenId(String code, String appId) {
        //获取微信支付渠道参数
        //String appId,String platformChannel,String payChannel
        PayChannelParamDTO payChannelParamDTO = payChannelService.queryParamByAppPlatformAndPayChannel(appId, "shanju_c2b", "WX_JSAPI");
        String param = payChannelParamDTO.getParam();
        //微信支付渠道参数
        WXConfigParam wxConfigParam = JSON.parseObject(param, WXConfigParam.class);
        //https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code
        String url = String.format("%s?appid=%s&secret=%s&code=%s&grant_type=authorization_code",
                oauth2Token,wxConfigParam.getAppId(), wxConfigParam.getAppSecret(), code);

        //申请openid，请求url
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
        //申请openid接口响应的内容，其中包括了openid
        String body = exchange.getBody();
        log.info("申请openid响应的内容:{}",body);
        //获取openid
        String openid = JSON.parseObject(body).getString("openid");
        return openid;
    }

    /**
     * 1、保存订单到闪聚平台，2、调用支付渠道代理服务调用微信的接口
     *
     * @param payOrderDTO
     * @return h5页面所需要的数据
     */
    @Override
    public Map<String, String> submitOrderByWechat(PayOrderDTO payOrderDTO) throws BusinessException {
        String openId = payOrderDTO.getOpenId();
        //支付渠道
        payOrderDTO.setChannel("WX_JSAPI");
        //保存订单到闪聚平台数据库
        PayOrderDTO save = save(payOrderDTO);
        //调用支付渠道代理服务，调用微信下单接口
        return weChatJsapi(openId,save.getTradeNo());
    }
    private Map<String,String> weChatJsapi(String openId,String tradeNo){
        //查询订单
        PayOrderDTO payOrderDTO = queryPayOrder(tradeNo);
        WeChatBean weChatBean = new WeChatBean();
        weChatBean.setOpenId(openId);//微信openid
        weChatBean.setOutTradeNo(payOrderDTO.getTradeNo());//闪聚平台的订单号
        weChatBean.setTotalFee(payOrderDTO.getTotalAmount());//金额（分）
        weChatBean.setSpbillCreateIp(payOrderDTO.getClientIp());
        weChatBean.setBody(payOrderDTO.getBody());
        weChatBean.setNotifyUrl("none");
        String appId = payOrderDTO.getAppId();
        //支付渠道配置参数，从数据库查询
        //String appId,String platformChannel,String payChannel
        PayChannelParamDTO payChannelParamDTO = payChannelService.queryParamByAppPlatformAndPayChannel(appId, "shanju_c2b", "WX_JSAPI");
        String paramJson = payChannelParamDTO.getParam();
        WXConfigParam wxConfigParam = JSON.parseObject(paramJson, WXConfigParam.class);
        //WXConfigParam wxConfigParam, WeChatBean weChatBean
        Map<String, String> payOrderByWeChatJSAPI = payChannelAgentService.createPayOrderByWeChatJSAPI(wxConfigParam, weChatBean);
        return payOrderByWeChatJSAPI;
    }

    //保存订单（公用）
    private PayOrderDTO save(PayOrderDTO payOrderDTO) throws BusinessException{
        PayOrder payOrder = PayOrderConvert.INSTANCE.dto2entity(payOrderDTO);
        //订单号
        payOrder.setTradeNo(PaymentUtil.genUniquePayOrderNo());//采用雪花片算法
        payOrder.setCreateTime(LocalDateTime.now());//创建时间
        payOrder.setExpireTime(LocalDateTime.now().plus(30, ChronoUnit.MINUTES));//过期时间是30分钟后
        payOrder.setCurrency("CNY");//人民币
        payOrder.setTradeState("0");//订单状态，0：订单生成
        payOrderMapper.insert(payOrder);//插入订单
        return PayOrderConvert.INSTANCE.entity2dto(payOrder);
    }

    //私有，校验商户id和应用id和门店id的合法性
    private void verifyAppAndStore(Long merchantId, String appId, Long storeId) {
        //根据 应用id和商户id查询
        Boolean aBoolean = appService.queryAppInMerchant(appId, merchantId);
        if(!aBoolean){
            throw new BusinessException(CommonErrorCode.E_200005);
        }
        //根据 门店id和商户id查询
        Boolean aBoolean1 = merchantService.queryStoreInMerchant(storeId, merchantId);
        if(!aBoolean1){
            throw new BusinessException(CommonErrorCode.E_200006);
        }
    }


}
