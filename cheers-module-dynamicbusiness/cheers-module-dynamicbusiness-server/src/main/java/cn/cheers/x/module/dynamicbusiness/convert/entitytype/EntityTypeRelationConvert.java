package cn.cheers.x.module.dynamicbusiness.convert.entitytype;

import cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo.EntityTypeRelationCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo.EntityTypeRelationRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeRelationDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface EntityTypeRelationConvert {
    EntityTypeRelationConvert INSTANCE = Mappers.getMapper(EntityTypeRelationConvert.class);

    EntityTypeRelationDO convert(EntityTypeRelationCreateReqVO bean);

    EntityTypeRelationRespVO convert(EntityTypeRelationDO bean);

    List<EntityTypeRelationRespVO> convertList(List<EntityTypeRelationDO> list);
}
