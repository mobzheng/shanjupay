package com.shanjupay.merchant.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import com.shanjupay.common.domain.PageVO;
import com.shanjupay.common.util.PhoneUtil;
import com.shanjupay.merchant.api.MerchantService;
import com.shanjupay.merchant.api.dto.MerchantDTO;
import com.shanjupay.merchant.api.dto.StaffDTO;
import com.shanjupay.merchant.api.dto.StoreDTO;
import com.shanjupay.merchant.convert.MerchantConvert;
import com.shanjupay.merchant.convert.StaffConvert;
import com.shanjupay.merchant.convert.StoreConvert;
import com.shanjupay.merchant.entity.Merchant;
import com.shanjupay.merchant.entity.Staff;
import com.shanjupay.merchant.entity.Store;
import com.shanjupay.merchant.entity.StoreStaff;
import com.shanjupay.merchant.mapper.MerchantMapper;
import com.shanjupay.merchant.mapper.StaffMapper;
import com.shanjupay.merchant.mapper.StoreMapper;
import com.shanjupay.merchant.mapper.StoreStaffMapper;
import com.shanjupay.user.api.TenantService;
import com.shanjupay.user.api.dto.tenant.CreateTenantRequestDTO;
import com.shanjupay.user.api.dto.tenant.TenantDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Administrator.
 */
@org.apache.dubbo.config.annotation.Service
@Slf4j
public class MerchantServiceImpl implements MerchantService {

    @Autowired
    MerchantMapper merchantMapper;

    @Autowired
    StoreMapper storeMapper;

    @Autowired
    StaffMapper staffMapper;

    @Autowired
    StoreStaffMapper storeStaffMapper;

    @Reference
    TenantService tenantService;

    @Override
    public MerchantDTO queryMerchantById(Long id) {
        Merchant merchant = merchantMapper.selectById(id);
//        MerchantDTO merchantDTO = new MerchantDTO();
//        merchantDTO.setId(merchant.getId());
//        merchantDTO.setMerchantName(merchant.getMerchantName());
        //....
        return MerchantConvert.INSTANCE.entity2dto(merchant);
    }

    /**
     * 根据租户id查询商户的信息
     *
     * @param tenantId
     * @return
     */
    @Override
    public MerchantDTO queryMerchantByTenantId(Long tenantId) {
        Merchant merchant = merchantMapper.selectOne(new LambdaQueryWrapper<Merchant>().eq(Merchant::getTenantId, tenantId));
        return MerchantConvert.INSTANCE.entity2dto(merchant);
    }

    /**
     * 注册商户服务接口，接收账号、密码、手机号，为了可扩展性使用merchantDto接收数据
     * 调用SaaS接口：新增租户、用户、绑定租户和用户的关系，初始化权限
     * @param merchantDTO 商户注册信息
     * @return 注册成功的商户信息
     */
    @Override
    public MerchantDTO createMerchant(MerchantDTO merchantDTO) throws BusinessException {
        //校验参数的合法性
        if(merchantDTO == null){
            throw new BusinessException(CommonErrorCode.E_100108);
        }
        if(StringUtils.isBlank(merchantDTO.getMobile())){
            throw new BusinessException(CommonErrorCode.E_100112);
        }
        if(StringUtils.isBlank(merchantDTO.getPassword())){
            throw new BusinessException(CommonErrorCode.E_100111);
        }
        //手机号格式校验
        if(!PhoneUtil.isMatches(merchantDTO.getMobile())){
            throw new BusinessException(CommonErrorCode.E_100109);
        }
        //校验手机号的唯一性
        //根据手机号查询商户表，如果存在记录则说明手机号已存在
        Integer count = merchantMapper.selectCount(new LambdaQueryWrapper<Merchant>().eq(Merchant::getMobile, merchantDTO.getMobile()));
        if(count>0){
            throw new BusinessException(CommonErrorCode.E_100113);
        }

        //调用SaaS接口
        //构建调用参数
        /**
         1、手机号

         2、账号

         3、密码

         4、租户类型：shanju-merchant

         5、默认套餐：shanju-merchant

         6、租户名称，同账号名

         */
        CreateTenantRequestDTO createTenantRequestDTO = new CreateTenantRequestDTO();
        createTenantRequestDTO.setMobile(merchantDTO.getMobile());
        createTenantRequestDTO.setUsername(merchantDTO.getUsername());
        createTenantRequestDTO.setPassword(merchantDTO.getPassword());
        createTenantRequestDTO.setTenantTypeCode("shanju-merchant");//租户类型
        createTenantRequestDTO.setBundleCode("shanju-merchant");//套餐，根据套餐进行分配权限
        createTenantRequestDTO.setName(merchantDTO.getUsername());//租户名称，和账号名一样

        //如果租户在SaaS已经存在，SaaS直接 返回此租户的信息，否则进行添加
        TenantDTO tenantAndAccount = tenantService.createTenantAndAccount(createTenantRequestDTO);
        //获取租户的id
        if(tenantAndAccount == null || tenantAndAccount.getId() == null){
            throw new BusinessException(CommonErrorCode.E_200012);
        }
        //租户的id
        Long tenantId = tenantAndAccount.getId();

        //租户id在商户表唯一
        //根据租户id从商户表查询，如果存在记录则不允许添加商户
        Integer count1 = merchantMapper.selectCount(new LambdaQueryWrapper<Merchant>().eq(Merchant::getTenantId, tenantId));
        if(count1>0){
            throw new BusinessException(CommonErrorCode.E_200017);
        }


//        Merchant merchant = new Merchant();
//        merchant.setMobile(merchantDTO.getMobile());
        //..写入其它属性
        //使用MapStruct进行对象转换
        Merchant merchant = MerchantConvert.INSTANCE.dto2entity(merchantDTO);
        //设置所对应的租户的Id
        merchant.setTenantId(tenantId);
        //审核状态为0-未进行资质申请
        merchant.setAuditStatus("0");
        //调用mapper向数据库写入记录
        merchantMapper.insert(merchant);

        //新增门店
        StoreDTO storeDTO = new StoreDTO();
        storeDTO.setStoreName("根门店");
        storeDTO.setMerchantId(merchant.getId());//商户id
        StoreDTO store = createStore(storeDTO);

        //新增员工
        StaffDTO staffDTO = new StaffDTO();
        staffDTO.setMobile(merchantDTO.getMobile());//手机号
        staffDTO.setUsername(merchantDTO.getUsername());//账号
        staffDTO.setStoreId(store.getId());//员所属门店id
        staffDTO.setMerchantId(merchant.getId());//商户id

        StaffDTO staff = createStaff(staffDTO);

        //为门店设置管理员
        bindStaffToStore(store.getId(),staff.getId());

        //将dto中写入新增商户的id
//        merchantDTO.setId(merchant.getId());
        //将entity转成dto
        return MerchantConvert.INSTANCE.entity2dto(merchant);
    }

    /**
     * 资质申请接口
     *
     * @param merchantId  商户id
     * @param merchantDTO 资质申请的信息
     * @throws BusinessException
     */
    @Override
    @Transactional
    public void applyMerchant(Long merchantId, MerchantDTO merchantDTO) throws BusinessException {
        if(merchantId == null || merchantDTO == null){
            throw new BusinessException(CommonErrorCode.E_300009);
        }
        //校验merchantId合法性，查询商户表，如果查询不到记录，认为非法
        Merchant merchant = merchantMapper.selectById(merchantId);
        if(merchant == null){
            throw new BusinessException(CommonErrorCode.E_200002);
        }
        //将dto转成entity
        Merchant entity = MerchantConvert.INSTANCE.dto2entity(merchantDTO);
        //将必要的参数设置到entity
        entity.setId(merchant.getId());
        entity.setMobile(merchant.getMobile());//因为资质申请的时候手机号不让改，还使用数据库中原来的手机号
        entity.setAuditStatus("1");//审核状态1-已申请待审核
        entity.setTenantId(merchant.getTenantId());
        //调用mapper更新商户表
        merchantMapper.updateById(entity);
    }

    /**
     * 新增门店
     *
     * @param storeDTO 门店信息
     * @return 新增成功的门店信息
     * @throws BusinessException
     */
    @Override
    public StoreDTO createStore(StoreDTO storeDTO) throws BusinessException {
        Store entity = StoreConvert.INSTANCE.dto2entity(storeDTO);
        log.info("新增门店：{}", JSON.toJSONString(entity));
        //新增门店
        storeMapper.insert(entity);

        return StoreConvert.INSTANCE.entity2dto(entity);
    }

    /**
     * 新增员工
     *
     * @param staffDTO 员工信息
     * @return 新增成功的员工信息
     * @throws BusinessException
     */
    @Override
    public StaffDTO createStaff(StaffDTO staffDTO) throws BusinessException {

        //参数合法性校验
        if(staffDTO ==  null || StringUtils.isBlank(staffDTO.getMobile())
                || StringUtils.isBlank(staffDTO.getUsername())
                || staffDTO.getStoreId() == null){
            throw new BusinessException(CommonErrorCode.E_300009);

        }

        //在同一个商户下员工的账号唯一
        Boolean existStaffByUserName = isExistStaffByUserName(staffDTO.getUsername(), staffDTO.getMerchantId());
        if(existStaffByUserName){
            throw new BusinessException(CommonErrorCode.E_100114);
        }

        //在同一个商户下员工的手机号唯一
        Boolean existStaffByMobile = isExistStaffByMobile(staffDTO.getMobile(), staffDTO.getMerchantId());
        if(existStaffByMobile){
            throw new BusinessException(CommonErrorCode.E_100113);
        }
        Staff staff = StaffConvert.INSTANCE.dto2entity(staffDTO);
        staffMapper.insert(staff);

        return StaffConvert.INSTANCE.entity2dto(staff);
    }

    /**
     * 将员工设置为门店的管理员
     *
     * @param storeId
     * @param staffId
     * @throws BusinessException
     */
    @Override
    public void bindStaffToStore(Long storeId, Long staffId) throws BusinessException {
        StoreStaff storeStaff = new StoreStaff();
        storeStaff.setStaffId(staffId);//员工id
        storeStaff.setStoreId(storeId);//门店id
        storeStaffMapper.insert(storeStaff);
    }

    /**
     * 门店列表的查询
     *
     * @param storeDTO 查询条件，必要参数：商户id
     * @param pageNo   页码
     * @param pageSize 分页记录数
     * @return
     */
    @Override
    public PageVO<StoreDTO> queryStoreByPage(StoreDTO storeDTO, Integer pageNo, Integer pageSize) {
        //分页条件
        Page<Store> page = new Page<>(pageNo, pageSize);
        //查询条件拼装
        LambdaQueryWrapper<Store> lambdaQueryWrapper = new LambdaQueryWrapper<Store>();
        //如果 传入商户id，此时要拼装 查询条件
        if(storeDTO !=null && storeDTO.getMerchantId()!=null){
            lambdaQueryWrapper.eq(Store::getMerchantId,storeDTO.getMerchantId());
        }
        //再拼装其它查询条件 ，比如：门店名称
        if(storeDTO !=null && StringUtils.isNotEmpty(storeDTO.getStoreName())){
            lambdaQueryWrapper.eq(Store::getStoreName,storeDTO.getStoreName());
        }

        //分页查询数据库
        IPage<Store> storeIPage = storeMapper.selectPage(page, lambdaQueryWrapper);
        //查询列表
        List<Store> records = storeIPage.getRecords();
        //将包含entity的list转成包含dto的list
        List<StoreDTO> storeDTOS = StoreConvert.INSTANCE.listentity2dto(records);
        return new PageVO(storeDTOS,storeIPage.getTotal(),pageNo,pageSize);
    }

    /**
     * 校验门店是否属于商户
     *
     * @param storeId
     * @param merchantId
     * @return true存在，false不存在
     */
    @Override
    public Boolean queryStoreInMerchant(Long storeId, Long merchantId) {

        Integer count = storeMapper.selectCount(new LambdaQueryWrapper<Store>().eq(Store::getId, storeId)
                .eq(Store::getMerchantId, merchantId));
        return count>0;
    }

    /**
     * 员工手机号在同一个商户下是唯一校验
     * @param mobile
     * @param merchantId
     * @return
     */
    Boolean isExistStaffByMobile(String mobile,Long merchantId){
        Integer count = staffMapper.selectCount(new LambdaQueryWrapper<Staff>().eq(Staff::getMobile, mobile)
                .eq(Staff::getMerchantId, merchantId));
        return count>0;
    }
    /**
     * 员工账号在同一个商户下是唯一校验
     * @param username
     * @param merchantId
     * @return
     */
    Boolean isExistStaffByUserName(String username,Long merchantId){
        Integer count = staffMapper.selectCount(new LambdaQueryWrapper<Staff>().eq(Staff::getUsername,username)
                .eq(Staff::getMerchantId, merchantId));
        return count>0;
    }
}
