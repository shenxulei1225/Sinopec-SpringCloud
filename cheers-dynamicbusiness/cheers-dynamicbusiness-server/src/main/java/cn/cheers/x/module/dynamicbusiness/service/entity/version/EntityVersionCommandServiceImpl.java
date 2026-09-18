package cn.cheers.x.module.dynamicbusiness.service.entity.version;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityVersionOptionRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.EntityRepository;
import cn.cheers.x.module.dynamicbusiness.service.entity.core.EntityCoreService;
import cn.cheers.x.module.dynamicbusiness.service.entitytype.EntityTypeCapabilityService;
import jakarta.annotation.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 通用实体版本命令实现。
 *
 * <p>固定口径：publish_status 只承载「UNPUBLISHED / PUBLISHED」。</p>
 */
@Service
public class EntityVersionCommandServiceImpl implements EntityVersionCommandService {

    private static final String STATUS_PUBLISHED = "PUBLISHED";
    private static final String STATUS_UNPUBLISHED = "UNPUBLISHED";

    @Resource
    private EntityCoreService entityCoreService;
    @Resource
    private EntityRepository entityRepository;
    @Resource
    private JdbcTemplate jdbcTemplate;
    @Resource
    private EntityTypeCapabilityService entityTypeCapabilityService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveToVersion(String entityTypeCode, long currentEntityId, int versionNo) {
        if (versionNo <= 0) {
            throw new ServiceException(400, "versionNo 必须大于 0");
        }
        EntityIdentity identity = resolveIdentity(entityTypeCode, currentEntityId, "保存版本");
        String table = entityRepository.resolvePhysicalTableName(identity.entityTypeCode());
        Map<String, Object> sourceRow = loadRowById(table, identity.tenantId(), currentEntityId);
        if (sourceRow == null) {
            throw new ServiceException(404, "源版本不存在或已删除");
        }
        ensureVersionColumns(sourceRow, identity.entityTypeCode());

        sourceRow.put("version_no", versionNo);
        sourceRow.put("publish_status", STATUS_UNPUBLISHED);
        sourceRow.put("update_time", new Timestamp(System.currentTimeMillis()));

        Long targetId = findTargetIdByCodeAndVersion(table, identity.tenantId(), identity.code(), versionNo);
        if (targetId != null) {
            Map<String, Object> targetRow = loadRowById(table, identity.tenantId(), targetId);
            String targetStatus = String.valueOf(targetRow == null ? "" : targetRow.get("publish_status"))
                    .trim()
                    .toUpperCase(Locale.ROOT);
            if (STATUS_PUBLISHED.equals(targetStatus)) {
                throw new ServiceException(400, "目标版本已发布，不能直接覆盖。请先撤回发布。");
            }
            updateRowById(table, identity.tenantId(), targetId, sourceRow);
            return;
        }
        insertRow(table, sourceRow);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publishByVersion(String entityTypeCode, long currentEntityId, int versionNo) {
        if (versionNo <= 0) {
            throw new ServiceException(400, "versionNo 必须大于 0");
        }
        EntityIdentity identity = resolveIdentity(entityTypeCode, currentEntityId, "发布版本");
        String table = entityRepository.resolvePhysicalTableName(identity.entityTypeCode());
        Long targetId = findTargetIdByCodeAndVersion(table, identity.tenantId(), identity.code(), versionNo);
        if (targetId == null) {
            throw new ServiceException(404, "未找到可发布版本：version_no=" + versionNo);
        }
        jdbcTemplate.update(
                "UPDATE " + table + " SET publish_status = ? WHERE tenant_id = ? AND code = ? AND deleted = false",
                STATUS_UNPUBLISHED, identity.tenantId(), identity.code()
        );
        jdbcTemplate.update(
                "UPDATE " + table + " SET publish_status = ? WHERE id = ? AND tenant_id = ? AND deleted = false",
                STATUS_PUBLISHED, targetId, identity.tenantId()
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unpublish(String entityTypeCode, long currentEntityId) {
        EntityIdentity identity = resolveIdentity(entityTypeCode, currentEntityId, "撤回发布");
        String table = entityRepository.resolvePhysicalTableName(identity.entityTypeCode());
        jdbcTemplate.update(
                "UPDATE " + table + " SET publish_status = ? WHERE tenant_id = ? AND code = ? AND deleted = false",
                STATUS_UNPUBLISHED, identity.tenantId(), identity.code()
        );
    }

    @Override
    public List<EntityVersionOptionRespVO> listVersionOptions(String entityTypeCode, long currentEntityId) {
        EntityIdentity identity = resolveIdentity(entityTypeCode, currentEntityId, "查询版本列表");
        String table = entityRepository.resolvePhysicalTableName(identity.entityTypeCode());
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT id, version_no, publish_status, update_time FROM " + table
                        + " WHERE tenant_id = ? AND code = ? AND deleted = false ORDER BY version_no DESC, id DESC",
                identity.tenantId(), identity.code()
        );
        List<EntityVersionOptionRespVO> out = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            Number id = row.get("id") instanceof Number ? (Number) row.get("id") : null;
            Number versionNo = row.get("version_no") instanceof Number ? (Number) row.get("version_no") : null;
            if (id == null || versionNo == null) continue;
            EntityVersionOptionRespVO item = new EntityVersionOptionRespVO();
            item.setEntityId(id.longValue());
            item.setVersionNo(versionNo.intValue());
            item.setPublishStatus(String.valueOf(row.getOrDefault("publish_status", STATUS_UNPUBLISHED)));
            Object updateTime = row.get("update_time");
            if (updateTime instanceof Timestamp ts) {
                item.setUpdateTime(ts.toInstant());
            }
            out.add(item);
        }
        return out;
    }

    private EntityIdentity resolveIdentity(String entityTypeCode, long currentEntityId, String actionLabel) {
        String normalizedTypeCode = entityTypeCode == null ? "" : entityTypeCode.trim();
        if (normalizedTypeCode.isEmpty()) {
            throw new ServiceException(400, "entityTypeCode 不能为空");
        }
        if (!entityTypeCapabilityService.hasCapability(
                normalizedTypeCode,
                EntityTypeCapabilityService.CAPABILITY_VERSION_MANAGEMENT)) {
            throw new ServiceException(400, "当前目录未启用版本管理能力，不能执行" + actionLabel);
        }
        EntityDO current = entityCoreService.get(currentEntityId, normalizedTypeCode);
        if (current == null || current.getId() == null) {
            throw new ServiceException(404, "实体不存在");
        }
        String code = current.getCode() == null ? "" : current.getCode().trim();
        if (code.isEmpty()) {
            throw new ServiceException(400, "实体缺少 code，无法执行" + actionLabel);
        }
        Long tenantId = current.getTenantId();
        if (tenantId == null) {
            throw new ServiceException(400, "实体缺少 tenantId，无法执行" + actionLabel);
        }
        return new EntityIdentity(normalizedTypeCode, tenantId, code);
    }

    private void ensureVersionColumns(Map<String, Object> row, String entityTypeCode) {
        if (!row.containsKey("version_no") || !row.containsKey("publish_status")) {
            throw new ServiceException(
                    400,
                    "当前目录未配置版本字段（version_no/publish_status），无法启用版本管理：" + entityTypeCode
            );
        }
    }

    private Long findTargetIdByCodeAndVersion(String table, Long tenantId, String code, int versionNo) {
        String sql = "SELECT id FROM " + table
                + " WHERE tenant_id = ? AND code = ? AND version_no = ? AND deleted = false LIMIT 1";
        List<Long> ids = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> rs.getLong("id"),
                tenantId, code, versionNo
        );
        if (ids == null || ids.isEmpty()) return null;
        return ids.get(0);
    }

    private Map<String, Object> loadRowById(String table, Long tenantId, Long id) {
        String sql = "SELECT * FROM " + table + " WHERE id = ? AND tenant_id = ? AND deleted = false LIMIT 1";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, id, tenantId);
        if (rows == null || rows.isEmpty()) return null;
        return new LinkedHashMap<>(rows.get(0));
    }

    private void updateRowById(String table, Long tenantId, Long id, Map<String, Object> row) {
        List<String> columns = new ArrayList<>();
        List<Object> args = new ArrayList<>();
        for (Map.Entry<String, Object> entry : row.entrySet()) {
            String column = entry.getKey();
            if (isUpdateLockedColumn(column)) continue;
            columns.add(column + " = ?");
            args.add(entry.getValue());
        }
        if (columns.isEmpty()) return;
        args.add(id);
        args.add(tenantId);
        String sql = "UPDATE " + table + " SET " + String.join(", ", columns)
                + " WHERE id = ? AND tenant_id = ? AND deleted = false";
        jdbcTemplate.update(sql, args.toArray());
    }

    private void insertRow(String table, Map<String, Object> row) {
        List<String> columns = new ArrayList<>();
        List<String> placeholders = new ArrayList<>();
        List<Object> args = new ArrayList<>();
        for (Map.Entry<String, Object> entry : row.entrySet()) {
            String column = entry.getKey();
            if ("id".equalsIgnoreCase(column)) continue;
            columns.add(column);
            placeholders.add("?");
            args.add(entry.getValue());
        }
        if (columns.isEmpty()) {
            throw new ServiceException(500, "版本复制失败：无可写入字段");
        }
        String sql = "INSERT INTO " + table + " (" + String.join(", ", columns)
                + ") VALUES (" + String.join(", ", placeholders) + ")";
        jdbcTemplate.update(sql, args.toArray());
    }

    private boolean isUpdateLockedColumn(String column) {
        String normalized = column == null ? "" : column.trim().toLowerCase(Locale.ROOT);
        return "id".equals(normalized)
                || "tenant_id".equals(normalized)
                || "code".equals(normalized)
                || "create_time".equals(normalized)
                || "creator".equals(normalized)
                || "deleted".equals(normalized);
    }

    private record EntityIdentity(String entityTypeCode, Long tenantId, String code) {
    }
}

