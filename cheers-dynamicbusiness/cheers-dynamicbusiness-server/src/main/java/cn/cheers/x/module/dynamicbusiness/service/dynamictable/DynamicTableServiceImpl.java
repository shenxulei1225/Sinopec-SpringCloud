package cn.cheers.x.module.dynamicbusiness.service.dynamictable;

import cn.cheers.x.framework.tenant.core.context.TenantContextHolder;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.security.core.util.SecurityFrameworkUtils;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.PhysicalColumnConfig;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.dynamictable.DynamicTableAuditLogDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.dynamictable.DynamicTableColumnDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.dynamictable.DynamicTableDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelFieldAssignmentDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.dynamictable.DynamicTableAuditLogMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.dynamictable.DynamicTableColumnMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.dynamictable.DynamicTableMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.field.FieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelFieldAssignmentMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.util.PhysicalColumnMappingUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 动态表管理 Service 实现类
 * 
 * @author yudao
 */
@Service
@Validated
@Slf4j
public class DynamicTableServiceImpl implements DynamicTableService {

    @Resource
    private DynamicTableMapper dynamicTableMapper;
    @Resource
    private DynamicTableColumnMapper dynamicTableColumnMapper;
    @Resource
    private DynamicTableAuditLogMapper dynamicTableAuditLogMapper;
    @Resource
    private ModelMapper modelMapper;
    @Resource
    private ModelFieldAssignmentMapper modelFieldAssignmentMapper;
    @Resource
    private FieldMapper fieldMapper;
    @Resource
    private JdbcTemplate jdbcTemplate;
    @Resource
    private SqlInjectionProtectionService sqlInjectionProtectionService;

    // ==================== 动态表管理 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createDynamicTableForEntityType(String entityTypeCode, String tableName, String tableComment) {
        // 委托给带物理列映射参数的方法，传入 null 表示不使用物理列
        return createDynamicTableForEntityType(entityTypeCode, tableName, tableComment, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createDynamicTableForEntityType(String entityTypeCode, String tableName, String tableComment,
                                                  Map<String, PhysicalColumnConfig> physicalColumnMapping) {
        // 1. 校验参数
        if (StrUtil.isBlank(entityTypeCode)) {
            throw new ServiceException(400, "业务类型编码不能为空");
        }
        if (StrUtil.isBlank(tableName)) {
            throw new ServiceException(400, "表名不能为空");
        }
        // 业务类型专用表名必须带 ent_ 前缀
        if (!StrUtil.startWith(tableName, "ent_")) {
            tableName = "ent_" + tableName;
        }

        // 2. 验证物理列映射配置（如果有）
        if (CollUtil.isNotEmpty(physicalColumnMapping)) {
            PhysicalColumnMappingUtils.ValidationResult validationResult = 
                    PhysicalColumnMappingUtils.validateMapping(physicalColumnMapping);
            if (!validationResult.isValid()) {
                throw new ServiceException(400, "物理列映射配置无效: " + validationResult.getErrorMessage());
            }
        }

        // 3. 检查表是否已存在
        if (tableExists(tableName)) {
            log.info("动态表 {} 已存在，跳过创建", tableName);
            // 历史坏表可能无 id 默认值 / 序列落后：即使跳过 CREATE 也要校准
            ensureDedicatedTableIdSerial(tableName);
            // 检查是否已有配置记录
            List<DynamicTableDO> existingTables = dynamicTableMapper.selectByEntityTypeCode(entityTypeCode);
            for (DynamicTableDO existing : existingTables) {
                if (tableName.equals(existing.getTableName())) {
                    return existing.getId();
                }
            }
        }

        // 4. 创建动态表配置记录（不关联 modelId）
        DynamicTableDO dynamicTable = DynamicTableDO.builder()
                .modelId(null) // 业务类型级别的动态表，不关联具体模型
                .entityTypeCode(entityTypeCode)
                .tableName(tableName)
                .tableComment(StrUtil.isNotBlank(tableComment) ? tableComment : entityTypeCode + " 数据表")
                .status(1)
                .version(1)
                .lastSyncTime(LocalDateTime.now())
                .build();
        dynamicTable.setTenantId(getTenantId());
        dynamicTableMapper.insert(dynamicTable);

        // 5. 创建物理表（如果不存在）
        if (!tableExists(tableName)) {
            String createTableSql = buildCreateTableSql(tableName, dynamicTable.getTableComment(), physicalColumnMapping);
            try {
                jdbcTemplate.execute(createTableSql);
                log.info("成功创建动态表: {}", tableName);
            } catch (Exception e) {
                log.error("创建动态表失败: {}", tableName, e);
                throw new ServiceException(500, "创建动态表失败: " + e.getMessage());
            }

            // 6. 记录审计日志
            saveAuditLog(dynamicTable.getId(), "CREATE_TABLE",
                    "为业务类型 " + entityTypeCode + " 创建动态表: " + tableName,
                    null, createTableSql, createTableSql, "SUCCESS", null);
        }

        // 新建或复用物理表后，统一保证 id 可自增（BIGSERIAL 正常；坏表可被修好）
        ensureDedicatedTableIdSerial(tableName);

        // 7. 注册到 SQL 注入防护白名单，确保后续动态 SQL 可以正常访问
        try {
            sqlInjectionProtectionService.registerDynamicTable(tableName);
        } catch (Exception e) {
            // 注册失败不回滚主流程，但记录告警日志
            log.warn("注册动态表到 SQL 注入白名单失败，tableName={}", tableName, e);
        }

        log.info("[createDynamicTableForEntityType] 成功为业务类型 {} 创建动态表 {}, 物理列数量: {}", 
                entityTypeCode, tableName, 
                physicalColumnMapping != null ? physicalColumnMapping.size() : 0);
        return dynamicTable.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createDynamicTable(Long modelId) {
        // 1. 校验模型存在
        ModelDO model = modelMapper.selectById(modelId);
        if (model == null) {
            throw new ServiceException(404, "业务模型不存在");
        }

        // 2. 校验是否已创建动态表
        if (hasDynamicTable(modelId)) {
            throw new ServiceException(400, "该模型已创建动态表");
        }

        // 3. 生成表名
        String tableName = generateTableName(model);

        // 4. 创建动态表配置记录
        DynamicTableDO dynamicTable = DynamicTableDO.builder()
                .modelId(modelId)
                .entityTypeCode(model.getEntityTypeCode())
                .tableName(tableName)
                .tableComment(model.getName() + " 数据表")
                .status(1)
                .version(1)
                .lastSyncTime(LocalDateTime.now())
                .build();
        dynamicTable.setTenantId(getTenantId());
        dynamicTableMapper.insert(dynamicTable);

        // 5. 创建物理表（不带物理列映射，使用默认结构）
        String createTableSql = buildCreateTableSql(tableName, model.getName(), null);
        try {
            jdbcTemplate.execute(createTableSql);
            log.info("成功创建动态表: {}", tableName);
        } catch (Exception e) {
            log.error("创建动态表失败: {}", tableName, e);
            throw new ServiceException(500, "创建动态表失败: " + e.getMessage());
        }
        ensureDedicatedTableIdSerial(tableName);

        // 6. 记录审计日志
        saveAuditLog(dynamicTable.getId(), "CREATE_TABLE", 
                "创建动态表: " + tableName, null, createTableSql, createTableSql, "SUCCESS", null);

        // 7. 注册到 SQL 注入防护白名单
        try {
            sqlInjectionProtectionService.registerDynamicTable(tableName);
        } catch (Exception e) {
            log.warn("注册模型动态表到 SQL 注入白名单失败，tableName={}", tableName, e);
        }

        // 8. 同步模型字段到动态表
        syncModelFieldsToDynamicTable(modelId);

        return dynamicTable.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDynamicTable(Long dynamicTableId) {
        // 1. 校验动态表存在
        DynamicTableDO dynamicTable = dynamicTableMapper.selectById(dynamicTableId);
        if (dynamicTable == null) {
            throw new ServiceException(404, "动态表配置不存在");
        }

        // 2. 逻辑删除动态表配置
        dynamicTable.setStatus(0);
        dynamicTableMapper.updateById(dynamicTable);

        // 3. 逻辑删除字段配置
        dynamicTableColumnMapper.deleteByDynamicTableId(dynamicTableId);

        // 4. 从 SQL 注入防护白名单移除
        try {
            sqlInjectionProtectionService.unregisterDynamicTable(dynamicTable.getTableName());
        } catch (Exception e) {
            log.warn("从 SQL 注入白名单移除动态表失败，tableName={}", dynamicTable.getTableName(), e);
        }

        // 5. 记录审计日志（不删除物理表，保留数据）
        saveAuditLog(dynamicTableId, "DROP_TABLE", 
                "逻辑删除动态表配置: " + dynamicTable.getTableName(), 
                null, null, null, "SUCCESS", null);

        log.info("逻辑删除动态表配置: {}", dynamicTable.getTableName());
    }

    @Override
    public DynamicTableDO getDynamicTable(Long dynamicTableId) {
        return dynamicTableMapper.selectById(dynamicTableId);
    }

    @Override
    public DynamicTableDO getDynamicTableByModelId(Long modelId) {
        // 采用 modelId → entityTypeCode → DynamicTable 的查询链路
        // 这样更符合当前的数据模型设计：动态表是按 entityTypeCode 创建的
        if (modelId == null) {
            return null;
        }
        
        ModelDO model = modelMapper.selectById(modelId);
        if (model == null || StrUtil.isBlank(model.getEntityTypeCode())) {
            return null;
        }
        
        List<DynamicTableDO> tables = dynamicTableMapper.selectByEntityTypeCode(model.getEntityTypeCode());
        return tables != null && !tables.isEmpty() ? tables.get(0) : null;
    }

    @Override
    public List<DynamicTableDO> listDynamicTablesByEntityType(String entityTypeCode) {
        return dynamicTableMapper.selectByEntityTypeCode(entityTypeCode);
    }

    @Override
    public boolean hasDynamicTable(Long modelId) {
        // 采用 modelId → entityTypeCode → DynamicTable 的查询链路
        return getDynamicTableByModelId(modelId) != null;
    }

    // ==================== 字段同步 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncModelFieldsToDynamicTable(Long modelId) {
        // 1. 获取动态表配置
        DynamicTableDO dynamicTable = getDynamicTableByModelId(modelId);
        if (dynamicTable == null) {
            log.warn("模型 {} 未创建动态表，跳过字段同步", modelId);
            return;
        }

        // 2. 获取模型的字段分配
        List<ModelFieldAssignmentDO> assignments = modelFieldAssignmentMapper.selectByModelId(modelId);
        Set<Long> assignedFieldIds = assignments.stream()
                .map(ModelFieldAssignmentDO::getFieldId)
                .collect(Collectors.toSet());

        // 3. 获取当前动态表的字段配置
        List<DynamicTableColumnDO> existingColumns = dynamicTableColumnMapper.selectByDynamicTableId(dynamicTable.getId());
        Set<Long> existingFieldIds = existingColumns.stream()
                .map(DynamicTableColumnDO::getFieldId)
                .collect(Collectors.toSet());

        // 4. 计算需要添加和删除的字段
        Set<Long> toAdd = new HashSet<>(assignedFieldIds);
        toAdd.removeAll(existingFieldIds);

        Set<Long> toRemove = new HashSet<>(existingFieldIds);
        toRemove.removeAll(assignedFieldIds);

        // 5. 添加新字段
        for (Long fieldId : toAdd) {
            addColumnToDynamicTable(dynamicTable.getId(), fieldId);
        }

        // 6. 删除旧字段
        for (Long fieldId : toRemove) {
            removeColumnFromDynamicTable(dynamicTable.getId(), fieldId);
        }

        // 7. 更新同步时间
        dynamicTable.setLastSyncTime(LocalDateTime.now());
        dynamicTable.setVersion(dynamicTable.getVersion() + 1);
        dynamicTableMapper.updateById(dynamicTable);

        log.info("同步模型 {} 字段到动态表完成，添加 {} 个字段，删除 {} 个字段", 
                modelId, toAdd.size(), toRemove.size());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addColumnToDynamicTable(Long dynamicTableId, Long fieldId) {
        // 1. 获取动态表配置
        DynamicTableDO dynamicTable = dynamicTableMapper.selectById(dynamicTableId);
        if (dynamicTable == null) {
            throw new ServiceException(404, "动态表配置不存在");
        }

        // 2. 获取字段定义
        FieldDO field = fieldMapper.selectById(fieldId);
        if (field == null) {
            throw new ServiceException(404, "字段不存在");
        }

        // 3. 检查字段是否已存在
        DynamicTableColumnDO existingColumn = dynamicTableColumnMapper
                .selectByDynamicTableIdAndFieldId(dynamicTableId, fieldId);
        if (existingColumn != null) {
            log.warn("字段 {} 已存在于动态表 {}", fieldId, dynamicTableId);
            return;
        }

        // 4. 生成列名和数据类型
        String columnName = generateColumnName(field);
        String dataType = mapFieldTypeToDbType(field.getType());

        // 5. 创建字段配置记录
        DynamicTableColumnDO column = DynamicTableColumnDO.builder()
                .dynamicTableId(dynamicTableId)
                .fieldId(fieldId)
                .columnName(columnName)
                .dataType(dataType)
                .nullable(true)
                .columnComment(field.getName())
                .sortOrder(getNextSortOrder(dynamicTableId))
                .status(1)
                .build();
        column.setTenantId(getTenantId());
        dynamicTableColumnMapper.insert(column);

        // 6. 执行 ALTER TABLE 添加列
        String alterSql = String.format("ALTER TABLE %s ADD COLUMN %s %s",
                dynamicTable.getTableName(), columnName, dataType);
        try {
            jdbcTemplate.execute(alterSql);
            log.info("成功添加列 {} 到表 {}", columnName, dynamicTable.getTableName());
        } catch (Exception e) {
            log.error("添加列失败: {}", alterSql, e);
            // 回滚字段配置记录
            dynamicTableColumnMapper.deleteById(column.getId());
            throw new ServiceException(500, "添加列失败: " + e.getMessage());
        }

        // 7. 记录审计日志
        saveAuditLog(dynamicTableId, "ADD_COLUMN",
                "添加列: " + columnName + " (" + field.getName() + ")",
                null, column.toString(), alterSql, "SUCCESS", null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeColumnFromDynamicTable(Long dynamicTableId, Long fieldId) {
        // 1. 获取动态表配置
        DynamicTableDO dynamicTable = dynamicTableMapper.selectById(dynamicTableId);
        if (dynamicTable == null) {
            throw new ServiceException(404, "动态表配置不存在");
        }

        // 2. 获取字段配置
        DynamicTableColumnDO column = dynamicTableColumnMapper
                .selectByDynamicTableIdAndFieldId(dynamicTableId, fieldId);
        if (column == null) {
            log.warn("字段 {} 不存在于动态表 {}", fieldId, dynamicTableId);
            return;
        }

        // 3. 逻辑删除字段配置（不删除物理列，保留数据）
        column.setStatus(0);
        dynamicTableColumnMapper.updateById(column);

        // 4. 记录审计日志
        saveAuditLog(dynamicTableId, "DROP_COLUMN",
                "逻辑删除列配置: " + column.getColumnName(),
                column.toString(), null, null, "SUCCESS", null);

        log.info("逻辑删除列配置: {} (物理列保留)", column.getColumnName());
    }

    @Override
    public List<DynamicTableColumnDO> listDynamicTableColumns(Long dynamicTableId) {
        return dynamicTableColumnMapper.selectByDynamicTableId(dynamicTableId);
    }

    // ==================== 统一数据访问接口 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long insertData(Long modelId, Map<Long, Object> data) {
        // 1. 获取动态表配置
        DynamicTableDO dynamicTable = getDynamicTableByModelId(modelId);
        if (dynamicTable == null) {
            throw new ServiceException(404, "模型未创建动态表");
        }

        // 2. 获取字段配置
        List<DynamicTableColumnDO> columns = listDynamicTableColumns(dynamicTable.getId());
        Map<Long, DynamicTableColumnDO> columnMap = columns.stream()
                .collect(Collectors.toMap(DynamicTableColumnDO::getFieldId, c -> c));

        // 3. 构建 INSERT SQL
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("INSERT INTO ").append(dynamicTable.getTableName()).append(" (");
        
        List<String> columnNames = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        
        // 添加基础字段
        columnNames.add("tenant_id");
        values.add(getTenantId());
        columnNames.add("creator");
        values.add(getLoginUserName());
        columnNames.add("create_time");
        values.add(LocalDateTime.now());
        columnNames.add("deleted");
        values.add(false);

        // 添加业务字段
        for (Map.Entry<Long, Object> entry : data.entrySet()) {
            DynamicTableColumnDO column = columnMap.get(entry.getKey());
            if (column != null) {
                columnNames.add(column.getColumnName());
                values.add(entry.getValue());
            }
        }

        sqlBuilder.append(String.join(", ", columnNames));
        sqlBuilder.append(") VALUES (");
        sqlBuilder.append(String.join(", ", Collections.nCopies(values.size(), "?")));
        sqlBuilder.append(") RETURNING id");

        // 4. 执行 INSERT
        try {
            Long id = jdbcTemplate.queryForObject(sqlBuilder.toString(), Long.class, values.toArray());
            log.info("成功插入数据到表 {}, ID: {}", dynamicTable.getTableName(), id);
            return id;
        } catch (Exception e) {
            log.error("插入数据失败: {}", sqlBuilder, e);
            throw new ServiceException(500, "插入数据失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateData(Long modelId, Long recordId, Map<Long, Object> data) {
        // 1. 获取动态表配置
        DynamicTableDO dynamicTable = getDynamicTableByModelId(modelId);
        if (dynamicTable == null) {
            throw new ServiceException(404, "模型未创建动态表");
        }

        // 2. 获取字段配置
        List<DynamicTableColumnDO> columns = listDynamicTableColumns(dynamicTable.getId());
        Map<Long, DynamicTableColumnDO> columnMap = columns.stream()
                .collect(Collectors.toMap(DynamicTableColumnDO::getFieldId, c -> c));

        // 3. 构建 UPDATE SQL
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("UPDATE ").append(dynamicTable.getTableName()).append(" SET ");
        
        List<String> setClauses = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        
        // 添加更新字段
        setClauses.add("updater = ?");
        values.add(getLoginUserName());
        setClauses.add("update_time = ?");
        values.add(LocalDateTime.now());

        // 添加业务字段
        for (Map.Entry<Long, Object> entry : data.entrySet()) {
            DynamicTableColumnDO column = columnMap.get(entry.getKey());
            if (column != null) {
                setClauses.add(column.getColumnName() + " = ?");
                values.add(entry.getValue());
            }
        }

        sqlBuilder.append(String.join(", ", setClauses));
        sqlBuilder.append(" WHERE id = ? AND tenant_id = ? AND deleted = false");
        values.add(recordId);
        values.add(getTenantId());

        // 4. 执行 UPDATE
        try {
            int affected = jdbcTemplate.update(sqlBuilder.toString(), values.toArray());
            if (affected == 0) {
                throw new ServiceException(404, "记录不存在或已删除");
            }
            log.info("成功更新数据, 表: {}, ID: {}", dynamicTable.getTableName(), recordId);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新数据失败: {}", sqlBuilder, e);
            throw new ServiceException(500, "更新数据失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteData(Long modelId, Long recordId) {
        // 1. 获取动态表配置
        DynamicTableDO dynamicTable = getDynamicTableByModelId(modelId);
        if (dynamicTable == null) {
            throw new ServiceException(404, "模型未创建动态表");
        }

        // 2. 构建逻辑删除 SQL
        String sql = String.format(
                "UPDATE %s SET deleted = true, updater = ?, update_time = ? WHERE id = ? AND tenant_id = ? AND deleted = false",
                dynamicTable.getTableName());

        // 3. 执行逻辑删除
        try {
            int affected = jdbcTemplate.update(sql, getLoginUserName(), LocalDateTime.now(), recordId, getTenantId());
            if (affected == 0) {
                throw new ServiceException(404, "记录不存在或已删除");
            }
            log.info("成功删除数据, 表: {}, ID: {}", dynamicTable.getTableName(), recordId);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除数据失败", e);
            throw new ServiceException(500, "删除数据失败: " + e.getMessage());
        }
    }

    @Override
    public Map<Long, Object> getData(Long modelId, Long recordId) {
        // 1. 获取动态表配置
        DynamicTableDO dynamicTable = getDynamicTableByModelId(modelId);
        if (dynamicTable == null) {
            throw new ServiceException(404, "模型未创建动态表");
        }

        // 2. 获取字段配置
        List<DynamicTableColumnDO> columns = listDynamicTableColumns(dynamicTable.getId());

        // 3. 构建查询 SQL
        String sql = String.format(
                "SELECT * FROM %s WHERE id = ? AND tenant_id = ? AND deleted = false",
                dynamicTable.getTableName());

        // 4. 执行查询
        try {
            Map<String, Object> row = jdbcTemplate.queryForMap(sql, recordId, getTenantId());
            return convertRowToFieldMap(row, columns);
        } catch (Exception e) {
            log.error("查询数据失败", e);
            return null;
        }
    }

    @Override
    public List<Map<Long, Object>> listData(Long modelId, int pageNo, int pageSize, Map<Long, Object> conditions) {
        // 1. 获取动态表配置
        DynamicTableDO dynamicTable = getDynamicTableByModelId(modelId);
        if (dynamicTable == null) {
            throw new ServiceException(404, "模型未创建动态表");
        }

        // 2. 获取字段配置
        List<DynamicTableColumnDO> columns = listDynamicTableColumns(dynamicTable.getId());
        Map<Long, DynamicTableColumnDO> columnMap = columns.stream()
                .collect(Collectors.toMap(DynamicTableColumnDO::getFieldId, c -> c));

        // 3. 构建查询 SQL
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT * FROM ").append(dynamicTable.getTableName());
        sqlBuilder.append(" WHERE tenant_id = ? AND deleted = false");
        
        List<Object> params = new ArrayList<>();
        params.add(getTenantId());

        // 添加条件
        if (CollUtil.isNotEmpty(conditions)) {
            for (Map.Entry<Long, Object> entry : conditions.entrySet()) {
                DynamicTableColumnDO column = columnMap.get(entry.getKey());
                if (column != null && entry.getValue() != null) {
                    sqlBuilder.append(" AND ").append(column.getColumnName()).append(" = ?");
                    params.add(entry.getValue());
                }
            }
        }

        // 添加分页
        int offset = (pageNo - 1) * pageSize;
        sqlBuilder.append(" ORDER BY id DESC LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add(offset);

        // 4. 执行查询
        try {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sqlBuilder.toString(), params.toArray());
            return rows.stream()
                    .map(row -> convertRowToFieldMap(row, columns))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("查询数据列表失败", e);
            return Collections.emptyList();
        }
    }

    @Override
    public long countData(Long modelId, Map<Long, Object> conditions) {
        // 1. 获取动态表配置
        DynamicTableDO dynamicTable = getDynamicTableByModelId(modelId);
        if (dynamicTable == null) {
            throw new ServiceException(404, "模型未创建动态表");
        }

        // 2. 获取字段配置
        List<DynamicTableColumnDO> columns = listDynamicTableColumns(dynamicTable.getId());
        Map<Long, DynamicTableColumnDO> columnMap = columns.stream()
                .collect(Collectors.toMap(DynamicTableColumnDO::getFieldId, c -> c));

        // 3. 构建统计 SQL
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT COUNT(*) FROM ").append(dynamicTable.getTableName());
        sqlBuilder.append(" WHERE tenant_id = ? AND deleted = false");
        
        List<Object> params = new ArrayList<>();
        params.add(getTenantId());

        // 添加条件
        if (CollUtil.isNotEmpty(conditions)) {
            for (Map.Entry<Long, Object> entry : conditions.entrySet()) {
                DynamicTableColumnDO column = columnMap.get(entry.getKey());
                if (column != null && entry.getValue() != null) {
                    sqlBuilder.append(" AND ").append(column.getColumnName()).append(" = ?");
                    params.add(entry.getValue());
                }
            }
        }

        // 4. 执行统计
        try {
            Long count = jdbcTemplate.queryForObject(sqlBuilder.toString(), Long.class, params.toArray());
            return count != null ? count : 0;
        } catch (Exception e) {
            log.error("统计数据失败", e);
            return 0;
        }
    }

    // ==================== 审计日志 ====================

    @Override
    public List<DynamicTableAuditLogDO> listAuditLogs(Long dynamicTableId) {
        return dynamicTableAuditLogMapper.selectByDynamicTableId(dynamicTableId);
    }

    // ==================== 表结构管理方法（供 EntityTypeBaseFieldService 使用） ====================

    @Override
    public boolean tableExists(String tableName) {
        if (StrUtil.isBlank(tableName)) {
            return false;
        }
        String sql = "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, tableName.toLowerCase());
        return count != null && count > 0;
    }

    @Override
    public boolean columnExists(String tableName, String columnName) {
        if (StrUtil.isBlank(tableName) || StrUtil.isBlank(columnName)) {
            return false;
        }
        String sql = "SELECT COUNT(*) FROM information_schema.columns WHERE table_name = ? AND column_name = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, 
                tableName.toLowerCase(), columnName.toLowerCase());
        return count != null && count > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addColumn(String tableName, cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeBaseFieldDO baseField) {
        if (baseField == null || StrUtil.isBlank(baseField.getFieldCode())) {
            throw new ServiceException(400, "字段定义不能为空");
        }
        
        // 列名与字段编码对齐：FLD-BASE-facility-REF_REGION → fld_base_facility_ref_region
        String columnName = baseField.getFieldCode().toLowerCase().replace("-", "_");
        String deprecatedColumnName = "_deprecated_" + columnName;

        // 曾废弃：恢复列名
        if (!columnExists(tableName, columnName) && columnExists(tableName, deprecatedColumnName)) {
            String renameSql = String.format("ALTER TABLE %s RENAME COLUMN %s TO %s",
                    tableName, deprecatedColumnName, columnName);
            try {
                jdbcTemplate.execute(renameSql);
                log.info("恢复废弃列 {} -> {} (表: {})", deprecatedColumnName, columnName, tableName);
            } catch (Exception e) {
                log.error("恢复废弃列失败: {}", renameSql, e);
                throw new ServiceException(500, "恢复废弃列失败: " + e.getMessage());
            }
            return;
        }

        // 检查列是否已存在
        if (columnExists(tableName, columnName)) {
            log.warn("列 {} 已存在于表 {}", columnName, tableName);
            return;
        }

        // 映射数据类型
        String dataType = mapFieldTypeToDbType(baseField.getDataType());

        // 构建 ALTER TABLE SQL
        StringBuilder alterSql = new StringBuilder();
        alterSql.append("ALTER TABLE ").append(tableName);
        alterSql.append(" ADD COLUMN ").append(columnName).append(" ").append(dataType);

        // 添加默认值（如果有）
        if (StrUtil.isNotBlank(baseField.getDefaultValue())) {
            alterSql.append(" DEFAULT '").append(baseField.getDefaultValue().replace("'", "''")).append("'");
        }

        String executedSql = alterSql.toString();
        try {
            jdbcTemplate.execute(executedSql);
            log.info("成功添加列 {} 到表 {}", columnName, tableName);

            // 添加列注释（字段编码 + 显示名）
            String comment = StrUtil.blankToDefault(baseField.getFieldCode(), columnName);
            if (StrUtil.isNotBlank(baseField.getFieldName())) {
                comment = comment + "（" + baseField.getFieldName() + "）";
            } else if (StrUtil.isNotBlank(baseField.getDescription())) {
                comment = baseField.getDescription();
            }
            String commentSql = String.format("COMMENT ON COLUMN %s.%s IS '%s'",
                    tableName, columnName, comment.replace("'", "''"));
            jdbcTemplate.execute(commentSql);
        } catch (Exception e) {
            log.error("添加列失败: {}", executedSql, e);
            throw new ServiceException(500, "添加列失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deprecateColumn(String tableName, String columnName) {
        if (StrUtil.isBlank(columnName)) {
            throw new ServiceException(400, "列名不能为空");
        }
        
        // 检查列是否存在
        if (!columnExists(tableName, columnName)) {
            log.warn("列 {} 不存在于表 {}", columnName, tableName);
            return;
        }
        
        // 检查列是否已经是废弃状态
        if (columnName.startsWith("_deprecated_")) {
            log.warn("列 {} 已经是废弃状态", columnName);
            return;
        }
        
        // 重命名列（添加 _deprecated_ 前缀）
        String deprecatedColumnName = "_deprecated_" + columnName;
        
        // 检查目标列名是否已存在（可能之前删除操作失败后留下的）
        if (columnExists(tableName, deprecatedColumnName)) {
            log.warn("目标列 {} 已存在于表 {}，可能之前已废弃，跳过重命名操作", deprecatedColumnName, tableName);
            return;
        }
        
        String renameSql = String.format("ALTER TABLE %s RENAME COLUMN %s TO %s",
                tableName, columnName, deprecatedColumnName);
        
        try {
            jdbcTemplate.execute(renameSql);
            log.info("成功废弃列 {} -> {} (表: {})", columnName, deprecatedColumnName, tableName);
        } catch (Exception e) {
            log.error("废弃列失败: {}", renameSql, e);
            throw new ServiceException(500, "废弃列失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addPhysicalColumn(String tableName, String fieldCode, PhysicalColumnConfig config) {
        // 1. 参数校验
        if (StrUtil.isBlank(tableName)) {
            throw new ServiceException(400, "表名不能为空");
        }
        if (StrUtil.isBlank(fieldCode)) {
            throw new ServiceException(400, "字段编码不能为空");
        }
        if (config == null) {
            throw new ServiceException(400, "物理列配置不能为空");
        }
        
        // 2. 验证配置有效性
        String validationError = config.validate();
        if (validationError != null) {
            throw new ServiceException(400, "物理列配置无效: " + validationError);
        }
        
        // 3. 检查表是否存在
        if (!tableExists(tableName)) {
            throw new ServiceException(404, "表不存在: " + tableName);
        }
        
        String columnName = config.getColumn();
        
        // 4. 检查列是否已存在（如果存在则报错，避免配置与数据库不一致）
        if (columnExists(tableName, columnName)) {
            throw new ServiceException(400, 
                    String.format("列 %s 已存在于表 %s，无法添加。如需使用该列名，请先通过数据库管理工具删除或重命名现有列", 
                            columnName, tableName));
        }
        
        // 5. 构建 ALTER TABLE SQL
        String dbType = config.toDbType();
        String alterSql = String.format("ALTER TABLE %s ADD COLUMN %s %s",
                tableName, columnName, dbType);
        
        try {
            // 6. 执行 ALTER TABLE 添加列
            jdbcTemplate.execute(alterSql);
            log.info("成功添加物理列 {} ({}) 到表 {}", columnName, dbType, tableName);
            
            // 7. 添加列注释
            String commentSql = String.format("COMMENT ON COLUMN %s.%s IS '物理列: %s'",
                    tableName, columnName, fieldCode);
            jdbcTemplate.execute(commentSql);
            
            // 8. 创建 B-Tree 索引
            String safeTableName = tableName.replace(".", "_").replace("-", "_");
            String indexName = String.format("idx_%s_%s", safeTableName, columnName);
            String indexSql = String.format("CREATE INDEX IF NOT EXISTS %s ON %s(%s)",
                    indexName, tableName, columnName);
            jdbcTemplate.execute(indexSql);
            log.info("成功为物理列 {} 创建索引 {}", columnName, indexName);
            
        } catch (Exception e) {
            log.error("添加物理列失败: {}", alterSql, e);
            throw new ServiceException(500, "添加物理列失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void dropPhysicalColumn(String tableName, String columnName) {
        // 1. 参数校验
        if (StrUtil.isBlank(tableName)) {
            throw new ServiceException(400, "表名不能为空");
        }
        if (StrUtil.isBlank(columnName)) {
            throw new ServiceException(400, "列名不能为空");
        }
        
        // 2. 检查表是否存在
        if (!tableExists(tableName)) {
            throw new ServiceException(404, "表不存在: " + tableName);
        }
        
        // 3. 检查列是否存在
        if (!columnExists(tableName, columnName)) {
            log.warn("列 {} 不存在于表 {}，跳过删除", columnName, tableName);
            return;
        }
        
        // 4. 删除索引（如果存在）
        String safeTableName = tableName.replace(".", "_").replace("-", "_");
        String indexName = String.format("idx_%s_%s", safeTableName, columnName);
        String dropIndexSql = String.format("DROP INDEX IF EXISTS %s", indexName);
        
        try {
            jdbcTemplate.execute(dropIndexSql);
            log.info("成功删除物理列索引 {}", indexName);
        } catch (Exception e) {
            log.warn("删除索引失败（可能不存在）: {}", indexName, e);
        }
        
        // 5. 构建 ALTER TABLE DROP COLUMN SQL
        String dropColumnSql = String.format("ALTER TABLE %s DROP COLUMN %s",
                tableName, columnName);
        
        try {
            jdbcTemplate.execute(dropColumnSql);
            log.info("成功删除物理列 {} 从表 {}", columnName, tableName);
        } catch (Exception e) {
            log.error("删除物理列失败: {}", dropColumnSql, e);
            throw new ServiceException(500, "删除物理列失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyPhysicalColumn(String tableName, String fieldCode, 
                                     PhysicalColumnConfig oldConfig, PhysicalColumnConfig newConfig) {
        // 1. 参数校验
        if (StrUtil.isBlank(tableName)) {
            throw new ServiceException(400, "表名不能为空");
        }
        if (StrUtil.isBlank(fieldCode)) {
            throw new ServiceException(400, "字段编码不能为空");
        }
        if (oldConfig == null || newConfig == null) {
            throw new ServiceException(400, "物理列配置不能为空");
        }
        
        // 2. 验证新配置有效性
        String validationError = newConfig.validate();
        if (validationError != null) {
            throw new ServiceException(400, "新物理列配置无效: " + validationError);
        }
        
        // 3. 检查表是否存在
        if (!tableExists(tableName)) {
            throw new ServiceException(404, "表不存在: " + tableName);
        }
        
        String oldColumnName = oldConfig.getColumn();
        String newColumnName = newConfig.getColumn();
        
        // 4. 检查旧列是否存在
        if (!columnExists(tableName, oldColumnName)) {
            throw new ServiceException(404, "列不存在: " + oldColumnName);
        }
        
        String safeTableName = tableName.replace(".", "_").replace("-", "_");
        
        try {
            // 5. 如果列名变更，先重命名列
            if (!oldColumnName.equals(newColumnName)) {
                // 检查新列名是否已存在
                if (columnExists(tableName, newColumnName)) {
                    throw new ServiceException(400, "新列名已存在: " + newColumnName);
                }
                
                String renameSql = String.format("ALTER TABLE %s RENAME COLUMN %s TO %s",
                        tableName, oldColumnName, newColumnName);
                jdbcTemplate.execute(renameSql);
                log.info("成功重命名列 {} -> {} (表: {})", oldColumnName, newColumnName, tableName);
                
                // 重命名索引
                String oldIndexName = String.format("idx_%s_%s", safeTableName, oldColumnName);
                String newIndexName = String.format("idx_%s_%s", safeTableName, newColumnName);
                String renameIndexSql = String.format("ALTER INDEX IF EXISTS %s RENAME TO %s",
                        oldIndexName, newIndexName);
                try {
                    jdbcTemplate.execute(renameIndexSql);
                    log.info("成功重命名索引 {} -> {}", oldIndexName, newIndexName);
                } catch (Exception e) {
                    log.warn("重命名索引失败（可能不存在）: {}", oldIndexName, e);
                }
            }
            
            // 6. 如果数据类型变更，修改列类型
            String oldDbType = oldConfig.toDbType();
            String newDbType = newConfig.toDbType();
            if (!oldDbType.equals(newDbType)) {
                // PostgreSQL 使用 ALTER COLUMN ... TYPE ... USING 语法
                String alterTypeSql = String.format(
                        "ALTER TABLE %s ALTER COLUMN %s TYPE %s USING %s::%s",
                        tableName, newColumnName, newDbType, newColumnName, newDbType);
                jdbcTemplate.execute(alterTypeSql);
                log.info("成功修改列 {} 类型: {} -> {} (表: {})", 
                        newColumnName, oldDbType, newDbType, tableName);
            }
            
            // 7. 更新列注释
            String commentSql = String.format("COMMENT ON COLUMN %s.%s IS '物理列: %s'",
                    tableName, newColumnName, fieldCode);
            jdbcTemplate.execute(commentSql);
            
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("修改物理列失败: fieldCode={}, tableName={}", fieldCode, tableName, e);
            throw new ServiceException(500, "修改物理列失败: " + e.getMessage());
        }
    }

    // ==================== 私有方法 ====================

    /**
     * 保证专用表 {@code id} 具备 nextval 默认值，并将序列对齐到 {@code MAX(id)}。
     *
     * <p>新建表模板使用 {@code BIGSERIAL}，一般已自带序列；本方法覆盖两类缺口：</p>
     * <ul>
     *   <li>历史 Flyway / 手工建表只有 {@code id bigint NOT NULL}，无 DEFAULT；</li>
     *   <li>seed 显式写入大 id 后序列未推进，后续插入主键冲突。</li>
     * </ul>
     */
    private void ensureDedicatedTableIdSerial(String tableName) {
        if (StrUtil.isBlank(tableName) || !tableName.matches("(?i)^[a-z][a-z0-9_]*$")) {
            throw new ServiceException(400, "非法表名: " + tableName);
        }
        if (!tableExists(tableName) || !columnExists(tableName, "id")) {
            return;
        }
        String safeTable = tableName.toLowerCase();
        String seqQual = "dynamicbusiness." + safeTable + "_id_seq";
        try {
            String idDefault = jdbcTemplate.query(
                    """
                    SELECT pg_get_expr(d.adbin, d.adrelid)
                    FROM pg_class c
                    JOIN pg_namespace n ON n.oid = c.relnamespace
                    JOIN pg_attribute a ON a.attrelid = c.oid AND a.attname = 'id'
                        AND NOT a.attisdropped AND a.attnum > 0
                    LEFT JOIN pg_attrdef d ON d.adrelid = c.oid AND d.adnum = a.attnum
                    WHERE n.nspname = 'dynamicbusiness' AND c.relname = ?
                    """,
                    rs -> rs.next() ? rs.getString(1) : null,
                    safeTable);
            if (StrUtil.isNotBlank(idDefault) && idDefault.startsWith("nextval(")) {
                int start = idDefault.indexOf('\'');
                int end = idDefault.indexOf('\'', start + 1);
                if (start >= 0 && end > start) {
                    seqQual = idDefault.substring(start + 1, end);
                }
            } else {
                jdbcTemplate.execute("CREATE SEQUENCE IF NOT EXISTS " + seqQual);
                jdbcTemplate.execute(
                        "ALTER TABLE dynamicbusiness." + safeTable
                                + " ALTER COLUMN id SET DEFAULT nextval('" + seqQual + "'::regclass)");
                try {
                    jdbcTemplate.execute(
                            "ALTER SEQUENCE " + seqQual + " OWNED BY dynamicbusiness." + safeTable + ".id");
                } catch (Exception ownedEx) {
                    log.debug("OWNED BY 可忽略: table={}, err={}", safeTable, ownedEx.getMessage());
                }
            }
            jdbcTemplate.queryForObject(
                    "SELECT setval(?::regclass, COALESCE((SELECT MAX(id) FROM dynamicbusiness." + safeTable + "), 1),"
                            + " EXISTS (SELECT 1 FROM dynamicbusiness." + safeTable + "))",
                    Long.class,
                    seqQual);
            log.info("[ensureDedicatedTableIdSerial] 已校准 id 序列: table={}, seq={}", safeTable, seqQual);
        } catch (Exception e) {
            log.error("[ensureDedicatedTableIdSerial] 校准失败: table={}", safeTable, e);
            throw new ServiceException(500, "校准专用表主键序列失败: " + safeTable + " — " + e.getMessage());
        }
    }

    /**
     * 生成表名
     */
    private String generateTableName(ModelDO model) {
        String entityTypeCode = StrUtil.isNotBlank(model.getEntityTypeCode()) 
                ? model.getEntityTypeCode().toLowerCase() : "default";
        String modelCode = model.getCode().toLowerCase().replace("-", "_");
        return "ent_" + entityTypeCode + "_" + modelCode;
    }

    /**
     * 生成列名
     */
    private String generateColumnName(FieldDO field) {
        // 将字段编码转换为下划线命名
        return "f_" + field.getCode().toLowerCase().replace("-", "_");
    }

    /**
     * 将字段类型映射为数据库类型
     */
    private String mapFieldTypeToDbType(String fieldType) {
        if (fieldType == null) {
            return "VARCHAR(255)";
        }
        return switch (fieldType.toUpperCase()) {
            case "TEXT" -> "VARCHAR(255)";
            case "LONG_TEXT" -> "TEXT";
            case "NUMBER" -> "NUMERIC(18,4)";
            case "INTEGER" -> "BIGINT";
            case "DATE" -> "DATE";
            case "DATETIME" -> "TIMESTAMP";
            case "BOOLEAN" -> "BOOLEAN";
            case "ENUM" -> "VARCHAR(100)";
            case "JSON" -> "JSONB";
            case "REF", "ENTITY_REF" -> "BIGINT";
            case "ENTITY_REF_MULTI", "MULTI_REF" -> "JSONB";
            default -> "VARCHAR(255)";
        };
    }

    /**
     * 构建创建表的 SQL
     * 
     * DEDICATED 表结构设计：
     * - 基础列：与 dynamic_entity 表结构一致，支持 EAV 模式
     * - 扩展字段：存储在 JSONB (attrs/custom_fields) 中
     * - 查询优化：扩展字段通过 dynamic_entity_field_index 索引表加速
     * 
     * 与 GENERIC 的区别：数据隔离（每个业务独立表），查询性能相同
     * 有 physicalColumnMapping 配置时：字段存储到物理列，查询性能更高
     */
    private String buildCreateTableSql(String tableName, String tableComment, 
                                       Map<String, PhysicalColumnConfig> physicalColumnMapping) {
        String safeTableName = tableName.replace(".", "_").replace("-", "_");
        
        // 构建物理列定义
        StringBuilder physicalColumnsBuilder = new StringBuilder();
        StringBuilder physicalColumnCommentsBuilder = new StringBuilder();
        StringBuilder physicalColumnIndexesBuilder = new StringBuilder();
        
        if (CollUtil.isNotEmpty(physicalColumnMapping)) {
            for (Map.Entry<String, PhysicalColumnConfig> entry : physicalColumnMapping.entrySet()) {
                String fieldCode = entry.getKey();
                PhysicalColumnConfig config = entry.getValue();
                
                if (config == null || !config.isValid()) {
                    log.warn("跳过无效的物理列配置: fieldCode={}", fieldCode);
                    continue;
                }
                
                String columnName = config.getColumn();
                String dbType = config.toDbType();
                
                // 添加列定义
                physicalColumnsBuilder.append(String.format(
                        "                    %s %s,\n", columnName, dbType));
                
                // 添加列注释
                physicalColumnCommentsBuilder.append(String.format(
                        "                COMMENT ON COLUMN %s.%s IS '物理列: %s';\n", 
                        tableName, columnName, fieldCode));
                
                // 添加 B-Tree 索引
                String indexName = String.format("idx_%s_%s", safeTableName, columnName);
                physicalColumnIndexesBuilder.append(String.format(
                        "                CREATE INDEX IF NOT EXISTS %s ON %s(%s);\n",
                        indexName, tableName, columnName));
            }
        }
        
        String physicalColumns = physicalColumnsBuilder.toString();
        String physicalColumnComments = physicalColumnCommentsBuilder.toString();
        String physicalColumnIndexes = physicalColumnIndexesBuilder.toString();
        
        return String.format("""
                CREATE TABLE IF NOT EXISTS %s (
                    -- ==================== 主键和租户 ====================
                    id BIGSERIAL PRIMARY KEY,
                    tenant_id BIGINT NOT NULL DEFAULT 0,
                    
                    -- ==================== 核心业务字段（与 dynamic_entity 一致） ====================
                    entity_type_code VARCHAR(64),
                    model_id BIGINT NOT NULL,
                    name VARCHAR(255) NOT NULL,
                    code VARCHAR(100),
                    status INTEGER DEFAULT 1,
                    area_id BIGINT,
                    parent_id BIGINT DEFAULT 0,
                    
                    -- ==================== 物理列（高性能查询字段） ====================
%s
                    -- ==================== 扩展字段（EAV 模式） ====================
                    attrs JSONB DEFAULT '{}',
                    custom_fields JSONB DEFAULT '{}',
                    
                    -- ==================== 系统字段 ====================
                    creator VARCHAR(64),
                    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updater VARCHAR(64),
                    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    deleted BOOLEAN DEFAULT FALSE
                );
                
                -- 表注释
                COMMENT ON TABLE %s IS '%s';
                COMMENT ON COLUMN %s.entity_type_code IS '业务类型编码';
                COMMENT ON COLUMN %s.model_id IS '所属 Model ID';
                COMMENT ON COLUMN %s.name IS '名称';
                COMMENT ON COLUMN %s.code IS '编码';
                COMMENT ON COLUMN %s.status IS '状态';
                COMMENT ON COLUMN %s.area_id IS '所属区域 ID';
                COMMENT ON COLUMN %s.parent_id IS '父节点 ID（0 表示根节点）';
                COMMENT ON COLUMN %s.attrs IS '扩展字段（JSONB）';
                COMMENT ON COLUMN %s.custom_fields IS '自定义字段（JSONB）';
%s
                -- 基础索引
                CREATE INDEX IF NOT EXISTS idx_%s_tenant ON %s(tenant_id);
                CREATE INDEX IF NOT EXISTS idx_%s_ent_type ON %s(entity_type_code);
                CREATE INDEX IF NOT EXISTS idx_%s_model ON %s(model_id);
                CREATE INDEX IF NOT EXISTS idx_%s_status ON %s(status);
                CREATE INDEX IF NOT EXISTS idx_%s_area ON %s(area_id);
                CREATE INDEX IF NOT EXISTS idx_%s_parent ON %s(parent_id);
                CREATE INDEX IF NOT EXISTS idx_%s_deleted ON %s(deleted);
                CREATE INDEX IF NOT EXISTS idx_%s_attrs ON %s USING GIN(attrs);
                
                -- 物理列索引（B-Tree，高性能查询）
%s
                """, 
                tableName, 
                physicalColumns,
                tableName, tableComment,
                tableName, tableName, tableName, tableName, tableName, tableName, tableName, tableName, tableName,
                physicalColumnComments,
                safeTableName, tableName,
                safeTableName, tableName,
                safeTableName, tableName,
                safeTableName, tableName,
                safeTableName, tableName,
                safeTableName, tableName,
                safeTableName, tableName,
                safeTableName, tableName,
                physicalColumnIndexes);
    }

    /**
     * 获取下一个排序顺序
     */
    private Integer getNextSortOrder(Long dynamicTableId) {
        List<DynamicTableColumnDO> columns = dynamicTableColumnMapper.selectByDynamicTableId(dynamicTableId);
        return columns.isEmpty() ? 1 : columns.stream()
                .mapToInt(DynamicTableColumnDO::getSortOrder)
                .max()
                .orElse(0) + 1;
    }

    /**
     * 将数据库行转换为字段ID映射
     */
    private Map<Long, Object> convertRowToFieldMap(Map<String, Object> row, List<DynamicTableColumnDO> columns) {
        Map<Long, Object> result = new HashMap<>();
        // 添加基础字段
        result.put(-1L, row.get("id")); // 使用 -1 表示记录ID
        
        // 添加业务字段
        for (DynamicTableColumnDO column : columns) {
            Object value = row.get(column.getColumnName());
            if (value != null) {
                result.put(column.getFieldId(), value);
            }
        }
        return result;
    }

    /**
     * 保存审计日志
     */
    private void saveAuditLog(Long dynamicTableId, String operationType, String operationDesc,
                              String beforeConfig, String afterConfig, String executedSql,
                              String executeResult, String errorMessage) {
        DynamicTableAuditLogDO auditLog = DynamicTableAuditLogDO.builder()
                .dynamicTableId(dynamicTableId)
                .operationType(operationType)
                .operationDesc(operationDesc)
                .beforeConfig(beforeConfig)
                .afterConfig(afterConfig)
                .executedSql(executedSql)
                .executeResult(executeResult)
                .errorMessage(errorMessage)
                .operationTime(LocalDateTime.now())
                .operatorId(getLoginUserId())
                .operatorName(getLoginUserName())
                .build();
        auditLog.setTenantId(getTenantId());
        dynamicTableAuditLogMapper.insert(auditLog);
    }

    private Long getTenantId() {
        return Objects.requireNonNullElse(TenantContextHolder.getRequiredTenantId(), 0L);
    }

    private Long getLoginUserId() {
        return SecurityFrameworkUtils.getLoginUserId();
    }

    private String getLoginUserName() {
        Long userId = getLoginUserId();
        return userId != null ? String.valueOf(userId) : "system";
    }
}
