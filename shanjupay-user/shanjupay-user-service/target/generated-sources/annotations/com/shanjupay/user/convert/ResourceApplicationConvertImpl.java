package com.shanjupay.user.convert;

import com.shanjupay.user.api.dto.resource.ApplicationDTO;
import com.shanjupay.user.entity.ResourceApplication;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2019-12-24T17:41:30+0800",
    comments = "version: 1.3.0.Final, compiler: javac, environment: Java 1.8.0_202 (Oracle Corporation)"
)
public class ResourceApplicationConvertImpl implements ResourceApplicationConvert {

    @Override
    public ApplicationDTO entity2dto(ResourceApplication entity) {
        if ( entity == null ) {
            return null;
        }

        ApplicationDTO applicationDTO = new ApplicationDTO();

        applicationDTO.setId( entity.getId() );
        applicationDTO.setName( entity.getName() );
        applicationDTO.setCode( entity.getCode() );
        applicationDTO.setTenantId( entity.getTenantId() );

        return applicationDTO;
    }

    @Override
    public ResourceApplication dto2entity(ApplicationDTO dto) {
        if ( dto == null ) {
            return null;
        }

        ResourceApplication resourceApplication = new ResourceApplication();

        resourceApplication.setId( dto.getId() );
        resourceApplication.setName( dto.getName() );
        resourceApplication.setCode( dto.getCode() );
        resourceApplication.setTenantId( dto.getTenantId() );

        return resourceApplication;
    }

    @Override
    public List<ApplicationDTO> entitylist2dto(List<ResourceApplication> bundle) {
        if ( bundle == null ) {
            return null;
        }

        List<ApplicationDTO> list = new ArrayList<ApplicationDTO>( bundle.size() );
        for ( ResourceApplication resourceApplication : bundle ) {
            list.add( entity2dto( resourceApplication ) );
        }

        return list;
    }
}
