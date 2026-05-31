package cn.cheers.x.module.dynamicbusiness.service.entity.relation;

import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRelationCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRelationRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRelationUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityRelationDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entity.EntityRelationMapper;
import cn.cheers.x.module.dynamicbusiness.enums.entity.EntityRelationTypeEnum;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 业务实体关联关系 Service 实现
 *
 * <p>通过 EntityService 进行实体查询，支持多存储策略（通用表和动态表）。</p>
 */
@Service
@Validated
@Slf4j
public class EntityRelationServiceImpl implements EntityRelationService {

    @Resource
    private EntityRelationMapper entityRelationMapper;

    @Resource
    @Lazy // 避免循环依赖
    private EntityService entityService;

    @Override
    public Long createRelation(EntityRelationCreateReqVO reqVO) {
        // 校验关联类型
        validateRelationType(reqVO.getRelationType());

        // 校验源实体存在并获取实体信息（使用 businessTypeCode 参数，支持多存储策略）
        EntityRespVO sourceEntity = validateEntityExists(reqVO.getSourceEntityId(), reqVO.getSourceBusinessTypeCode(), "源实体");

        // 校验目标实体存在并获取实体信息（使用 businessTypeCode 参数，支持多存储策略）
        EntityRespVO targetEntity = validateEntityExists(reqVO.getTargetEntityId(), reqVO.getTargetBusinessTypeCode(), "目标实体");

        // 校验不能自关联
        if (reqVO.getSourceEntityId().equals(reqVO.getTargetEntityId())) {
            throw new ServiceException(400, "实体不能与自身建立关联关系");
        }

        // 校验关联关系不存在
        if (existsRelation(reqVO.getSourceEntityId(), reqVO.getSourceBusinessTypeCode(),
                reqVO.getTargetEntityId(), reqVO.getTargetBusinessTypeCode())) {
            throw new ServiceException(400, "该关联关系已存在");
        }

        // 获取源实体和目标实体的业务类型编码（优先使用请求中的值，如果没有则使用从实体中获取的值）
        String sourceBusinessTypeCode = reqVO.getSourceBusinessTypeCode();
        if (sourceBusinessTypeCode == null || sourceBusinessTypeCode.isEmpty()) {
            sourceBusinessTypeCode = sourceEntity.getBusinessTypeCode();
        }
        String targetBusinessTypeCode = reqVO.getTargetBusinessTypeCode();
        if (targetBusinessTypeCode == null || targetBusinessTypeCode.isEmpty()) {
            targetBusinessTypeCode = targetEntity.getBusinessTypeCode();
        }

        // 创建关联关系
        EntityRelationDO relation = EntityRelationDO.builder()
                .sourceEntityId(reqVO.getSourceEntityId())
                .targetEntityId(reqVO.getTargetEntityId())
                .sourceBusinessTypeCode(sourceBusinessTypeCode)
                .targetBusinessTypeCode(targetBusinessTypeCode)
                .relationType(reqVO.getRelationType())
                .relationName(reqVO.getRelationName())
                .description(reqVO.getDescription())
                .relationAttributes(reqVO.getRelationAttributes())
                .status(1)
                .build();
        relation.setTenantId(getTenantId());

        entityRelationMapper.insert(relation);
        return relation.getId();
    }

    @Override
    public void updateRelation(EntityRelationUpdateReqVO reqVO) {
        // 校验关联关系存在
        getRelationDO(reqVO.getId());

        // 校验关联类型
        if (reqVO.getRelationType() != null) {
            validateRelationType(reqVO.getRelationType());
        }

        // 更新关联关系
        EntityRelationDO update = new EntityRelationDO();
        update.setId(reqVO.getId());
        update.setRelationType(reqVO.getRelationType());
        update.setRelationName(reqVO.getRelationName());
        update.setDescription(reqVO.getDescription());
        update.setRelationAttributes(reqVO.getRelationAttributes());
        update.setStatus(reqVO.getStatus());

        entityRelationMapper.updateById(update);
    }


    @Override
    public void deleteRelation(Long id, String sourceBusinessTypeCode, String targetBusinessTypeCode) {
        // 校验关联关系存在
        getRelationDO(id);
        // businessTypeCode 参数预留用于后续扩展（如验证实体存在性）
        entityRelationMapper.deleteById(id);
    }


    @Override
    public EntityRelationRespVO getRelation(Long id, String sourceBusinessTypeCode, String targetBusinessTypeCode) {
        EntityRelationDO relation = getRelationDO(id);
        return convertToRespVO(relation, sourceBusinessTypeCode, targetBusinessTypeCode);
    }


    @Override
    public List<EntityRelationRespVO> getRelationsBySourceEntity(Long sourceEntityId, String businessTypeCode) {
        List<EntityRelationDO> relations = entityRelationMapper.selectBySourceEntityId(sourceEntityId);
        return convertToRespVOList(relations, businessTypeCode, null);
    }


    @Override
    public List<EntityRelationRespVO> getRelationsByTargetEntity(Long targetEntityId, String businessTypeCode) {
        List<EntityRelationDO> relations = entityRelationMapper.selectByTargetEntityId(targetEntityId);
        // 如果数据库中有存储 sourceBusinessTypeCode，优先使用；否则尝试从实体中获取
        return convertToRespVOList(relations, null, businessTypeCode);
    }


    @Override
    public List<EntityRelationRespVO> getAllRelationsByEntity(Long entityId, String businessTypeCode) {
        List<EntityRelationDO> relations = entityRelationMapper.selectByEntityId(entityId);
        return convertToRespVOList(relations, businessTypeCode, businessTypeCode);
    }





    @Override
    public List<EntityRelationRespVO> getRelatedEntities(Long sourceEntityId, String sourceBusinessTypeCode,
                                                            Long targetEntityId, String targetBusinessTypeCode,
                                                            String relationType) {
        // 参数校验：必须提供源实体参数或目标实体参数之一
        // 支持两种场景：
        // 1. 通过源实体查询：sourceEntityId + sourceBusinessTypeCode（可选：targetBusinessTypeCode用于过滤）
        // 2. 通过目标实体查询：targetEntityId + sourceBusinessTypeCode（用于过滤源业务类型）
        boolean hasSourceParams = sourceEntityId != null && sourceBusinessTypeCode != null;
        boolean hasTargetParams = targetEntityId != null && sourceBusinessTypeCode != null;

        if (!hasSourceParams && !hasTargetParams) {
            throw new ServiceException(400, "必须提供源实体参数（sourceEntityId和sourceBusinessTypeCode）或目标实体参数（targetEntityId和sourceBusinessTypeCode）");
        }

        List<EntityRelationDO> relations;

        // 根据提供的参数类型查询
        if (sourceEntityId != null && sourceBusinessTypeCode != null) {
            // 通过源实体查询：查询目标业务类型的关联实体
            // 如果提供了targetBusinessTypeCode，则按目标业务类型过滤
            if (targetBusinessTypeCode != null && !targetBusinessTypeCode.isEmpty()) {
                // 按目标业务类型编码过滤
                if (relationType != null && !relationType.isEmpty()) {
                    relations = entityRelationMapper.selectBySourceEntityIdAndTargetBusinessTypeCodeAndType(
                            sourceEntityId, targetBusinessTypeCode, relationType);
                } else {
                    relations = entityRelationMapper.selectBySourceEntityIdAndTargetBusinessTypeCode(
                            sourceEntityId, targetBusinessTypeCode);
                }
            } else {
                // 不按目标业务类型过滤，查询所有关联实体
                if (relationType != null && !relationType.isEmpty()) {
                    relations = entityRelationMapper.selectBySourceAndType(sourceEntityId, relationType);
                } else {
                    relations = entityRelationMapper.selectBySourceEntityId(sourceEntityId);
                }
            }
            return convertToRespVOList(relations, sourceBusinessTypeCode, targetBusinessTypeCode);
        } else {
            // 通过目标实体查询：查询源业务类型的关联实体
            // 使用sourceBusinessTypeCode来过滤源业务类型
            if (relationType != null && !relationType.isEmpty()) {
                relations = entityRelationMapper.selectByTargetEntityIdAndSourceBusinessTypeCodeAndType(
                        targetEntityId, sourceBusinessTypeCode, relationType);
            } else {
                relations = entityRelationMapper.selectByTargetEntityIdAndSourceBusinessTypeCode(
                        targetEntityId, sourceBusinessTypeCode);
            }
            return convertToRespVOList(relations, sourceBusinessTypeCode, targetBusinessTypeCode);
        }
    }



    @Override
    public boolean hasRelations(Long entityId, String businessTypeCode) {
        return countRelations(entityId, businessTypeCode) > 0;
    }



    @Override
    public Long countRelations(Long entityId, String businessTypeCode) {
        // businessTypeCode 参数预留用于后续扩展
        return entityRelationMapper.countByEntityId(entityId);
    }



    @Override
    public int deleteAllRelationsByEntity(Long entityId, String businessTypeCode) {
        // businessTypeCode 参数预留用于后续扩展
        return entityRelationMapper.deleteByEntityId(entityId);
    }



    @Override
    public boolean existsRelation(Long sourceEntityId, String sourceBusinessTypeCode,
                                    Long targetEntityId, String targetBusinessTypeCode) {
        // businessTypeCode 参数预留用于后续扩展（如验证实体存在性）
        EntityRelationDO relation = entityRelationMapper.selectBySourceAndTarget(sourceEntityId, targetEntityId);
        return relation != null;
    }

    @Override
    public List<EntityRelationRespVO> getRelationsByTargetEntity(Long targetEntityId, String businessTypeCode, String sourceModelCode) {
        List<EntityRelationDO> relations;
        if (sourceModelCode != null && !sourceModelCode.isEmpty()) {
            // 按源 Model 过滤
            relations = entityRelationMapper.selectByTargetEntityIdAndSourceModelCode(targetEntityId, sourceModelCode);
        } else {
            // 不过滤，返回所有反向关联
            relations = entityRelationMapper.selectByTargetEntityId(targetEntityId);
        }
        return convertToRespVOList(relations, null, businessTypeCode);
    }

    @Override
    public Long countRelationsByTargetEntity(Long targetEntityId, String businessTypeCode) {
        // 使用 Mapper 的统计方法
        List<EntityRelationDO> relations = entityRelationMapper.selectByTargetEntityId(targetEntityId);
        return (long) relations.size();
    }

    @Override
    public List<EntityRelationRespVO> getRelationsByFieldCode(Long entityId, String businessTypeCode, String fieldCode) {
        List<EntityRelationDO> relations = entityRelationMapper.selectBySourceEntityIdAndFieldCode(entityId, fieldCode);
        return convertToRespVOList(relations, businessTypeCode, null);
    }

    @Override
    public List<Long> listEntityIdsByRelationFieldAndRelatedIds(String fieldCode, List<Long> relatedEntityIds) {
        if (fieldCode == null || fieldCode.isBlank() || relatedEntityIds == null || relatedEntityIds.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        return entityRelationMapper.selectSourceEntityIdsByFieldCodeAndTargetIds(fieldCode, relatedEntityIds);
    }

    private void validateRelationType(String relationType) {
        if (!EntityRelationTypeEnum.isValid(relationType)) {
            throw new ServiceException(400, "无效的关联类型：" + relationType +
                    "，支持的类型：ONE_TO_ONE, ONE_TO_MANY, MANY_TO_MANY");
        }
    }

    private EntityRespVO validateEntityExists(Long entityId, String businessTypeCode, String entityDesc) {
        EntityRespVO entity;
        try {
            entity = entityService.get(entityId, businessTypeCode);
        } catch (Exception e) {
            log.warn("查询实体失败: entityId={}, businessTypeCode={}, error={}",
                    entityId, businessTypeCode, e.getMessage());
            throw new ServiceException(404, entityDesc + "不存在");
        }

        if (entity == null) {
            throw new ServiceException(404, entityDesc + "不存在");
        }
        return entity;
    }

    private EntityRelationDO getRelationDO(Long id) {
        EntityRelationDO relation = entityRelationMapper.selectById(id);
        if (relation == null) {
            throw new ServiceException(404, "关联关系不存在");
        }
        return relation;
    }

    private EntityRelationRespVO convertToRespVO(EntityRelationDO relation) {
        return convertToRespVO(relation, null, null);
    }

    private EntityRelationRespVO convertToRespVO(EntityRelationDO relation,
                                                    String sourceBusinessTypeCode,
                                                    String targetBusinessTypeCode) {
        EntityRelationRespVO respVO = new EntityRelationRespVO();
        respVO.setId(relation.getId());
        respVO.setSourceEntityId(relation.getSourceEntityId());
        respVO.setTargetEntityId(relation.getTargetEntityId());
        respVO.setRelationType(relation.getRelationType());
        respVO.setRelationName(relation.getRelationName());
        respVO.setDescription(relation.getDescription());
        respVO.setRelationAttributes(relation.getRelationAttributes());
        respVO.setStatus(relation.getStatus());
        respVO.setCreateTime(relation.getCreateTime());
        respVO.setUpdateTime(relation.getUpdateTime());

        String actualSourceBusinessTypeCode = relation.getSourceBusinessTypeCode() != null
                ? relation.getSourceBusinessTypeCode()
                : sourceBusinessTypeCode;
        String actualTargetBusinessTypeCode = relation.getTargetBusinessTypeCode() != null
                ? relation.getTargetBusinessTypeCode()
                : targetBusinessTypeCode;

        try {
            EntityRespVO sourceEntity;
            sourceEntity = entityService.get(relation.getSourceEntityId(), actualSourceBusinessTypeCode);
            if (sourceEntity != null) {
                respVO.setSourceEntityName(sourceEntity.getName());
                respVO.setSourceBusinessTypeCode(
                    relation.getSourceBusinessTypeCode() != null
                        ? relation.getSourceBusinessTypeCode()
                        : sourceEntity.getBusinessTypeCode()
                );
            } else if (relation.getSourceBusinessTypeCode() != null) {
                respVO.setSourceBusinessTypeCode(relation.getSourceBusinessTypeCode());
            }
        } catch (Exception e) {
            log.debug("获取源实体名称失败: sourceEntityId={}, error={}", relation.getSourceEntityId(), e.getMessage());
            if (relation.getSourceBusinessTypeCode() != null) {
                respVO.setSourceBusinessTypeCode(relation.getSourceBusinessTypeCode());
            }
        }

        try {
            EntityRespVO targetEntity;
            targetEntity = entityService.get(relation.getTargetEntityId(), actualTargetBusinessTypeCode);
            if (targetEntity != null) {
                respVO.setTargetEntityName(targetEntity.getName());
                respVO.setTargetBusinessTypeCode(
                    relation.getTargetBusinessTypeCode() != null
                        ? relation.getTargetBusinessTypeCode()
                        : targetEntity.getBusinessTypeCode()
                );
            } else if (relation.getTargetBusinessTypeCode() != null) {
                respVO.setTargetBusinessTypeCode(relation.getTargetBusinessTypeCode());
            }
        } catch (Exception e) {
            log.debug("获取目标实体名称失败: targetEntityId={}, error={}", relation.getTargetEntityId(), e.getMessage());
            if (relation.getTargetBusinessTypeCode() != null) {
                respVO.setTargetBusinessTypeCode(relation.getTargetBusinessTypeCode());
            }
        }

        return respVO;
    }

    private List<EntityRelationRespVO> convertToRespVOList(List<EntityRelationDO> relations) {
        return convertToRespVOList(relations, null, null);
    }

    private List<EntityRelationRespVO> convertToRespVOList(List<EntityRelationDO> relations,
                                                            String sourceBusinessTypeCode,
                                                            String targetBusinessTypeCode) {
        List<EntityRelationRespVO> result = new ArrayList<>();
        for (EntityRelationDO relation : relations) {
            result.add(convertToRespVO(relation, sourceBusinessTypeCode, targetBusinessTypeCode));
        }
        return result;
    }

    private Long getTenantId() {
        return Objects.requireNonNullElse(TenantContextHolder.getRequiredTenantId(), 0L);
    }
}
