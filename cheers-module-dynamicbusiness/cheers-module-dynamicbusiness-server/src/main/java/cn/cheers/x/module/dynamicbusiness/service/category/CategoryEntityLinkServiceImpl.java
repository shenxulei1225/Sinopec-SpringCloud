package cn.cheers.x.module.dynamicbusiness.service.category;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.category.CategoryEntityLinkDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.category.CategoryEntityLinkMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

import static cn.cheers.x.module.dynamicbusiness.enums.ErrorCodeConstants.CATEGORY_ENTITY_LINK_NOT_FOUND;

/**
 * 分类与实体链接 Service 实现类
 *
 * @author 基础服务模块
 */
@Service
@Validated
@Slf4j
public class CategoryEntityLinkServiceImpl implements CategoryEntityLinkService {

    @Resource
    private CategoryEntityLinkMapper linkMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long linkCategoryToEntity(@NotNull(message = "分类ID不能为空") Long categoryId,
                                     @NotNull(message = "实体ID不能为空") Long entityId,
                                     Long entityModelId) {
        // 1. 校验是否已存在链接
        CategoryEntityLinkDO existingLink = linkMapper.selectByCategoryId(categoryId);
        if (existingLink != null) {
            throw new ServiceException(400, "分类已关联实体，请勿重复创建");
        }

        // 2. 创建链接
        CategoryEntityLinkDO link = CategoryEntityLinkDO.builder()
                .categoryId(categoryId)
                .entityId(entityId)
                .entityModelId(entityModelId)
                .build();
        linkMapper.insert(link);
        return link.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCategoryEntityLink(@Valid CategoryEntityLinkDO linkDO) {
        // 1. 校验存在
        validateCategoryEntityLinkExists(linkDO.getId());
        // 2. 更新
        linkMapper.updateById(linkDO);
    }

    @Override
    public CategoryEntityLinkDO getLinkByCategoryId(@NotNull(message = "分类ID不能为空") Long categoryId) {
        return linkMapper.selectByCategoryId(categoryId);
    }

    @Override
    public CategoryEntityLinkDO getLinkByEntityId(@NotNull(message = "实体ID不能为空") Long entityId) {
        return linkMapper.selectByEntityId(entityId);
    }

    @Override
    public boolean isEntityCategory(@NotNull(message = "分类ID不能为空") Long categoryId) {
        return linkMapper.selectByCategoryId(categoryId) != null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unlinkCategoryEntity(@NotNull(message = "分类ID不能为空") Long categoryId) {
        linkMapper.deleteByCategoryId(categoryId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unlinkEntityCategory(@NotNull(message = "实体ID不能为空") Long entityId) {
        linkMapper.deleteByEntityId(entityId);
    }

    @Override
    public List<CategoryEntityLinkDO> getLinksByCategoryIds(@NotNull(message = "分类ID列表不能为空") List<Long> categoryIds) {
        return linkMapper.selectByCategoryIds(categoryIds);
    }

    private void validateCategoryEntityLinkExists(Long id) {
        if (linkMapper.selectById(id) == null) {
            throw new ServiceException(CATEGORY_ENTITY_LINK_NOT_FOUND);
        }
    }
}