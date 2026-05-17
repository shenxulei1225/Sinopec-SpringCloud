package cn.iocoder.yudao.module.system.api.category;

import cn.hutool.core.bean.BeanUtil;
import cn.iocoder.yudao.framework.common.biz.system.category.CategoryCommonApi;
import cn.iocoder.yudao.framework.common.biz.system.category.dto.CategoryCreateReqDTO;
import cn.iocoder.yudao.framework.common.biz.system.category.dto.CategoryUpdateReqDTO;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.system.controller.admin.category.vo.CategoryCreateReqVO;
import cn.iocoder.yudao.module.system.controller.admin.category.vo.CategoryDeleteReqVO;
import cn.iocoder.yudao.module.system.controller.admin.category.vo.CategoryRespVO;
import cn.iocoder.yudao.module.system.controller.admin.category.vo.CategoryUpdateReqVO;
import cn.iocoder.yudao.module.system.service.category.CategoryService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static java.util.stream.Collectors.toList;

@RestController
@Validated
public class CategoryApiImpl implements CategoryCommonApi {

    @Resource
    private CategoryService categoryService;

    @Override
    public CommonResult<Map<String, Object>> getCategory(Long id, String categoryTypeCode) {
        CategoryRespVO category = categoryService.getCategoryVO(id);
        if (category == null || !categoryTypeCode.equals(category.getCategoryTypeCode())) {
            return success(null);
        }
        return success(BeanUtil.beanToMap(category));
    }

    @Override
    public CommonResult<Boolean> existsCategory(Long id, String categoryTypeCode) {
        CategoryRespVO category = categoryService.getCategoryVO(id);
        return success(category != null && categoryTypeCode.equals(category.getCategoryTypeCode()));
    }

    @Override
    public CommonResult<List<Map<String, Object>>> getCategoryTree(String categoryTypeCode, Integer status) {
        return success(categoryService.getCategoryTreeByType(categoryTypeCode, status).stream()
                .map(BeanUtil::beanToMap)
                .collect(toList()));
    }

    @Override
    public CommonResult<Long> createCategory(@Valid CategoryCreateReqDTO reqDTO) {
        CategoryCreateReqVO reqVO = BeanUtils.toBean(reqDTO, CategoryCreateReqVO.class);
        return success(categoryService.createCategory(reqVO));
    }

    @Override
    public CommonResult<Boolean> updateCategory(@Valid CategoryUpdateReqDTO reqDTO) {
        CategoryUpdateReqVO reqVO = BeanUtils.toBean(reqDTO, CategoryUpdateReqVO.class);
        categoryService.updateCategory(reqVO);
        return success(true);
    }

    @Override
    public CommonResult<Boolean> deleteCategory(Long id, boolean cascade, String categoryTypeCode) {
        CategoryDeleteReqVO reqVO = new CategoryDeleteReqVO();
        reqVO.setId(id);
        reqVO.setCascade(cascade);
        reqVO.setCategoryTypeCode(categoryTypeCode);
        categoryService.deleteCategory(reqVO);
        return success(true);
    }

    @Override
    public CommonResult<Boolean> moveCategory(Long id, Long targetParentId, String categoryTypeCode) {
        categoryService.moveCategory(id, targetParentId, categoryTypeCode);
        return success(true);
    }

    @Override
    public CommonResult<Boolean> sortCategories(List<Long> categoryIds, String categoryTypeCode) {
        categoryService.sortCategories(null, categoryIds, categoryTypeCode);
        return success(true);
    }

    @Override
    public CommonResult<Boolean> enableCategory(Long id, String categoryTypeCode) {
        categoryService.updateStatus(id, 1, categoryTypeCode);
        return success(true);
    }

    @Override
    public CommonResult<Boolean> disableCategory(Long id, String categoryTypeCode) {
        categoryService.updateStatus(id, 0, categoryTypeCode);
        return success(true);
    }

    @Override
    public CommonResult<List<Map<String, Object>>> searchCategories(String keyword, String categoryTypeCode) {
        return success(categoryService.searchCategoryList(keyword, categoryTypeCode).stream()
                .map(BeanUtil::beanToMap)
                .collect(toList()));
    }

    @Override
    public CommonResult<List<Map<String, Object>>> getPath(Long id, String categoryTypeCode) {
        return success(categoryService.getPath(id, categoryTypeCode).stream()
                .map(BeanUtil::beanToMap)
                .collect(toList()));
    }

    @Override
    public CommonResult<List<Map<String, Object>>> getChildren(Long id, String categoryTypeCode, Integer status) {
        return success(categoryService.listByParent(categoryTypeCode, id, status).stream()
                .map(BeanUtil::beanToMap)
                .collect(toList()));
    }
}
