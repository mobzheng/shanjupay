package com.shanjupay.merchant.convert;

import com.shanjupay.merchant.api.dto.MerchantDTO;
import com.shanjupay.merchant.entity.Merchant;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

/**
 * 定义dto和entity之间的转换规则
 * Created by Administrator.
 */
@Mapper //对象属性的映射
public interface MerchantConvert {

    //转换类实例
    MerchantConvert INSTANCE = Mappers.getMapper(MerchantConvert.class);

    //把dto转换成entity
    Merchant dto2entity(MerchantDTO merchantDTO);

    //把entity转换成dto
    MerchantDTO entity2dto(Merchant merchant);

    //list之间也可以转换，很entity的List转成MerchantDTO list
    List<MerchantDTO> entityList2dtoList(List<Merchant> merchants);


    public static void main(String[] args) {
        //将dto转成entity
        Merchant merchant  =new Merchant();
        merchant.setUsername("测试");
        merchant.setMobile("123456");
        MerchantDTO merchantDTO = MerchantConvert.INSTANCE.entity2dto(merchant);
        System.out.println(merchantDTO);

        //将entity转成dto
        merchantDTO.setMerchantName("商户名称");
        Merchant merchant1 = MerchantConvert.INSTANCE.dto2entity(merchantDTO);
        System.out.println(merchant1);

        //定义的list
        List entityList = new ArrayList();
        entityList.add(merchant);
        //将lIST转成包含dto的list
        List list = MerchantConvert.INSTANCE.entityList2dtoList(entityList);
        System.out.println(list);

    }

}
