package cn.cheers.x.module.dynamicbusiness.service.model.core;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
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
    boolean existsById(Long modelId, String businessTypeCode);

    /**
     * 批量过滤出“真实存在”的模型ID集合。
     */
    Set<Long> filterExistingModelIds(List<Long> modelIds, String businessTypeCode);

    // ==================== CRUD ====================

    Long create(ModelDO model);

    void update(ModelDO model);

    void delete(Long id);

    ModelDO get(Long id);

    List<ModelDO> listByBusinessTypeCode(String businessTypeCode);

    List<ModelDO> listEnabledModelsByBusinessTypeCode(String businessTypeCode);

    List<ModelDO> searchLikeInBusinessType(String keyword, String businessTypeCode);


    ModelDO getByNameInBusinessType(String name, String businessTypeCode);

    List<ModelDO> listByIds(List<Long> ids);

    PageResult<ModelDO> pageModels(String businessTypeCode, String keyword, Integer status, Integer pageNo, Integer pageSize);
}
