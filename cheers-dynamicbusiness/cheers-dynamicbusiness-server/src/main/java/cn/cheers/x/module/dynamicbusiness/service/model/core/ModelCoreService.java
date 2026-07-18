package cn.cheers.x.module.dynamicbusiness.service.model.core;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;

import java.util.List;
import java.util.Set;

/**
 * Model Core Service
 *
 * <p>仅负责模型本体（模型表）相关的标准能力：</p>
 * <ul>
 *   <li>基础写入：create / update / delete</li>
 *   <li>基础读取：get / list / page（仅模型表条件）</li>
 *   <li>存在性检查：exists / filterExistingIds</li>
 * </ul>
 */
public interface ModelCoreService {

    // ==================== Existence ====================

    /**
     * 检查指定业务类型下模型是否存在。
     */
    boolean existsById(Long modelId, String entityTypeCode);

    /**
     * 批量过滤出“真实存在”的模型ID集合。
     */
    Set<Long> filterExistingModelIds(List<Long> modelIds, String entityTypeCode);

    // ==================== CRUD ====================

    Long create(ModelDO model);

    void update(ModelDO model);

    void delete(Long id);

    ModelDO get(Long id);

    List<ModelDO> listByEntityTypeCode(String entityTypeCode);

    List<ModelDO> listEnabledModelsByEntityTypeCode(String entityTypeCode);

    List<ModelDO> searchLikeInEntityType(String keyword, String entityTypeCode);


    ModelDO getByNameInEntityType(String name, String entityTypeCode);

    List<ModelDO> listByIds(List<Long> ids);

    PageResult<ModelDO> pageModels(String entityTypeCode, String dataScope, String keyword, Integer status, Integer pageNo, Integer pageSize);
}
