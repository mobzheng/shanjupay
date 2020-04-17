package com.shanjupay.transaction.api;

import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.transaction.api.dto.PayChannelDTO;
import com.shanjupay.transaction.api.dto.PayChannelParamDTO;
import com.shanjupay.transaction.api.dto.PlatformChannelDTO;

import java.util.List;

/**
 * Created by Administrator.
 */
public interface PayChannelService {
    /**
     * 查询平台的服务类型
     * @return
     * @throws BusinessException
     */
    List<PlatformChannelDTO> queryPlatformChannel() throws BusinessException;

    /**
     * 为某个应用绑定一个服务类型
     * @param appId 应用id
     * @param platformChannelCodes 服务类型的code
     * @throws BusinessException
     */
    void bindPlatformChannelForApp(String appId,String platformChannelCodes) throws BusinessException;


    /**
     * 应用绑定服务类型的状态
     * @param appId
     * @param platformChannel
     * @return 已绑定 1,否则0
     * @throws BusinessException
     */
    int queryAppBindPlatformChannel(String appId,String platformChannel)throws BusinessException;

    /**
     * 根据服务类型查询支付渠道
     * @param platformChannelCode 服务类型编码
     * @return 支付渠道列表
     * @throws BusinessException
     */
    List<PayChannelDTO> queryPayChannelByPlatformChannel(String platformChannelCode) throws BusinessException;

    /**
     * 支付渠道参数配置
     * @param payChannelParam 配置支付渠道参数：包括：商户id、应用id，服务类型code，支付渠道code，配置名称，配置参数(json)
     * @throws BusinessException
     */
    void savePayChannelParam(PayChannelParamDTO payChannelParam) throws BusinessException;

    /**
     * 根据应用和服务类型查询支付渠道参数列表
     * @param appId 应用id
     * @param platformChannel 服务类型code
     * @return
     */
    List<PayChannelParamDTO> queryPayChannelParamByAppAndPlatform(String appId,String platformChannel);

    /**
     * 根据应用、服务类型和支付渠道的代码查询该支付渠道的参数配置信息
     * @param appId应用id
     * @param platformChannel 服务类型code
     * @param payChannel  支付渠道代码
     * @return
     */
    PayChannelParamDTO queryParamByAppPlatformAndPayChannel(String appId,String platformChannel,String payChannel);
}
