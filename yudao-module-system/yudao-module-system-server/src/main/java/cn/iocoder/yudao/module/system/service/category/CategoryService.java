package cn.iocoder.yudao.module.system.service.category;

import cn.iocoder.yudao.module.system.controller.admin.category.vo.CategoryBatchDeleteRespVO;
import cn.iocoder.yudao.module.system.controller.admin.category.vo.CategoryCreateReqVO;
import cn.iocoder.yudao.module.system.controller.admin.category.vo.CategoryDeleteReqVO;
import cn.iocoder.yudao.module.system.controller.admin.category.vo.CategoryDragReqVO;
import cn.iocoder.yudao.module.system.controller.admin.category.vo.CategoryRespVO;
import cn.iocoder.yudao.module.system.controller.admin.category.vo.CategoryTreeRespVO;
import cn.iocoder.yudao.module.system.controller.admin.category.vo.CategoryUpdateReqVO;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CategoryService {

    boolean existsById(Long categoryId);

    Set<Long> filterExistingCategoryIds(List<Long> categoryIds);

    Long createCategory(CategoryCreateReqVO reqVO);

    void updateCategory(CategoryUpdateReqVO reqVO);

    void deleteCategory(CategoryDeleteReqVO reqVO);

    CategoryRespVO getCategoryVO(Long id);

    List<CategoryTreeRespVO> getCategoryTreeByType(String categoryTypeCode, Integer status);

    CategoryTreeRespVO getCategorySubtreeWithRoot(Long id, String categoryTypeCode, Integer status);

    void moveCategory(Long id, Long targetParentId, String categoryTypeCode);

    void sortCategories(Long parentId, List<Long> orderedIds, String categoryTypeCode);

    void dragCategory(CategoryDragReqVO reqVO);

    List<CategoryRespVO> searchCategoryList(String keyword, String categoryTypeCode);

    List<CategoryTreeRespVO> searchCategoryTree(String keyword, String categoryTypeCode);

    void updateStatus(Long id, Integer status, String categoryTypeCode);

    List<CategoryRespVO> listByParent(String categoryTypeCode, Long parentId, Integer status);

    List<CategoryRespVO> listByParentRecursive(String categoryTypeCode, Long parentId, Integer status);

    List<CategoryRespVO> getPath(Long id, String categoryTypeCode);

    List<CategoryRespVO> batchCreateCategory(List<CategoryCreateReqVO> categories);

    List<CategoryRespVO> batchUpdateCategory(List<CategoryUpdateReqVO> categories);

    CategoryBatchDeleteRespVO batchDeleteCategory(List<Long> ids, boolean cascade, String categoryTypeCode);

    List<Long> getAllCategoryIdsIncludingChildren(Long categoryId, String categoryTypeCode);

    Map<Long, List<Long>> getAllCategoryIdsIncludingChildrenBatch(List<Long> categoryIds, String categoryTypeCode);
}
