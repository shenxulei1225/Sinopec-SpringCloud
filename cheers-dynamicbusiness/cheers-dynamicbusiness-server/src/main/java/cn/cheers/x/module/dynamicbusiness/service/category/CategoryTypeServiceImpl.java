package cn.cheers.x.module.dynamicbusiness.service.category;

import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.framework.common.util.object.BeanUtils;
import cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo.CategoryTypeCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo.CategoryTypeRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo.CategoryTypeUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.category.CategoryDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.category.CategoryTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.category.CategoryTypeMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.category.CategoryMapper;
import cn.cheers.x.module.dynamicbusiness.framework.category.utils.CategoryUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.cheers.x.module.dynamicbusiness.enums.ErrorCodeConstants.*;

/**
 * 分类类型服务实现
 */
@Service
@Validated
@RequiredArgsConstructor
@Slf4j
public class CategoryTypeServiceImpl implements CategoryTypeService {

    private final CategoryTypeMapper categoryTypeMapper;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCategoryType(CategoryTypeCreateReqVO reqVO) {
        // 检查分类类型编码是否已存在
        if (existsByCategoryTypeCode(reqVO.getCategoryTypeCode(), null)) {
            throw ServiceExceptionUtil.exception(CATEGORY_TYPE_CODE_EXISTS);
        }

        // 防御：存在同编码的顶层节点时阻止创建
        if (categoryMapper.selectByCategoryTypeCode(reqVO.getCategoryTypeCode()).stream()
                .anyMatch(item -> item.getParentId() == null)) {
            throw ServiceExceptionUtil.exception(CATEGORY_TYPE_CODE_EXISTS);
        }

        // 创建分类类型
        CategoryTypeDO categoryTypeDO = BeanUtils.toBean(reqVO, CategoryTypeDO.class);
        // 按规范：topLevelCategoryId 由系统自动创建并写入，不允许前端传入
        categoryTypeDO.setTopLevelCategoryId(null);
        categoryTypeDO.setCategoryMode(CategoryModeSupport.normalizeForWrite(reqVO.getCategoryMode()));
        categoryTypeDO.setEntityAssociationMode(
                EntityAssociationModeSupport.normalizeForWrite(reqVO.getEntityAssociationMode()));

        if (categoryTypeDO.getStatus() == null) {
            categoryTypeDO.setStatus(1);
        }
        categoryTypeMapper.insert(categoryTypeDO);

        // 自动创建该分类类型的顶层节点（Top Level Category）
        // 约定：顶层节点 name 使用分类类型名称，parentId=null
        CategoryDO top = new CategoryDO();
        top.setParentId(null);
        top.setCode(categoryTypeDO.getCategoryTypeCode() + "_root");
        top.setName(categoryTypeDO.getName());
        top.setCategoryTypeCode(categoryTypeDO.getCategoryTypeCode());
        top.setStatus(categoryTypeDO.getStatus() != null ? categoryTypeDO.getStatus() : 1);
        top.setDescription(categoryTypeDO.getDescription());
        top.setSort(1);
        top.setLevel(1);
        categoryMapper.insert(top);
        top.setTreePath(CategoryUtils.buildIdTreePath(null, top.getId()));
        categoryMapper.updateById(top);

        // 回写 topLevelCategoryId
        CategoryTypeDO update = new CategoryTypeDO();
        update.setId(categoryTypeDO.getId());
        update.setTopLevelCategoryId(top.getId());
        categoryTypeMapper.updateById(update);

        return categoryTypeDO.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCategoryType(CategoryTypeUpdateReqVO reqVO) {
        // 检查分类类型是否存在
        CategoryTypeDO categoryTypeDO = categoryTypeMapper.selectById(reqVO.getId());
        if (categoryTypeDO == null) {
            throw ServiceExceptionUtil.exception(CATEGORY_TYPE_NOT_EXISTS);
        }

        // 检查分类类型编码是否已存在（排除自身）
        if (existsByCategoryTypeCode(reqVO.getCategoryTypeCode(), reqVO.getId())) {
            throw ServiceExceptionUtil.exception(CATEGORY_TYPE_CODE_EXISTS);
        }

        // 更新分类类型
        CategoryTypeDO updateDO = BeanUtils.toBean(reqVO, CategoryTypeDO.class);
        if (reqVO.getCategoryMode() != null) {
            updateDO.setCategoryMode(CategoryModeSupport.normalizeForWrite(reqVO.getCategoryMode()));
        }
        if (reqVO.getEntityAssociationMode() != null) {
            updateDO.setEntityAssociationMode(
                    EntityAssociationModeSupport.normalizeForWrite(reqVO.getEntityAssociationMode()));
        }
        categoryTypeMapper.updateById(updateDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCategoryType(Long id) {
        // 检查分类类型是否存在
        CategoryTypeDO categoryTypeDO = categoryTypeMapper.selectById(id);
        if (categoryTypeDO == null) {
            throw ServiceExceptionUtil.exception(CATEGORY_TYPE_NOT_EXISTS);
        }

        // 检查是否有分类在使用此类型，如果有则不允许删除
        Long categoryCount = categoryTypeMapper.selectCountByCategoryTypeCode(categoryTypeDO.getCategoryTypeCode());
        if (categoryCount > 0) {
            throw ServiceExceptionUtil.exception(CATEGORY_TYPE_CANNOT_DELETE);
        }

        // 删除分类类型
        categoryTypeMapper.deleteById(id);
    }

    @Override
    public CategoryTypeRespVO getCategoryType(Long id) {
        CategoryTypeDO categoryTypeDO = categoryTypeMapper.selectById(id);
        if (categoryTypeDO == null) {
            throw ServiceExceptionUtil.exception(CATEGORY_TYPE_NOT_EXISTS);
        }
        return BeanUtils.toBean(categoryTypeDO, CategoryTypeRespVO.class);
    }

    @Override
    public List<CategoryTypeRespVO> getEnabledCategoryTypes() {
        List<CategoryTypeDO> categoryTypeDOs = categoryTypeMapper.selectEnabledList();
        return BeanUtils.toBean(categoryTypeDOs, CategoryTypeRespVO.class);
    }

    @Override
    public List<CategoryTypeRespVO> getCategoryTypesByCreator(String creator) {
        List<CategoryTypeDO> categoryTypeDOs = categoryTypeMapper.selectByCreator(creator);
        return BeanUtils.toBean(categoryTypeDOs, CategoryTypeRespVO.class);
    }

    @Override
    public List<CategoryTypeRespVO> searchCategoryTypes(String keyword) {
        List<CategoryTypeDO> categoryTypeDOs = categoryTypeMapper.searchLike(keyword);
        return BeanUtils.toBean(categoryTypeDOs, CategoryTypeRespVO.class);
    }

    @Override
    public CategoryTypeRespVO getCategoryTypeByCode(String categoryTypeCode) {
        CategoryTypeDO categoryTypeDO = categoryTypeMapper.selectByCategoryTypeCode(categoryTypeCode);
        if (categoryTypeDO == null) {
            throw ServiceExceptionUtil.exception(CATEGORY_TYPE_NOT_EXISTS);
        }
        return BeanUtils.toBean(categoryTypeDO, CategoryTypeRespVO.class);
    }

    @Override
    public boolean existsByCategoryTypeCode(String categoryTypeCode, Long excludeId) {
        CategoryTypeDO categoryTypeDO = categoryTypeMapper.selectByCategoryTypeCode(categoryTypeCode);
        return categoryTypeDO != null && !categoryTypeDO.getId().equals(excludeId);
    }

}