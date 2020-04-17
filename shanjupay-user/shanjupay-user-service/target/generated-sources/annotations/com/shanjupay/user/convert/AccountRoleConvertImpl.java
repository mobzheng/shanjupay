package com.shanjupay.user.convert;

import com.shanjupay.user.api.dto.tenant.AccountRoleDTO;
import com.shanjupay.user.entity.AccountRole;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2020-01-07T14:47:57+0800",
    comments = "version: 1.3.0.Final, compiler: javac, environment: Java 1.8.0_202 (Oracle Corporation)"
)
public class AccountRoleConvertImpl implements AccountRoleConvert {

    @Override
    public AccountRoleDTO entity2dto(AccountRole entity) {
        if ( entity == null ) {
            return null;
        }

        AccountRoleDTO accountRoleDTO = new AccountRoleDTO();

        accountRoleDTO.setUsername( entity.getUsername() );
        accountRoleDTO.setRoleCode( entity.getRoleCode() );
        accountRoleDTO.setTenantId( entity.getTenantId() );

        return accountRoleDTO;
    }

    @Override
    public AccountRole dto2entity(AccountRoleDTO dto) {
        if ( dto == null ) {
            return null;
        }

        AccountRole accountRole = new AccountRole();

        accountRole.setUsername( dto.getUsername() );
        accountRole.setRoleCode( dto.getRoleCode() );
        accountRole.setTenantId( dto.getTenantId() );

        return accountRole;
    }

    @Override
    public List<AccountRoleDTO> listentity2dto(List<AccountRole> app) {
        if ( app == null ) {
            return null;
        }

        List<AccountRoleDTO> list = new ArrayList<AccountRoleDTO>( app.size() );
        for ( AccountRole accountRole : app ) {
            list.add( entity2dto( accountRole ) );
        }

        return list;
    }
}
