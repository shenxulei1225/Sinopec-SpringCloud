package cn.cheers.x.module.dynamicbusiness.service.category;

import cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo.CategoryTypeCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo.CategoryTypeRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo.CategoryTypeUpdateReqVO;

import java.util.List;

/**
 * 分类类型服务接口
 */
public interface CategoryTypeService {

    /**
     * 创建分类类型
     *
     * @param reqVO 创建请求
     * @return 分类类型ID
     */
    Long createCategoryType(CategoryTypeCreateReqVO reqVO);

    /**
     * 更新分类类型
     *
     * @param reqVO 更新请求
     */
    void updateCategoryType(CategoryTypeUpdateReqVO reqVO);

    /**
     * 删除分类类型
     *
     * @param id 分类类型ID
     */
    void deleteCategoryType(Long id);

    /**
     * 获取分类类型详情
     *
     * @param id 分类类型ID
     * @return 分类类型详情
     */
    CategoryTypeRespVO getCategoryType(Long id);

    /**
     * 获取所有启用的分类类型列表
     *
     * @return 分类类型列表
     */
    List<CategoryTypeRespVO> getEnabledCategoryTypes();

    /**
     * 根据用户获取分类类型列表
     *
     * @param creator 创建者
     * @return 分类类型列表
     */
    List<CategoryTypeRespVO> getCategoryTypesByCreator(String creator);

    /**
     * 搜索分类类型
     *
     * @param keyword 关键词
     * @return 分类类型列表
     */
    List<CategoryTypeRespVO> searchCategoryTypes(String keyword);

    /**
     * 根据分类类型编码获取详情
     *
     * @param categoryTypeCode 分类类型编码
     * @return 分类类型详情
     */
    CategoryTypeRespVO getCategoryTypeByCode(String categoryTypeCode);

    /**
     * 检查分类类型编码是否存在
     *
     * @param categoryTypeCode 分类类型编码
     * @param excludeId 排除的ID（用于更新时检查）
     * @return 是否存在
     */
    boolean existsByCategoryTypeCode(String categoryTypeCode, Long excludeId);

}