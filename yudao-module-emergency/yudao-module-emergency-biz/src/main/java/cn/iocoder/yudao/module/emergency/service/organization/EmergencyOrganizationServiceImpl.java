package cn.iocoder.yudao.module.emergency.service.organization;

import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.emergency.controller.admin.organization.vo.*;
import cn.iocoder.yudao.module.emergency.convert.organization.EmergencyOrganizationConvert;
import cn.iocoder.yudao.module.emergency.dal.dataobject.organization.EmergencyOrganizationDO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.organization.EmergencyOrganizationMemberDO;
import cn.iocoder.yudao.module.emergency.dal.mysql.organization.EmergencyOrganizationMapper;
import cn.iocoder.yudao.module.emergency.dal.mysql.organization.EmergencyOrganizationMemberMapper;
import cn.iocoder.yudao.module.emergency.enums.error.ErrorCodeConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 应急组织 Service 实现类
 */
@Service
@Slf4j
public class EmergencyOrganizationServiceImpl implements EmergencyOrganizationService {

    private final EmergencyOrganizationMapper organizationMapper;
    private final EmergencyOrganizationMemberMapper memberMapper;

    public EmergencyOrganizationServiceImpl(EmergencyOrganizationMapper organizationMapper,
                                            EmergencyOrganizationMemberMapper memberMapper) {
        this.organizationMapper = organizationMapper;
        this.memberMapper = memberMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createOrganization(OrganizationCreateReqVO createReqVO) {
        // 校验组织编号唯一性
        validateOrgCodeUnique(null, createReqVO.getOrgCode());
        
        // 转换并插入
        EmergencyOrganizationDO organization = EmergencyOrganizationConvert.INSTANCE.convert(createReqVO);
        organizationMapper.insert(organization);
        return organization.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOrganization(OrganizationUpdateReqVO updateReqVO) {
        // 校验存在
        validateOrganizationExists(updateReqVO.getId());
        // 校验组织编号唯一性
        validateOrgCodeUnique(updateReqVO.getId(), updateReqVO.getOrgCode());
        
        // 更新
        EmergencyOrganizationDO updateObj = EmergencyOrganizationConvert.INSTANCE.convert(updateReqVO);
        organizationMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOrganization(Long id) {
        // 校验存在
        validateOrganizationExists(id);
        // 删除
        organizationMapper.deleteById(id);
        // 删除组织成员
        memberMapper.delete(new LambdaQueryWrapperX<EmergencyOrganizationMemberDO>()
                .eq(EmergencyOrganizationMemberDO::getOrgId, id));
    }

    private EmergencyOrganizationDO validateOrganizationExists(Long id) {
        EmergencyOrganizationDO organization = organizationMapper.selectById(id);
        if (organization == null) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.ORGANIZATION_NOT_EXISTS);
        }
        return organization;
    }

    private void validateOrgCodeUnique(Long id, String orgCode) {
        EmergencyOrganizationDO organization = organizationMapper.selectOne(
                new LambdaQueryWrapperX<EmergencyOrganizationDO>()
                        .eq(EmergencyOrganizationDO::getOrgCode, orgCode)
                        .neIfPresent(EmergencyOrganizationDO::getId, id)
                        .last("LIMIT 1"));
        if (organization != null) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.ORGANIZATION_CODE_DUPLICATE);
        }
    }

    @Override
    public OrganizationRespVO getOrganization(Long id) {
        EmergencyOrganizationDO organization = validateOrganizationExists(id);
        return EmergencyOrganizationConvert.INSTANCE.convertToOrganizationRespVO(organization);
    }

    @Override
    public PageResult<OrganizationRespVO> getOrganizationPage(OrganizationPageReqVO pageReqVO) {
        PageResult<EmergencyOrganizationDO> pageResult = organizationMapper.selectPage(pageReqVO,
                new LambdaQueryWrapperX<EmergencyOrganizationDO>()
                        .likeIfPresent(EmergencyOrganizationDO::getOrgName, pageReqVO.getOrgName())
                        .eqIfPresent(EmergencyOrganizationDO::getOrgType, pageReqVO.getOrgType())
                        .eqIfPresent(EmergencyOrganizationDO::getParentId, pageReqVO.getParentId())
                        .eqIfPresent(EmergencyOrganizationDO::getIsEnabled, pageReqVO.getIsEnabled())
                        .orderByDesc(EmergencyOrganizationDO::getId));
        return EmergencyOrganizationConvert.INSTANCE.convertToOrganizationRespVOPage(pageResult);
    }

    @Override
    public List<OrganizationRespVO> getOrganizationList(String orgType) {
        List<EmergencyOrganizationDO> list = organizationMapper.selectList(
                new LambdaQueryWrapperX<EmergencyOrganizationDO>()
                        .eqIfPresent(EmergencyOrganizationDO::getOrgType, orgType)
                        .eq(EmergencyOrganizationDO::getIsEnabled, true)
                        .orderByAsc(EmergencyOrganizationDO::getOrgName));
        return EmergencyOrganizationConvert.INSTANCE.convertToOrganizationRespVOList(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addOrganizationMember(OrganizationMemberCreateReqVO createReqVO) {
        // 校验组织存在
        validateOrganizationExists(createReqVO.getOrgId());
        // 校验成员是否已存在
        EmergencyOrganizationMemberDO existingMember = memberMapper.selectOne(
                new LambdaQueryWrapperX<EmergencyOrganizationMemberDO>()
                        .eq(EmergencyOrganizationMemberDO::getOrgId, createReqVO.getOrgId())
                        .eq(EmergencyOrganizationMemberDO::getUserId, createReqVO.getUserId())
                        .last("LIMIT 1"));
        if (existingMember != null) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.ORGANIZATION_MEMBER_EXISTS);
        }
        
        // 转换并插入
        EmergencyOrganizationMemberDO member = EmergencyOrganizationConvert.INSTANCE.convert(createReqVO);
        memberMapper.insert(member);
        return member.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeOrganizationMember(Long id) {
        // 校验存在
        EmergencyOrganizationMemberDO member = memberMapper.selectById(id);
        if (member == null) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.ORGANIZATION_MEMBER_NOT_EXISTS);
        }
        // 删除
        memberMapper.deleteById(id);
    }

    @Override
    public List<OrganizationMemberRespVO> getOrganizationMemberList(Long orgId) {
        List<EmergencyOrganizationMemberDO> list = memberMapper.selectList(
                new LambdaQueryWrapperX<EmergencyOrganizationMemberDO>()
                        .eq(EmergencyOrganizationMemberDO::getOrgId, orgId)
                        .orderByAsc(EmergencyOrganizationMemberDO::getRole));
        return EmergencyOrganizationConvert.INSTANCE.convertMemberList(list);
    }
}
