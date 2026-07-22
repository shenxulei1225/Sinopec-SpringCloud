package cn.iocoder.yudao.module.emergency.convert.organization;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.emergency.controller.admin.organization.vo.abrole.ABRoleCreateReqVO;
import cn.iocoder.yudao.module.emergency.controller.admin.organization.vo.abrole.ABRoleRespVO;
import cn.iocoder.yudao.module.emergency.controller.admin.organization.vo.abrole.ABRoleUpdateReqVO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.organization.EmergencyABRoleDO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ABRoleConvert {

    ABRoleConvert INSTANCE = Mappers.getMapper(ABRoleConvert.class);

    EmergencyABRoleDO convert(ABRoleCreateReqVO bean);

    EmergencyABRoleDO convert(ABRoleUpdateReqVO bean);

    ABRoleRespVO convert(EmergencyABRoleDO bean);

    List<ABRoleRespVO> convertList(List<EmergencyABRoleDO> list);

    PageResult<ABRoleRespVO> convertPage(PageResult<EmergencyABRoleDO> page);
}
