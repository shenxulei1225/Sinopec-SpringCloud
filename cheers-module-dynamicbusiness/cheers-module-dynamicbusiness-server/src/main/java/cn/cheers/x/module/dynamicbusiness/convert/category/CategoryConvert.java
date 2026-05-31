package cn.cheers.x.module.dynamicbusiness.convert.category;

import cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo.CategoryCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo.CategoryRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo.CategoryTreeRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo.CategoryUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.category.CategoryDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryConvert {

    CategoryConvert INSTANCE = Mappers.getMapper(CategoryConvert.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "level", ignore = true)
    @Mapping(target = "treePath", ignore = true)
    CategoryDO convert(CategoryCreateReqVO bean);

    @Mapping(target = "code", ignore = true)
    @Mapping(target = "level", ignore = true)
    @Mapping(target = "treePath", ignore = true)
    CategoryDO convert(CategoryUpdateReqVO bean);

    @Mapping(target = "customFields", ignore = true)  // customFields 需要从关联的 Entity 中获取
    CategoryRespVO convert(CategoryDO bean);

    List<CategoryRespVO> convertList(List<CategoryDO> list);

    CategoryTreeRespVO convertTree(CategoryDO bean);

    List<CategoryTreeRespVO> convertTreeList(List<CategoryDO> list);
}

