package cn.cheers.x.module.dynamicbusiness.service.model;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelPageReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelAvailableFieldRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelBatchSortReqVO;

import java.util.List;

/**
 * 业务模型 Service 接口
 *
 * @author yudao
 */
public interface ModelService {

    // ==================== 存在性检查方法 ====================

    /**
     * 检查模型是否存在
     *
     * @param modelId 模型ID
     * @return 是否存在
     */
    boolean existsById(Long modelId);

    // ==================== 基础 CRUD 操作 ====================

    /**
     * 创建业务模型
     *
     * @param reqVO 创建信息
     * @return 模型ID
     */
    Long createModel(ModelCreateReqVO reqVO);

    /**
     * 更新业务模型
     *
     * @param reqVO 更新信息
     */
    void updateModel(ModelUpdateReqVO reqVO);

    /**
     * 删除业务模型
     *
     * @param id 模型ID
     */
    void deleteModel(Long id);

    /**
     * 获取业务模型详情
     *
     * @param id 模型ID
     * @return 模型详情
     */
    ModelRespVO getModel(Long id);

    /**
     * 根据业务类型编码获取模型列表
     *
     * 规则：
     * - 仅返回指定 entityTypeCode 下的模型
     * - 返回扁平列表，不构建树结构
     * - 自动填充每个模型关联的分类ID列表
     *
     * @param entityTypeCode 业务类型编码
     * @return 模型列表
     */
    List<ModelRespVO> listModelsByEntityType(String entityTypeCode);

    /**
     * 按分类体系查询未挂接任何分类节点的模型（Pattern B 数据管理「未分类」）。
     */
    List<ModelRespVO> listUncategorizedModelsByCategoryType(String categoryTypeCode, String entityTypeCode);

    /**
     * 获取跨业务类型的模型列表（不分页，含启用/停用）。
     *
     * 规则：
     * - 返回扁平列表，不构建树结构
     * - 自动填充每个模型关联的分类ID列表
     *
     * @return 模型列表
     */
    List<ModelRespVO> listModelsAcrossEntityTypes();

    /**
     * 获取跨业务类型的启用模型列表（不分页）。
     *
     * 规则：
     * - 仅返回启用（status = 1）的模型
     * - 跨业务类型返回扁平列表，不构建树结构
     * - 自动填充每个模型关联的分类ID列表
     *
     * @return 模型列表
     */
    List<ModelRespVO> listEnabledModelsAcrossEntityTypes();

    /**
     * 兼容旧命名：获取跨业务类型的启用模型列表（不分页）。
     *
     * @deprecated 请使用 {@link #listEnabledModelsAcrossEntityTypes()}
     */
    @Deprecated
    List<ModelRespVO> listAllModels();


    /**
     * 分页查询业务类型下的业务模型
     *
     * @param reqVO 分页查询条件
     * @return 分页结果
     */
    PageResult<ModelRespVO> pageModelByEntityTypeCode(ModelPageReqVO reqVO);

    /**
     * 搜索业务模型
     *
     * @param keyword 关键词
     * @param entityTypeCode 业务类型编码
     * @return 模型列表
     */
    List<ModelRespVO> searchModels(String keyword, String entityTypeCode);

    // ========== 模型字段查询（视图配置用）==========

    /**
     * 获取模型可用字段列表（供视图配置使用）
     *
     * 返回该模型所有可用字段（包括固定列字段和扩展字段），
     * 用于前端视图配置时选择展示字段。
     *
     * @param modelId 模型ID
     * @return 可用字段列表
     */
    List<ModelAvailableFieldRespVO> listModelAvailableFields(Long modelId);

    // ========== 批量查询接口（性能优化）==========

    /**
     * 批量获取业务模型详情
     *
     * 用于前端一次性获取多个模型的信息，减少 N+1 查询问题。
     *
     * @param ids 模型ID列表
     * @return 模型详情列表
     */
    List<ModelRespVO> getModelsByIds(List<Long> ids);

    /**
     * 按“分类 + 业务”统一查询有序模型ID。
     *
     * <p>当 pageNo/pageSize 为空时返回全量；当 pageNo/pageSize 有值时返回分页切片。</p>
     * <p>当未选分类时，使用 categoryTypeCode 对应根分类作为默认分类范围。</p>
     */
    PageResult<Long> queryOrderedModelIdsByCategoriesInBusiness(List<Long> categoryIds, String categoryTypeCode, String entityTypeCode,
                                                                Integer pageNo, Integer pageSize);

    /**
     * 批量更新模型在业务类型下的排序（用于拖拽后一次提交）。
     */
    void batchUpdateModelSort(ModelBatchSortReqVO reqVO);
}

