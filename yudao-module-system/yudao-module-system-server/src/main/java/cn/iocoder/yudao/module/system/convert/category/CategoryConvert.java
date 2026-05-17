package cn.iocoder.yudao.module.system.convert.category;

import cn.iocoder.yudao.module.system.controller.admin.category.vo.CategoryCreateReqVO;
import cn.iocoder.yudao.module.system.controller.admin.category.vo.CategoryRespVO;
import cn.iocoder.yudao.module.system.controller.admin.category.vo.CategoryTreeRespVO;
import cn.iocoder.yudao.module.system.controller.admin.category.vo.CategoryUpdateReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.category.CategoryDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryConvert {

    CategoryConvert INSTANCE = Mappers.getMapper(CategoryConvert.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "treePath", ignore = true)
    @Mapping(target = "level", ignore = true)
    CategoryDO convert(CategoryCreateReqVO bean);

    @Mapping(target = "treePath", ignore = true)
    @Mapping(target = "level", ignore = true)
    CategoryDO convert(CategoryUpdateReqVO bean);

    CategoryRespVO convert(CategoryDO bean);

    List<CategoryRespVO> convertList(List<CategoryDO> list);

    CategoryTreeRespVO convertTree(CategoryDO bean);

    List<CategoryTreeRespVO> convertTreeList(List<CategoryDO> list);
}
