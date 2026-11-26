package cn.cheers.x.module.dynamicbusiness.service.work;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.tenant.core.context.TenantContextHolder;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelFieldAssignmentDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelFieldAssignmentMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.framework.entity.EntityTableNameHandler;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * 人员资质、外部施工人员和焊接记录的保存核对。
 *
 * 管什么：创建和更新这三本目录时，按作业管理说明拒绝不合格的保存，并把证书上的作业范围写成所选型号已经定好的准许作业。
 * 不管什么：不建证书类型，不补外部施工人员，不改许可状态。
 * 禁止：打开页面或查询时调用。禁止用旧的组织用户编号字段代替持证人。
 */
@Component
public class CertificateWriteGuard {

    static final String PERSON_CERTIFICATE = "person_certificate";
    static final String EXTERNAL_WORKER = "external_worker";
    static final String FIELD_RECORD = "repair_field_record";
    private static final Pattern TABLE_NAME = Pattern.compile("^ent_[a-z0-9_]+$");

    private final ModelMapper modelMapper;
    private final ModelFieldAssignmentMapper assignmentMapper;
    private final EntityTableNameHandler entityTableNameHandler;
    private final JdbcTemplate jdbcTemplate;

    public CertificateWriteGuard(
            ModelMapper modelMapper,
            ModelFieldAssignmentMapper assignmentMapper,
            EntityTableNameHandler entityTableNameHandler,
            JdbcTemplate jdbcTemplate) {
        this.modelMapper = modelMapper;
        this.assignmentMapper = assignmentMapper;
        this.entityTableNameHandler = entityTableNameHandler;
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 保存前核对。人员资质会把作业范围改成型号上的准许作业。
     *
     * @param entityId 新建时为空。更新时用来排除当前这一条，避免和自己撞重复
     */
    public void checkOnSave(String entityTypeCode, Long modelId, Long entityId, Map<String, Object> physicalColumns) {
        if (PERSON_CERTIFICATE.equals(entityTypeCode)) {
            checkCertificate(modelId, entityId, physicalColumns);
            return;
        }
        if (EXTERNAL_WORKER.equals(entityTypeCode)) {
            checkExternalWorker(entityId, physicalColumns);
            return;
        }
        if (FIELD_RECORD.equals(entityTypeCode)) {
            checkWelding(entityId, physicalColumns);
        }
    }

    private void checkCertificate(Long modelId, Long entityId, Map<String, Object> physicalColumns) {
        ModelDO model = modelMapper.selectById(modelId);
        if (model == null || Boolean.TRUE.equals(model.getDeleted())) {
            throw new ServiceException(400, "请先选择证书类型");
        }
        ModelFieldAssignmentDO work = assignmentMapper.selectByModelCodeAndFieldCode(
                model.getCode(), "certificate_work_range");
        String allowedWork = work == null || work.getDefaultValue() == null
                ? null
                : work.getDefaultValue().trim();
        Map<String, Object> row = merged(PERSON_CERTIFICATE, entityId, physicalColumns);
        String holderKind = text(row.get("holder_kind"));
        Long internalId = longValue(row.get("internal_user_id"));
        Long externalId = longValue(row.get("external_worker_id"));
        boolean exists = holderExists(holderKind, internalId, externalId);
        boolean duplicate = CertificateRecordRules.KIND_INTERNAL.equals(holderKind)
                || CertificateRecordRules.KIND_EXTERNAL.equals(holderKind)
                ? hasUnexpiredCertificate(modelId, entityId, holderKind, internalId, externalId)
                : false;
        String error = CertificateRecordRules.certificateSaveError(
                model.getCode(), allowedWork, holderKind, internalId, externalId, exists, duplicate);
        if (error != null) {
            throw new ServiceException(400, error);
        }
        physicalColumns.put("certificate_work_range", allowedWork);
    }

    private void checkExternalWorker(Long entityId, Map<String, Object> physicalColumns) {
        Map<String, Object> row = merged(EXTERNAL_WORKER, entityId, physicalColumns);
        String idDocumentNo = text(row.get("id_document_no"));
        boolean duplicate = StringUtils.hasText(idDocumentNo)
                && hasSameIdDocument(entityId, idDocumentNo);
        String error = CertificateRecordRules.externalWorkerSaveError(idDocumentNo, duplicate);
        if (error != null) {
            throw new ServiceException(400, error);
        }
    }

    private void checkWelding(Long entityId, Map<String, Object> physicalColumns) {
        Map<String, Object> row = merged(FIELD_RECORD, entityId, physicalColumns);
        String recordKind = text(row.get("record_kind"));
        if (!CertificateRecordRules.WELDING.equals(recordKind)) {
            return;
        }
        String holderKind = text(row.get("welder_holder_kind"));
        Long internalId = longValue(row.get("welder_internal_user_id"));
        Long externalId = longValue(row.get("welder_external_worker_id"));
        boolean exists = holderExists(holderKind, internalId, externalId);
        boolean covered = exists && hasWeldingCertificate(holderKind, internalId, externalId);
        String error = CertificateRecordRules.weldingSaveError(
                recordKind, holderKind, internalId, externalId, exists, covered);
        if (error != null) {
            throw new ServiceException(400, error);
        }
    }

    private boolean holderExists(String holderKind, Long internalId, Long externalId) {
        if (CertificateRecordRules.KIND_INTERNAL.equals(holderKind)) {
            return recordExists("user_account", internalId);
        }
        if (CertificateRecordRules.KIND_EXTERNAL.equals(holderKind)) {
            return recordExists(EXTERNAL_WORKER, externalId);
        }
        return false;
    }

    /** 人必须在对应目录的专用表里，且未删除。 */
    private boolean recordExists(String catalog, Long id) {
        if (id == null) {
            return false;
        }
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM " + tableOf(catalog) + " WHERE deleted = false AND id = ?",
                Integer.class,
                id);
        return count != null && count > 0;
    }

    private boolean hasUnexpiredCertificate(
            Long modelId, Long entityId, String holderKind, Long internalId, Long externalId) {
        String table = tableOf(PERSON_CERTIFICATE);
        String column = CertificateRecordRules.KIND_INTERNAL.equals(holderKind)
                ? "internal_user_id"
                : "external_worker_id";
        Long holderId = CertificateRecordRules.KIND_INTERNAL.equals(holderKind) ? internalId : externalId;
        if (holderId == null) {
            return false;
        }
        Integer count = jdbcTemplate.queryForObject(
                """
                        SELECT COUNT(*) FROM %s
                        WHERE tenant_id = ? AND deleted = false AND model_id = ?
                          AND holder_kind = ? AND %s = ?
                          AND certificate_valid_until IS NOT NULL
                          AND certificate_valid_until >= CURRENT_DATE
                          AND id <> ?
                        """.formatted(table, column),
                Integer.class,
                tenantId(),
                modelId,
                holderKind,
                holderId,
                entityId == null ? 0L : entityId);
        return count != null && count > 0;
    }

    private boolean hasWeldingCertificate(String holderKind, Long internalId, Long externalId) {
        String table = tableOf(PERSON_CERTIFICATE);
        String column = CertificateRecordRules.KIND_INTERNAL.equals(holderKind)
                ? "internal_user_id"
                : "external_worker_id";
        Long holderId = CertificateRecordRules.KIND_INTERNAL.equals(holderKind) ? internalId : externalId;
        if (holderId == null) {
            return false;
        }
        Integer count = jdbcTemplate.queryForObject(
                """
                        SELECT COUNT(*) FROM %s cert
                        JOIN dynamicbusiness.dynamic_model_field_assignment work
                          ON work.model_id = cert.model_id
                         AND work.field_code = 'certificate_work_range'
                         AND work.deleted = false
                         AND work.default_value = '焊接'
                        WHERE cert.tenant_id = ? AND cert.deleted = false
                          AND cert.holder_kind = ? AND cert.%s = ?
                          AND cert.certificate_valid_until IS NOT NULL
                          AND cert.certificate_valid_until >= CURRENT_DATE
                        """.formatted(table, column),
                Integer.class,
                tenantId(),
                holderKind,
                holderId);
        return count != null && count > 0;
    }

    private boolean hasSameIdDocument(Long entityId, String idDocumentNo) {
        String table = tableOf(EXTERNAL_WORKER);
        Integer count = jdbcTemplate.queryForObject(
                """
                        SELECT COUNT(*) FROM %s
                        WHERE tenant_id = ? AND deleted = false AND id_document_no = ? AND id <> ?
                        """.formatted(table),
                Integer.class,
                tenantId(),
                idDocumentNo.trim(),
                entityId == null ? 0L : entityId);
        return count != null && count > 0;
    }

    private Map<String, Object> merged(String catalog, Long entityId, Map<String, Object> submitted) {
        if (entityId == null) {
            return submitted;
        }
        String table = tableOf(catalog);
        Map<String, Object> stored = jdbcTemplate.query(
                "SELECT * FROM " + table + " WHERE id = ? AND deleted = false",
                rs -> {
                    if (!rs.next()) {
                        return Map.<String, Object>of();
                    }
                    java.sql.ResultSetMetaData meta = rs.getMetaData();
                    java.util.LinkedHashMap<String, Object> values = new java.util.LinkedHashMap<>();
                    for (int i = 1; i <= meta.getColumnCount(); i++) {
                        values.put(meta.getColumnLabel(i), rs.getObject(i));
                    }
                    return values;
                },
                entityId);
        java.util.LinkedHashMap<String, Object> merged = new java.util.LinkedHashMap<>();
        if (stored != null) {
            merged.putAll(stored);
        }
        if (submitted != null) {
            merged.putAll(submitted);
        }
        return merged;
    }

    private String tableOf(String catalog) {
        String table = entityTableNameHandler.resolvePhysicalTableName(catalog);
        if (!StringUtils.hasText(table) || !TABLE_NAME.matcher(table).matches()) {
            throw new ServiceException(400, "这本目录还没有专用表");
        }
        return table;
    }

    private static Long tenantId() {
        Long tenantId = TenantContextHolder.getTenantId();
        if (tenantId == null) {
            throw new ServiceException(400, "请先选择租户");
        }
        return tenantId;
    }

    private static String text(Object value) {
        if (value == null) {
            return null;
        }
        String text = String.valueOf(value).trim();
        return text.isEmpty() ? null : text;
    }

    private static Long longValue(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        String text = String.valueOf(value).trim();
        if (text.isEmpty()) {
            return null;
        }
        try {
            return Long.valueOf(text);
        } catch (NumberFormatException ex) {
            throw new ServiceException(400, "人员编号不正确");
        }
    }
}
