package cn.iocoder.yudao.module.emergency.convert.contact;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.emergency.controller.admin.contact.vo.*;
import cn.iocoder.yudao.module.emergency.dal.dataobject.contact.EmergencyContactDO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EmergencyContactConvert {

    EmergencyContactConvert INSTANCE = Mappers.getMapper(EmergencyContactConvert.class);

    EmergencyContactDO convert(ContactCreateReqVO bean);

    EmergencyContactDO convert(ContactUpdateReqVO bean);

    ContactRespVO convert(EmergencyContactDO bean);

    PageResult<ContactRespVO> convertPage(PageResult<EmergencyContactDO> page);
}



