package cn.cheers.x.module.dynamicbusiness.convert.entity;

import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EntityConvert {

    EntityConvert INSTANCE = Mappers.getMapper(EntityConvert.class);

    EntityDO convert(EntityCreateReqVO bean);

    EntityDO convert(EntityUpdateReqVO bean);

    EntityRespVO convert(EntityDO bean);

    List<EntityRespVO> convertList(List<EntityDO> list);
}












































