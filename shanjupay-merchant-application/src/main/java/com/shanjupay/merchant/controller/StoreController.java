package com.shanjupay.merchant.controller;

import com.shanjupay.common.domain.PageVO;
import com.shanjupay.common.util.QRCodeUtil;
import com.shanjupay.merchant.api.MerchantService;
import com.shanjupay.merchant.api.dto.MerchantDTO;
import com.shanjupay.merchant.api.dto.StoreDTO;
import com.shanjupay.merchant.common.util.SecurityUtil;
import com.shanjupay.transaction.api.TransactionService;
import com.shanjupay.transaction.api.dto.QRCodeDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * 门店管理相关接口定义
 * @author Administrator
 * @version 1.0
 **/
@Api(value = "商户平台-门店管理", tags = "商户平台-门店管理", description = "商户平台-门店的增删改查")
@RestController
@Slf4j
public class StoreController {

    //"%s商品"
    @Value("${shanjupay.c2b.subject}")
    String subject;
    //"向%s付款"
    @Value("${shanjupay.c2b.body}")
    String body;


    @Reference
    MerchantService merchantService;

    @Reference
    TransactionService transactionService;

    @ApiOperation("门店列表查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name="pageNo",value = "页码",required = true,dataType = "int",paramType = "query"),
            @ApiImplicitParam(name="pageSize",value = "每页记录数",required = true,dataType = "int",paramType = "query")
    })
    @PostMapping("/my/stores/merchants/page")
    public PageVO<StoreDTO> queryStoreByPage(Integer pageNo,Integer pageSize){
        //商户id
        Long merchantId = SecurityUtil.getMerchantId();
        //查询条件
        StoreDTO storeDTO = new StoreDTO();
        storeDTO.setMerchantId(merchantId);//商户id
        //调用service查询列表
        PageVO<StoreDTO> storeDTOS = merchantService.queryStoreByPage(storeDTO, pageNo, pageSize);
        return storeDTOS;
    }

    @ApiOperation("生成商户应用门店的二维码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "appId", value = "商户应用id", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "storeId", value = "商户门店id", required = true, dataType = "String", paramType = "path"),
    })
    @GetMapping(value = "/my/apps/{appId}/stores/{storeId}/app-store-qrcode")
    public String createCScanBStoreQRCode(@PathVariable("storeId") Long storeId, @PathVariable("appId")String appId) throws IOException {

        //获取商户id
        Long merchantId = SecurityUtil.getMerchantId();
        //商户信息
        MerchantDTO merchantDTO = merchantService.queryMerchantById(merchantId);

        QRCodeDto qrCodeDto = new QRCodeDto();
        qrCodeDto.setMerchantId(merchantId);
        qrCodeDto.setStoreId(storeId);
        qrCodeDto.setAppId(appId);
        //标题.用商户名称替换 %s
        String subjectFormat = String.format(subject, merchantDTO.getMerchantName());
        qrCodeDto.setSubject(subjectFormat);
        //内容
        String bodyFormat = String.format(body, merchantDTO.getMerchantName());
        qrCodeDto.setBody(bodyFormat);

        //获取二维码的URL
        String storeQRCodeURL = transactionService.createStoreQRCode(qrCodeDto);

        //调用工具类生成二维码图片
        QRCodeUtil qrCodeUtil = new QRCodeUtil();
        //二维码图片base64编码
        String qrCode = qrCodeUtil.createQRCode(storeQRCodeURL, 200, 200);
        return qrCode;

    }
}
