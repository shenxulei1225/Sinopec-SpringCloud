package cn.cheers.x.module.dynamicbusiness.service.relation;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.relation.vo.RelationFieldLibraryCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.relation.vo.RelationFieldLibraryPageReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.relation.vo.RelationFieldLibraryRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.relation.vo.RelationFieldLibraryUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.relation.vo.RelationFieldLibraryWithStatusVO;

import java.util.List;

/**
 * 关联字段库 Service 接口
 * 
 * 业务含义：管理全局的关联字段定义库，存储"字段名称 → 关联目标"的映射关系。
 * 使用松散引用（存储编码，不使用外键约束），支持预定义尚未创建的目标。
 * 
 * 需求：FR-BDA-010~016
 * 
 * @author yudao
 */
public interface RelationFieldLibraryService {

    // ========== CRUD 方法 ==========

    /**
     * 创建关联字段
     * 
     * 业务规则：
     * - 字段编码全局唯一
     * - 允许引用尚未创建的目标（松散引用）
     * - 创建时不验证目标是否存在，状态标记为"待建"
     * 
     * @param reqVO 创建请求
     * @return 字段ID
     */
    Long createRelationField(RelationFieldLibraryCreateReqVO reqVO);

    /**
     * 更新关联字段
     * 
     * 业务规则：
     * - 系统预置字段可以更新（但不能删除）
     * - 字段编码更新时需要验证唯一性
     * 
     * @param reqVO 更新请求
     */
    void updateRelationField(RelationFieldLibraryUpdateReqVO reqVO);

    /**
     * 删除关联字段
     * 
     * 业务规则：
     * - 系统预置字段不可删除
     * - 正在使用中的字段不可删除（usageCount > 0）
     * 
     * @param id 字段ID
     */
    void deleteRelationField(Long id);

    /**
     * 获取关联字段详情
     * 
     * @param id 字段ID
     * @return 字段详情
     */
    RelationFieldLibraryRespVO getRelationField(Long id);

    /**
     * 根据字段编码获取关联字段
     * 
     * @param fieldCode 字段编码
     * @return 字段详情
     */
    RelationFieldLibraryRespVO getRelationFieldByCode(String fieldCode);

    // ========== 列表查询方法 ==========

    /**
     * 获取关联字段列表（带状态）
     * 
     * 状态计算规则：
     * - AVAILABLE: 关联目标存在
     * - PENDING: 关联目标不存在
     * 
     * @return 包含可用/待建状态的字段列表
     */
    List<RelationFieldLibraryWithStatusVO> getRelationFieldListWithStatus();

    /**
     * 获取可用的关联字段列表（目标已存在）
     * 
     * @param refEntityType 关联业务类型（可选过滤）
     * @return 可用的字段列表
     */
    List<RelationFieldLibraryRespVO> getAvailableRelationFields(String refEntityType);

    /**
     * 分页查询关联字段
     * 
     * @param reqVO 分页查询条件
     * @return 分页结果
     */
    PageResult<RelationFieldLibraryWithStatusVO> getRelationFieldPage(RelationFieldLibraryPageReqVO reqVO);

    /**
     * 获取系统预置字段列表
     * 
     * @return 系统预置字段列表
     */
    List<RelationFieldLibraryRespVO> getSystemRelationFields();

    // ========== 状态检查方法 ==========

    /**
     * 检查关联目标是否存在
     * 
     * 检查规则：
     * - 业务类型编码对应的 EntityTypeConfig 存在
     * - Model 编码对应的 Model 存在且属于该业务类型
     * 
     * @param businessType 业务类型编码
     * @param modelCode Model 编码
     * @return 是否存在
     */
    boolean checkTargetExists(String entityType, String modelCode);

    /**
     * 获取关联业务类型的名称信息
     * 
     * @param businessType 业务类型编码
     * @param modelCode Model 编码（兼容旧逻辑，可为空）
     * @return 名称信息数组 [业务类型名称, Model名称]，不存在时对应位置为 null
     */
    String[] getTargetNames(String entityType, String modelCode);

    // ========== 使用次数管理 ==========

    /**
     * 增加使用次数
     * 
     * 当 Model 选用该关联字段时调用
     * 
     * @param id 字段ID
     */
    void incrementUsageCount(Long id);

    /**
     * 减少使用次数
     * 
     * 当 Model 移除该关联字段时调用
     * 
     * @param id 字段ID
     */
    void decrementUsageCount(Long id);

    // ========== 验证方法 ==========

    /**
     * 验证关联字段是否存在
     * 
     * @param id 字段ID
     */
    void validateRelationFieldExists(Long id);

    /**
     * 验证关联目标是否可用（用于 Model 选用字段时）
     * 
     * 验证规则：
     * - 关联目标必须存在
     * - 如果不存在，抛出异常提示先创建目标
     * 
     * @param businessType 业务类型编码
     * @param modelCode Model 编码
     */
    void validateTargetAvailable(String entityType, String modelCode);
}
