package com.shanjupay.merchant.convert;

import com.shanjupay.merchant.api.dto.MerchantDTO;
import com.shanjupay.merchant.vo.MerchantDetailVO;
import javax.annotation.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2020-02-03T15:28:15+0800",
    comments = "version: 1.3.0.Final, compiler: javac, environment: Java 1.8.0_202 (Oracle Corporation)"
)
public class MerchantDetailConvertImpl implements MerchantDetailConvert {

    @Override
    public MerchantDetailVO dto2vo(MerchantDTO merchantDTO) {
        if ( merchantDTO == null ) {
            return null;
        }

        MerchantDetailVO merchantDetailVO = new MerchantDetailVO();

        merchantDetailVO.setMerchantName( merchantDTO.getMerchantName() );
        if ( merchantDTO.getMerchantNo() != null ) {
            merchantDetailVO.setMerchantNo( String.valueOf( merchantDTO.getMerchantNo() ) );
        }
        merchantDetailVO.setMerchantAddress( merchantDTO.getMerchantAddress() );
        merchantDetailVO.setMerchantType( merchantDTO.getMerchantType() );
        merchantDetailVO.setBusinessLicensesImg( merchantDTO.getBusinessLicensesImg() );
        merchantDetailVO.setIdCardFrontImg( merchantDTO.getIdCardFrontImg() );
        merchantDetailVO.setIdCardAfterImg( merchantDTO.getIdCardAfterImg() );
        merchantDetailVO.setUsername( merchantDTO.getUsername() );
        merchantDetailVO.setContactsAddress( merchantDTO.getContactsAddress() );

        return merchantDetailVO;
    }

    @Override
    public MerchantDTO vo2dto(MerchantDetailVO merchantDetailVO) {
        if ( merchantDetailVO == null ) {
            return null;
        }

        MerchantDTO merchantDTO = new MerchantDTO();

        merchantDTO.setMerchantName( merchantDetailVO.getMerchantName() );
        if ( merchantDetailVO.getMerchantNo() != null ) {
            merchantDTO.setMerchantNo( Long.parseLong( merchantDetailVO.getMerchantNo() ) );
        }
        merchantDTO.setMerchantAddress( merchantDetailVO.getMerchantAddress() );
        merchantDTO.setMerchantType( merchantDetailVO.getMerchantType() );
        merchantDTO.setBusinessLicensesImg( merchantDetailVO.getBusinessLicensesImg() );
        merchantDTO.setIdCardFrontImg( merchantDetailVO.getIdCardFrontImg() );
        merchantDTO.setIdCardAfterImg( merchantDetailVO.getIdCardAfterImg() );
        merchantDTO.setUsername( merchantDetailVO.getUsername() );
        merchantDTO.setContactsAddress( merchantDetailVO.getContactsAddress() );

        return merchantDTO;
    }
}
