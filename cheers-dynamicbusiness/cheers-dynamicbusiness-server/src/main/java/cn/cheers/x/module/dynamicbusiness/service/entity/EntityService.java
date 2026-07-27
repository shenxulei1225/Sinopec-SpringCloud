package cn.cheers.x.module.dynamicbusiness.service.entity;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityBatchCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityBatchDeleteReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.BatchEntityCategoryAssociationRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityBatchCategoryRelationReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityBatchMoveReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityBatchOperationRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityBatchReplaceCategoriesReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityBatchUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityPageReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntitySearchReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntitySearchRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntitySceneQueryRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.AssociationCategoryViewReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityDeleteReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityFieldAvailabilityRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.FieldFilterReqVO;

import cn.cheers.x.module.dynamicbusiness.enums.entity.EntityQueryScene;

import java.util.List;

/**
 * 实体服务接口
 *
 * <p>Service 层负责业务逻辑处理，不关心数据存储细节。
 * 通过 Repository 层访问数据，底层使用 MyBatis-Plus 动态表名机制实现存储透明。
 * 实体一律落在专用表 {@code ent_*}；禁止绕过 Repository 直接访问 Mapper / 通用表。</p>
 *
 * <h3>架构说明</h3>
 * <ul>
 *   <li>Service 层：业务逻辑、数据转换、验证、字段信息填充</li>
 *   <li>Repository 层：CRUD 操作，存储透明</li>
 *   <li>Mapper 层：SQL 执行，通过动态表名拦截器自动路由</li>
 * </ul>
 *
 * <h3>分类-实体联动查询约定（权威）</h3>
 * <ol>
 *   <li><b>分类搜索场景</b>：CategoryService 负责左侧分类树更新；
 *       EntityCategoryRelationService 负责 categoryIds -> orderedEntityIds；
 *       EntityService 负责实体详情查询并更新右侧列表。</li>
 *   <li><b>实体搜索场景</b>：左侧分类树保持不变；
 *       使用当前选中 categoryId（或子树）先取候选 entityIds，
 *       再在 EntityService 内按实体字段搜索并更新右侧列表。</li>
 * </ol>
 *
 * @author 基础服务模块
 */
public interface EntityService {

    // ==================== 基础 CRUD 操作 ====================

    /**
     * 创建实体
     *
     * <p>执行验证、转换、存储等业务逻辑。
     * 存储操作委托给 Repository 层，自动路由到对应的表。</p>
     *
     * @param reqVO 创建请求
     * @return 实体ID
     */
    Long create(EntityCreateReqVO reqVO);

    /**
     * 更新实体
     *
     * <p>执行验证、转换、更新等业务逻辑。
     * 存储操作委托给 Repository 层，自动路由到对应的表。</p>
     *
     * @param reqVO 更新请求
     */
    void update(EntityUpdateReqVO reqVO);

    /**
     * 变更模型专用写路径：允许修改 modelId（字段迁移由 {@link cn.cheers.x.module.dynamicbusiness.service.entity.modelchange.EntityModelChangeService} 完成）。
     */
    void updateIncludingModelChange(EntityUpdateReqVO reqVO);

    /**
     * 删除实体
     *
     * <p>执行验证、删除等业务逻辑。
     * 存储操作委托给 Repository 层，自动路由到对应的表。</p>
     *
     * @param id 实体ID
     * @param entityTypeCode 业务类型编码
     * @param forceDelete 是否强制删除（可为 null，默认 false）
     */
    void delete(EntityDeleteReqVO reqVO);

    /**
     * CRUD 弹窗字段异步校验：当前支持实体 name 在同 model 下唯一。
     */
    EntityFieldAvailabilityRespVO checkFieldUnique(
            String entityTypeCode,
            Long modelId,
            String fieldKey,
            String value,
            Long excludeId);


    // ==================== 查询实体 操作 ====================
    /**
     * 1. 查询单个实体详情
     * 2. 按分类查询实体列表
     *  2.1  单分类查询实体列表
     *      2.1.1 按分类ID查询实体列表
     *      2.1.2 按分类ID查询实体分页列表
     *      2.1.3 统一场景搜索实体列表
     *  2.2  多分类查询实体列表
     *      2.2.1 按分类ID列表查询实体列表
     *      2.2.2 按分类ID列表查询实体分页列表
     * 3. 按模型查询实体列表
     *  3.1 按模型ID查询实体列表
     *  3.2 按模型ID查询实体分页列表
     *  3.3 按模型ID查询实体轻量级列表
     *  3.4 按模型ID查询实体轻量级分页列表
     * 4. 搜索实体列表
     *  4.1 高级搜索实体列表
     *  4.2 统一场景搜索实体列表
     */

    /**
     * 获取单个实体详情（指定业务类型）
     *
     * <p>通过 Repository 层查询，自动路由到对应的存储表。
     * Service 层不关心数据存在哪个表，存储完全透明。</p>
     *
     * @param id 实体ID
     * @param entityTypeCode 业务类型编码
     * @return 实体详情
     */
    EntityRespVO get(Long id, String entityTypeCode);

    /**
     * 获取实体详情，可选填充关联字段数据（见 entity-detail-associations-design.md）。
     *
     * @param associationCategoryViews 多视角配置；null 或空时关联块为 default 扁平行（不解析分类）
     */
    EntityRespVO get(Long id, String entityTypeCode, boolean includeAssociations,
            List<AssociationCategoryViewReqVO> associationCategoryViews);

    /**
     * 分页搜索实体列表
     *
     * <p>通过 Repository 层查询，自动路由到对应的存储表。</p>
     *
     * @param reqVO 搜索分页参数
     * @return 分页结果
     */
    PageResult<EntityRespVO> pageSearchEntities(EntityPageReqVO reqVO);

    /**
     * 移动实体到新的父实体下，建立层级关系。
     * 比如设备下的子设备，子设备下的子设备
     * @param entityId 实体ID
     * @param entityTypeCode 业务类型编码
     * @param newParentId 新的父实体ID
     */
    void moveEntity(Long entityId, String entityTypeCode, Long newParentId);


    /**
     * 按模型维度查询实体树（推荐入口，命名无歧义）。
     *
     * <p><b>核心语义</b>：
     * 在指定模型范围内查询实体，并按实体自身的 {@code parentId -> children}
     * 关系组装为树结构返回。</p>
     *
     * <p><b>参数规则</b>：</p>
     * <ul>
     *   <li>{@code modelId}：必填，表示查询范围限定在该模型下。</li>
     *   <li>{@code entityTypeCode}：建议传入。
     *       当传入时用于明确存储路由与边界；
     *       当为空时由实现层根据 modelId 推导/校验业务类型。</li>
     * </ul>
     *
     * <p><b>返回结构说明</b>：</p>
     * <ul>
     *   <li>返回的是“该模型实体子集”的树，而非系统全量实体树。</li>
     *   <li>若某节点父实体不在当前模型子集中（或父节点缺失），该节点会作为根节点返回。</li>
     * </ul>
     *
     * <p><b>典型场景</b>：模型管理页、模型详情页、模型配置页中的实体树展示。</p>
     *
     * @param entityTypeCode 业务类型编码（建议传入，提升路由明确性）
     * @param modelId 模型 ID（必填）
     * @return 模型范围内的实体树根节点列表
     */
    List<EntityRespVO> getEntityTreeByModelId(String entityTypeCode, Long modelId);

    /**
     * 获取实体路径
     */
    List<String> getEntityPath(Long entityId, String entityTypeCode);

    // ==================== 分类关联操作（已移除，请直接使用 EntityCategoryRelationService）====================

    /**
     * 高级搜索实体
     * 支持全文搜索、高级过滤、多字段排序
     *
     * @param reqVO 搜索请求参数
     * @return 搜索结果（包含分页信息和聚合统计）
     */
    EntitySearchRespVO searchAdvanced(EntitySearchReqVO reqVO);

    // ==================== 统一实体查询接口 ====================

    /**
     * 统一实体查询接口（支持所有模式）
     *
     * <p>通过 scene 参数明确表达查询意图，支持模式A/B/C/D的所有场景（灵活分类视图/模型分类视图/分类实体视图/实体关联视图）。
     * 包括点击操作和拖动后的刷新操作。</p>
     *
     * @param scene 查询场景（必填）
     * @param entityTypeCode 业务类型编码（部分场景必填）
     * @param modelId 模型ID（部分场景必填）
     * @param categoryIds 分类ID列表（部分场景必填；单分类场景传单元素列表）
     * @param entityId 实体ID（部分场景必填）
     * @param rootEntityId 根实体ID（部分场景必填）
     * @param entitySourceEntityType 实体来源业务类型（模式C必填）
     * @param pageNo 页码（LIST形态时使用，默认1）
     * @param pageSize 每页条数（LIST形态时使用，默认20）
     * @param keyword 搜索关键词（可选）
     * @return 分页结果，包含实体列表和总数（树形结构或平铺结构，根据场景而定）
     */
    EntitySceneQueryRespVO queryEntities(EntityQueryScene scene, String resultShape, String resultDetail, String categoryTypeCode, String entityTypeCode,
            List<Long> modelIds, List<Long> categoryIds, String categoryViaRefPathCode, Long entityId, Long rootEntityId, String entitySourceEntityType,
            Integer pageNo, Integer pageSize, String keyword, String domain,
            List<FieldFilterReqVO> filters);


    /**
     * Pattern C（1对1）：根据分类ID查询其绑定的实体详情。
     *
     * <p>该接口返回单对象，不走分页语义。</p>
     *
     * @param categoryId 分类ID
     * @param entityTypeCode 业务类型编码（可选，未传时按链接记录路由）
     * @return 绑定实体详情，不存在时返回 null
     */
    EntityRespVO getCategoryLinkedEntity(Long categoryId, String entityTypeCode);

    // ==================== 批量操作相关方法 ====================
    /**
     * 批量/关联操作决策树（对外入口）
     *
     * <p>1) 批量改实体字段：{@link #batchUpdate(EntityBatchUpdateReqVO)}</p>
     * <p>2) 批量删实体：{@link #batchDelete(EntityBatchDeleteReqVO)}</p>
     * <p>3) 批量迁移到目标分类（拖拽常见）：{@link #batchRelocateCategory(EntityBatchMoveReqVO)}
     *    （仅改实体-分类关系，不改 parentId/treePath）</p>
     * <p>4) 先看影响范围再执行：{@link #getBatchOperationPreview(String, List)}</p>
     *
     * <p>说明：追加关联、取消关联、单实体全量替换、批量导入覆盖、分类合并迁移等场景，
     * 均应由 Service 层统一编排后调用关系服务原子方法，Controller 不直接调用关系服务。</p>
     */

    /**
     * 批量创建实体
     *
     * <p>批量创建多个实体，返回创建成功的实体ID列表。
     * 每个实体独立创建，单个失败不影响其他实体。</p>
     *
     * @param reqVOs 创建请求列表
     * @return 创建成功的实体ID列表
     */
    List<Long> batchCreate(EntityBatchCreateReqVO reqVO);

    /**
     * 批量更新实体
     * 支持批量修改多个实体的字段值
     * 单次操作最多1000条，超过时自动分批异步执行
     *
     * @param reqVO 批量更新请求
     * @return 批量操作结果
     */
    EntityBatchOperationRespVO batchUpdate(EntityBatchUpdateReqVO reqVO);

    /**
     * 批量删除实体
     * 批量删除前会检查关联关系
     * 单次操作最多1000条，超过时自动分批异步执行
     *
     * @param reqVO 批量删除请求
     * @return 批量操作结果
     */
    EntityBatchOperationRespVO batchDelete(EntityBatchDeleteReqVO reqVO);

    /**
     * 批量调整实体的分类关联（目标分类）。
     *
     * <p>说明：该操作仅调整“实体-分类关系”，不修改实体树 parentId/treePath。</p>
     *
     * @param reqVO 批量分类关联调整请求
     * @return 批量操作结果
     */
    EntityBatchOperationRespVO batchRelocateCategory(EntityBatchMoveReqVO reqVO);

    /**
     * 批量追加实体到目标分类（保留实体原有关联）。
     */
    BatchEntityCategoryAssociationRespVO batchAppendCategory(EntityBatchCategoryRelationReqVO reqVO);

    /**
     * 批量取消实体与目标分类的关联（不影响实体其它分类关联）。
     */
    BatchEntityCategoryAssociationRespVO batchRemoveCategory(EntityBatchCategoryRelationReqVO reqVO);

    /**
     * 批量覆盖实体分类集合（用于导入覆盖等场景）。
     */
    BatchEntityCategoryAssociationRespVO batchReplaceCategories(EntityBatchReplaceCategoriesReqVO reqVO);

    /**
     * 获取批量操作预览信息（指定业务类型）
     * 返回受影响的实体数量和详情
     *
     * @param entityTypeCode 业务类型编码（必填）
     * @param ids 实体ID列表
     * @return 预览信息
     */
    EntityBatchOperationRespVO getBatchOperationPreview(String entityTypeCode, List<Long> ids);

    // ==================== 预计算相关方法 ====================


}


























