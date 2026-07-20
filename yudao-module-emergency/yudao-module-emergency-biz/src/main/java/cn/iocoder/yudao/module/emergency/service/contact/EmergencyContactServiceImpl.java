package cn.iocoder.yudao.module.emergency.service.contact;

import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.framework.common.pojo.PageParam;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.emergency.controller.admin.contact.vo.*;
import cn.iocoder.yudao.module.emergency.convert.contact.EmergencyContactConvert;
import cn.iocoder.yudao.module.emergency.dal.dataobject.contact.EmergencyContactDO;
import cn.iocoder.yudao.module.emergency.dal.mysql.contact.EmergencyContactMapper;
import cn.iocoder.yudao.module.emergency.enums.error.ErrorCodeConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 应急联络通讯录 Service 实现类
 */
@Service
@Slf4j
public class EmergencyContactServiceImpl implements EmergencyContactService {

    private final EmergencyContactMapper contactMapper;

    public EmergencyContactServiceImpl(EmergencyContactMapper contactMapper) {
        this.contactMapper = contactMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createContact(ContactCreateReqVO createReqVO) {
        // 校验联系人编号唯一性
        validateContactCodeUnique(null, createReqVO.getContactCode());
        
        // 转换并插入
        EmergencyContactDO contact = EmergencyContactConvert.INSTANCE.convert(createReqVO);
        if (contact.getIsEnabled() == null) {
            contact.setIsEnabled(true);
        }
        contactMapper.insert(contact);
        return contact.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateContact(ContactUpdateReqVO updateReqVO) {
        // 校验存在
        validateContactExists(updateReqVO.getId());
        // 校验联系人编号唯一性
        validateContactCodeUnique(updateReqVO.getId(), updateReqVO.getContactCode());
        
        // 更新
        EmergencyContactDO updateObj = EmergencyContactConvert.INSTANCE.convert(updateReqVO);
        contactMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteContact(Long id) {
        // 校验存在
        validateContactExists(id);
        // 删除
        contactMapper.deleteById(id);
    }

    private EmergencyContactDO validateContactExists(Long id) {
        EmergencyContactDO contact = contactMapper.selectById(id);
        if (contact == null) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.CONTACT_NOT_EXISTS);
        }
        return contact;
    }

    private void validateContactCodeUnique(Long id, String contactCode) {
        EmergencyContactDO contact = contactMapper.selectOne(
                new LambdaQueryWrapperX<EmergencyContactDO>()
                        .eq(EmergencyContactDO::getContactCode, contactCode)
                        .neIfPresent(EmergencyContactDO::getId, id)
                        .last("LIMIT 1"));
        if (contact != null) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.CONTACT_CODE_DUPLICATE);
        }
    }

    @Override
    public ContactRespVO getContact(Long id) {
        EmergencyContactDO contact = validateContactExists(id);
        return EmergencyContactConvert.INSTANCE.convert(contact);
    }

    @Override
    public PageResult<ContactRespVO> getContactPage(ContactPageReqVO pageReqVO) {
        PageResult<EmergencyContactDO> pageResult = contactMapper.selectPage(pageReqVO,
                new LambdaQueryWrapperX<EmergencyContactDO>()
                        .likeIfPresent(EmergencyContactDO::getContactName, pageReqVO.getContactName())
                        .eqIfPresent(EmergencyContactDO::getContactType, pageReqVO.getContactType())
                        .likeIfPresent(EmergencyContactDO::getOrganization, pageReqVO.getOrganization())
                        .eqIfPresent(EmergencyContactDO::getEmergencyLevel, pageReqVO.getEmergencyLevel())
                        .eqIfPresent(EmergencyContactDO::getIsEnabled, pageReqVO.getIsEnabled())
                        .orderByDesc(EmergencyContactDO::getId));
        return EmergencyContactConvert.INSTANCE.convertPage(pageResult);
    }

    @Override
    public PageResult<ContactRespVO> searchContact(String keyword, String contactType) {
        PageParam pageParam = new PageParam();
        pageParam.setPageNo(1);
        pageParam.setPageSize(100);
        
        LambdaQueryWrapperX<EmergencyContactDO> queryWrapper = new LambdaQueryWrapperX<EmergencyContactDO>()
                .eqIfPresent(EmergencyContactDO::getContactType, contactType)
                .eq(EmergencyContactDO::getIsEnabled, true);
        
        // 如果有关键词，使用like查询多个字段（简化处理，主要查询联系人姓名和组织）
        if (keyword != null && !keyword.isEmpty()) {
            queryWrapper.nested(wrapper -> wrapper
                    .like(EmergencyContactDO::getContactName, keyword)
                    .or().like(EmergencyContactDO::getOrganization, keyword)
                    .or().like(EmergencyContactDO::getDepartment, keyword)
                    .or().like(EmergencyContactDO::getPhone, keyword)
                    .or().like(EmergencyContactDO::getMobile, keyword));
        }
        
        queryWrapper.orderByDesc(EmergencyContactDO::getEmergencyLevel)
                .orderByAsc(EmergencyContactDO::getContactName);
        
        PageResult<EmergencyContactDO> pageResult = contactMapper.selectPage(pageParam, queryWrapper);
        return EmergencyContactConvert.INSTANCE.convertPage(pageResult);
    }
}

