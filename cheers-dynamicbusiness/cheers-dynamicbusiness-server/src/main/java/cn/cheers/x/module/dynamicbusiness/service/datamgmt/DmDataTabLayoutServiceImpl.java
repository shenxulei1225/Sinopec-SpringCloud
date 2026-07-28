package cn.cheers.x.module.dynamicbusiness.service.datamgmt;

import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmDataTabLayoutRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmDataTabLayoutSaveItemVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmDataTabLayoutSaveReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datamgmt.DmDataTabLayoutDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.datamgmt.DmDataTabLayoutMapper;
import cn.cheers.x.module.dynamicbusiness.enums.datamgmt.DmDataTabLayoutKindEnum;
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
public class DmDataTabLayoutServiceImpl implements DmDataTabLayoutService {

    @Resource
    private DmDataTabLayoutMapper dmDataTabLayoutMapper;

    @Resource
    private EntityTypeCategoryBootstrapService entityTypeCategoryBootstrapService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<DmDataTabLayoutRespVO> listByEntityTypeCode(String entityTypeCode) {
        String code = normalizeEntityTypeCode(entityTypeCode);
        entityTypeCategoryBootstrapService.ensureForEntityTypeCode(code);
        List<DmDataTabLayoutDO> rows = dmDataTabLayoutMapper.selectListByEntityTypeCode(code);
        if (rows.isEmpty()) {
            return buildDefaultColumns(code);
        }
        return rows.stream().map(this::toRespVO).toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveLayouts(DmDataTabLayoutSaveReqVO reqVO) {
        String code = normalizeEntityTypeCode(reqVO.getEntityTypeCode());
        List<DmDataTabLayoutSaveItemVO> items = dedupeSaveItems(reqVO.getLayouts());
        validateSaveItems(items);

        List<DmDataTabLayoutDO> existing = dmDataTabLayoutMapper.selectListByEntityTypeCode(code);
        Map<String, DmDataTabLayoutDO> existingByScope = new LinkedHashMap<>();
        for (DmDataTabLayoutDO row : existing) {
            existingByScope.put(scopeKey(row.getColumnKind(), row.getPerspectiveId()), row);
        }

        Set<String> savedScopes = new HashSet<>();
        for (DmDataTabLayoutSaveItemVO item : items) {
            String kind = item.getColumnKind().trim().toUpperCase();
            String perspectiveId = normalizePerspectiveId(item.getPerspectiveId());
            String scope = scopeKey(kind, perspectiveId);
            savedScopes.add(scope);

            DmDataTabLayoutDO row = existingByScope.get(scope);
            if (row != null) {
                row.setPropsId(item.getPropsId());
                row.setEnabled(item.getEnabled() == null || item.getEnabled());
                row.setCategoryColumn(toMetaMap(item.getCategoryColumn()));
                dmDataTabLayoutMapper.updateById(row);
                continue;
            }

            DmDataTabLayoutDO insert = new DmDataTabLayoutDO();
            insert.setEntityTypeCode(code);
            insert.setColumnKind(kind);
            insert.setPerspectiveId(perspectiveId);
            insert.setPropsId(item.getPropsId());
            insert.setEnabled(item.getEnabled() == null || item.getEnabled());
            insert.setCategoryColumn(toMetaMap(item.getCategoryColumn()));
            dmDataTabLayoutMapper.insert(insert);
        }

        for (DmDataTabLayoutDO row : existing) {
            String scope = scopeKey(row.getColumnKind(), row.getPerspectiveId());
            if (!savedScopes.contains(scope)) {
                dmDataTabLayoutMapper.deleteById(row.getId());
            }
        }

        dmDataTabLayoutMapper.deletePhysicalSoftDeletedByEntityTypeCode(code);
    }

    private List<DmDataTabLayoutSaveItemVO> dedupeSaveItems(List<DmDataTabLayoutSaveItemVO> layouts) {
        if (layouts == null || layouts.isEmpty()) {
            return List.of();
        }
        Map<String, DmDataTabLayoutSaveItemVO> deduped = new LinkedHashMap<>();
        for (DmDataTabLayoutSaveItemVO item : layouts) {
            String kind = item.getColumnKind() == null ? "" : item.getColumnKind().trim().toUpperCase();
            String perspectiveId = normalizePerspectiveId(item.getPerspectiveId());
            deduped.put(scopeKey(kind, perspectiveId), item);
        }
        return new ArrayList<>(deduped.values());
    }

    private String scopeKey(String columnKind, String perspectiveId) {
        String kind = columnKind == null ? "" : columnKind.trim().toUpperCase();
        String perspective = perspectiveId == null ? "" : perspectiveId.trim();
        return kind + "|" + perspective;
    }

    private void validateSaveItems(List<DmDataTabLayoutSaveItemVO> layouts) {
        Set<String> scopes = new HashSet<>();
        for (DmDataTabLayoutSaveItemVO item : layouts) {
            if (!DmDataTabLayoutKindEnum.isValid(item.getColumnKind())) {
                throw new ServiceException(400, "无效的 columnKind: " + item.getColumnKind());
            }
            String kind = item.getColumnKind().trim().toUpperCase();
            if (DmDataTabLayoutKindEnum.CATEGORY.getCode().equals(kind)
                    && !StringUtils.hasText(item.getPerspectiveId())) {
                throw new ServiceException(400, "CATEGORY 列必须提供 perspectiveId");
            }
            if (!DmDataTabLayoutKindEnum.CATEGORY.getCode().equals(kind)
                    && StringUtils.hasText(item.getPerspectiveId())) {
                throw new ServiceException(400, kind + " 列不应设置 perspectiveId");
            }
            String scope = scopeKey(kind, normalizePerspectiveId(item.getPerspectiveId()));
            if (!scopes.add(scope)) {
                throw new ServiceException(400, "数据 Tab 布局重复：" + kind
                        + (StringUtils.hasText(item.getPerspectiveId()) ? " / " + item.getPerspectiveId() : ""));
            }
        }
    }

    private List<DmDataTabLayoutRespVO> buildDefaultColumns(String entityTypeCode) {
        List<DmDataTabLayoutRespVO> list = new ArrayList<>();
        list.add(defaultSimpleColumn(entityTypeCode, DmDataTabLayoutKindEnum.MODEL.getCode()));
        list.add(defaultSimpleColumn(entityTypeCode, DmDataTabLayoutKindEnum.ENTITY.getCode()));
        return list;
    }

    private DmDataTabLayoutRespVO defaultSimpleColumn(String entityTypeCode, String kind) {
        DmDataTabLayoutRespVO row = new DmDataTabLayoutRespVO();
        row.setEntityTypeCode(entityTypeCode);
        row.setColumnKind(kind);
        row.setEnabled(true);
        return row;
    }

    private DmDataTabLayoutRespVO toRespVO(DmDataTabLayoutDO row) {
        DmDataTabLayoutRespVO vo = new DmDataTabLayoutRespVO();
        vo.setId(row.getId());
        vo.setEntityTypeCode(row.getEntityTypeCode());
        vo.setColumnKind(row.getColumnKind());
        vo.setPerspectiveId(row.getPerspectiveId());
        vo.setPropsId(row.getPropsId());
        vo.setEnabled(row.getEnabled());
        vo.setCategoryColumn(row.getCategoryColumn());
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
                throw new ServiceException(400, "列扩展 JSON 不合法");
            }
        }
        if (meta instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        }
        throw new ServiceException(400, "列扩展必须是 JSON 对象");
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
