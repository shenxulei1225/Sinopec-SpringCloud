package cn.iocoder.yudao.module.emergency.convert.response;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.emergency.controller.admin.response.vo.ResponseRespVO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.response.EmergencyResponseDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface EmergencyResponseConvert {

    EmergencyResponseConvert INSTANCE = Mappers.getMapper(EmergencyResponseConvert.class);

    ResponseRespVO convert(EmergencyResponseDO response);

    PageResult<ResponseRespVO> convertPage(PageResult<EmergencyResponseDO> page);
}

