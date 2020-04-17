package com.shanjupay.merchant.service;

import com.alibaba.fastjson.JSON;
import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Administrator
 * @version 1.0
 **/
@org.springframework.stereotype.Service  //实例为一个bean
@Slf4j
public class SmsServiceImpl implements SmsService{

    @Value("${sms.url}")
    String url;

    @Value("${sms.effectiveTime}")
    String effectiveTime;

    @Autowired
    RestTemplate restTemplate;

    /**
     * 发送手机验证码
     *
     * @param phone 手机号
     * @return 验证码对应的key
     */
    @Override
    public String sendMsg(String phone) {

        //向验证码服务发送请求的地址
        String sms_url = url+"/generate?name=sms&effectiveTime="+effectiveTime;

        //请求体
        Map<String,Object> body = new HashMap<>();
        body.put("mobile",phone);
        //请求头
        HttpHeaders httpHeaders =new HttpHeaders();
        //指定Content-Type: application/json
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        //请求信息,传入body，header
        HttpEntity httpEntity = new HttpEntity(body,httpHeaders);
        //向url请求
        ResponseEntity<Map> exchange = null;

        Map bodyMap = null;
        try {
            exchange = restTemplate.exchange(sms_url, HttpMethod.POST, httpEntity, Map.class);
            log.info("请求验证码服务，得到响应:{}", JSON.toJSONString(exchange));
            bodyMap = exchange.getBody();
        } catch (RestClientException e) {
            e.printStackTrace();
            throw new RuntimeException("发送验证码失败");
        }
        if(bodyMap == null || bodyMap.get("result") == null){
            throw new RuntimeException("发送验证码失败");
        }

        Map result = (Map) bodyMap.get("result");
        String key = (String) result.get("key");
        log.info("得到发送验证码对应的key:{}",key);
        return key;
    }

    /**
     * 校验手机验证码
     *
     * @param verifiyKey  验证码的key
     * @param verifiyCode 验证码
     */
    @Override
    public void checkVerifiyCode(String verifiyKey, String verifiyCode) throws BusinessException {
        //校验验证码的url
        String url = "http://localhost:56085/sailing/verify?name=sms&verificationCode="+verifiyCode+"&verificationKey="+verifiyKey;

        Map bodyMap = null;
        try {
            //使用restTemplate请求验证码服务
            ResponseEntity<Map> exchange = restTemplate.exchange(url, HttpMethod.POST, HttpEntity.EMPTY, Map.class);
            log.info("请求验证码服务，得到响应:{}", JSON.toJSONString(exchange));
            bodyMap = exchange.getBody();
        }catch (Exception e){
            e.printStackTrace();
            throw new BusinessException(CommonErrorCode.E_100102);
//            throw new RuntimeException("校验验证码失败");

        }
        if(bodyMap == null || bodyMap.get("result") == null || !(Boolean) bodyMap.get("result")){
            throw new BusinessException(CommonErrorCode.E_100102);
        }
    }
}
