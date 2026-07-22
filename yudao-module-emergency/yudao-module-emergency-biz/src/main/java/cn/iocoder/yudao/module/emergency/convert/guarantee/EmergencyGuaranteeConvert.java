package cn.iocoder.yudao.module.emergency.convert.guarantee;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.emergency.controller.admin.guarantee.vo.*;
import cn.iocoder.yudao.module.emergency.dal.dataobject.guarantee.EmergencyGuaranteeDO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.guarantee.EmergencyGuaranteeResourceDO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EmergencyGuaranteeConvert {

    EmergencyGuaranteeConvert INSTANCE = Mappers.getMapper(EmergencyGuaranteeConvert.class);

    EmergencyGuaranteeDO convert(GuaranteeCreateReqVO bean);

    EmergencyGuaranteeDO convert(GuaranteeUpdateReqVO bean);

    GuaranteeRespVO convert(EmergencyGuaranteeDO bean);

    PageResult<GuaranteeRespVO> convertPage(PageResult<EmergencyGuaranteeDO> page);

    EmergencyGuaranteeResourceDO convert(GuaranteeResourceCreateReqVO bean);
}



