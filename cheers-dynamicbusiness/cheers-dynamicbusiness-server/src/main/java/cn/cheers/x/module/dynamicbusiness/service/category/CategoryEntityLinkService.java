package cn.cheers.x.module.dynamicbusiness.service.category;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.category.CategoryEntityLinkDO;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 分类与实体链接 Service 接口
 *
 * @author 基础服务模块
 */
@Validated
public interface CategoryEntityLinkService {

    /**
     * 创建分类与实体链接
     *
     * @param categoryId 分类ID
     * @param entityId 实体ID
     * @param entityModelId 实体模型ID
     */
    Long linkCategoryToEntity(@NotNull(message = "分类ID不能为空") Long categoryId,
                              @NotNull(message = "实体ID不能为空") Long entityId,
                              Long entityModelId);

    /**
     * 创建分类与实体链接，并写入实体类型编码 / 业务域（避免跨表同 id 误判）。
     */
    Long linkCategoryToEntity(@NotNull Long categoryId,
                              @NotNull Long entityId,
                              Long entityModelId,
                              String entityTypeCode,
                              String domain);

    /**
     * 更新分类与实体链接
     *
     * @param linkDO 链接DO
     */
    void updateCategoryEntityLink(@Valid CategoryEntityLinkDO linkDO);

    /**
     * 获取分类关联的实体链接
     *
     * @param categoryId 分类ID
     * @return 实体链接
     */
    CategoryEntityLinkDO getLinkByCategoryId(@NotNull(message = "分类ID不能为空") Long categoryId);

    /**
     * 获取实体关联的分类链接
     *
     * @param entityId 实体ID
     * @return 实体链接
     */
    CategoryEntityLinkDO getLinkByEntityId(@NotNull(message = "实体ID不能为空") Long entityId);

    /**
     * 按实体 ID + 实体类型编码获取分类链接（避免跨表同 id 误命中）。
     */
    CategoryEntityLinkDO getLinkByEntityIdAndEntityTypeCode(@NotNull Long entityId, String entityTypeCode);

    /**
     * 判断分类是否为实体分类
     *
     * @param categoryId 分类ID
     * @return 是否为实体分类
     */
    boolean isEntityCategory(@NotNull(message = "分类ID不能为空") Long categoryId);

    /**
     * 解除分类与实体的链接
     *
     * @param categoryId 分类ID
     */
    void unlinkCategoryEntity(@NotNull(message = "分类ID不能为空") Long categoryId);

    /**
     * 解除实体与分类的链接（仅按 entityId，历史兼容）。
     *
     * @param entityId 实体ID
     */
    void unlinkEntityCategory(@NotNull(message = "实体ID不能为空") Long entityId);

    /**
     * 按实体 ID + 实体类型编码解除链接。
     */
    void unlinkEntityCategory(@NotNull Long entityId, String entityTypeCode);

    /**
     * 批量根据分类ID查询链接（用于优化批量删除时的 N+1 查询问题）
     *
     * @param categoryIds 分类ID列表
     * @return 链接列表
     */
    List<CategoryEntityLinkDO> getLinksByCategoryIds(@NotNull(message = "分类ID列表不能为空") List<Long> categoryIds);
}