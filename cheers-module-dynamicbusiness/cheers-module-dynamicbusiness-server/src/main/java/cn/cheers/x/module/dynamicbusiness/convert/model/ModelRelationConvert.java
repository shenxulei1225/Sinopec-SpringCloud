package cn.cheers.x.module.dynamicbusiness.convert.model;

import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelRelationRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelRelationDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Model 关联 Convert
 * 
 * @author yudao
 */
@Mapper
public interface ModelRelationConvert {

    ModelRelationConvert INSTANCE = Mappers.getMapper(ModelRelationConvert.class);

    ModelRelationRespVO convert(ModelRelationDO bean);

    List<ModelRelationRespVO> convertList(List<ModelRelationDO> list);
}
