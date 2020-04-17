package com.shanjupay.user.convert;

import com.shanjupay.user.api.dto.menu.MenuDTO;
import com.shanjupay.user.entity.ResourceMenu;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2019-12-24T17:41:31+0800",
    comments = "version: 1.3.0.Final, compiler: javac, environment: Java 1.8.0_202 (Oracle Corporation)"
)
public class ResourceMenuConvertImpl implements ResourceMenuConvert {

    @Override
    public MenuDTO entity2dto(ResourceMenu entity) {
        if ( entity == null ) {
            return null;
        }

        MenuDTO menuDTO = new MenuDTO();

        menuDTO.setId( entity.getId() );
        menuDTO.setParentId( entity.getParentId() );
        menuDTO.setTitle( entity.getTitle() );
        menuDTO.setUrl( entity.getUrl() );
        menuDTO.setIcon( entity.getIcon() );
        menuDTO.setSort( entity.getSort() );
        menuDTO.setComment( entity.getComment() );
        menuDTO.setApplicationCode( entity.getApplicationCode() );
        menuDTO.setPrivilegeCode( entity.getPrivilegeCode() );
        menuDTO.setStatus( entity.getStatus() );

        return menuDTO;
    }

    @Override
    public ResourceMenu dto2entity(MenuDTO dto) {
        if ( dto == null ) {
            return null;
        }

        ResourceMenu resourceMenu = new ResourceMenu();

        resourceMenu.setId( dto.getId() );
        resourceMenu.setParentId( dto.getParentId() );
        resourceMenu.setTitle( dto.getTitle() );
        resourceMenu.setUrl( dto.getUrl() );
        resourceMenu.setIcon( dto.getIcon() );
        resourceMenu.setSort( dto.getSort() );
        resourceMenu.setComment( dto.getComment() );
        resourceMenu.setStatus( dto.getStatus() );
        resourceMenu.setApplicationCode( dto.getApplicationCode() );
        resourceMenu.setPrivilegeCode( dto.getPrivilegeCode() );

        return resourceMenu;
    }

    @Override
    public List<MenuDTO> entitylist2dto(List<ResourceMenu> resourceMenu) {
        if ( resourceMenu == null ) {
            return null;
        }

        List<MenuDTO> list = new ArrayList<MenuDTO>( resourceMenu.size() );
        for ( ResourceMenu resourceMenu1 : resourceMenu ) {
            list.add( entity2dto( resourceMenu1 ) );
        }

        return list;
    }

    @Override
    public List<ResourceMenu> dtolist2entity(List<MenuDTO> menuDTO) {
        if ( menuDTO == null ) {
            return null;
        }

        List<ResourceMenu> list = new ArrayList<ResourceMenu>( menuDTO.size() );
        for ( MenuDTO menuDTO1 : menuDTO ) {
            list.add( dto2entity( menuDTO1 ) );
        }

        return list;
    }
}
