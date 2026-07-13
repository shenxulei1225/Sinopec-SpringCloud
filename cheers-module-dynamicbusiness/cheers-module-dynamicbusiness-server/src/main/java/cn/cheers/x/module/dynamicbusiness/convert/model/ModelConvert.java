package cn.cheers.x.module.dynamicbusiness.convert.model;

import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 业务模型 Convert
 * 
 * @author yudao
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ModelConvert {

    ModelConvert INSTANCE = Mappers.getMapper(ModelConvert.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true)
    ModelDO convert(ModelCreateReqVO bean);

    @Mapping(target = "code", ignore = true)
    ModelDO convert(ModelUpdateReqVO bean);

    /**
     * DO 转响应 VO
     * 
     * 注意：以下字段需要在 Service 层设置：
     * - canCreateEntity: 是否可以创建实体（现在所有 Model 都可以）
     */
    default ModelRespVO convert(ModelDO bean) {
        if (bean == null) {
            return null;
        }
        ModelRespVO vo = new ModelRespVO();
        vo.setId(bean.getId());
        vo.setCode(bean.getCode());
        vo.setName(bean.getName());
        vo.setEntityTypeCode(bean.getEntityTypeCode());
        vo.setDescription(bean.getDescription());
        vo.setStatus(bean.getStatus());
        vo.setSort(bean.getSort());
        vo.setDataScope(bean.getDataScope());
        vo.setCreateTime(bean.getCreateTime());
        vo.setUpdateTime(bean.getUpdateTime());
        
        // 以下字段需要在 Service 层设置
        // vo.setCategoryId(...)
        
        return vo;
    }

    default List<ModelRespVO> convertList(List<ModelDO> list) {
        if (list == null) {
            return null;
        }
        return list.stream().map(this::convert).toList();
    }
}

