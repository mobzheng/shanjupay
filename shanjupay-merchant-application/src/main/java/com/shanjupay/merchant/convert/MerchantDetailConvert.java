package com.shanjupay.merchant.convert;

import com.shanjupay.merchant.api.dto.MerchantDTO;
import com.shanjupay.merchant.vo.MerchantDetailVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 将商户资质申请vo和dto进行转换
 * Created by Administrator.
 */
@Mapper
public interface MerchantDetailConvert {

    MerchantDetailConvert INSTANCE = Mappers.getMapper(MerchantDetailConvert.class);

    //将dto转成vo
    MerchantDetailVO dto2vo(MerchantDTO merchantDTO);
    //将vo转成dto
    MerchantDTO vo2dto(MerchantDetailVO merchantDetailVO);

}
