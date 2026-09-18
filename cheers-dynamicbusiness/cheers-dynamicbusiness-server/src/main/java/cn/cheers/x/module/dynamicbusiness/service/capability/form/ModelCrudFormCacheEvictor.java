package cn.cheers.x.module.dynamicbusiness.service.capability.form;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;

/**
 * 开能力后清模型 CRUD 表单缓存。
 *
 * <p>负责：清当前目录，以及共用同一底座的 DOMAIN 子目录（如底座 task 与巡检任务 task_patrol）。
 * 下次打开表单时按字段库/分配重建，才能带上刚补的用途列。</p>
 * <p>不负责：重建表单正文；读路径猜用途。</p>
 */
public final class ModelCrudFormCacheEvictor {

    private ModelCrudFormCacheEvictor() {
    }

    public static void evictRegistryAndDomainCatalogs(JdbcTemplate jdbcTemplate, String entityTypeCode) {
        if (jdbcTemplate == null || !StringUtils.hasText(entityTypeCode)) {
            return;
        }
        String code = entityTypeCode.trim();
        jdbcTemplate.update(
                """
                DELETE FROM model_crud_form_definition
                WHERE entity_type_code = ?
                   OR entity_type_code IN (
                     SELECT code FROM dynamic_entity_type
                     WHERE deleted = false
                       AND entry_kind = ?
                       AND base_entity_type_code = ?
                   )
                """,
                code,
                EntityTypeDO.ENTRY_KIND_DOMAIN,
                code
        );
    }
}
