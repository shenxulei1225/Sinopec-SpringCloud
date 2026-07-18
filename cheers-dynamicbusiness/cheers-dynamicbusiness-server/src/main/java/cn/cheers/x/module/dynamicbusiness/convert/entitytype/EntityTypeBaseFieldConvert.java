package cn.cheers.x.module.dynamicbusiness.convert.entitytype;

import cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo.EntityTypeBaseFieldRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo.EntityTypeBaseFieldSaveReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeBaseFieldDO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface EntityTypeBaseFieldConvert {
    EntityTypeBaseFieldConvert INSTANCE = Mappers.getMapper(EntityTypeBaseFieldConvert.class);

    EntityTypeBaseFieldDO convert(EntityTypeBaseFieldSaveReqVO bean);

    EntityTypeBaseFieldRespVO convert(EntityTypeBaseFieldDO bean);

    List<EntityTypeBaseFieldRespVO> convertList(List<EntityTypeBaseFieldDO> list);

    void update(@MappingTarget EntityTypeBaseFieldDO target, EntityTypeBaseFieldSaveReqVO source);
}
