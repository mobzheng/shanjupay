package com.shanjupay.transaction.controller;

import com.alibaba.fastjson.JSON;
import com.github.wxpay.sdk.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Administrator
 * @version 1.0
 **/
@Slf4j
@Controller
public class WxPayController {

    String appID = "wxd2bf2dba2e86a8c7";
    String mchID = "1502570431";
    String appSecret = "cec1a9185ad435abe1bced4b93f7ef2e";
    String key = "95fe355daca50f1ae82f0865c2ce87c8";
    //申请授权码地址
    String wxOAuth2RequestUrl = "https://open.weixin.qq.com/connect/oauth2/authorize";
    //授权回调地址
    String wxOAuth2CodeReturnUrl = "http://xfc.nat300.top/transaction/wx-oauth-code-return";
    String state="";

    //获取授权码
    @GetMapping("/getWXOAuth2Code")
    public String getWXOAuth2Code(HttpServletRequest request, HttpServletResponse response){

        //https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect
        String url = String.format("https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect",
                appID, wxOAuth2CodeReturnUrl
        );

        return "redirect:"+url;

    }

    /**
     * //授权码回调，传入授权码和state，/wx-oauth-code-return?code=授权码&state=
     * @param code 授权码
     * @param state 申请授权码传入微信的值，被原样返回
     * @return
     */
    @GetMapping("/wx-oauth-code-return2")
    public String wxOAuth2CodeReturn(@RequestParam String code,@RequestParam String state){

        //https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code
        String url = String.format("https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code",
                appID, appSecret, code
        );

        //申请openid，请求url
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
        //申请openid接口响应的内容，其中包括了openid
        String body = exchange.getBody();
        log.info("申请openid响应的内容:{}",body);
        //获取openid
        String openid = JSON.parseObject(body).getString("openid");
        //重定向到统一下单接口
        return "redirect:http://xfc.nat300.top/transaction/wxjspay?openid=" + openid;
    }


    //统一下单，接收openid
    @GetMapping("/wxjspay")
    public ModelAndView wxjspay(HttpServletRequest request,HttpServletResponse response) throws Exception {
        //创建sdk客户端
        WXPay wxPay = new WXPay(new WXPayConfigCustom());
        //构造请求的参数
        Map<String,String> requestParam = new HashMap<>();
        requestParam.put("out_trade_no","10029293889");//订单号
        requestParam.put("body", "iphone8");//订单描述
        requestParam.put("fee_type", "CNY");//人民币
        requestParam.put("total_fee", String.valueOf(1)); //金额
        requestParam.put("spbill_create_ip", "127.0.0.1");//客户端ip
        requestParam.put("notify_url", "none");//微信异步通知支付结果接口，暂时不用
        requestParam.put("trade_type", "JSAPI");
        //从请求中获取openid
        String openid = request.getParameter("openid");
        requestParam.put("openid",openid);
        //调用统一下单接口
        Map<String, String> resp = wxPay.unifiedOrder(requestParam);

        //准备h5网页需要的数据
        Map<String,String> jsapiPayParam = new HashMap<>();
        jsapiPayParam.put("appId",appID);
        jsapiPayParam.put("timeStamp",System.currentTimeMillis()/1000+"");
        jsapiPayParam.put("nonceStr", UUID.randomUUID().toString());//随机字符串
        jsapiPayParam.put("package","prepay_id="+resp.get("prepay_id"));
        jsapiPayParam.put("signType","HMAC-SHA256");
        //将h5网页响应给前端
        jsapiPayParam.put("paySign", WXPayUtil.generateSignature(jsapiPayParam,key,WXPayConstants.SignType.HMACSHA256));

        return new ModelAndView("wxpay",jsapiPayParam);
    }

    class  WXPayConfigCustom extends WXPayConfig{

        @Override
        protected String getAppID() {
            return appID;
        }

        @Override
        protected String getMchID() {
            return mchID;
        }

        @Override
        protected String getKey() {
            return key;
        }

        @Override
        protected InputStream getCertStream() {
            return null;
        }

        @Override
        protected IWXPayDomain getWXPayDomain() {
            return new IWXPayDomain() {
                @Override
                public void report(String s, long l, Exception e) {

                }

                @Override
                public DomainInfo getDomain(WXPayConfig wxPayConfig) {
                    return new DomainInfo(WXPayConstants.DOMAIN_API,true);
                }
            };
        }
    }



}
