package cn.cheers.x.module.dynamicbusiness.enums;

import cn.cheers.x.framework.common.exception.ErrorCode;

/**
 * 扩展字段查询服务错误码枚举类
 *
 * 扩展字段查询模块，使用 1-002-040-000 段
 * 
 * @author Kiro
 */
public interface ExtendFieldQueryErrorCodeConstants {

    // ========== 查询服务错误 1-002-040-000 ==========
    
    /** 字段不可查询 */
    ErrorCode FIELD_NOT_SEARCHABLE = new ErrorCode(1_002_040_000, "字段 {} 不可查询");
    
    /** 查询条件超限 */
    ErrorCode TOO_MANY_CONDITIONS = new ErrorCode(1_002_040_001, "查询条件数量超限，当前 {} 个，最大允许 {} 个");
    
    /** 查询超时 */
    ErrorCode QUERY_TIMEOUT = new ErrorCode(1_002_040_002, "查询超时");
    
    /** 数据同步失败 */
    ErrorCode SYNC_FAILED = new ErrorCode(1_002_040_003, "数据同步失败：{}");
    
    /** 查询引擎不可用 */
    ErrorCode ENGINE_NOT_AVAILABLE = new ErrorCode(1_002_040_004, "查询引擎 {} 不可用");
    
    /** 无效的操作符 */
    ErrorCode INVALID_OPERATOR = new ErrorCode(1_002_040_005, "无效的操作符：{}");
    
    /** 值类型不匹配 */
    ErrorCode INVALID_VALUE_TYPE = new ErrorCode(1_002_040_006, "字段 {} 的值类型不匹配，期望 {}，实际 {}");
    
    /** 字段不存在 */
    ErrorCode FIELD_NOT_EXISTS = new ErrorCode(1_002_040_007, "字段 {} 不存在");
    
    /** Model 不存在 */
    ErrorCode MODEL_NOT_EXISTS_FOR_QUERY = new ErrorCode(1_002_040_008, "Model {} 不存在");
    
    /** 索引重建进行中 */
    ErrorCode INDEX_REBUILD_IN_PROGRESS = new ErrorCode(1_002_040_009, "索引重建进行中，请稍后再试");
    
    /** 聚合类型不支持 */
    ErrorCode UNSUPPORTED_AGGREGATE_TYPE = new ErrorCode(1_002_040_010, "不支持的聚合类型：{}");

    // ========== Entity 验证错误 1-002-041-000 ==========
    
    /** area_id 引用无效 - 目标不是区域类型 */
    ErrorCode INVALID_AREA_REF_NOT_AREA_MODEL = new ErrorCode(1_002_041_000, "area_id 引用无效：目标 Entity {} 不是区域类型");
    
    /** area_id 引用无效 - 不在允许的 Model 范围内 */
    ErrorCode INVALID_AREA_REF_MODEL_NOT_ALLOWED = new ErrorCode(1_002_041_001, "area_id 引用无效：目标 Model {} 不在允许的范围内，允许的 Model：{}");
    
    /** area_id 引用无效 - 目标 Entity 不存在 */
    ErrorCode INVALID_AREA_REF_NOT_EXISTS = new ErrorCode(1_002_041_002, "area_id 引用无效：目标 Entity {} 不存在");
    
    /** parent_id 引用无效 - 必须指向同 Model 的 Entity */
    ErrorCode INVALID_PARENT_REF_DIFFERENT_MODEL = new ErrorCode(1_002_041_003, "parent_id 引用无效：必须指向同 Model 的 Entity，当前 Model：{}，目标 Model：{}");
    
    /** parent_id 引用无效 - 目标 Entity 不存在 */
    ErrorCode INVALID_PARENT_REF_NOT_EXISTS = new ErrorCode(1_002_041_004, "parent_id 引用无效：目标 Entity {} 不存在");
    
    /** 循环引用检测 */
    ErrorCode CIRCULAR_REFERENCE_DETECTED = new ErrorCode(1_002_041_005, "检测到循环引用：{}");
    
    /** ENTITY_REF 字段引用无效 - 不在允许的 Model 范围内 */
    ErrorCode INVALID_ENTITY_REF_MODEL_NOT_ALLOWED = new ErrorCode(1_002_041_006, "字段 {} 引用无效：目标 Model {} 不在允许的范围内，允许的 Model：{}");
    
    /** ENTITY_REF 字段引用无效 - 目标 Entity 不存在 */
    ErrorCode INVALID_ENTITY_REF_NOT_EXISTS = new ErrorCode(1_002_041_007, "字段 {} 引用无效：目标 Entity {} 不存在");

    // ========== 业务关联错误 1-002-042-000 ==========
    
    /** 业务类型关联不存在 */
    ErrorCode BUSINESS_TYPE_RELATION_NOT_EXISTS = new ErrorCode(1_002_042_000, "业务类型关联不存在");
    
    /** 业务类型关联已存在 */
    ErrorCode BUSINESS_TYPE_RELATION_EXISTS = new ErrorCode(1_002_042_001, "业务类型 {} 与 {} 的关联已存在");
    
    /** Model 关联不存在 */
    ErrorCode MODEL_RELATION_NOT_EXISTS = new ErrorCode(1_002_042_002, "Model 关联不存在");
    
    /** Model 关联已存在 */
    ErrorCode MODEL_RELATION_EXISTS = new ErrorCode(1_002_042_003, "Model {} 与 {} 的关联已存在");
    
    /** 不能删除自动生成的 Model 关联 */
    ErrorCode CANNOT_DELETE_AUTO_GENERATED_RELATION = new ErrorCode(1_002_042_004, "不能直接删除自动生成的 Model 关联，请删除对应的业务类型关联");

    // ========== 索引管理错误 1-002-043-000 ==========
    
    /** 字段索引不存在 */
    ErrorCode FIELD_INDEX_NOT_EXISTS = new ErrorCode(1_002_043_000, "字段索引不存在");
    
    /** 同步失败日志不存在 */
    ErrorCode SYNC_FAIL_LOG_NOT_EXISTS = new ErrorCode(1_002_043_001, "同步失败日志不存在");
    
    /** 补同步失败 */
    ErrorCode RESYNC_FAILED = new ErrorCode(1_002_043_002, "补同步失败：{}");

}
