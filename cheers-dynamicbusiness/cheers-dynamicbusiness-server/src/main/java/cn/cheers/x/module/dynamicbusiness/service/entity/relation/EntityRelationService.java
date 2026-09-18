package cn.cheers.x.module.dynamicbusiness.service.entity.relation;

import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRelationCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRelationRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRelationUpdateReqVO;

import java.util.List;

/**
 * 业务实体关联关系 Service 接口
 */
public interface EntityRelationService {

    Long createRelation(EntityRelationCreateReqVO reqVO);

    void updateRelation(EntityRelationUpdateReqVO reqVO);

    void deleteRelation(Long id, String sourceEntityTypeCode, String targetEntityTypeCode);

    EntityRelationRespVO getRelation(Long id, String sourceEntityTypeCode, String targetEntityTypeCode);

    List<EntityRelationRespVO> getRelationsBySourceEntity(Long sourceEntityId, String entityTypeCode);

    List<EntityRelationRespVO> getRelationsByTargetEntity(Long targetEntityId, String entityTypeCode);

    List<EntityRelationRespVO> getAllRelationsByEntity(Long entityId, String entityTypeCode);

    List<EntityRelationRespVO> getRelatedEntities(Long sourceEntityId, String sourceEntityTypeCode,
                                                    Long targetEntityId, String targetEntityTypeCode,
                                                    String relationType);

    boolean hasRelations(Long entityId, String entityTypeCode);

    Long countRelations(Long entityId, String entityTypeCode);

    int deleteAllRelationsByEntity(Long entityId, String entityTypeCode);

    boolean existsRelation(Long sourceEntityId, String sourceEntityTypeCode,
                            Long targetEntityId, String targetEntityTypeCode);

    List<EntityRelationRespVO> getRelationsByTargetEntity(Long targetEntityId, String entityTypeCode, String sourceModelCode);

    Long countRelationsByTargetEntity(Long targetEntityId, String entityTypeCode);

    List<EntityRelationRespVO> getRelationsByFieldCode(Long entityId, String entityTypeCode, String fieldCode);

    /**
     * 根据关联字段编码和关联实体ID列表，查询当前实体ID列表。
     *
     * <p>用于实体筛选场景：当 filter 作用于 REF/REF_MULTI 字段时，
     * 通过 dynamic_entity_relation 快速定位命中的 current_entity_id。</p>
     *
     * @param fieldCode 关联字段编码
     * @param relatedEntityIds 关联实体ID列表
     * @return 命中的当前实体ID列表（去重）
     */
    List<Long> listEntityIdsByRelationFieldAndRelatedIds(String fieldCode, List<Long> relatedEntityIds);

    /**
     * 经 REF 反查主体：在 {@link #listEntityIdsByRelationFieldAndRelatedIds} 基础上按源实体类型编码过滤。
     *
     * @param refFieldCode          主体实体上的 REF 字段编码（{@code dynamic_field.code}）
     * @param subjectEntityTypeCode 主体实体类型编码（{@code dynamic_entity_relation.source_entity_type_code}）
     * @param targetEntityIds       REF 指向的目标实体 id 集合
     */
    List<Long> listSubjectEntityIdsByRefFieldAndTargetIds(String refFieldCode,
                                                          String subjectEntityTypeCode,
                                                          List<Long> targetEntityIds);

    /**
     * 实体—实体筛选：按关联表双向取对端实体 id，不按 field_code 过滤。
     *
     * <p>勾选适用写入的行没有 REF 字段码。方向 1：related 作 source、list 作 target → 取 target id；
     * 方向 2：list 作 source、related 作 target → 取 source id。</p>
     *
     * @param relatedEntityIds 上游已选实体 id
     * @param relatedEntityTypeCode 上游实体存储类型
     * @param listEntityTypeCode 当前列表实体存储类型
     * @return 对端实体 id（去重保序）；任一侧空则空列表
     */
    List<Long> listCounterpartEntityIds(List<Long> relatedEntityIds,
                                        String relatedEntityTypeCode,
                                        String listEntityTypeCode);
}
