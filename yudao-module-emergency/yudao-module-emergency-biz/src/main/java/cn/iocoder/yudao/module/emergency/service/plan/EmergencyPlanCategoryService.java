package cn.iocoder.yudao.module.emergency.service.plan;

import cn.cheers.x.framework.common.biz.system.category.CategoryCommonApi;
import cn.cheers.x.framework.common.biz.system.category.dto.CategoryCreateReqDTO;
import cn.cheers.x.framework.common.biz.system.category.dto.CategoryUpdateReqDTO;
import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.emergency.enums.error.ErrorCodeConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 应急预案分类管理服务
 * 
 * 通过调用 System 模块的 CategoryCommonApi 实现应急预案的分类管理
 * 使用 businessTypeCode = "emergency_plan" 来区分应急预案分类
 * 
 * 示例：展示如何在业务模块中使用统一的分类功能
 * 
 * @author 系统生成
 */
@Slf4j
@Service
public class EmergencyPlanCategoryService {

    /**
     * 应急预案分类的业务类型编码
     */
    private static final String BUSINESS_TYPE_CODE = "emergency_plan";

    @Autowired(required = false)
    private CategoryCommonApi categoryApi;

    /**
     * 创建应急预案分类
     * 
     * @param name 分类名称
     * @param parentId 父分类ID（可选，为null表示根分类）
     * @param sort 排序
     * @param description 描述
     * @return 分类ID
     */
    public Long createCategory(String name, Long parentId, Integer sort, String description) {
        if (categoryApi == null) {
            throw new ServiceException(ErrorCodeConstants.CATEGORY_SERVICE_UNAVAILABLE);
        }

        CategoryCreateReqDTO reqDTO = new CategoryCreateReqDTO();
        reqDTO.setName(name);
        reqDTO.setParentId(parentId);
        reqDTO.setCategoryTypeCode(BUSINESS_TYPE_CODE);
        reqDTO.setSort(sort != null ? sort : 0);
        reqDTO.setStatus(1); // 默认启用
        reqDTO.setDescription(description);

        CommonResult<Long> result = categoryApi.createCategory(reqDTO);
        if (!result.isSuccess()) {
            log.error("创建应急预案分类失败：name={}, error={}", name, result.getMsg());
            throw new ServiceException(ErrorCodeConstants.CATEGORY_CREATE_FAILED);
        }

        log.info("创建应急预案分类成功：name={}, id={}", name, result.getData());
        return result.getData();
    }

    /**
     * 更新应急预案分类
     * 
     * @param id 分类ID
     * @param name 分类名称
     * @param parentId 父分类ID
     * @param sort 排序
     * @param status 状态（1启用，0禁用）
     * @param description 描述
     */
    public void updateCategory(Long id, String name, Long parentId, Integer sort, Integer status, String description) {
        if (categoryApi == null) {
            throw new ServiceException(ErrorCodeConstants.CATEGORY_SERVICE_UNAVAILABLE);
        }

        CategoryUpdateReqDTO reqDTO = new CategoryUpdateReqDTO();
        reqDTO.setId(id);
        reqDTO.setName(name);
        reqDTO.setParentId(parentId);
        reqDTO.setCategoryTypeCode(BUSINESS_TYPE_CODE);
        reqDTO.setSort(sort);
        reqDTO.setStatus(status);
        reqDTO.setDescription(description);

        CommonResult<Boolean> result = categoryApi.updateCategory(reqDTO);
        if (!result.isSuccess()) {
            log.error("更新应急预案分类失败：id={}, error={}", id, result.getMsg());
            throw new ServiceException(ErrorCodeConstants.CATEGORY_UPDATE_FAILED);
        }

        log.info("更新应急预案分类成功：id={}", id);
    }

    /**
     * 删除应急预案分类
     * 
     * @param id 分类ID
     * @param cascade 是否级联删除子分类
     */
    public void deleteCategory(Long id, boolean cascade) {
        if (categoryApi == null) {
            throw new ServiceException(ErrorCodeConstants.CATEGORY_SERVICE_UNAVAILABLE);
        }

        CommonResult<Boolean> result = categoryApi.deleteCategory(id, cascade, BUSINESS_TYPE_CODE);
        if (!result.isSuccess()) {
            log.error("删除应急预案分类失败：id={}, error={}", id, result.getMsg());
            throw new ServiceException(ErrorCodeConstants.CATEGORY_DELETE_FAILED);
        }

        log.info("删除应急预案分类成功：id={}, cascade={}", id, cascade);
    }

    /**
     * 获取应急预案分类信息
     * 
     * @param id 分类ID
     * @return 分类信息
     */
    public Map<String, Object> getCategory(Long id) {
        if (categoryApi == null) {
            throw new ServiceException(ErrorCodeConstants.CATEGORY_SERVICE_UNAVAILABLE);
        }

        CommonResult<Map<String, Object>> result = categoryApi.getCategory(id, BUSINESS_TYPE_CODE);
        if (!result.isSuccess() || result.getData() == null) {
            log.warn("应急预案分类不存在：id={}", id);
            throw new ServiceException(ErrorCodeConstants.CATEGORY_NOT_EXISTS);
        }

        return result.getData();
    }

    /**
     * 验证分类是否存在
     * 
     * @param id 分类ID
     * @return 是否存在
     */
    public boolean existsCategory(Long id) {
        if (categoryApi == null) {
            log.warn("分类服务不可用，跳过验证：id={}", id);
            return false;
        }

        CommonResult<Boolean> result = categoryApi.existsCategory(id, BUSINESS_TYPE_CODE);
        return result.isSuccess() && Boolean.TRUE.equals(result.getData());
    }

    /**
     * 获取应急预案分类树
     * 
     * @param status 状态（1启用，0禁用），null表示全部
     * @return 分类树
     */
    public List<Map<String, Object>> getCategoryTree(Integer status) {
        if (categoryApi == null) {
            throw new ServiceException(ErrorCodeConstants.CATEGORY_SERVICE_UNAVAILABLE);
        }

        CommonResult<List<Map<String, Object>>> result = categoryApi.getCategoryTree(BUSINESS_TYPE_CODE, status);
        if (!result.isSuccess()) {
            log.error("获取应急预案分类树失败：error={}", result.getMsg());
            throw new ServiceException(ErrorCodeConstants.CATEGORY_QUERY_FAILED);
        }

        return result.getData();
    }

    /**
     * 移动分类
     * 
     * @param id 分类ID
     * @param targetParentId 目标父分类ID（null表示移到根）
     */
    public void moveCategory(Long id, Long targetParentId) {
        if (categoryApi == null) {
            throw new ServiceException(ErrorCodeConstants.CATEGORY_SERVICE_UNAVAILABLE);
        }

        CommonResult<Boolean> result = categoryApi.moveCategory(id, targetParentId, BUSINESS_TYPE_CODE);
        if (!result.isSuccess()) {
            log.error("移动应急预案分类失败：id={}, targetParentId={}, error={}", id, targetParentId, result.getMsg());
            throw new ServiceException(ErrorCodeConstants.CATEGORY_MOVE_FAILED);
        }

        log.info("移动应急预案分类成功：id={}, targetParentId={}", id, targetParentId);
    }

    /**
     * 排序分类
     * 
     * @param categoryIds 分类ID列表（按排序顺序）
     */
    public void sortCategories(List<Long> categoryIds) {
        if (categoryApi == null) {
            throw new ServiceException(ErrorCodeConstants.CATEGORY_SERVICE_UNAVAILABLE);
        }

        CommonResult<Boolean> result = categoryApi.sortCategories(categoryIds, BUSINESS_TYPE_CODE);
        if (!result.isSuccess()) {
            log.error("排序应急预案分类失败：error={}", result.getMsg());
            throw new ServiceException(ErrorCodeConstants.CATEGORY_SORT_FAILED);
        }

        log.info("排序应急预案分类成功：categoryIds={}", categoryIds);
    }

    /**
     * 启用分类
     * 
     * @param id 分类ID
     */
    public void enableCategory(Long id) {
        if (categoryApi == null) {
            throw new ServiceException(ErrorCodeConstants.CATEGORY_SERVICE_UNAVAILABLE);
        }

        CommonResult<Boolean> result = categoryApi.enableCategory(id, BUSINESS_TYPE_CODE);
        if (!result.isSuccess()) {
            log.error("启用应急预案分类失败：id={}, error={}", id, result.getMsg());
            throw new ServiceException(ErrorCodeConstants.CATEGORY_ENABLE_FAILED);
        }

        log.info("启用应急预案分类成功：id={}", id);
    }

    /**
     * 禁用分类
     * 
     * @param id 分类ID
     */
    public void disableCategory(Long id) {
        if (categoryApi == null) {
            throw new ServiceException(ErrorCodeConstants.CATEGORY_SERVICE_UNAVAILABLE);
        }

        CommonResult<Boolean> result = categoryApi.disableCategory(id, BUSINESS_TYPE_CODE);
        if (!result.isSuccess()) {
            log.error("禁用应急预案分类失败：id={}, error={}", id, result.getMsg());
            throw new ServiceException(ErrorCodeConstants.CATEGORY_DISABLE_FAILED);
        }

        log.info("禁用应急预案分类成功：id={}", id);
    }

    /**
     * 搜索分类
     * 
     * @param keyword 关键词
     * @return 分类列表
     */
    public List<Map<String, Object>> searchCategories(String keyword) {
        if (categoryApi == null) {
            throw new ServiceException(ErrorCodeConstants.CATEGORY_SERVICE_UNAVAILABLE);
        }

        CommonResult<List<Map<String, Object>>> result = categoryApi.searchCategories(keyword, BUSINESS_TYPE_CODE);
        if (!result.isSuccess()) {
            log.error("搜索应急预案分类失败：keyword={}, error={}", keyword, result.getMsg());
            throw new ServiceException(ErrorCodeConstants.CATEGORY_SEARCH_FAILED);
        }

        return result.getData();
    }

    /**
     * 获取分类路径
     * 
     * @param id 分类ID
     * @return 分类路径（从根到当前分类）
     */
    public List<Map<String, Object>> getCategoryPath(Long id) {
        if (categoryApi == null) {
            throw new ServiceException(ErrorCodeConstants.CATEGORY_SERVICE_UNAVAILABLE);
        }

        CommonResult<List<Map<String, Object>>> result = categoryApi.getPath(id, BUSINESS_TYPE_CODE);
        if (!result.isSuccess()) {
            log.error("获取应急预案分类路径失败：id={}, error={}", id, result.getMsg());
            throw new ServiceException(ErrorCodeConstants.CATEGORY_QUERY_FAILED);
        }

        return result.getData();
    }

    /**
     * 获取子分类列表
     * 
     * @param id 父分类ID
     * @param status 状态（1启用，0禁用），null表示全部
     * @return 子分类列表
     */
    public List<Map<String, Object>> getChildren(Long id, Integer status) {
        if (categoryApi == null) {
            throw new ServiceException(ErrorCodeConstants.CATEGORY_SERVICE_UNAVAILABLE);
        }

        CommonResult<List<Map<String, Object>>> result = categoryApi.getChildren(id, BUSINESS_TYPE_CODE, status);
        if (!result.isSuccess()) {
            log.error("获取应急预案子分类失败：id={}, error={}", id, result.getMsg());
            throw new ServiceException(ErrorCodeConstants.CATEGORY_QUERY_FAILED);
        }

        return result.getData();
    }
}

