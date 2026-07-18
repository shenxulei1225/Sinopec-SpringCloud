package cn.cheers.x.module.dynamicbusiness.convert.field;

import cn.cheers.x.module.dynamicbusiness.controller.admin.field.vo.FieldCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.field.vo.FieldRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.field.vo.FieldUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FieldConvert {

    FieldConvert INSTANCE = Mappers.getMapper(FieldConvert.class);

    FieldDO convert(FieldCreateReqVO bean);

    FieldDO convert(FieldUpdateReqVO bean);

    FieldRespVO convert(FieldDO bean);

    List<FieldRespVO> convertList(List<FieldDO> list);
}

