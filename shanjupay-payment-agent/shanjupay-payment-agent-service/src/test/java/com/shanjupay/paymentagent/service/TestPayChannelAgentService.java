package com.shanjupay.paymentagent.service;

import com.shanjupay.paymentagent.api.PayChannelAgentService;
import com.shanjupay.paymentagent.api.conf.AliConfigParam;
import com.shanjupay.paymentagent.api.conf.WXConfigParam;
import com.shanjupay.paymentagent.api.dto.PaymentResponseDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Administrator
 * @version 1.0
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestPayChannelAgentService {

    @Autowired
    PayChannelAgentService payChannelAgentService;

    @Test
    public void testqueryPayOrderByAli(){
        //应用id
        String APP_ID = "2016101900725017";
        //应用私钥
        String APP_PRIVATE_KEY = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCVH5Y2CJwAlDQWE093mRqjPvI7sqObVM+ym7qgi+Ewlg0t7UTT9/u+9SWQwZsZ5Oabt3OWxF+1CjX5dqCARLh0uGKZQmLs+t4rSpSVA67RHtauHOG8mPkskkm+akTSsGDDFkOfdJ2T8f7ykBa1R2hZRQB5KRP+6pRifdYM1V2B9TWBmeb7YPyZnpe/4UHRkRz9iEOlXxBuy4nAxF/2LwJTwtILsASZwFnMT6HVhaJ9M4J9o6wO3Le/NEelUXiDRgp6K/fEmJXo2lqHHeIncAthp0Pe2i4RovEHBGIFolvDyz9cr+HXRMTvMT6FkXX+dwLx+EDKM7eaByUfDkleDPvBAgMBAAECggEBAIvhdEK9MrCsVGt8s6dJO55ztj9fY26aULWGtLpSW+r6ZeR104FN9Zx7UkCGC1pvyA27OLbs/6XPrS+TIh0P2ULLjtmaVzdaXzqWm+dn6/0Hg3MRboI9sEYp9vihsGs9vZZh9Q7HEeUsM+0OUe67y9spCbedW79cBdKZnUaugicbEQQtmvlwwf3sjct6ymY30GQHnmEA20+4LFVO0Oz/VoxuodX/4FPk5Wehn9XyD8ql2T+6BmRTrVyR7BRepl2NAbYRPBLSyjDQRBWswNqOKPdA0DBanQWCmD6gfMxdpIWNtvqL/Pe939Q2lhexIZPhemudYRJCUkW3tCSskgUWvP0CgYEAxKmxGj4AUdQeAYU9jtfgoQKFJECZJMyFDRq4ntackmiOi/1vMsLalyUu/kLzaaUEekOM3CzVDX7AyZo0fhQ12qaDUY5wjKrbPpjLFAU/wvkis9HdKRYtj854pETMXnhS5BNFz1EihviIRMhPE93eUjfqViT4Wh9aA4wAsMWpJ4sCgYEAwh3t7OVwcZwLYkmlJyi8mH+Npn01TkElM5R0j3Thcr9Nrt06aNNByS4lc3BzCnjbLztBBESuibcQ0zTIQbz75KPgveGTcymjuV3TyKCHcYqcoFYrF3Z/lEL/1fggp9v8oQd/dDC2VZ/qhB8uh+9WNuZjZfsZFz56M9EOdZiGM2MCgYBGZUOa1CTDJb2T58DQN+AVYlSMVH89F+Rse2somEO1eK3F2HMMZYlfXxUCOK7u2v3OUg3C5l1hFkUmJQezNcLNQJty/xy0jZgrQQA4a2SWgx1z5qRWx7SJbGvsyR+qlUF+mJN9kVpdm+sTS+xafSnOnmtIaLVpdR2AdYXtwOM63wKBgC3PNljThIerFZyqWIk3OdqbL2h839mJ0ier/x3jrViRANolZyVZxYqek0P844kzIDazOxKwyqfBRf0x1Zh96sv4SI9G7q8cwoB6PgJ4r74BwhisfRraYh0w+oMaPUqfxaySwmc/H6hxRrzLRwXZW3U1RXvJmBA+NESKL2ldBz6VAoGBAIGoQeP5GTx2J0NnP4+G6LrA5ZNbhyBSSNeXB/eOmHOid1RpiCPOLJv5WD6lCaNNrDwz6tYugGz85JkU3/jA2LZrHoen31tOyGinXNXscUoklEOyicUDyQtpqot1E6nR9hoNuNAp2Mj5Mjwhp1W5PqqbNhh7dF/gt3ztFiY5Z2/X";
        //支付宝公钥
        String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAoy8UIDCY2ZrrpDRbKIW/Zic3LPBXHGe5nRPLu9t1ud7PRDv5UDzwlhDRwTRnNAe8q3NZJP+ki3NzAY1Ky0QlIJZ6J9R4PRWHshR56U8kReLwuFfhQrYB5aKy8PMpRp41VT39+ywQNlD+UNbziSuRlmT0sjKPM7UCg3D9NucLKlWPfvH5mq+rWIs6pAOfcUDhSOCPS3lgHpMhpr7lYe2RFReKifFsBzEIOWBM8MGbwl0CYyASHKUtydfVDWE2k5g9N7Ypf3QgWYdNpc07vgYjSo3HPl5wLCE7bd7Haphai9gvaGFuEiscApDbQ4b2qWAIpLcwcBJnR+uQbMfYNFr2cQIDAQAB";
        String CHARSET = "utf-8";
        String serverUrl = "https://openapi.alipaydev.com/gateway.do";
        AliConfigParam aliConfigParam = new AliConfigParam();
        aliConfigParam.setUrl(serverUrl);
        aliConfigParam.setCharest(CHARSET);
        aliConfigParam.setAlipayPublicKey(ALIPAY_PUBLIC_KEY);
        aliConfigParam.setRsaPrivateKey(APP_PRIVATE_KEY);
        aliConfigParam.setAppId(APP_ID);
        aliConfigParam.setFormat("json");
        aliConfigParam.setSigntype("RSA2");

        //AliConfigParam aliConfigParam,String outTradeNo
        PaymentResponseDTO paymentResponseDTO = payChannelAgentService.queryPayOrderByAli(aliConfigParam, "SJ1217987323129917440");
        System.out.println(paymentResponseDTO);
    }

    @Test
    public void testQueryPayOrderByWeChat(){
        String appID = "wxd2bf2dba2e86a8c7";
        String mchID = "1502570431";
        String appSecret = "cec1a9185ad435abe1bced4b93f7ef2e";
        String key = "95fe355daca50f1ae82f0865c2ce87c8";
        WXConfigParam wxConfigParam = new WXConfigParam();
        wxConfigParam.setKey(key);
        wxConfigParam.setAppSecret(appSecret);
        wxConfigParam.setAppId(appID);
        wxConfigParam.setMchId(mchID);

        //WXConfigParam wxConfigParam,String outTradeNo
        PaymentResponseDTO paymentResponseDTO = payChannelAgentService.queryPayOrderByWeChat(wxConfigParam, "SJ1218090459880816640");
        System.out.println(paymentResponseDTO);
    }
}
