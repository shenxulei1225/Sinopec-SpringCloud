package cn.cheers.x.module.dynamicbusiness.service.entity.relation;

import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRelationCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRelationRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRelationUpdateReqVO;

import java.util.List;
import java.util.Map;

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

    /**
     * 经引用字段反查主体：单值专用列字段查引用方专用表的那一列，其余查实体关联表。
     *
     * @param refFieldCode          主体实体上的 REF 字段编码（{@code dynamic_field.code}）
     * @param subjectEntityTypeCode 主体实体类型编码（决定去哪张专用表查列；也是关联表的源类型）
     * @param targetEntityIds       REF 指向的目标实体 id 集合
     */
    List<Long> listSubjectEntityIdsByRefFieldAndTargetIds(String refFieldCode,
                                                          String subjectEntityTypeCode,
                                                          List<Long> targetEntityIds);

    /**
     * 实体—实体筛选：两类记录互相挂了哪些，不按字段编码过滤。
     *
     * <p>来源：两边的单值引用专用列（双向），实体关联表（双向，含勾选适用写入的无字段码行）。</p>
     *
     * @param relatedEntityIds 上游已选实体 id
     * @param relatedEntityTypeCode 上游实体存储类型
     * @param listEntityTypeCode 当前列表实体存储类型
     * @return 对端实体 id（去重保序）；任一侧空则空列表
     */
    List<Long> listCounterpartEntityIds(List<Long> relatedEntityIds,
                                        String relatedEntityTypeCode,
                                        String listEntityTypeCode);

    /**
     * 按每个上游实体保留对端编号分组。
     */
    Map<Long, List<Long>> listCounterpartEntityIdsGrouped(List<Long> relatedEntityIds,
                                                          String relatedEntityTypeCode,
                                                          String listEntityTypeCode);
}
