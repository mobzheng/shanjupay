package com.shanjupay.user.convert;

import com.shanjupay.user.api.dto.tenant.BundleDTO;
import com.shanjupay.user.entity.Bundle;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2019-12-24T17:41:30+0800",
    comments = "version: 1.3.0.Final, compiler: javac, environment: Java 1.8.0_202 (Oracle Corporation)"
)
public class BundleConvertImpl implements BundleConvert {

    @Override
    public BundleDTO entity2dto(Bundle entity) {
        if ( entity == null ) {
            return null;
        }

        BundleDTO bundleDTO = new BundleDTO();

        bundleDTO.setId( entity.getId() );
        bundleDTO.setName( entity.getName() );
        bundleDTO.setCode( entity.getCode() );
        bundleDTO.setTenantTypeCode( entity.getTenantTypeCode() );
        bundleDTO.setAbility( entity.getAbility() );
        bundleDTO.setNumberOfInvocation( entity.getNumberOfInvocation() );
        bundleDTO.setNumberOfConcurrent( entity.getNumberOfConcurrent() );
        bundleDTO.setNumberOfApp( entity.getNumberOfApp() );
        bundleDTO.setComment( entity.getComment() );
        bundleDTO.setInitialize( entity.getInitialize() );

        return bundleDTO;
    }

    @Override
    public Bundle dto2entity(BundleDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Bundle bundle = new Bundle();

        bundle.setId( dto.getId() );
        bundle.setName( dto.getName() );
        bundle.setCode( dto.getCode() );
        bundle.setTenantTypeCode( dto.getTenantTypeCode() );
        bundle.setAbility( dto.getAbility() );
        bundle.setNumberOfInvocation( dto.getNumberOfInvocation() );
        bundle.setNumberOfConcurrent( dto.getNumberOfConcurrent() );
        bundle.setNumberOfApp( dto.getNumberOfApp() );
        bundle.setComment( dto.getComment() );
        bundle.setInitialize( dto.getInitialize() );

        return bundle;
    }

    @Override
    public List<BundleDTO> entitylist2dto(List<Bundle> bundle) {
        if ( bundle == null ) {
            return null;
        }

        List<BundleDTO> list = new ArrayList<BundleDTO>( bundle.size() );
        for ( Bundle bundle1 : bundle ) {
            list.add( entity2dto( bundle1 ) );
        }

        return list;
    }
}
