package cn.iocoder.yudao.module.system.service.category;

import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.system.controller.admin.category.vo.CategoryTypeCreateReqVO;
import cn.iocoder.yudao.module.system.controller.admin.category.vo.CategoryTypeRespVO;
import cn.iocoder.yudao.module.system.controller.admin.category.vo.CategoryTypeUpdateReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.category.CategoryDO;
import cn.iocoder.yudao.module.system.dal.dataobject.category.CategoryTypeDO;
import cn.iocoder.yudao.module.system.dal.mysql.category.CategoryMapper;
import cn.iocoder.yudao.module.system.dal.mysql.category.CategoryTypeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.CATEGORY_TYPE_CANNOT_DELETE;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.CATEGORY_TYPE_CODE_EXISTS;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.CATEGORY_TYPE_NOT_EXISTS;

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
        if (existsByCategoryTypeCode(reqVO.getCategoryTypeCode(), null)) {
            throw ServiceExceptionUtil.exception(CATEGORY_TYPE_CODE_EXISTS);
        }
        if (categoryMapper.selectByCategoryTypeCode(reqVO.getCategoryTypeCode()).stream()
                .anyMatch(item -> item.getParentId() == null)) {
            throw ServiceExceptionUtil.exception(CATEGORY_TYPE_CODE_EXISTS);
        }

        CategoryTypeDO categoryTypeDO = BeanUtils.toBean(reqVO, CategoryTypeDO.class);
        categoryTypeDO.setTopLevelCategoryId(null);
        if (categoryTypeDO.getStatus() == null) {
            categoryTypeDO.setStatus(1);
        }
        categoryTypeMapper.insert(categoryTypeDO);

        CategoryDO top = new CategoryDO();
        top.setParentId(null);
        top.setName(categoryTypeDO.getName());
        top.setCode("ROOT-" + categoryTypeDO.getCategoryTypeCode());
        top.setCategoryTypeCode(categoryTypeDO.getCategoryTypeCode());
        top.setStatus(categoryTypeDO.getStatus());
        top.setDescription(categoryTypeDO.getDescription());
        top.setSort(1);
        top.setLevel(1);
        top.setTreePath(categoryTypeDO.getName());
        categoryMapper.insert(top);

        CategoryTypeDO update = new CategoryTypeDO();
        update.setId(categoryTypeDO.getId());
        update.setTopLevelCategoryId(top.getId());
        categoryTypeMapper.updateById(update);
        return categoryTypeDO.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCategoryType(CategoryTypeUpdateReqVO reqVO) {
        CategoryTypeDO categoryTypeDO = categoryTypeMapper.selectById(reqVO.getId());
        if (categoryTypeDO == null) {
            throw ServiceExceptionUtil.exception(CATEGORY_TYPE_NOT_EXISTS);
        }
        if (existsByCategoryTypeCode(reqVO.getCategoryTypeCode(), reqVO.getId())) {
            throw ServiceExceptionUtil.exception(CATEGORY_TYPE_CODE_EXISTS);
        }
        CategoryTypeDO updateDO = BeanUtils.toBean(reqVO, CategoryTypeDO.class);
        categoryTypeMapper.updateById(updateDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCategoryType(Long id) {
        CategoryTypeDO categoryTypeDO = categoryTypeMapper.selectById(id);
        if (categoryTypeDO == null) {
            throw ServiceExceptionUtil.exception(CATEGORY_TYPE_NOT_EXISTS);
        }
        Long categoryCount = categoryMapper.selectCountByCategoryTypeCode(categoryTypeDO.getCategoryTypeCode());
        if (categoryCount != null && categoryCount > 1) {
            throw ServiceExceptionUtil.exception(CATEGORY_TYPE_CANNOT_DELETE);
        }
        if (categoryTypeDO.getTopLevelCategoryId() != null) {
            categoryMapper.deleteById(categoryTypeDO.getTopLevelCategoryId());
        }
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
        return BeanUtils.toBean(categoryTypeMapper.selectEnabledList(), CategoryTypeRespVO.class);
    }

    @Override
    public List<CategoryTypeRespVO> getCategoryTypesByCreator(String creator) {
        return BeanUtils.toBean(categoryTypeMapper.selectByCreator(creator), CategoryTypeRespVO.class);
    }

    @Override
    public List<CategoryTypeRespVO> searchCategoryTypes(String keyword) {
        return BeanUtils.toBean(categoryTypeMapper.searchLike(keyword), CategoryTypeRespVO.class);
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
