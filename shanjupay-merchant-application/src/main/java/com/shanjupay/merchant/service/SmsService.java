package com.shanjupay.merchant.service;

import com.shanjupay.common.domain.BusinessException;

/**
 * Created by Administrator.
 */
public interface SmsService {

    /**
     * 发送手机验证码
     * @param phone 手机号
     * @return 验证码对应的key
     */
    String sendMsg(String phone);


    /**
     *  校验手机验证码
     * @param verifiyKey 验证码的key
     * @param verifiyCode 验证码
     */
    void checkVerifiyCode(String verifiyKey,String verifiyCode) throws BusinessException;
}
