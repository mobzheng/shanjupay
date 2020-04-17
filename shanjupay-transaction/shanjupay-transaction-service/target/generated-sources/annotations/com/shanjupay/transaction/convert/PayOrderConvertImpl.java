package com.shanjupay.transaction.convert;

import com.shanjupay.transaction.api.dto.PayOrderDTO;
import com.shanjupay.transaction.entity.PayOrder;
import com.shanjupay.transaction.vo.OrderConfirmVO;
import javax.annotation.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2020-02-03T15:28:22+0800",
    comments = "version: 1.3.0.Final, compiler: javac, environment: Java 1.8.0_202 (Oracle Corporation)"
)
public class PayOrderConvertImpl implements PayOrderConvert {

    @Override
    public PayOrderDTO entity2dto(PayOrder entity) {
        if ( entity == null ) {
            return null;
        }

        PayOrderDTO payOrderDTO = new PayOrderDTO();

        payOrderDTO.setMerchantId( entity.getMerchantId() );
        payOrderDTO.setStoreId( entity.getStoreId() );
        payOrderDTO.setAppId( entity.getAppId() );
        payOrderDTO.setChannel( entity.getChannel() );
        payOrderDTO.setTradeNo( entity.getTradeNo() );
        payOrderDTO.setOutTradeNo( entity.getOutTradeNo() );
        payOrderDTO.setSubject( entity.getSubject() );
        payOrderDTO.setBody( entity.getBody() );
        payOrderDTO.setCurrency( entity.getCurrency() );
        payOrderDTO.setTotalAmount( entity.getTotalAmount() );
        payOrderDTO.setOptional( entity.getOptional() );
        payOrderDTO.setAnalysis( entity.getAnalysis() );
        payOrderDTO.setExtra( entity.getExtra() );
        payOrderDTO.setDevice( entity.getDevice() );
        payOrderDTO.setClientIp( entity.getClientIp() );
        payOrderDTO.setPayChannel( entity.getPayChannel() );
        payOrderDTO.setTradeState( entity.getTradeState() );
        payOrderDTO.setCreateTime( entity.getCreateTime() );
        payOrderDTO.setUpdateTime( entity.getUpdateTime() );
        payOrderDTO.setExpireTime( entity.getExpireTime() );
        payOrderDTO.setPaySuccessTime( entity.getPaySuccessTime() );

        return payOrderDTO;
    }

    @Override
    public PayOrder dto2entity(PayOrderDTO dto) {
        if ( dto == null ) {
            return null;
        }

        PayOrder payOrder = new PayOrder();

        payOrder.setTradeNo( dto.getTradeNo() );
        payOrder.setMerchantId( dto.getMerchantId() );
        payOrder.setStoreId( dto.getStoreId() );
        payOrder.setAppId( dto.getAppId() );
        payOrder.setPayChannel( dto.getPayChannel() );
        payOrder.setChannel( dto.getChannel() );
        payOrder.setOutTradeNo( dto.getOutTradeNo() );
        payOrder.setSubject( dto.getSubject() );
        payOrder.setBody( dto.getBody() );
        payOrder.setCurrency( dto.getCurrency() );
        payOrder.setTotalAmount( dto.getTotalAmount() );
        payOrder.setOptional( dto.getOptional() );
        payOrder.setAnalysis( dto.getAnalysis() );
        payOrder.setExtra( dto.getExtra() );
        payOrder.setTradeState( dto.getTradeState() );
        payOrder.setDevice( dto.getDevice() );
        payOrder.setClientIp( dto.getClientIp() );
        payOrder.setCreateTime( dto.getCreateTime() );
        payOrder.setUpdateTime( dto.getUpdateTime() );
        payOrder.setExpireTime( dto.getExpireTime() );
        payOrder.setPaySuccessTime( dto.getPaySuccessTime() );

        return payOrder;
    }

    @Override
    public PayOrderDTO vo2dto(OrderConfirmVO vo) {
        if ( vo == null ) {
            return null;
        }

        PayOrderDTO payOrderDTO = new PayOrderDTO();

        if ( vo.getStoreId() != null ) {
            payOrderDTO.setStoreId( Long.parseLong( vo.getStoreId() ) );
        }
        payOrderDTO.setAppId( vo.getAppId() );
        payOrderDTO.setChannel( vo.getChannel() );
        payOrderDTO.setTradeNo( vo.getTradeNo() );
        payOrderDTO.setSubject( vo.getSubject() );
        payOrderDTO.setBody( vo.getBody() );
        payOrderDTO.setOpenId( vo.getOpenId() );

        return payOrderDTO;
    }
}
