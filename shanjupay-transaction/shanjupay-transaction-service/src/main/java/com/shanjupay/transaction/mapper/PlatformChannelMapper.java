package com.shanjupay.transaction.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shanjupay.transaction.api.dto.PayChannelDTO;
import com.shanjupay.transaction.entity.PlatformChannel;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author author
 * @since 2019-11-15
 */
@Repository
public interface PlatformChannelMapper extends BaseMapper<PlatformChannel> {

    /**
     * 根据服务类型code查询对应的支付渠道
     * @param platformChannelCode
     * @return
     */
    @Select("SELECT " +
            "  pc.* " +
            "FROM" +
            "  platform_pay_channel ppc," +
            "  pay_channel pc," +
            "  platform_channel pla " +
            "WHERE ppc.PAY_CHANNEL = pc.CHANNEL_CODE " +
            "  AND ppc.PLATFORM_CHANNEL = pla.CHANNEL_CODE " +
            "  AND pla.CHANNEL_CODE = #{platformChannelCode}  ")
    List<PayChannelDTO> selectPayChannelByPlatformChannel(String platformChannelCode);
}
