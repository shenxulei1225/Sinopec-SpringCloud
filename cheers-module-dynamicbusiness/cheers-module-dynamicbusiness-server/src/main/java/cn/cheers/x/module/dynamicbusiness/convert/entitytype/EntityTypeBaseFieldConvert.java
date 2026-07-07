package cn.cheers.x.module.dynamicbusiness.convert.businesstype;

import cn.cheers.x.module.dynamicbusiness.controller.admin.businesstype.vo.BusinessTypeBaseFieldRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.businesstype.vo.BusinessTypeBaseFieldSaveReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.businesstype.BusinessTypeBaseFieldDO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface BusinessTypeBaseFieldConvert {
    BusinessTypeBaseFieldConvert INSTANCE = Mappers.getMapper(BusinessTypeBaseFieldConvert.class);

    BusinessTypeBaseFieldDO convert(BusinessTypeBaseFieldSaveReqVO bean);

    BusinessTypeBaseFieldRespVO convert(BusinessTypeBaseFieldDO bean);

    List<BusinessTypeBaseFieldRespVO> convertList(List<BusinessTypeBaseFieldDO> list);

    void update(@MappingTarget BusinessTypeBaseFieldDO target, BusinessTypeBaseFieldSaveReqVO source);
}
