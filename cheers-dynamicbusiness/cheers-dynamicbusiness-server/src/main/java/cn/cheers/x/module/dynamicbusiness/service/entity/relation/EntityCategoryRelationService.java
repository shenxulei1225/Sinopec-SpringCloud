package cn.cheers.x.module.dynamicbusiness.service.entity.relation;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.BatchEntityCategoryAssociationRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityCategoryAssociationRespVO;

import java.util.List;

/**
 * 实体-分类关联服务
 *
 * <p><b>边界约束（重要）</b>：本服务只负责“关联关系与ID序列”，不返回实体详情对象。</p>
 *
 * <h3>职责范围</h3>
 * <ul>
 *   <li>关联的增删改查操作</li>
 *   <li>单实体/多实体 × 单分类/多分类 的所有组合</li>
 *   <li>级联删除清理</li>
 *   <li>按分类上下文生成有序 entityId 列表（排序语义来自 relation.sort）</li>
 * </ul>
 *
 * <h3>查询链路约定（权威）</h3>
 * <ol>
 *   <li><b>搜索分类（命中多个分类）</b>：
 *       <br/>CategoryService 先返回命中的 {@code categoryIds}；
 *       <br/>本服务必须调用 {@code listEntityIdsByCategoryIdsAndEntityType(categoryIds, entityTypeCode)}；
 *       <br/>再由 EntityService 按返回 IDs 查询实体并更新右侧列表。</li>
 *   <li><b>搜索实体（左树不变）</b>：
 *       <br/>基于当前选中分类范围获取候选 IDs：
 *       <br/>- 仅当前分类（不包含子分类）：调用 {@code listEntityIdsByCategoryId(categoryId)}；
 *       <br/>- 含子分类范围：先由 CategoryService 展开 {@code categoryIds}，再调用
 *       {@code listEntityIdsByCategoryIdsAndEntityType(categoryIds, entityTypeCode)}；
 *       <br/>最后由 EntityService 按实体字段（name/customFields）搜索并更新右侧列表。</li>
 * </ol>
 *
 * <p><b>范围判定规则（必须遵守）</b>：
 * 查询策略的选择依据是“是否包含子分类范围”，而不是“是否点击节点”或“节点是否有子分类”。</p>
 *
 * <p>实体详情组装（EntityDO -> EntityRespVO）统一由 EntityService 负责。</p>
 *
 * @author 基础服务模块
 */
public interface EntityCategoryRelationService {

    // ==================== 单实体-单分类操作 ====================

        /**
         * 关联单个实体到单个分类
         *
         * @param entityId 实体ID
         * @param categoryId 分类ID
         * @param entityTypeCode 业务类型编码（必填）
         */
        EntityCategoryAssociationRespVO associate(Long entityId, Long categoryId, String entityTypeCode);

        /**
         * 取消单个实体与单个分类的关联。
         *
         * @param entityId 实体ID
         * @param categoryId 分类ID
         */
        EntityCategoryAssociationRespVO disassociate(Long entityId, Long categoryId, String entityTypeCode);

        /**
         * 检查关联是否存在。
         *
         * @param entityId 实体ID
         * @param categoryId 分类ID
         * @return 是否存在关联
         */
        boolean existsRelation(Long entityId, Long categoryId, String entityTypeCode);

        // ==================== 单实体-多分类操作 ====================

        /**
         * 批量关联单个实体到多个分类（带验证）
         *
         * <p>会验证实体和分类是否存在，返回详细的成功/失败信息。</p>
         *
         * @param entityId 实体ID
         * @param categoryIds 分类ID列表
         * @param entityTypeCode 业务类型编码，用于路由到正确的存储表验证实体存在性
         * @return 关联操作结果，包含成功/失败详情
         */
        EntityCategoryAssociationRespVO batchAssociateEntityToCategories(
                Long entityId, List<Long> categoryIds, String entityTypeCode);

        /**
         * 批量取消单个实体与多个分类的关联。
         *
         * <p><b>限制条件</b>：必须显式传入 {@code entityTypeCode}，用于命中业务分区与索引。</p>
         * <p><b>适用范围</b>：单个实体场景（该实体对应唯一业务类型）。</p>
         *
         * @param entityId 实体ID
         * @param categoryIds 分类ID列表
         * @param entityTypeCode 业务类型编码（必填）
         */
        EntityCategoryAssociationRespVO batchDisassociateEntityFromCategories(Long entityId, List<Long> categoryIds, String entityTypeCode);

        /**
         * 替换单个实体的所有分类关联（带验证）
         *
         * <p>先删除实体的所有旧关联，再创建新关联。
         * 会验证实体和分类是否存在，返回详细的成功/失败信息。</p>
         *
         * @param entityId 实体ID
         * @param categoryIds 新的分类ID列表
         * @param entityTypeCode 业务类型编码
         * @return 关联操作结果，包含成功/失败详情
         */
        EntityCategoryAssociationRespVO updateAssociation(
                Long entityId, List<Long> categoryIds, String entityTypeCode);

        // ==================== 多实体-单分类操作 ====================

        /**
         * 批量关联多个实体到单个分类
         *
         * @param entityIds 实体ID列表
         * @param categoryId 分类ID
         * @param entityTypeCode 业务类型编码
         * @return 批量操作结果
         */
        BatchEntityCategoryAssociationRespVO batchAssociateEntitiesToCategory(
                List<Long> entityIds, Long categoryId, String entityTypeCode);

        /**
         * 批量关联多个实体到单个分类。
         *
         * @param entityAssociationMode SINGLE=单归属 / MULTI=多归属；null 时回退分类种类配置
         */
        BatchEntityCategoryAssociationRespVO batchAssociateEntitiesToCategory(
                List<Long> entityIds, Long categoryId, String entityTypeCode,
                String entityAssociationMode);

        /**
        * 批量取消多个实体与单个分类的关联。
        *
        * <p><b>限制条件</b>：调用方需保证 {@code entityIds} 对应同一 {@code entityTypeCode}。</p>
        * <p>若同一批次内存在多个业务类型，请按业务类型拆分后分别调用，或使用按实体维度携带业务类型的专用批量方法。</p>
        *
        * @param entityIds 实体ID列表
        * @param categoryId 分类ID
        * @param entityTypeCode 业务类型编码（必填）
        * @return 批量操作结果
        */
        BatchEntityCategoryAssociationRespVO batchDisassociateEntitiesFromCategory(
                List<Long> entityIds, Long categoryId, String entityTypeCode);

        // ==================== 多实体-多分类操作 ====================

        /**
         * 批量关联多个实体到多个分类（多对多）
         *
         * <p>为每个实体创建与每个分类的关联。
         * 会验证实体和分类是否存在，返回详细的成功/失败信息。</p>
         *
         * @param entityIds 实体ID列表
         * @param categoryIds 分类ID列表
         * @param entityTypeCode 业务类型编码
         * @return 批量操作结果
         */
        BatchEntityCategoryAssociationRespVO batchAssociateEntitiesToCategories(
                List<Long> entityIds, List<Long> categoryIds, String entityTypeCode);

        /**
         * 批量关联多个实体到多个分类。
         *
         * @param entityAssociationMode SINGLE/MULTI；null 时回退分类种类配置
         */
        BatchEntityCategoryAssociationRespVO batchAssociateEntitiesToCategories(
                List<Long> entityIds, List<Long> categoryIds, String entityTypeCode,
                String entityAssociationMode);

        /**
         * 批量取消多个实体与多个分类的关联（多对多）。
         *
         * <p><b>限制条件</b>：调用方需保证本次请求内所有实体属于同一 {@code entityTypeCode}。</p>
         * <p>若存在跨业务类型实体，请先按业务类型分组并分批调用；超出该限制时应使用按实体维度传入业务类型的批量接口。</p>
         *
         * @param entityIds 实体ID列表
         * @param categoryIds 分类ID列表
         * @param entityTypeCode 业务类型编码（必填）
         * @return 批量操作结果
         */
        BatchEntityCategoryAssociationRespVO batchDisassociateEntitiesFromCategories(
                List<Long> entityIds, List<Long> categoryIds, String entityTypeCode);

        /**
         * 批量替换多个实体的分类关联
         *
         * <p>先删除这些实体的所有旧关联，再创建新关联。</p>
         *
         * @param entityIds 实体ID列表
         * @param categoryIds 新的分类ID列表
         * @param entityTypeCode 业务类型编码
         * @return 批量操作结果
         */
        BatchEntityCategoryAssociationRespVO batchUpdateAssociation(
                List<Long> entityIds, List<Long> categoryIds, String entityTypeCode);

        // ==================== 实体分类关系查询接口（仅关系查询，不做树解析） ====================

        /**
         * 获取实体关联的所有分类ID列表。
         *
         * @param entityId 实体ID
         * @param entityTypeCode 业务类型编码（必填，用于过滤出当前业务下的关联）
         * @return 分类ID列表
         */
        List<Long> listCategoryIdsByEntityId(Long entityId, String entityTypeCode);

        /**
         * 单分类（仅当前分类，不含子树）查询实体ID列表。
         *
         * <p><b>注意</b>：如果需要“当前分类 + 子分类”范围，请不要调用本方法，
         * 应改用 {@link #listEntityIdsByCategoryIdWithDescendants(Long, String)}。</p>
         *
         * @param categoryId 分类ID
         * @param entityTypeCode 业务类型编码（必填）
         * @return 有序实体ID列表（按 relation.sort，稳定去重）
         */
        List<Long> listEntityIdsByCategoryIdOnly(Long categoryId, String entityTypeCode);

        /**
         * 查询单个分类（含其子分类）关联的实体ID列表。
         *
         * <p>前置约束：CategoryService 应先展开 descendants，再把展开后的 categoryIds 交给关系服务。
         * 本服务不负责树结构解析。</p>
         *
         * @param categoryId 分类ID
         * @param entityTypeCode 业务类型编码
         * @return 有序实体ID列表（分类顺序优先，其次 relation.sort，稳定去重）
         */
        List<Long> listEntityIdsByCategoryIdWithDescendants(Long categoryId, String categoryTypeCode, String entityTypeCode);

        /**
         * 查询多个分类（仅输入分类本身，不含子树）关联的实体ID列表。
         *
         * @param categoryIds 分类ID列表
         * @param entityTypeCode 业务类型编码
         * @return 有序实体ID列表（先分类顺序，再 relation.sort，稳定去重）
         */
        List<Long> listEntityIdsByCategoryIdsOnly(List<Long> categoryIds, String entityTypeCode);

        /**
         * 查询多分类（每个分类都含子树）关联的实体ID列表。
         *
         * <p>前置约束：CategoryService 先对每个输入分类展开 descendants 并合并去重后，
         * 再调用关系服务进行关联查询。</p>
         *
         * <ul>
         *   <li>返回 entityId 列表（供 EntityService 继续查详情）；</li>
         *   <li>按关联排序语义保持顺序，并做去重（同一实体命中多个分类仅保留一次）。</li>
         * </ul>
         *
         * <p><b>不适用场景</b>：若仅需当前分类（不包含子分类）请使用
         * {@link #listEntityIdsByCategoryIdsOnly(List, String)}。</p>
         *
         * @param categoryIds 分类ID列表（建议传入已展开的 descendant categoryIds）
         * @param entityTypeCode 业务类型编码
         * @return 有序实体ID列表（先分类顺序，再 relation.sort，稳定去重）
         */
        List<Long> listEntityIdsByCategoryIdsWithDescendants(List<Long> categoryIds, String entityTypeCode);

        // ==================== 实体分类关系分页查询接口（仅返回 entityId） ====================

        /**
         * 单分类（仅当前分类，不含子树）查询实体ID分页。
         */
        PageResult<Long> pageEntityIdsByCategoryIdOnly(Long categoryId, String entityTypeCode,
                                                        Integer pageNo, Integer pageSize);

        /**
         * 单分类（含子树）查询实体ID分页。
         */
        PageResult<Long> pageEntityIdsByCategoryIdWithDescendants(Long categoryId, String categoryTypeCode, String entityTypeCode,
                                                                        Integer pageNo, Integer pageSize);

        /**
         * 多分类（仅输入分类本身，不含子树）查询实体ID分页。
         */
        PageResult<Long> pageEntityIdsByCategoryIdsOnly(List<Long> categoryIds, String entityTypeCode,
                                                                Integer pageNo, Integer pageSize);

        /**
         * 多分类（每个分类都含子树）查询实体ID分页。
         */
        PageResult<Long> pageEntityIdsByCategoryIdsWithDescendants(List<Long> categoryIds, String entityTypeCode,
                                                                        Integer pageNo, Integer pageSize);

        // ==================== DB 前置分页查询接口（新增，保留原内存分页方案） ====================

        /**
         * 多分类（仅输入分类本身，不含子树）DB 前置分页查询实体ID。
         *
         * <p>排序规则：categoryIds 输入顺序(rank) -> 分类内 sort -> relation.id，去重后分页。</p>
         */
        PageResult<Long> pageEntityIdsByCategoryIdsOnlyDb(List<Long> categoryIds, String entityTypeCode,
                                                                Integer pageNo, Integer pageSize);

        /**
         * 多分类（每个分类都含子树）DB 前置分页查询实体ID。
         *
         * <p>先展开子树，再按 rank 排序与去重后分页。</p>
         */
        PageResult<Long> pageEntityIdsByCategoryIdsWithDescendantsDb(List<Long> categoryIds, String entityTypeCode,
                                                                        Integer pageNo, Integer pageSize);

        // ==================== 级联删除操作 ====================

        /**
         * 删除实体的所有分类关联（用于删除实体时级联清理）
         *
         * @param entityId 实体ID
         */
        void deleteAllByEntityId(Long entityId);

        /**
         * 按业务类型删除实体的分类关联（避免跨表同 id）。
         */
        void deleteAllByEntityIdInBusiness(Long entityId, String entityTypeCode);

        /**
         * 批量删除多个实体的所有分类关联
         *
         * @param entityIds 实体ID列表
         */
        void deleteAllByEntityIds(List<Long> entityIds);

        /**
         * 删除分类的所有实体关联（用于删除分类时级联清理）
         *
         * @param categoryId 分类ID
         * @param entityTypeCode 业务类型编码
         */
        void deleteAllByCategoryId(Long categoryId);

        /**
         * 删除分类在指定业务下的所有实体关联。
         */
        void deleteAllByCategoryIdInBusiness(Long categoryId, String entityTypeCode);

        /**
         * 批量删除多个分类的所有实体关联（按业务类型隔离）
         *
         * @param categoryIds 分类ID列表
         * @param entityTypeCode 业务类型编码
         */
        void deleteAllByCategoryIds(List<Long> categoryIds);

        /**
         * 批量删除多个分类在指定业务下的所有实体关联。
         */
        void deleteAllByCategoryIdsInBusiness(List<Long> categoryIds, String entityTypeCode);

        // ==================== 业务域同步 ====================

        /**
         * 实体业务域变更后，把新业务域同步到这些实体的全部分类关联（含软删除的排除标记）。
         *
         * <p>关联行的业务域只是实体行的镜像；型号跨业务域迁移、单实体换型号都必须调用本方法，
         * 否则点分类按业务域过滤会漏掉刚迁移的实体。</p>
         *
         * @param entityIds 实体ID列表
         * @param entityTypeCode 数据类型编码（内部归一为实际存储类型）
         * @param domain 新业务域；为空表示实体无业务域
         * @return 实际更新的关联行数
         */
        int syncRelationDomainByEntityIds(List<Long> entityIds, String entityTypeCode, String domain);

        /**
         * 按提交顺序重写指定分类下实体关联的 sort（SparseSortUtils.reindexSortByPosition）。
         *
         * <p>仅更新 {@code dynamic_entity_category_relation.sort}，不改实体表 sort。</p>
         *
         * @param categoryId 分类 ID
         * @param entityTypeCode 业务类型编码（存储类型）
         * @param entityIdsInOrder 目标顺序的实体 ID 列表（从前往后为第 0、1、… 位）
         */
        void reindexEntitySortInCategory(Long categoryId, String entityTypeCode, List<Long> entityIdsInOrder);
}
