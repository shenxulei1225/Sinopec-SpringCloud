package cn.iocoder.yudao.module.system.service.category;

import cn.iocoder.yudao.module.system.controller.admin.category.vo.CategoryTypeCreateReqVO;
import cn.iocoder.yudao.module.system.controller.admin.category.vo.CategoryTypeRespVO;
import cn.iocoder.yudao.module.system.controller.admin.category.vo.CategoryTypeUpdateReqVO;

import java.util.List;

public interface CategoryTypeService {

    Long createCategoryType(CategoryTypeCreateReqVO reqVO);

    void updateCategoryType(CategoryTypeUpdateReqVO reqVO);

    void deleteCategoryType(Long id);

    CategoryTypeRespVO getCategoryType(Long id);

    List<CategoryTypeRespVO> getEnabledCategoryTypes();

    List<CategoryTypeRespVO> getCategoryTypesByCreator(String creator);

    List<CategoryTypeRespVO> searchCategoryTypes(String keyword);

    CategoryTypeRespVO getCategoryTypeByCode(String categoryTypeCode);

    boolean existsByCategoryTypeCode(String categoryTypeCode, Long excludeId);
}
