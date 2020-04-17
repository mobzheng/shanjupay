package com.shanjupay.merchant.convert;

import com.shanjupay.merchant.api.dto.StaffDTO;
import com.shanjupay.merchant.entity.Staff;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2020-02-03T15:28:09+0800",
    comments = "version: 1.3.0.Final, compiler: javac, environment: Java 1.8.0_202 (Oracle Corporation)"
)
public class StaffConvertImpl implements StaffConvert {

    @Override
    public StaffDTO entity2dto(Staff entity) {
        if ( entity == null ) {
            return null;
        }

        StaffDTO staffDTO = new StaffDTO();

        staffDTO.setId( entity.getId() );
        staffDTO.setMerchantId( entity.getMerchantId() );
        staffDTO.setFullName( entity.getFullName() );
        staffDTO.setPosition( entity.getPosition() );
        staffDTO.setUsername( entity.getUsername() );
        staffDTO.setMobile( entity.getMobile() );
        staffDTO.setStoreId( entity.getStoreId() );
        staffDTO.setLastLoginTime( entity.getLastLoginTime() );
        staffDTO.setStaffStatus( entity.getStaffStatus() );

        return staffDTO;
    }

    @Override
    public Staff dto2entity(StaffDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Staff staff = new Staff();

        staff.setId( dto.getId() );
        staff.setMerchantId( dto.getMerchantId() );
        staff.setFullName( dto.getFullName() );
        staff.setPosition( dto.getPosition() );
        staff.setUsername( dto.getUsername() );
        staff.setMobile( dto.getMobile() );
        staff.setStoreId( dto.getStoreId() );
        staff.setLastLoginTime( dto.getLastLoginTime() );
        staff.setStaffStatus( dto.getStaffStatus() );

        return staff;
    }

    @Override
    public List<StaffDTO> listentity2dto(List<Staff> staff) {
        if ( staff == null ) {
            return null;
        }

        List<StaffDTO> list = new ArrayList<StaffDTO>( staff.size() );
        for ( Staff staff1 : staff ) {
            list.add( entity2dto( staff1 ) );
        }

        return list;
    }
}
