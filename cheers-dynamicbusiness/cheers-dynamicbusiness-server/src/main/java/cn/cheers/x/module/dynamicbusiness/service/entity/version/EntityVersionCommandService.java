package cn.cheers.x.module.dynamicbusiness.service.entity.version;

import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityVersionOptionRespVO;

import java.util.List;

/**
 * 通用实体版本命令服务。
 *
 * <p>负责：按 entityTypeCode + entityId 执行保存到版本、发布、撤回发布。</p>
 * <p>不负责：页面草稿临时态；业务字段语义解释。</p>
 */
public interface EntityVersionCommandService {

    /**
     * 将当前实体内容保存到指定版本号。
     *
     * @param entityTypeCode 数据类型编码
     * @param currentEntityId 当前实体 id（作为源版本）
     * @param versionNo 目标版本号
     */
    void saveToVersion(String entityTypeCode, long currentEntityId, int versionNo);

    /**
     * 按版本号发布。
     *
     * @param entityTypeCode 数据类型编码
     * @param currentEntityId 当前实体 id（用于锁定同 code 版本范围）
     * @param versionNo 目标发布版本号
     */
    void publishByVersion(String entityTypeCode, long currentEntityId, int versionNo);

    /**
     * 撤回发布（同 code 所有版本置未发布）。
     *
     * @param entityTypeCode 数据类型编码
     * @param currentEntityId 当前实体 id
     */
    void unpublish(String entityTypeCode, long currentEntityId);

    /**
     * 查询同一业务 code 下的可选版本列表（倒序）。
     *
     * @param entityTypeCode 数据类型编码
     * @param currentEntityId 当前实体 id（用于锁定 tenant/code）
     */
    List<EntityVersionOptionRespVO> listVersionOptions(
            String entityTypeCode,
            long currentEntityId
    );
}

