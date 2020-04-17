package com.shanjupay.merchant.controller;

import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import com.shanjupay.common.util.PhoneUtil;
import com.shanjupay.merchant.api.MerchantService;
import com.shanjupay.merchant.api.dto.MerchantDTO;
import com.shanjupay.merchant.common.util.SecurityUtil;
import com.shanjupay.merchant.convert.MerchantDetailConvert;
import com.shanjupay.merchant.convert.MerchantRegisterConvert;
import com.shanjupay.merchant.service.FileService;
import com.shanjupay.merchant.service.SmsService;
import com.shanjupay.merchant.vo.MerchantDetailVO;
import com.shanjupay.merchant.vo.MerchantRegisterVO;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * @author Administrator
 * @version 1.0
 **/
@RestController
@Api(value="商户平台应用接口",tags = "商户平台应用接口",description = "商户平台应用接口")
public class MerchantController {

    @org.apache.dubbo.config.annotation.Reference  //注入的远程调用的接口
    MerchantService merchantService;

    @Autowired //注入本地的bean
    SmsService smsService;

    @Autowired
    FileService fileService;

    @ApiOperation(value="根据id查询商户信息")
    @GetMapping("/merchants/{id}")
    public MerchantDTO queryMerchantById(@PathVariable("id") Long id){

        MerchantDTO merchantDTO = merchantService.queryMerchantById(id);
        return merchantDTO;
    }

    @ApiOperation("获取登录用户的商户信息")
    @GetMapping(value="/my/merchants")
    public MerchantDTO getMyMerchantInfo(){
        //从token中获取商户id
        Long merchantId = SecurityUtil.getMerchantId();
        return merchantService.queryMerchantById(merchantId);
    }
    @ApiOperation("获取手机验证码")
    @GetMapping("/sms")
    @ApiImplicitParam(value = "手机号",name = "phone",required = true,dataType = "string",paramType = "query")
    public String getSMSCode(@RequestParam("phone") String phone){
        //向验证码服务请求发送验证码
        return smsService.sendMsg(phone);
    }

    @ApiOperation("商户注册")
    @ApiImplicitParam(value = "商户注册信息",name = "merchantRegisterVO",required = true,dataType = "MerchantRegisterVO",paramType = "body")
    @PostMapping("/merchants/register")
    public MerchantRegisterVO registerMerchant(@RequestBody MerchantRegisterVO merchantRegisterVO){

        //校验参数的合法性
        if(merchantRegisterVO == null){
            throw new BusinessException(CommonErrorCode.E_100108);
        }
        if(StringUtils.isBlank(merchantRegisterVO.getMobile())){
            throw new BusinessException(CommonErrorCode.E_100112);
        }
        //手机号格式校验
        if(!PhoneUtil.isMatches(merchantRegisterVO.getMobile())){
            throw new BusinessException(CommonErrorCode.E_100109);
        }

        //校验验证码
        smsService.checkVerifiyCode(merchantRegisterVO.getVerifiykey(),merchantRegisterVO.getVerifiyCode());
        //调用dubbo服务接口
//        MerchantDTO merchantDTO = new MerchantDTO();
        //向dto写入商户注册的信息
//        merchantDTO.setMobile(merchantRegisterVO.getMobile());
//        merchantDTO.setUsername(merchantRegisterVO.getUsername());
        //...
        //使用MapStruct转换对象
        MerchantDTO merchantDTO = MerchantRegisterConvert.INSTANCE.vo2dto(merchantRegisterVO);
        merchantService.createMerchant(merchantDTO);
        return merchantRegisterVO;
    }

    //上传证件照
    @ApiOperation("上传证件照")
    @PostMapping("/upload")
    public String upload(@ApiParam(value = "证件照",required = true) @RequestParam("file") MultipartFile multipartFile) throws IOException {

        //调用fileService上传文件
        //生成的文件名称fileName，要保证它的唯一
        //文件原始名称
        String originalFilename = multipartFile.getOriginalFilename();
        //扩展名
        String suffix = originalFilename.substring(originalFilename.lastIndexOf(".")-1);
        //文件名称
        String fileName = UUID.randomUUID()+suffix;
        //byte[] bytes,String fileName
        return fileService.upload(multipartFile.getBytes(),fileName);
    }

    @ApiOperation("资质申请")
    @PostMapping("/my/merchants/save")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "merchantInfo", value = "商户认证资料", required = true, dataType = "MerchantDetailVO", paramType = "body")
    })
    public void saveMerchant(@RequestBody  MerchantDetailVO merchantInfo){
        //解析token，取出当前登录商户的id
        Long merchantId = SecurityUtil.getMerchantId();

        //Long merchantId,MerchantDTO merchantDTO
        MerchantDTO merchantDTO = MerchantDetailConvert.INSTANCE.vo2dto(merchantInfo);
        merchantService.applyMerchant(merchantId,merchantDTO);
    }

    @ApiOperation("测试")
    @GetMapping(path = "/hello")
    public String hello(){
        return "hello";
    }

    @ApiOperation("测试")
    @ApiImplicitParam(name = "name", value = "姓名", required = true, dataType = "string")
    @PostMapping(value = "/hi")
    public String hi(String name) {
        return "hi,"+name;
    }
}
