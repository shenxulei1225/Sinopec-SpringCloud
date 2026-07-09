package cn.cheers.x.module.dynamicbusiness.service.dynamictable;

import java.util.List;
import java.util.Set;

/**
 * SQL 注入防护服务接口
 * 
 * <p>提供动态 SQL 执行的安全防护机制：</p>
 * <ul>
 *   <li>表名白名单验证：只允许已注册的动态表</li>
 *   <li>列名白名单验证：只允许 dynamic_entity_type_base_field 中定义的字段</li>
 *   <li>所有值使用参数化查询（PreparedStatement）</li>
 *   <li>记录所有动态 SQL 执行的审计日志</li>
 * </ul>
 * 
 * <p>业务规则：BR-STG-033, BR-STG-034, BR-STG-035</p>
 * 
 * @author yudao
 */
public interface SqlInjectionProtectionService {

    // ==================== 表名验证 ====================

    /**
     * 验证表名是否在白名单中
     * 
     * <p>只允许已注册的动态表名，防止 SQL 注入攻击。</p>
     * 
     * @param tableName 表名
     * @return 是否合法
     */
    boolean isTableNameAllowed(String tableName);

    /**
     * 验证表名并抛出异常
     * 
     * @param tableName 表名
     * @throws cn.iocoder.yudao.framework.common.exception.ServiceException 如果表名不合法
     */
    void validateTableName(String tableName);

    /**
     * 注册动态表到白名单
     * 
     * @param tableName 表名
     */
    void registerDynamicTable(String tableName);

    /**
     * 从白名单移除动态表
     * 
     * @param tableName 表名
     */
    void unregisterDynamicTable(String tableName);

    /**
     * 获取所有已注册的动态表名
     * 
     * @return 动态表名集合
     */
    Set<String> getRegisteredTables();

    /**
     * 刷新动态表白名单（从数据库重新加载）
     */
    void refreshTableWhitelist();

    // ==================== 列名验证 ====================

    /**
     * 验证列名是否在白名单中
     * 
     * <p>只允许以下列名：</p>
     * <ul>
     *   <li>基础列（id, tenant_id, creator, create_time 等）</li>
     *   <li>dynamic_entity_type_base_field 中定义的固定列字段</li>
     *   <li>dynamic_dynamic_table_column 中定义的动态列</li>
     * </ul>
     * 
     * @param tableName 表名
     * @param columnName 列名
     * @return 是否合法
     */
    boolean isColumnNameAllowed(String tableName, String columnName);

    /**
     * 验证列名并抛出异常
     * 
     * @param tableName 表名
     * @param columnName 列名
     * @throws cn.iocoder.yudao.framework.common.exception.ServiceException 如果列名不合法
     */
    void validateColumnName(String tableName, String columnName);

    /**
     * 批量验证列名
     * 
     * @param tableName 表名
     * @param columnNames 列名列表
     * @throws cn.iocoder.yudao.framework.common.exception.ServiceException 如果任一列名不合法
     */
    void validateColumnNames(String tableName, List<String> columnNames);

    /**
     * 获取表的所有允许列名
     * 
     * @param tableName 表名
     * @return 允许的列名集合
     */
    Set<String> getAllowedColumns(String tableName);

    /**
     * 刷新列名白名单（从数据库重新加载）
     * 
     * @param tableName 表名
     */
    void refreshColumnWhitelist(String tableName);

    // ==================== SQL 安全构建 ====================

    /**
     * 安全地构建 SELECT SQL
     * 
     * <p>验证表名和列名后构建 SQL，所有条件值使用参数化查询。</p>
     * 
     * @param tableName 表名
     * @param columns 要查询的列名列表（null 表示 SELECT *）
     * @param whereColumns WHERE 条件的列名列表
     * @return 安全的 SQL 语句（带 ? 占位符）
     */
    String buildSafeSelectSql(String tableName, List<String> columns, List<String> whereColumns);

    /**
     * 安全地构建 INSERT SQL
     * 
     * @param tableName 表名
     * @param columns 要插入的列名列表
     * @return 安全的 SQL 语句（带 ? 占位符）
     */
    String buildSafeInsertSql(String tableName, List<String> columns);

    /**
     * 安全地构建 UPDATE SQL
     * 
     * @param tableName 表名
     * @param setColumns SET 子句的列名列表
     * @param whereColumns WHERE 条件的列名列表
     * @return 安全的 SQL 语句（带 ? 占位符）
     */
    String buildSafeUpdateSql(String tableName, List<String> setColumns, List<String> whereColumns);

    /**
     * 安全地构建 DELETE SQL（逻辑删除）
     * 
     * @param tableName 表名
     * @param whereColumns WHERE 条件的列名列表
     * @return 安全的 SQL 语句（带 ? 占位符）
     */
    String buildSafeDeleteSql(String tableName, List<String> whereColumns);

    // ==================== 审计日志 ====================

    /**
     * 记录动态 SQL 执行的审计日志
     * 
     * @param tableName 表名
     * @param operationType 操作类型（SELECT/INSERT/UPDATE/DELETE）
     * @param sql 执行的 SQL 语句
     * @param parameters 参数值列表
     * @param success 是否执行成功
     * @param errorMessage 错误信息（失败时）
     * @param affectedRows 影响的行数
     */
    void logSqlExecution(String tableName, String operationType, String sql, 
                         List<Object> parameters, boolean success, 
                         String errorMessage, Integer affectedRows);

    // ==================== 安全检查 ====================

    /**
     * 检查字符串是否包含 SQL 注入风险字符
     * 
     * @param input 输入字符串
     * @return 是否包含风险字符
     */
    boolean containsSqlInjectionRisk(String input);

    /**
     * 清理输入字符串中的危险字符
     * 
     * <p>注意：这只是额外的防护层，主要防护应该依赖参数化查询。</p>
     * 
     * @param input 输入字符串
     * @return 清理后的字符串
     */
    String sanitizeInput(String input);

    /**
     * 验证标识符格式（表名、列名）
     * 
     * <p>只允许字母、数字、下划线，且必须以字母或下划线开头。</p>
     * 
     * @param identifier 标识符
     * @return 是否符合格式要求
     */
    boolean isValidIdentifier(String identifier);
}
