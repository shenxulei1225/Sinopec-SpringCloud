package cn.cheers.x.module.dynamicbusiness.convert.businesstype;

import cn.cheers.x.module.dynamicbusiness.controller.admin.businesstype.vo.BusinessTypeRelationCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.businesstype.vo.BusinessTypeRelationRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.businesstype.BusinessTypeRelationDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface BusinessTypeRelationConvert {
    BusinessTypeRelationConvert INSTANCE = Mappers.getMapper(BusinessTypeRelationConvert.class);

    BusinessTypeRelationDO convert(BusinessTypeRelationCreateReqVO bean);

    BusinessTypeRelationRespVO convert(BusinessTypeRelationDO bean);

    List<BusinessTypeRelationRespVO> convertList(List<BusinessTypeRelationDO> list);
}
