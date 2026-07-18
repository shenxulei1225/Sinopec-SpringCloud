package cn.cheers.x.module.dynamicbusiness.service.computed;

import cn.cheers.x.module.dynamicbusiness.controller.admin.computed.vo.ComputedFieldCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.computed.vo.ComputedFieldRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.computed.vo.ComputedFieldUpdateReqVO;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

/**
 * 计算字段服务接口
 * 
 * 提供计算字段的 CRUD 操作、字段值计算和缓存管理功能。
 * 支持两种计算类型：
 * - AGGREGATE: 聚合统计（COUNT、SUM、AVG、MAX、MIN）
 * - FORMULA: 公式计算（+、-、*、/、%）
 * 
 * @author yudao
 */
public interface ComputedFieldService {

    // ========== CRUD 操作 ==========

    /**
     * 创建计算字段
     * 
     * @param reqVO 创建请求
     * @return 计算字段 ID
     */
    Long createComputedField(@Valid ComputedFieldCreateReqVO reqVO);

    /**
     * 更新计算字段
     * 
     * @param reqVO 更新请求
     */
    void updateComputedField(@Valid ComputedFieldUpdateReqVO reqVO);

    /**
     * 删除计算字段
     * 
     * @param id 计算字段 ID
     */
    void deleteComputedField(Long id);

    /**
     * 获取计算字段详情
     * 
     * @param id 计算字段 ID
     * @return 计算字段详情
     */
    ComputedFieldRespVO getComputedField(Long id);

    /**
     * 获取 Model 的计算字段列表
     * 
     * @param modelId Model ID
     * @return 计算字段列表
     */
    List<ComputedFieldRespVO> getComputedFieldsByModelId(Long modelId);

    // ========== 字段值计算 ==========

    /**
     * 计算单个字段值（实时计算）
     * 
     * @param entityId 实体 ID
     * @param fieldCode 字段编码
     * @return 计算结果
     */
    Object computeFieldValue(Long entityId, String fieldCode);

    /**
     * 批量计算字段值
     * 
     * @param entityId 实体 ID
     * @param fieldCodes 字段编码列表
     * @return 字段编码 -> 计算结果 的映射
     */
    Map<String, Object> computeFieldValues(Long entityId, List<String> fieldCodes);

    /**
     * 计算 Model 的所有计算字段值
     * 
     * @param entityId 实体 ID
     * @param modelId Model ID
     * @return 字段编码 -> 计算结果 的映射
     */
    Map<String, Object> computeAllFieldValues(Long entityId, Long modelId);

    // ========== 缓存管理 ==========

    /**
     * 刷新指定字段的缓存
     * 
     * @param modelId Model ID
     * @param fieldCode 字段编码
     */
    void refreshCache(Long modelId, String fieldCode);

    /**
     * 刷新 Model 的所有缓存字段
     * 
     * @param modelId Model ID
     */
    void refreshAllCache(Long modelId);

    /**
     * 触发预计算（用于 PRECOMPUTED 策略）
     * 
     * @param entityId 实体 ID
     */
    void triggerPrecompute(Long entityId);

    /**
     * 清除指定实体的所有缓存
     * 
     * @param entityId 实体 ID
     */
    void clearEntityCache(Long entityId);

    /**
     * 批量刷新实体缓存
     * 
     * @param modelId Model ID
     * @param entityIds 实体 ID 列表
     * @return 清除的缓存数量
     */
    int batchRefreshEntityCache(Long modelId, List<Long> entityIds);

    /**
     * 预热指定字段的缓存
     * 
     * @param modelId Model ID
     * @param fieldCode 字段编码
     * @param entityIds 需要预热的实体 ID 列表
     * @return 预热成功的数量
     */
    int warmupFieldCache(Long modelId, String fieldCode, List<Long> entityIds);

    /**
     * 预热指定 Model 的所有缓存字段
     * 
     * @param modelId Model ID
     * @param entityIds 需要预热的实体 ID 列表
     * @return 预热成功的数量
     */
    int warmupModelCache(Long modelId, List<Long> entityIds);

    /**
     * 获取缓存统计信息
     * 
     * @param modelId Model ID（可选，为 null 时返回全局统计）
     * @return 缓存统计信息
     */
    CacheStatisticsVO getCacheStatistics(Long modelId);

    /**
     * 检查缓存是否存在
     * 
     * @param modelId Model ID
     * @param fieldCode 字段编码
     * @param entityId 实体 ID
     * @return 是否存在
     */
    boolean isCached(Long modelId, String fieldCode, Long entityId);

    /**
     * 获取缓存剩余过期时间（秒）
     * 
     * @param modelId Model ID
     * @param fieldCode 字段编码
     * @param entityId 实体 ID
     * @return 剩余过期时间（秒），-1 表示永不过期，-2 表示不存在
     */
    long getCacheTtl(Long modelId, String fieldCode, Long entityId);

    /**
     * 清除所有计算字段缓存
     * 
     * @return 清除的缓存数量
     */
    int clearAllCache();

    /**
     * 缓存统计信息 VO
     */
    record CacheStatisticsVO(
            long totalKeys,
            long hitCount,
            long missCount,
            double hitRate,
            long memoryUsageBytes
    ) {}

    // ========== 验证方法 ==========

    /**
     * 验证公式表达式语法
     * 
     * @param formulaExpression 公式表达式
     * @return 验证结果，null 表示验证通过，否则返回错误信息
     */
    String validateFormulaExpression(String formulaExpression);

    /**
     * 检测循环依赖
     * 
     * @param modelId Model ID
     * @param fieldCode 字段编码
     * @param formulaFields 公式引用的字段列表
     * @return 如果存在循环依赖，返回依赖链；否则返回 null
     */
    String detectCircularDependency(Long modelId, String fieldCode, List<String> formulaFields);

    /**
     * 获取字段的依赖顺序
     * 
     * @param modelId Model ID
     * @param fieldCodes 字段编码列表
     * @return 按依赖顺序排列的字段编码列表
     */
    List<String> getDependencyOrder(Long modelId, List<String> fieldCodes);
}
