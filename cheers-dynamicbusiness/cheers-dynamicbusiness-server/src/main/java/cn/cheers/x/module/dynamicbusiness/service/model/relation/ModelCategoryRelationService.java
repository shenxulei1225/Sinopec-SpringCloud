package cn.cheers.x.module.dynamicbusiness.service.model.relation;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.BatchModelCategoryAssociationRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelCategoryAssociationRespVO;

import java.util.List;

/**
 * 模型-分类关联服务。
 *
 * <p><b>边界约束（重要）</b>：本服务只负责“关联关系与ID序列”，不返回分类详情对象。</p>
 *
 * <h3>职责范围</h3>
 * <ul>
 *   <li>关联的增删改查操作</li>
 *   <li>单模型/多模型 × 单分类/多分类 的所有组合</li>
 *   <li>级联删除清理</li>
 *   <li>按分类上下文生成有序 modelId 列表（排序语义来自 relation.sort）</li>
 * </ul>
 */
public interface ModelCategoryRelationService {

    // ==================== 单模型-单分类操作 ====================

    /**
     * 关联单个模型到单个分类。
     *
     * @param modelId 模型ID
     * @param categoryId 分类ID
     * @param entityTypeCode 业务类型编码（必填）
     * @return 关联操作结果
     */
    ModelCategoryAssociationRespVO associate(Long modelId, Long categoryId, String entityTypeCode);

    /**
     * 取消单个模型与单个分类的关联。
     *
     * @param modelId 模型ID
     * @param categoryId 分类ID
     * @param entityTypeCode 业务类型编码（必填）
     * @return 解除关联结果
     */
    ModelCategoryAssociationRespVO disassociate(Long modelId, Long categoryId, String entityTypeCode);

    /**
     * 检查关联是否存在。
     *
     * @param modelId 模型ID
     * @param categoryId 分类ID
     * @param entityTypeCode 业务类型编码
     * @return 是否存在关联
     */
    boolean existsRelation(Long modelId, Long categoryId, String entityTypeCode);

    // ==================== 单模型-多分类操作 ====================

    /**
     * 批量关联单个模型到多个分类（带验证）。
     *
     * @param modelId 模型ID
     * @param categoryIds 分类ID列表
     * @param entityTypeCode 业务类型编码
     * @return 关联操作结果
     */
    ModelCategoryAssociationRespVO batchAssociateModelToCategories(Long modelId, List<Long> categoryIds, String entityTypeCode);

    /**
     * 批量取消单个模型与多个分类的关联。
     *
     * <p><b>限制条件</b>：必须显式传入 {@code entityTypeCode}，用于命中业务分区与索引。</p>
     * <p><b>适用范围</b>：单个模型场景（该模型对应唯一业务类型）。</p>
     *
     * @param modelId 模型ID
     * @param categoryIds 分类ID列表
     * @param entityTypeCode 业务类型编码（必填）
     * @return 解除关联结果
     */
    ModelCategoryAssociationRespVO batchDisassociateModelFromCategories(Long modelId, List<Long> categoryIds, String entityTypeCode);

    /**
     * 更新单个模型关联的分类（差集同步）。
     *
     * @param modelId 模型ID
     * @param categoryIds 新的分类ID列表
     * @param entityTypeCode 业务类型编码
     * @return 更新结果
     */
    ModelCategoryAssociationRespVO updateAssociation(Long modelId, List<Long> categoryIds, String entityTypeCode);

    /**
     * 在同一分类下按 BEFORE/AFTER 重排模型顺序。
     *
     * @param sourceModelId 被拖拽模型ID
     * @param targetModelId 目标锚点模型ID
     * @param categoryId 分类ID
     * @param position BEFORE 或 AFTER
     */
    void reorderModelInCategory(Long sourceModelId, Long targetModelId, Long categoryId, String position);

    /**
     * 按提交顺序重写分类语境下的型号关联 sort。
     *
     * <p>仅更新 {@code dynamic_model_category_relation.sort}，不改型号主表 sort。</p>
     * <ul>
     *   <li>叶子节点：写该节点上的关联 sort</li>
     *   <li>父节点（含子树）：写每个型号在子树里<strong>真实挂接</strong>那条关联的 sort；
     *       列表读路径按子分类分桶拼接，故<strong>禁止跨子分类</strong>调序——发现跨桶则拒绝并提示选中子分类再排</li>
     * </ul>
     *
     * @param categoryId 树上当前选中的分类（可为父节点）
     * @param entityTypeCode 业务类型编码
     * @param modelIdsInOrder 拖拽后的扁列表顺序（通常为当前页）
     */
    void reindexModelSortInCategory(Long categoryId, String entityTypeCode, List<Long> modelIdsInOrder);

    /**
     * 将模型移动/绑定到目标分类（如提供 sourceCategoryId 且与目标不同，会先解绑源分类）。
     *
     * @param modelId 模型ID
     * @param sourceCategoryId 源分类ID（可空）
     * @param targetCategoryId 目标分类ID
     */
    void moveOrBindModelToCategory(Long modelId, Long sourceCategoryId, Long targetCategoryId);

    // ==================== 多模型-单分类操作 ====================

    /**
     * 批量关联多个模型到单个分类。
     *
     * <p>处理步骤：</p>
     * <ol>
     *   <li>校验分类存在；</li>
     *   <li>校验模型存在（按 entityTypeCode）；</li>
     *   <li>对每个模型执行关联（复用软删除记录，避免唯一键冲突）；</li>
     *   <li>汇总模型维度成功/失败结果并返回。</li>
     * </ol>
     */
    BatchModelCategoryAssociationRespVO batchAssociateModelsToCategory(List<Long> modelIds, Long categoryId, String entityTypeCode);

    /**
     * 批量取消多个模型与单个分类的关联。
     *
     * <p><b>限制条件</b>：调用方需保证 {@code modelIds} 对应同一 {@code entityTypeCode}。</p>
     * <p>若同一批次内存在多个业务类型，请按业务类型拆分后分别调用，或使用按模型维度携带业务类型的专用批量方法。</p>
     */
    BatchModelCategoryAssociationRespVO batchDisassociateModelsFromCategory(List<Long> modelIds, Long categoryId, String entityTypeCode);

    // ==================== 多模型-多分类操作 ====================

    /**
     * 批量关联多个模型到多个分类（多对多）。
     *
     * <p>处理步骤：</p>
     * <ol>
     *   <li>批量校验模型存在性（按 entityTypeCode）；</li>
     *   <li>批量校验分类存在性；</li>
     *   <li>读取现有关联并按“已存在/可恢复/需新增”分流；</li>
     *   <li>执行恢复与新增，并维护分类内 sort；</li>
     *   <li>汇总模型维度结果并返回。</li>
     * </ol>
     */
    BatchModelCategoryAssociationRespVO batchAssociateModelsToCategories(List<Long> modelIds, List<Long> categoryIds, String entityTypeCode);

    /**
     * 批量取消多个模型与多个分类的关联（多对多）。
     *
     * <p><b>限制条件</b>：调用方需保证本次请求内所有模型属于同一 {@code entityTypeCode}。</p>
     * <p>若存在跨业务类型模型，请先按业务类型分组并分批调用；超出该限制时应使用按模型维度传入业务类型的批量接口。</p>
     */
    BatchModelCategoryAssociationRespVO batchDisassociateModelsFromCategories(List<Long> modelIds, List<Long> categoryIds, String entityTypeCode);

    /**
     * 批量更新多个模型关联的分类（差集同步）。
     *
     * <p>语义：目标分类集合为最终状态，不在目标集合中的旧关联会被移除。</p>
     *
     * @param modelIds 模型ID列表
     * @param categoryIds 目标分类ID列表
     * @param entityTypeCode 业务类型编码（必填）
     * @return 批量更新结果（按模型汇总）
     */
    BatchModelCategoryAssociationRespVO batchUpdateAssociation(List<Long> modelIds, List<Long> categoryIds, String entityTypeCode);

    // ==================== 模型分类关系查询接口（仅关系查询，不做树解析） ====================

    /**
     * 获取模型关联的分类ID列表。
     */
    List<Long> listCategoryIdsByModelId(Long modelId, String entityTypeCode);

    /**
     * 查找指定业务下单分类（仅当前分类，不含子树）关联的模型ID列表。
     */
    List<Long> listModelIdsByCategoryIdOnly(Long categoryId, String entityTypeCode);

    /**
     * 查找指定业务下单分类（含子树）关联的模型ID列表。
     */
    List<Long> listModelIdsByCategoryIdWithDescendants(Long categoryId, String entityTypeCode);

    /**
     * 多分类（仅输入分类本身，不含子树）查询模型ID列表。
     */
    List<Long> listModelIdsByCategoryIdsOnly(List<Long> categoryIds, String entityTypeCode);

    /**
     * 多分类（每个分类都含子树）查询模型ID列表。
     */
    List<Long> listModelIdsByCategoryIdsWithDescendants(List<Long> categoryIds, String entityTypeCode);

    // ==================== 模型分类关系分页查询接口（仅返回 modelId） ====================

    PageResult<Long> pageModelIdsByCategoryIdOnly(Long categoryId, String entityTypeCode, Integer pageNo, Integer pageSize);

    PageResult<Long> pageModelIdsByCategoryIdWithDescendants(Long categoryId, String entityTypeCode, Integer pageNo, Integer pageSize);

    PageResult<Long> pageModelIdsByCategoryIdsOnly(List<Long> categoryIds, String entityTypeCode, Integer pageNo, Integer pageSize);

    PageResult<Long> pageModelIdsByCategoryIdsWithDescendants(List<Long> categoryIds, String entityTypeCode, Integer pageNo, Integer pageSize);

    // ==================== DB 前置分页查询接口 ====================

    PageResult<Long> pageModelIdsByCategoryIdsOnlyDb(List<Long> categoryIds, String entityTypeCode, Integer pageNo, Integer pageSize);

    PageResult<Long> pageModelIdsByCategoryIdsWithDescendantsDb(List<Long> categoryIds, String entityTypeCode, Integer pageNo, Integer pageSize);


    // ==================== 级联删除操作 ====================

    void deleteAllByModelId(Long modelId);

    void deleteAllByModelIds(List<Long> modelIds);

    /**
     * 删除分类在所有业务下的模型关联（级联删除分类时使用）。
     */
    void deleteAllByCategoryId(Long categoryId);

    /**
     * 批量删除多个分类在所有业务下的模型关联（级联删除分类树时使用）。
     */
    void deleteAllByCategoryIds(List<Long> categoryIds);

    /**
     * 删除分类在指定业务下的模型关联（业务内维护场景）。
     */
    void deleteAllByCategoryIdInBusiness(Long categoryId, String entityTypeCode);

    /**
     * 批量删除多个分类在指定业务下的模型关联（业务内维护场景）。
     */
    void deleteAllByCategoryIdsInBusiness(List<Long> categoryIds, String entityTypeCode);
}
