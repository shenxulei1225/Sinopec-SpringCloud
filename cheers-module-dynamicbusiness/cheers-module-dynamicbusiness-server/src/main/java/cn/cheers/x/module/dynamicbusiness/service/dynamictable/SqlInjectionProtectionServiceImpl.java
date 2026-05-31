package cn.cheers.x.module.dynamicbusiness.service.dynamictable;

import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.businesstype.BusinessTypeBaseFieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.dynamictable.DynamicTableColumnDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.dynamictable.DynamicTableDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.businesstype.BusinessTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.businesstype.BusinessTypeBaseFieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.businesstype.BusinessTypeMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.dynamictable.DynamicTableColumnMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.dynamictable.DynamicTableMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.dynamictable.DynamicSqlAuditLogMapper;
import cn.cheers.x.module.dynamicbusiness.enums.businesstype.StorageTypeEnum;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.dynamictable.DynamicSqlAuditLogDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * SQL 注入防护服务实现类
 * 
 * <p>提供动态 SQL 执行的安全防护机制：</p>
 * <ul>
 *   <li>表名白名单验证：只允许已注册的动态表</li>
 *   <li>列名白名单验证：只允许 dynamic_business_type_base_field 中定义的字段</li>
 *   <li>所有值使用参数化查询（PreparedStatement）</li>
 *   <li>记录所有动态 SQL 执行的审计日志</li>
 * </ul>
 * 
 * <p>业务规则：BR-STG-033, BR-STG-034, BR-STG-035</p>
 * 
 * @author yudao
 */
@Service
@Slf4j
public class SqlInjectionProtectionServiceImpl implements SqlInjectionProtectionService {

    /**
     * 标识符格式正则：只允许字母、数字、下划线,且必须以字母或下划线开头
     */
    private static final Pattern IDENTIFIER_PATTERN = Pattern.compile("^[a-zA-Z_]\\w*$");

    /**
     * SQL 注入风险字符正则
     */
    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
            "(?i)(-- |;|'|\"|\\b(SELECT|INSERT|UPDATE|DELETE|DROP|TRUNCATE|ALTER|CREATE|EXEC|EXECUTE|UNION|OR|AND)\\b)",
            Pattern.CASE_INSENSITIVE
    );

    /**
     * 基础列名列表（所有动态表都包含的列）
     */
    private static final Set<String> BASE_COLUMNS = Set.of(
            "id", "tenant_id", "creator", "create_time", "updater", "update_time", "deleted",
            "business_type_code", "model_id", "name", "custom_fields", "status", "parent_id", "tree_path"
    );

    /**
     * 动态表白名单缓存
     */
    private final Set<String> tableWhitelist = ConcurrentHashMap.newKeySet();

    /**
     * 列名白名单缓存：表名 -> 允许的列名集合
     */
    private final Map<String, Set<String>> columnWhitelistCache = new ConcurrentHashMap<>();

    @Resource
    private DynamicTableMapper dynamicTableMapper;

    @Resource
    private DynamicTableColumnMapper dynamicTableColumnMapper;

    @Resource
    private BusinessTypeMapper businessTypeMapper;

    @Resource
    private BusinessTypeBaseFieldMapper businessTypeBaseFieldMapper;

    @Resource
    private DynamicSqlAuditLogMapper dynamicSqlAuditLogMapper;

    /**
     * 初始化时加载白名单
     */
    @PostConstruct
    public void init() {
        refreshTableWhitelist();
        log.info("SQL 注入防护服务初始化完成,已加载 {} 个动态表到白名单", tableWhitelist.size());
    }

    // ==================== 表名验证 ====================

    @Override
    public boolean isTableNameAllowed(String tableName) {
        if (StrUtil.isBlank(tableName)) {
            return false;
        }
        // 1. 检查格式是否合法
        if (!isValidIdentifier(tableName)) {
            return false;
        }
        // 2. 检查是否在白名单中
        return tableWhitelist.contains(tableName.toLowerCase());
    }

    @Override
    public void validateTableName(String tableName) {
        if (StrUtil.isBlank(tableName)) {
            log.warn("SQL 注入防护: 表名为空");
            throw new ServiceException(400, "表名不能为空");
        }
        if (!isValidIdentifier(tableName)) {
            log.warn("SQL 注入防护: 表名格式不合法 - {}", tableName);
            throw new ServiceException(400, "表名格式不合法: " + tableName);
        }
        // 检查是否包含 SQL 注入风险字符
        if (containsSqlInjectionRisk(tableName)) {
            log.error("SQL 注入防护: 检测到表名中包含危险字符 - {}, 操作人: {}, IP: {}", 
                    tableName, getLoginUserName(), getClientIp());
            throw new ServiceException(403, "表名包含非法字符: " + tableName);
        }
        if (!tableWhitelist.contains(tableName.toLowerCase())) {
            log.warn("SQL 注入防护: 尝试访问未注册的表 - {}, 操作人: {}, IP: {}", 
                    tableName, getLoginUserName(), getClientIp());
            throw new ServiceException(403, "表名不在允许列表中: " + tableName);
        }
    }

    @Override
    public void registerDynamicTable(String tableName) {
        if (StrUtil.isBlank(tableName)) {
            return;
        }
        if (!isValidIdentifier(tableName)) {
            throw new ServiceException(400, "表名格式不合法: " + tableName);
        }
        tableWhitelist.add(tableName.toLowerCase());
        log.info("注册动态表到白名单: {}", tableName);
    }

    @Override
    public void unregisterDynamicTable(String tableName) {
        if (StrUtil.isBlank(tableName)) {
            return;
        }
        tableWhitelist.remove(tableName.toLowerCase());
        columnWhitelistCache.remove(tableName.toLowerCase());
        log.info("从白名单移除动态表: {}", tableName);
    }

    @Override
    public Set<String> getRegisteredTables() {
        return Collections.unmodifiableSet(new HashSet<>(tableWhitelist));
    }

    @Override
    public void refreshTableWhitelist() {
        tableWhitelist.clear();
        
        // 1. 加载所有动态表
        List<DynamicTableDO> dynamicTables = dynamicTableMapper.selectAllActive();
        for (DynamicTableDO table : dynamicTables) {
            if (StrUtil.isNotBlank(table.getTableName())) {
                tableWhitelist.add(table.getTableName().toLowerCase());
            }
        }
        
        // 2. 加载所有 DEDICATED 类型的专用表
        List<BusinessTypeDO> businessTypes = businessTypeMapper.selectAllList();
        for (BusinessTypeDO bt : businessTypes) {
            StorageTypeEnum storageType = StorageTypeEnum.getByCode(bt.getStorageType());
            if (storageType != null && storageType.isDedicated() && StrUtil.isNotBlank(bt.getDedicatedTableName())) {
                tableWhitelist.add(bt.getDedicatedTableName().toLowerCase());
            }
        }
        
        log.info("刷新动态表白名单完成,共 {} 个表", tableWhitelist.size());
    }

    // ==================== 列名验证 ====================

    @Override
    public boolean isColumnNameAllowed(String tableName, String columnName) {
        if (StrUtil.isBlank(tableName) || StrUtil.isBlank(columnName)) {
            return false;
        }
        // 1. 检查格式是否合法
        if (!isValidIdentifier(columnName)) {
            return false;
        }
        // 2. 检查是否在白名单中
        Set<String> allowedColumns = getAllowedColumns(tableName);
        return allowedColumns.contains(columnName.toLowerCase());
    }

    @Override
    public void validateColumnName(String tableName, String columnName) {
        if (StrUtil.isBlank(columnName)) {
            log.warn("SQL 注入防护: 列名为空");
            throw new ServiceException(400, "列名不能为空");
        }
        if (!isValidIdentifier(columnName)) {
            log.warn("SQL 注入防护: 列名格式不合法 - {}.{}", tableName, columnName);
            throw new ServiceException(400, "列名格式不合法: " + columnName);
        }
        // 检查是否包含 SQL 注入风险字符
        if (containsSqlInjectionRisk(columnName)) {
            log.error("SQL 注入防护: 检测到列名中包含危险字符 - {}.{}, 操作人: {}, IP: {}", 
                    tableName, columnName, getLoginUserName(), getClientIp());
            throw new ServiceException(403, "列名包含非法字符: " + columnName);
        }
        if (!isColumnNameAllowed(tableName, columnName)) {
            log.warn("SQL 注入防护: 尝试访问未注册的列 - {}.{}, 操作人: {}, IP: {}", 
                    tableName, columnName, getLoginUserName(), getClientIp());
            throw new ServiceException(403, "列名不在允许列表中: " + tableName + "." + columnName);
        }
    }

    @Override
    public void validateColumnNames(String tableName, List<String> columnNames) {
        if (CollUtil.isEmpty(columnNames)) {
            return;
        }
        Set<String> allowedColumns = getAllowedColumns(tableName);
        List<String> invalidColumns = new ArrayList<>();
        
        for (String columnName : columnNames) {
            if (StrUtil.isBlank(columnName)) {
                throw new ServiceException(400, "列名不能为空");
            }
            if (!isValidIdentifier(columnName)) {
                throw new ServiceException(400, "列名格式不合法: " + columnName);
            }
            // 检查是否包含 SQL 注入风险字符
            if (containsSqlInjectionRisk(columnName)) {
                log.error("SQL 注入防护: 检测到列名中包含危险字符 - {}.{}, 操作人: {}, IP: {}", 
                        tableName, columnName, getLoginUserName(), getClientIp());
                throw new ServiceException(403, "列名包含非法字符: " + columnName);
            }
            if (!allowedColumns.contains(columnName.toLowerCase())) {
                invalidColumns.add(columnName);
            }
        }
        
        if (!invalidColumns.isEmpty()) {
            log.warn("SQL 注入防护: 尝试访问未注册的列 - {}.{}, 操作人: {}, IP: {}", 
                    tableName, invalidColumns, getLoginUserName(), getClientIp());
            throw new ServiceException(403, "列名不在允许列表中: " + tableName + "." + invalidColumns);
        }
    }

    @Override
    public Set<String> getAllowedColumns(String tableName) {
        if (StrUtil.isBlank(tableName)) {
            return Collections.emptySet();
        }
        
        String tableNameLower = tableName.toLowerCase();
        
        // 尝试从缓存获取
        Set<String> cached = columnWhitelistCache.get(tableNameLower);
        if (cached != null) {
            return cached;
        }
        
        // 构建列名白名单
        Set<String> allowedColumns = new HashSet<>(BASE_COLUMNS);
        
        // 1. 查找对应的业务类型（专用表 -> BusinessType）
        BusinessTypeDO businessType = businessTypeMapper.selectByDedicatedTableName(tableName);
        if (businessType != null) {
            // 2. 加载固定列字段
            List<BusinessTypeBaseFieldDO> baseFields =
                    businessTypeBaseFieldMapper.selectByBusinessTypeCode(businessType.getCode());
            for (BusinessTypeBaseFieldDO field : baseFields) {
                if (StrUtil.isNotBlank(field.getFieldCode())) {
                    // 转换为下划线命名
                    String columnName = camelToSnake(field.getFieldCode());
                    allowedColumns.add(columnName.toLowerCase());
                }
            }

            // 2.1 加载物理列映射配置中的列名（BusinessType.physicalColumnMapping JSON）
            if (StrUtil.isNotBlank(businessType.getPhysicalColumnMapping())) {
                try {
                    Map<String, cn.cheers.x.module.dynamicbusiness.dal.dataobject.businesstype.PhysicalColumnConfig> physicalColumnMapping =
                            new ObjectMapper().readValue(businessType.getPhysicalColumnMapping(),
                                    new TypeReference<Map<String, cn.cheers.x.module.dynamicbusiness.dal.dataobject.businesstype.PhysicalColumnConfig>>() {});
                    for (cn.cheers.x.module.dynamicbusiness.dal.dataobject.businesstype.PhysicalColumnConfig colConfig : physicalColumnMapping.values()) {
                        if (colConfig != null && StrUtil.isNotBlank(colConfig.getColumn())) {
                            allowedColumns.add(colConfig.getColumn().toLowerCase());
                        }
                    }
                } catch (Exception e) {
                    log.warn("[getAllowedColumns][解析 physicalColumnMapping 失败,tableName={}, businessTypeCode={}]", tableName, businessType.getCode(), e);
                }
            }
        }
        
        // 3. 查找动态表配置
        DynamicTableDO dynamicTable = dynamicTableMapper.selectByTableName(tableName);
        if (dynamicTable != null) {
            // 4. 加载动态列
            List<DynamicTableColumnDO> columns = 
                    dynamicTableColumnMapper.selectByDynamicTableId(dynamicTable.getId());
            for (DynamicTableColumnDO column : columns) {
                if (StrUtil.isNotBlank(column.getColumnName())) {
                    allowedColumns.add(column.getColumnName().toLowerCase());
                }
            }
        }
        
        // 缓存结果
        columnWhitelistCache.put(tableNameLower, allowedColumns);
        
        return allowedColumns;
    }

    @Override
    public void refreshColumnWhitelist(String tableName) {
        if (StrUtil.isBlank(tableName)) {
            return;
        }
        columnWhitelistCache.remove(tableName.toLowerCase());
        log.info("刷新列名白名单: {}", tableName);
    }

    // ==================== SQL 安全构建 ====================

    @Override
    public String buildSafeSelectSql(String tableName, List<String> columns, List<String> whereColumns) {
        // 1. 验证表名
        validateTableName(tableName);
        
        // 2. 构建 SELECT 子句
        StringBuilder sql = new StringBuilder("SELECT ");
        if (CollUtil.isEmpty(columns)) {
            sql.append("*");
        } else {
            validateColumnNames(tableName, columns);
            sql.append(columns.stream()
                    .map(this::quoteIdentifier)
                    .collect(Collectors.joining(", ")));
        }
        
        // 3. 构建 FROM 子句
        sql.append(" FROM ").append(quoteIdentifier(tableName));
        
        // 4. 构建 WHERE 子句
        if (CollUtil.isNotEmpty(whereColumns)) {
            validateColumnNames(tableName, whereColumns);
            sql.append(" WHERE ");
            sql.append(whereColumns.stream()
                    .map(col -> quoteIdentifier(col) + " = ?")
                    .collect(Collectors.joining(" AND ")));
        }
        
        return sql.toString();
    }

    @Override
    public String buildSafeInsertSql(String tableName, List<String> columns) {
        // 1. 验证表名
        validateTableName(tableName);
        
        // 2. 验证列名
        if (CollUtil.isEmpty(columns)) {
            throw new ServiceException(400, "INSERT 语句必须指定列名");
        }
        validateColumnNames(tableName, columns);
        
        // 3. 构建 INSERT SQL
        StringBuilder sql = new StringBuilder("INSERT INTO ");
        sql.append(quoteIdentifier(tableName));
        sql.append(" (");
        sql.append(columns.stream()
                .map(this::quoteIdentifier)
                .collect(Collectors.joining(", ")));
        sql.append(") VALUES (");
        sql.append(columns.stream()
                .map(col -> "?")
                .collect(Collectors.joining(", ")));
        sql.append(")");
        
        return sql.toString();
    }

    @Override
    public String buildSafeUpdateSql(String tableName, List<String> setColumns, List<String> whereColumns) {
        // 1. 验证表名
        validateTableName(tableName);
        
        // 2. 验证 SET 列名
        if (CollUtil.isEmpty(setColumns)) {
            throw new ServiceException(400, "UPDATE 语句必须指定要更新的列");
        }
        validateColumnNames(tableName, setColumns);
        
        // 3. 构建 UPDATE SQL
        StringBuilder sql = new StringBuilder("UPDATE ");
        sql.append(quoteIdentifier(tableName));
        sql.append(" SET ");
        sql.append(setColumns.stream()
                .map(col -> quoteIdentifier(col) + " = ?")
                .collect(Collectors.joining(", ")));
        
        // 4. 构建 WHERE 子句
        if (CollUtil.isNotEmpty(whereColumns)) {
            validateColumnNames(tableName, whereColumns);
            sql.append(" WHERE ");
            sql.append(whereColumns.stream()
                    .map(col -> quoteIdentifier(col) + " = ?")
                    .collect(Collectors.joining(" AND ")));
        }
        
        return sql.toString();
    }

    @Override
    public String buildSafeDeleteSql(String tableName, List<String> whereColumns) {
        // 1. 验证表名
        validateTableName(tableName);
        
        // 2. 构建逻辑删除 SQL
        StringBuilder sql = new StringBuilder("UPDATE ");
        sql.append(quoteIdentifier(tableName));
        sql.append(" SET deleted = true, updater = ?, update_time = ?");
        
        // 3. 构建 WHERE 子句
        if (CollUtil.isNotEmpty(whereColumns)) {
            validateColumnNames(tableName, whereColumns);
            sql.append(" WHERE ");
            sql.append(whereColumns.stream()
                    .map(col -> quoteIdentifier(col) + " = ?")
                    .collect(Collectors.joining(" AND ")));
        }
        
        return sql.toString();
    }

    // ==================== 审计日志 ====================

    @Override
    public void logSqlExecution(String tableName, String operationType, String sql,
                                List<Object> parameters, boolean success,
                                String errorMessage, Integer affectedRows) {
        try {
            DynamicSqlAuditLogDO auditLog = DynamicSqlAuditLogDO.builder()
                    .tableName(tableName)
                    .operationType(operationType)
                    .executedSql(sql)
                    .parameters(parameters != null ? parameters.toString() : null)
                    .success(success)
                    .errorMessage(errorMessage)
                    .affectedRows(affectedRows)
                    .executionTime(LocalDateTime.now())
                    .operatorId(getLoginUserId())
                    .operatorName(getLoginUserName())
                    .clientIp(getClientIp())
                    .requestId(getRequestId())
                    .build();
            auditLog.setTenantId(getTenantId());
            
            dynamicSqlAuditLogMapper.insert(auditLog);
            
            // 记录安全相关的日志
            if (!success) {
                log.warn("动态 SQL 执行失败 - 表: {}, 操作: {}, 错误: {}, 操作人: {}, IP: {}", 
                        tableName, operationType, errorMessage, getLoginUserName(), getClientIp());
            } else if (log.isDebugEnabled()) {
                log.debug("动态 SQL 执行成功 - 表: {}, 操作: {}, 影响行数: {}", 
                        tableName, operationType, affectedRows);
            }
        } catch (Exception e) {
            // 审计日志记录失败不应影响主业务
            log.error("记录动态 SQL 审计日志失败: {}", e.getMessage(), e);
        }
    }

    // ==================== 安全检查 ====================

    @Override
    public boolean containsSqlInjectionRisk(String input) {
        if (StrUtil.isBlank(input)) {
            return false;
        }
        return SQL_INJECTION_PATTERN.matcher(input).find();
    }

    @Override
    public String sanitizeInput(String input) {
        if (StrUtil.isBlank(input)) {
            return input;
        }
        // 移除危险字符
        return input.replaceAll("[;'\"\\-\\-]", "");
    }

    @Override
    public boolean isValidIdentifier(String identifier) {
        if (StrUtil.isBlank(identifier)) {
            return false;
        }
        // 长度限制
        if (identifier.length() > 128) {
            return false;
        }
        return IDENTIFIER_PATTERN.matcher(identifier).matches();
    }

    // ==================== 私有方法 ====================

    /**
     * 引用标识符（防止关键字冲突）
     */
    private String quoteIdentifier(String identifier) {
        // PostgreSQL 使用双引号引用标识符
        // 但为了简化,这里只对包含特殊字符的标识符进行引用
        // 由于我们已经验证了标识符格式,这里直接返回
        return identifier;
    }

    /**
     * 驼峰命名转下划线命名
     */
    private String camelToSnake(String camelCase) {
        if (StrUtil.isBlank(camelCase)) {
            return camelCase;
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < camelCase.length(); i++) {
            char c = camelCase.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i > 0) {
                    result.append('_');
                }
                result.append(Character.toLowerCase(c));
            } else {
                result.append(c);
            }
        }
        return result.toString();
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

    /**
     * 获取客户端 IP 地址
     */
    private String getClientIp() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                // 优先从 X-Forwarded-For 获取（经过代理的情况）
                String ip = request.getHeader("X-Forwarded-For");
                if (StrUtil.isNotBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
                    // 多个代理时,第一个 IP 是真实 IP
                    int index = ip.indexOf(',');
                    if (index != -1) {
                        return ip.substring(0, index).trim();
                    }
                    return ip.trim();
                }
                // 尝试其他常见的代理头
                ip = request.getHeader("X-Real-IP");
                if (StrUtil.isNotBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
                    return ip.trim();
                }
                ip = request.getHeader("Proxy-Client-IP");
                if (StrUtil.isNotBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
                    return ip.trim();
                }
                ip = request.getHeader("WL-Proxy-Client-IP");
                if (StrUtil.isNotBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
                    return ip.trim();
                }
                // 直接获取远程地址
                return request.getRemoteAddr();
            }
        } catch (Exception e) {
            log.debug("获取客户端 IP 失败: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 获取请求 ID（用于链路追踪）
     */
    private String getRequestId() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                // 尝试从常见的请求头获取请求 ID
                String requestId = request.getHeader("X-Request-ID");
                if (StrUtil.isNotBlank(requestId)) {
                    return requestId;
                }
                requestId = request.getHeader("X-Trace-ID");
                if (StrUtil.isNotBlank(requestId)) {
                    return requestId;
                }
                // 使用请求的唯一标识
                return String.valueOf(request.hashCode());
            }
        } catch (Exception e) {
            log.debug("获取请求 ID 失败: {}", e.getMessage());
        }
        return null;
    }
}
