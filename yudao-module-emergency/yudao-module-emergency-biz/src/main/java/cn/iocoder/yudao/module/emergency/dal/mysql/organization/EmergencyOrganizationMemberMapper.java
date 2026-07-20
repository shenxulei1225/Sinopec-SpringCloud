package cn.iocoder.yudao.module.emergency.dal.mysql.organization;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.emergency.controller.admin.organization.vo.member.OrganizationMemberPageReqVO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.organization.EmergencyOrganizationMemberDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 应急组织成员 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface EmergencyOrganizationMemberMapper extends BaseMapperX<EmergencyOrganizationMemberDO> {

    default PageResult<EmergencyOrganizationMemberDO> selectPage(OrganizationMemberPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<EmergencyOrganizationMemberDO>()
                .eqIfPresent(EmergencyOrganizationMemberDO::getOrgId, reqVO.getOrgId())
                .eqIfPresent(EmergencyOrganizationMemberDO::getUserId, reqVO.getUserId())
                .eqIfPresent(EmergencyOrganizationMemberDO::getRole, reqVO.getRole())
                .eqIfPresent(EmergencyOrganizationMemberDO::getIsEnabled, reqVO.getIsEnabled())
                .orderByDesc(EmergencyOrganizationMemberDO::getId));
    }
}












































