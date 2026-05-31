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

    void deleteRelation(Long id, String sourceBusinessTypeCode, String targetBusinessTypeCode);

    EntityRelationRespVO getRelation(Long id, String sourceBusinessTypeCode, String targetBusinessTypeCode);

    List<EntityRelationRespVO> getRelationsBySourceEntity(Long sourceEntityId, String businessTypeCode);

    List<EntityRelationRespVO> getRelationsByTargetEntity(Long targetEntityId, String businessTypeCode);

    List<EntityRelationRespVO> getAllRelationsByEntity(Long entityId, String businessTypeCode);

    List<EntityRelationRespVO> getRelatedEntities(Long sourceEntityId, String sourceBusinessTypeCode,
                                                    Long targetEntityId, String targetBusinessTypeCode,
                                                    String relationType);

    boolean hasRelations(Long entityId, String businessTypeCode);

    Long countRelations(Long entityId, String businessTypeCode);

    int deleteAllRelationsByEntity(Long entityId, String businessTypeCode);

    boolean existsRelation(Long sourceEntityId, String sourceBusinessTypeCode,
                            Long targetEntityId, String targetBusinessTypeCode);

    List<EntityRelationRespVO> getRelationsByTargetEntity(Long targetEntityId, String businessTypeCode, String sourceModelCode);

    Long countRelationsByTargetEntity(Long targetEntityId, String businessTypeCode);

    List<EntityRelationRespVO> getRelationsByFieldCode(Long entityId, String businessTypeCode, String fieldCode);

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
}
