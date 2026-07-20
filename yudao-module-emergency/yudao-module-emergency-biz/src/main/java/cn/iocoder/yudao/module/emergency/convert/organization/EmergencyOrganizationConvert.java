package cn.iocoder.yudao.module.emergency.convert.organization;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.emergency.controller.admin.organization.vo.*;
import cn.iocoder.yudao.module.emergency.dal.dataobject.organization.EmergencyOrganizationDO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.organization.EmergencyOrganizationMemberDO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 应急组织架构 Convert
 *
 * @author 芋道源码
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EmergencyOrganizationConvert {

    EmergencyOrganizationConvert INSTANCE = Mappers.getMapper(EmergencyOrganizationConvert.class);

    EmergencyOrganizationDO convert(EmergencyOrganizationCreateReqVO bean);

    EmergencyOrganizationDO convert(EmergencyOrganizationUpdateReqVO bean);

    EmergencyOrganizationDO convert(OrganizationCreateReqVO bean);

    EmergencyOrganizationDO convert(OrganizationUpdateReqVO bean);

    EmergencyOrganizationMemberDO convert(OrganizationMemberCreateReqVO bean);

    EmergencyOrganizationRespVO convert(EmergencyOrganizationDO bean);

    OrganizationRespVO convertToOrganizationRespVO(EmergencyOrganizationDO bean);

    List<EmergencyOrganizationRespVO> convertList(List<EmergencyOrganizationDO> list);

    List<OrganizationRespVO> convertToOrganizationRespVOList(List<EmergencyOrganizationDO> list);

    PageResult<EmergencyOrganizationRespVO> convertPage(PageResult<EmergencyOrganizationDO> page);

    PageResult<OrganizationRespVO> convertToOrganizationRespVOPage(PageResult<EmergencyOrganizationDO> page);

    List<OrganizationMemberRespVO> convertMemberList(List<EmergencyOrganizationMemberDO> list);
}









