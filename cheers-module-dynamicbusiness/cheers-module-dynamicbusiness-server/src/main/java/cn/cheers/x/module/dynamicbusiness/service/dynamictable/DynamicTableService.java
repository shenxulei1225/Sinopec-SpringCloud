package cn.cheers.x.module.dynamicbusiness.service.dynamictable;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeBaseFieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.PhysicalColumnConfig;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.dynamictable.DynamicTableAuditLogDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.dynamictable.DynamicTableColumnDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.dynamictable.DynamicTableDO;

import java.util.List;
import java.util.Map;

/**
 * 动态表管理 Service 接口
 * 
 * @author yudao
 */
public interface DynamicTableService {

    // ==================== 表管理方法 ====================

    /**
     * 为业务类型创建动态表（在创建 EntityTypeConfig 时调用）
     * 
     * @param entityTypeCode 业务类型编码
     * @param tableName 表名
     * @param tableComment 表注释
     * @return 动态表ID
     */
    Long createDynamicTableForEntityType(String entityTypeCode, String tableName, String tableComment);

    /**
     * 为业务类型创建动态表（支持物理列映射配置）
     * 
     * <p>当配置了 physicalColumnMapping 时，会在创建表时添加物理列定义和 B-Tree 索引。
     * 物理列用于高频查询字段，提供更好的查询性能。</p>
     * 
     * @param entityTypeCode 业务类型编码
     * @param tableName 表名
     * @param tableComment 表注释
     * @param physicalColumnMapping 物理列映射配置，key 为字段编码，value 为列配置；可为 null
     * @return 动态表ID
     */
    Long createDynamicTableForEntityType(String entityTypeCode, String tableName, String tableComment,
                                           Map<String, PhysicalColumnConfig> physicalColumnMapping);

    Long createDynamicTable(Long modelId);
    void deleteDynamicTable(Long dynamicTableId);
    DynamicTableDO getDynamicTable(Long dynamicTableId);
    DynamicTableDO getDynamicTableByModelId(Long modelId);
    List<DynamicTableDO> listDynamicTablesByEntityType(String entityTypeCode);
    boolean hasDynamicTable(Long modelId);

    // ==================== 表结构管理方法 ====================

    boolean tableExists(String tableName);
    boolean columnExists(String tableName, String columnName);
    void addColumn(String tableName, EntityTypeBaseFieldDO baseField);
    void deprecateColumn(String tableName, String columnName);

    /**
     * 添加物理列到动态表
     * 
     * <p>用于后期为已存在的动态表添加物理列映射。
     * 会执行 ALTER TABLE 添加列，并创建 B-Tree 索引。</p>
     * 
     * @param tableName 表名
     * @param fieldCode 字段编码
     * @param config 物理列配置
     */
    void addPhysicalColumn(String tableName, String fieldCode, PhysicalColumnConfig config);

    /**
     * 删除物理列
     * 
     * <p>从动态表中删除物理列。注意：这会导致该列的数据丢失！
     * 会执行 ALTER TABLE DROP COLUMN。</p>
     * 
     * @param tableName 表名
     * @param columnName 列名
     */
    void dropPhysicalColumn(String tableName, String columnName);

    /**
     * 修改物理列配置
     * 
     * <p>修改物理列的数据类型。注意：某些类型转换可能导致数据丢失或转换失败！
     * 会执行 ALTER TABLE ALTER COLUMN。</p>
     * 
     * @param tableName 表名
     * @param fieldCode 字段编码
     * @param oldConfig 旧配置
     * @param newConfig 新配置
     */
    void modifyPhysicalColumn(String tableName, String fieldCode, PhysicalColumnConfig oldConfig, PhysicalColumnConfig newConfig);

    // ==================== 字段同步方法 ====================

    void syncModelFieldsToDynamicTable(Long modelId);
    void addColumnToDynamicTable(Long dynamicTableId, Long fieldId);
    void removeColumnFromDynamicTable(Long dynamicTableId, Long fieldId);
    List<DynamicTableColumnDO> listDynamicTableColumns(Long dynamicTableId);

    // ==================== 数据访问方法 ====================

    Long insertData(Long modelId, Map<Long, Object> data);
    void updateData(Long modelId, Long recordId, Map<Long, Object> data);
    void deleteData(Long modelId, Long recordId);
    Map<Long, Object> getData(Long modelId, Long recordId);
    List<Map<Long, Object>> listData(Long modelId, int pageNo, int pageSize, Map<Long, Object> conditions);
    long countData(Long modelId, Map<Long, Object> conditions);

    // ==================== 审计日志 ====================

    List<DynamicTableAuditLogDO> listAuditLogs(Long dynamicTableId);
}
