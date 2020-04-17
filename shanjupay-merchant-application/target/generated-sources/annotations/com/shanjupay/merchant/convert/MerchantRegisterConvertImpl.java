package com.shanjupay.merchant.convert;

import com.shanjupay.merchant.api.dto.MerchantDTO;
import com.shanjupay.merchant.vo.MerchantRegisterVO;
import javax.annotation.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2020-02-03T15:28:15+0800",
    comments = "version: 1.3.0.Final, compiler: javac, environment: Java 1.8.0_202 (Oracle Corporation)"
)
public class MerchantRegisterConvertImpl implements MerchantRegisterConvert {

    @Override
    public MerchantRegisterVO dto2vo(MerchantDTO merchantDTO) {
        if ( merchantDTO == null ) {
            return null;
        }

        MerchantRegisterVO merchantRegisterVO = new MerchantRegisterVO();

        merchantRegisterVO.setMobile( merchantDTO.getMobile() );
        merchantRegisterVO.setUsername( merchantDTO.getUsername() );
        merchantRegisterVO.setPassword( merchantDTO.getPassword() );

        return merchantRegisterVO;
    }

    @Override
    public MerchantDTO vo2dto(MerchantRegisterVO merchantRegisterVO) {
        if ( merchantRegisterVO == null ) {
            return null;
        }

        MerchantDTO merchantDTO = new MerchantDTO();

        merchantDTO.setUsername( merchantRegisterVO.getUsername() );
        merchantDTO.setMobile( merchantRegisterVO.getMobile() );
        merchantDTO.setPassword( merchantRegisterVO.getPassword() );

        return merchantDTO;
    }
}
