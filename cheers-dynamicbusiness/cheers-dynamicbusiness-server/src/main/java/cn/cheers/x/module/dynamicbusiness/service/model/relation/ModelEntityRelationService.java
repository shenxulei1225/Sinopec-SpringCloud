package cn.cheers.x.module.dynamicbusiness.service.model.relation;

import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelEntityAssociationRespVO;

import java.util.List;

/**
 * 型号—实体多对多关联（跨类型挂靠）。
 *
 * <p>边界：只维护关联与 ID 序列；不返回实体详情。本类型实体归属仍走实体表 model_id。</p>
 */
public interface ModelEntityRelationService {

    ModelEntityAssociationRespVO associate(Long modelId, Long entityId,
                                           String modelEntityTypeCode, String entityTypeCode);

    ModelEntityAssociationRespVO disassociate(Long modelId, Long entityId,
                                              String modelEntityTypeCode, String entityTypeCode);

    boolean existsRelation(Long modelId, Long entityId,
                           String modelEntityTypeCode, String entityTypeCode);

    ModelEntityAssociationRespVO batchAssociateEntitiesToModel(Long modelId, List<Long> entityIds,
                                                               String modelEntityTypeCode, String entityTypeCode);

    ModelEntityAssociationRespVO batchDisassociateEntitiesFromModel(Long modelId, List<Long> entityIds,
                                                                    String modelEntityTypeCode, String entityTypeCode);

    /**
     * 某型号下挂靠的实体 ID（按 sort、id）。
     */
    List<Long> listEntityIdsByModelId(Long modelId, String modelEntityTypeCode, String entityTypeCode);

    /**
     * 多型号下挂靠的实体 ID（按型号输入顺序展开，去重保序）。
     */
    List<Long> listEntityIdsByModelIds(List<Long> modelIds, String modelEntityTypeCode, String entityTypeCode);

    /**
     * 某实体挂靠的型号 ID。
     *
     * @param modelEntityTypeCode 可选；非空时只返回该类型型号
     */
    List<Long> listModelIdsByEntityId(Long entityId, String entityTypeCode, String modelEntityTypeCode);
}
