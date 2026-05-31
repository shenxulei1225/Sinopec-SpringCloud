package cn.cheers.x.module.dynamicbusiness.convert.relation;

import cn.cheers.x.module.dynamicbusiness.controller.admin.relation.vo.ModelRelationDeclarationCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.relation.vo.ModelRelationDeclarationRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.relation.ModelRelationDeclarationDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Model 关联声明 Convert
 * 
 * @author yudao
 */
@Mapper
public interface ModelRelationDeclarationConvert {

    ModelRelationDeclarationConvert INSTANCE = Mappers.getMapper(ModelRelationDeclarationConvert.class);

    /**
     * 创建请求 VO 转 DO
     */
    ModelRelationDeclarationDO convert(ModelRelationDeclarationCreateReqVO reqVO);

    /**
     * DO 转响应 VO
     * 
     * 注意：targetBusinessTypeName 需要在 Service 层设置
     */
    default ModelRelationDeclarationRespVO convert(ModelRelationDeclarationDO declaration) {
        if (declaration == null) {
            return null;
        }
        ModelRelationDeclarationRespVO vo = new ModelRelationDeclarationRespVO();
        vo.setId(declaration.getId());
        vo.setModelId(declaration.getModelId());
        vo.setTargetBusinessType(declaration.getTargetBusinessType());
        vo.setCreateTime(declaration.getCreateTime());
        // targetBusinessTypeName 需要在 Service 层设置
        return vo;
    }

    /**
     * DO 列表转响应 VO 列表
     */
    default List<ModelRelationDeclarationRespVO> convertList(List<ModelRelationDeclarationDO> list) {
        if (list == null) {
            return null;
        }
        return list.stream().map(this::convert).toList();
    }
}
