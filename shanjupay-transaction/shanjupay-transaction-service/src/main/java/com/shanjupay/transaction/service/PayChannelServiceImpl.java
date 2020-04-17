package com.shanjupay.transaction.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shanjupay.common.cache.Cache;
import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import com.shanjupay.common.util.RedisUtil;
import com.shanjupay.transaction.api.PayChannelService;
import com.shanjupay.transaction.api.dto.PayChannelDTO;
import com.shanjupay.transaction.api.dto.PayChannelParamDTO;
import com.shanjupay.transaction.api.dto.PlatformChannelDTO;
import com.shanjupay.transaction.convert.PayChannelParamConvert;
import com.shanjupay.transaction.convert.PlatformChannelConvert;
import com.shanjupay.transaction.entity.AppPlatformChannel;
import com.shanjupay.transaction.entity.PayChannelParam;
import com.shanjupay.transaction.entity.PlatformChannel;
import com.shanjupay.transaction.mapper.AppPlatformChannelMapper;
import com.shanjupay.transaction.mapper.PayChannelParamMapper;
import com.shanjupay.transaction.mapper.PlatformChannelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Administrator
 * @version 1.0
 **/
@org.apache.dubbo.config.annotation.Service
public class PayChannelServiceImpl implements PayChannelService {

    @Autowired
    Cache cache;

    @Autowired
    PlatformChannelMapper platformChannelMapper;

    @Autowired
    AppPlatformChannelMapper appPlatformChannelMapper;

    @Autowired
    PayChannelParamMapper payChannelParamMapper;


    /**
     * 查询平台的服务类型
     *
     * @return
     * @throws BusinessException
     */
    @Override
    public List<PlatformChannelDTO> queryPlatformChannel() throws BusinessException {
        //查询platform_channel表的全部记录
        List<PlatformChannel> platformChannels = platformChannelMapper.selectList(null);
        //将platformChannels转成包含dto的list
        return PlatformChannelConvert.INSTANCE.listentity2listdto(platformChannels);
    }

    /**
     * 为某个应用绑定一个服务类型
     *
     * @param appId                应用id
     * @param platformChannelCodes 服务类型的code
     * @throws BusinessException
     */
    @Override
    @Transactional
    public void bindPlatformChannelForApp(String appId, String platformChannelCodes) throws BusinessException {

        //根据应用id和服务类型code查询 ，如果已经绑定则不再插入，否则插入记录
        AppPlatformChannel appPlatformChannel = appPlatformChannelMapper.selectOne(new LambdaQueryWrapper<AppPlatformChannel>().eq(AppPlatformChannel::getAppId, appId)
                .eq(AppPlatformChannel::getPlatformChannel, platformChannelCodes));
        if(appPlatformChannel == null){
            //向app_platform_channel插入
            AppPlatformChannel entity = new AppPlatformChannel();
            entity.setAppId(appId);//应用id
            entity.setPlatformChannel(platformChannelCodes);//服务类型code
            appPlatformChannelMapper.insert(entity);
        }
    }

    /**
     * 应用绑定服务类型的状态
     *
     * @param appId
     * @param platformChannel
     * @return 已绑定 1,否则0
     * @throws BusinessException
     */
    @Override
    public int queryAppBindPlatformChannel(String appId, String platformChannel) throws BusinessException {
        AppPlatformChannel appPlatformChannel = appPlatformChannelMapper.selectOne(new LambdaQueryWrapper<AppPlatformChannel>().eq(AppPlatformChannel::getAppId, appId)
                .eq(AppPlatformChannel::getPlatformChannel, platformChannel));
        if(appPlatformChannel !=null){
            return 1;
        }
        return 0;
    }

    /**
     * 根据服务类型查询支付渠道
     *
     * @param platformChannelCode 服务类型编码
     * @return 支付渠道列表
     * @throws BusinessException
     */
    @Override
    public List<PayChannelDTO> queryPayChannelByPlatformChannel(String platformChannelCode) throws BusinessException {
        //调用mapper 查询数据库platform_pay_channel，pay_channel，platform_channel

        return platformChannelMapper.selectPayChannelByPlatformChannel(platformChannelCode);
    }

    /**
     * 支付渠道参数配置
     *
     * @param payChannelParam 配置支付渠道参数：包括：商户id、应用id，服务类型code，支付渠道code，配置名称，配置参数(json)
     * @throws BusinessException
     */
    @Override
    public void savePayChannelParam(PayChannelParamDTO payChannelParam) throws BusinessException {

        if(payChannelParam == null || payChannelParam.getChannelName() == null || payChannelParam.getParam()== null){
            throw new BusinessException(CommonErrorCode.E_300009);
        }
        //根据应用、服务类型、支付渠道查询一条记录
        //根据应用、服务类型查询应用与服务类型的绑定id
        Long appPlatformChannelId = selectIdByAppPlatformChannel(payChannelParam.getAppId(), payChannelParam.getPlatformChannelCode());
       if(appPlatformChannelId == null){
           throw new BusinessException(CommonErrorCode.E_300010);
       }
        //根据应用与服务类型的绑定id和支付渠道查询PayChannelParam的一条记录
        PayChannelParam entity = payChannelParamMapper.selectOne(new LambdaQueryWrapper<PayChannelParam>().eq(PayChannelParam::getAppPlatformChannelId, appPlatformChannelId)
                .eq(PayChannelParam::getPayChannel, payChannelParam.getPayChannel()));
        //如果存在配置则更新
        if(entity != null){
            entity.setChannelName(payChannelParam.getChannelName());//配置名称
            entity.setParam(payChannelParam.getParam());//json格式的参数
            payChannelParamMapper.updateById(entity);
        }else{
            //否则添加配置
            PayChannelParam entityNew = PayChannelParamConvert.INSTANCE.dto2entity(payChannelParam);
            entityNew.setId(null);
            entityNew.setAppPlatformChannelId(appPlatformChannelId);//应用与服务类型绑定关系id
            payChannelParamMapper.insert(entityNew);
        }

        //保存到redis
        updateCache(payChannelParam.getAppId(),payChannelParam.getPlatformChannelCode());
    }

    /**
     * 根据应用和服务类型将查询到支付渠道参数配置列表写入redis
     * @param appId 应用id
     * @param platformChannelCode 服务类型code
     */
    private void updateCache(String appId,String platformChannelCode){

        //得到redis中key(付渠道参数配置列表的key)
        //格式：SJ_PAY_PARAM:应用id:服务类型code，例如：SJ_PAY_PARAM：ebcecedd-3032-49a6-9691-4770e66577af：shanju_c2b
        String redisKey = RedisUtil.keyBuilder(appId, platformChannelCode);
        //根据key查询redis
        Boolean exists = cache.exists(redisKey);
        if(exists){
            cache.del(redisKey);
        }
        //根据应用id和服务类型code查询支付渠道参数
        //根据应用和服务类型找到它们绑定id
        Long appPlatformChannelId = selectIdByAppPlatformChannel(appId, platformChannelCode);
        if(appPlatformChannelId != null){
            //应用和服务类型绑定id查询支付渠道参数记录
            List<PayChannelParam> payChannelParams = payChannelParamMapper.selectList(new LambdaQueryWrapper<PayChannelParam>().eq(PayChannelParam::getAppPlatformChannelId, appPlatformChannelId));
            List<PayChannelParamDTO> payChannelParamDTOS = PayChannelParamConvert.INSTANCE.listentity2listdto(payChannelParams);
            //将payChannelParamDTOS转成json串存入redis
            cache.set(redisKey, JSON.toJSON(payChannelParamDTOS).toString());
        }

    }
    /**
     * 根据应用和服务类型查询支付渠道参数列表
     *
     * @param appId           应用id
     * @param platformChannel 服务类型code
     * @return
     */
    @Override
    public List<PayChannelParamDTO> queryPayChannelParamByAppAndPlatform(String appId, String platformChannel) {
        //先从redis查询，如果有则返回
        String redisKey = RedisUtil.keyBuilder(appId, platformChannel);
        Boolean exists = cache.exists(redisKey);

        if(exists){
            //从redis获取支付渠道参数列表（json串）
            String PayChannelParamDTO_String = cache.get(redisKey);
            //将json串转成 List<PayChannelParamDTO>
            List<PayChannelParamDTO> payChannelParamDTOS = JSON.parseArray(PayChannelParamDTO_String, PayChannelParamDTO.class);
            return payChannelParamDTOS;
        }

        //根据应用和服务类型找到它们绑定id
        Long appPlatformChannelId = selectIdByAppPlatformChannel(appId, platformChannel);
        if(appPlatformChannelId == null){
            return null;
        }
        //应用和服务类型绑定id查询支付渠道参数记录
        List<PayChannelParam> payChannelParams = payChannelParamMapper.selectList(new LambdaQueryWrapper<PayChannelParam>().eq(PayChannelParam::getAppPlatformChannelId, appPlatformChannelId));
        List<PayChannelParamDTO> payChannelParamDTOS = PayChannelParamConvert.INSTANCE.listentity2listdto(payChannelParams);
        //保存到redis
        updateCache(appId,platformChannel);
        return payChannelParamDTOS;
    }

    /**
     * 根据应用、服务类型和支付渠道的代码查询该支付渠道的参数配置信息
     *
     * @param appId
     * @param platformChannel 服务类型code
     * @param payChannel      支付渠道代码
     * @return
     */
    @Override
    public PayChannelParamDTO queryParamByAppPlatformAndPayChannel(String appId, String platformChannel, String payChannel) {
        //根据应用和服务类型查询支付渠道参数列表
        List<PayChannelParamDTO> payChannelParamDTOS = queryPayChannelParamByAppAndPlatform(appId, platformChannel);
        for(PayChannelParamDTO payChannelParamDTO:payChannelParamDTOS){
            if(payChannelParamDTO.getPayChannel().equals(payChannel)){
                return payChannelParamDTO;
            }
        }
        return null;
    }

    /**
     * 根据应用、服务类型查询应用与服务类型的绑定id
     * @param appId
     * @param platformChannelCode
     * @return
     */
    private Long selectIdByAppPlatformChannel(String appId,String platformChannelCode){
        AppPlatformChannel appPlatformChannel = appPlatformChannelMapper.selectOne(new LambdaQueryWrapper<AppPlatformChannel>().eq(AppPlatformChannel::getAppId, appId)
                .eq(AppPlatformChannel::getPlatformChannel, platformChannelCode));
        if(appPlatformChannel!=null){
            return appPlatformChannel.getId();//应用与服务类型的绑定id
        }
        return null;
    }

}
