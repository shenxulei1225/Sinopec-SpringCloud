package cn.cheers.x.module.dynamicbusiness.service.datamgmt;

import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmEntityDimensionRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmEntityDimensionSaveItemVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmEntityDimensionSaveReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datamgmt.DmEntityDimensionDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.datamgmt.DmEntityDimensionMapper;
import cn.cheers.x.module.dynamicbusiness.enums.datamgmt.DmDimensionKindEnum;
import cn.cheers.x.module.dynamicbusiness.service.entitytype.EntityTypeCategoryBootstrapService;
import cn.cheers.x.framework.common.exception.ServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Validated
public class DmEntityDimensionServiceImpl implements DmEntityDimensionService {

    @Resource
    private DmEntityDimensionMapper dmEntityDimensionMapper;

    @Resource
    private EntityTypeCategoryBootstrapService entityTypeCategoryBootstrapService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<DmEntityDimensionRespVO> listByEntityTypeCode(String entityTypeCode) {
        String code = normalizeEntityTypeCode(entityTypeCode);
        entityTypeCategoryBootstrapService.ensureForEntityTypeCode(code);
        List<DmEntityDimensionDO> rows = dmEntityDimensionMapper.selectListByEntityTypeCode(code);
        if (rows.isEmpty()) {
            return buildDefaultDimensions(code);
        }
        return rows.stream().map(this::toRespVO).toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveDimensions(DmEntityDimensionSaveReqVO reqVO) {
        String code = normalizeEntityTypeCode(reqVO.getEntityTypeCode());
        List<DmEntityDimensionSaveItemVO> items = dedupeSaveItems(reqVO.getDimensions());
        validateSaveItems(items);

        List<DmEntityDimensionDO> existing = dmEntityDimensionMapper.selectListByEntityTypeCode(code);
        Map<String, DmEntityDimensionDO> existingByScope = new LinkedHashMap<>();
        for (DmEntityDimensionDO row : existing) {
            existingByScope.put(scopeKey(row.getDimensionKind(), row.getPerspectiveId()), row);
        }

        Set<String> savedScopes = new HashSet<>();
        for (DmEntityDimensionSaveItemVO item : items) {
            String kind = item.getDimensionKind().trim().toUpperCase();
            String perspectiveId = normalizePerspectiveId(item.getPerspectiveId());
            String scope = scopeKey(kind, perspectiveId);
            savedScopes.add(scope);

            DmEntityDimensionDO row = existingByScope.get(scope);
            if (row != null) {
                row.setPropsId(item.getPropsId());
                row.setEnabled(item.getEnabled() == null || item.getEnabled());
                row.setCategoryDimensionMeta(toMetaMap(item.getCategoryDimensionMeta()));
                row.setModelAdminCategoryMeta(toMetaMap(item.getModelAdminCategoryMeta()));
                dmEntityDimensionMapper.updateById(row);
                continue;
            }

            DmEntityDimensionDO insert = new DmEntityDimensionDO();
            insert.setEntityTypeCode(code);
            insert.setDimensionKind(kind);
            insert.setPerspectiveId(perspectiveId);
            insert.setPropsId(item.getPropsId());
            insert.setEnabled(item.getEnabled() == null || item.getEnabled());
            insert.setCategoryDimensionMeta(toMetaMap(item.getCategoryDimensionMeta()));
            insert.setModelAdminCategoryMeta(toMetaMap(item.getModelAdminCategoryMeta()));
            dmEntityDimensionMapper.insert(insert);
        }

        for (DmEntityDimensionDO row : existing) {
            String scope = scopeKey(row.getDimensionKind(), row.getPerspectiveId());
            if (!savedScopes.contains(scope)) {
                dmEntityDimensionMapper.deleteById(row.getId());
            }
        }

        dmEntityDimensionMapper.deletePhysicalSoftDeletedByEntityTypeCode(code);
    }

    private List<DmEntityDimensionSaveItemVO> dedupeSaveItems(List<DmEntityDimensionSaveItemVO> dimensions) {
        if (dimensions == null || dimensions.isEmpty()) {
            return List.of();
        }
        Map<String, DmEntityDimensionSaveItemVO> deduped = new LinkedHashMap<>();
        for (DmEntityDimensionSaveItemVO item : dimensions) {
            String kind = item.getDimensionKind() == null ? "" : item.getDimensionKind().trim().toUpperCase();
            String perspectiveId = normalizePerspectiveId(item.getPerspectiveId());
            deduped.put(scopeKey(kind, perspectiveId), item);
        }
        return new ArrayList<>(deduped.values());
    }

    private String scopeKey(String dimensionKind, String perspectiveId) {
        String kind = dimensionKind == null ? "" : dimensionKind.trim().toUpperCase();
        String perspective = perspectiveId == null ? "" : perspectiveId.trim();
        return kind + "|" + perspective;
    }

    private void validateSaveItems(List<DmEntityDimensionSaveItemVO> dimensions) {
        Set<String> scopes = new HashSet<>();
        for (DmEntityDimensionSaveItemVO item : dimensions) {
            if (!DmDimensionKindEnum.isValid(item.getDimensionKind())) {
                throw new ServiceException(400, "无效的 dimensionKind: " + item.getDimensionKind());
            }
            String kind = item.getDimensionKind().trim().toUpperCase();
            if (DmDimensionKindEnum.CATEGORY.getCode().equals(kind)
                    && !StringUtils.hasText(item.getPerspectiveId())) {
                throw new ServiceException(400, "CATEGORY 维度必须提供 perspectiveId");
            }
            if (!DmDimensionKindEnum.CATEGORY.getCode().equals(kind)
                    && StringUtils.hasText(item.getPerspectiveId())) {
                throw new ServiceException(400, kind + " 维度不应设置 perspectiveId");
            }
            String scope = scopeKey(kind, normalizePerspectiveId(item.getPerspectiveId()));
            if (!scopes.add(scope)) {
                throw new ServiceException(400, "浏览维度配置重复：" + kind
                        + (StringUtils.hasText(item.getPerspectiveId()) ? " / " + item.getPerspectiveId() : ""));
            }
        }
    }

    private List<DmEntityDimensionRespVO> buildDefaultDimensions(String entityTypeCode) {
        List<DmEntityDimensionRespVO> list = new ArrayList<>();
        list.add(defaultSimpleDimension(entityTypeCode, DmDimensionKindEnum.MODEL.getCode()));
        list.add(defaultSimpleDimension(entityTypeCode, DmDimensionKindEnum.ENTITY.getCode()));
        return list;
    }

    private DmEntityDimensionRespVO defaultSimpleDimension(String entityTypeCode, String kind) {
        DmEntityDimensionRespVO row = new DmEntityDimensionRespVO();
        row.setEntityTypeCode(entityTypeCode);
        row.setDimensionKind(kind);
        row.setEnabled(true);
        return row;
    }

    private DmEntityDimensionRespVO toRespVO(DmEntityDimensionDO row) {
        DmEntityDimensionRespVO vo = new DmEntityDimensionRespVO();
        vo.setId(row.getId());
        vo.setEntityTypeCode(row.getEntityTypeCode());
        vo.setDimensionKind(row.getDimensionKind());
        vo.setPerspectiveId(row.getPerspectiveId());
        vo.setPropsId(row.getPropsId());
        vo.setEnabled(row.getEnabled());
        vo.setCategoryDimensionMeta(row.getCategoryDimensionMeta());
        vo.setModelAdminCategoryMeta(row.getModelAdminCategoryMeta());
        return vo;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> toMetaMap(Object meta) {
        if (meta == null) {
            return null;
        }
        if (meta instanceof String text) {
            if (!StringUtils.hasText(text)) {
                return null;
            }
            try {
                return objectMapper.readValue(text, LinkedHashMap.class);
            } catch (JsonProcessingException e) {
                throw new ServiceException(400, "categoryDimensionMeta 不是合法 JSON");
            }
        }
        if (meta instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        }
        throw new ServiceException(400, "维度元数据必须是 JSON 对象");
    }

    private String normalizeEntityTypeCode(String entityTypeCode) {
        if (!StringUtils.hasText(entityTypeCode)) {
            throw new ServiceException(400, "entityTypeCode 不能为空");
        }
        return entityTypeCode.trim();
    }

    private String normalizePerspectiveId(String perspectiveId) {
        if (!StringUtils.hasText(perspectiveId)) {
            return null;
        }
        return perspectiveId.trim();
    }
}
