package cn.cheers.x.module.dynamicbusiness.service.datamgmt;

import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmDataTabLayoutRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmDataTabLayoutSaveItemVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmDataTabLayoutSaveReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datamgmt.DmDataTabLayoutDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datamgmt.DmWorkbenchLayoutDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.datamgmt.DmDataTabLayoutMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeMapper;
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
    private DmWorkbenchLayoutService dmWorkbenchLayoutService;

    @Resource
    private EntityTypeCategoryBootstrapService entityTypeCategoryBootstrapService;

    @Resource
    private EntityTypeMapper entityTypeMapper;

    @Resource
    private DmDataTabColumnRelationBootstrapService dmDataTabColumnRelationBootstrapService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<DmDataTabLayoutRespVO> listByLayoutId(Long layoutId) {
        dmWorkbenchLayoutService.requireLayout(layoutId);
        return dmDataTabLayoutMapper.selectListByLayoutId(layoutId).stream()
                .map(this::toRespVO)
                .toList();
    }

    @Override
    public List<DmDataTabLayoutRespVO> listByEntityTypeCode(String entityTypeCode) {
        String code = normalizeEntityTypeCode(entityTypeCode);
        entityTypeCategoryBootstrapService.ensureForEntityTypeCode(code);
        EntityTypeDO entityType = entityTypeMapper.selectByCode(code);
        if (entityType == null || entityType.getDataLayoutId() == null) {
            throw new ServiceException(400, "数据类型尚未挂载 dataLayoutId：" + code);
        }
        return listByLayoutId(entityType.getDataLayoutId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveLayouts(DmDataTabLayoutSaveReqVO reqVO) {
        Long layoutId = resolveSaveLayoutId(reqVO);
        DmWorkbenchLayoutDO header = dmWorkbenchLayoutService.requireLayout(layoutId);
        if (Boolean.TRUE.equals(header.getIsTemplate())) {
            throw new ServiceException(400, "禁止直接改写模版栏行；请改实例");
        }

        List<DmDataTabLayoutSaveItemVO> items = dedupeSaveItems(reqVO.getLayouts());
        validateSaveItems(items);

        String entityTypeCode = StringUtils.hasText(reqVO.getEntityTypeCode())
                ? reqVO.getEntityTypeCode().trim()
                : null;

        List<DmDataTabLayoutDO> existing = dmDataTabLayoutMapper.selectListByLayoutId(layoutId);
        Map<String, DmDataTabLayoutDO> existingByScope = new LinkedHashMap<>();
        for (DmDataTabLayoutDO row : existing) {
            existingByScope.put(scopeKey(row.getColumnKind(), row.getTabId()), row);
        }

        Set<String> savedScopes = new HashSet<>();
        for (DmDataTabLayoutSaveItemVO item : items) {
            String kind = item.getColumnKind().trim().toUpperCase();
            String tabId = normalizeTabId(item.getTabId());
            String scope = scopeKey(kind, tabId);
            savedScopes.add(scope);

            DmDataTabLayoutDO row = existingByScope.get(scope);
            if (row != null) {
                row.setPropsId(item.getPropsId());
                row.setEnabled(item.getEnabled() == null || item.getEnabled());
                row.setColumnMeta(toMetaMap(item.getColumnMeta()));
                if (entityTypeCode != null) {
                    row.setEntityTypeCode(entityTypeCode);
                }
                dmDataTabLayoutMapper.updateById(row);
                continue;
            }

            DmDataTabLayoutDO insert = new DmDataTabLayoutDO();
            insert.setLayoutId(layoutId);
            insert.setEntityTypeCode(entityTypeCode);
            insert.setColumnKind(kind);
            insert.setTabId(tabId);
            insert.setPropsId(item.getPropsId());
            insert.setEnabled(item.getEnabled() == null || item.getEnabled());
            insert.setColumnMeta(toMetaMap(item.getColumnMeta()));
            dmDataTabLayoutMapper.insert(insert);
        }

        for (DmDataTabLayoutDO row : existing) {
            String scope = scopeKey(row.getColumnKind(), row.getTabId());
            if (!savedScopes.contains(scope)) {
                dmDataTabLayoutMapper.deleteById(row.getId());
            }
        }

        dmDataTabLayoutMapper.deletePhysicalSoftDeletedByLayoutId(layoutId);
        dmDataTabColumnRelationBootstrapService.pruneOrphanColumnRelations(layoutId);
    }

    private Long resolveSaveLayoutId(DmDataTabLayoutSaveReqVO reqVO) {
        if (reqVO.getLayoutId() != null) {
            return reqVO.getLayoutId();
        }
        if (!StringUtils.hasText(reqVO.getEntityTypeCode())) {
            throw new ServiceException(400, "layoutId 与 entityTypeCode 不能同时为空");
        }
        String code = reqVO.getEntityTypeCode().trim();
        entityTypeCategoryBootstrapService.ensureForEntityTypeCode(code);
        return dmWorkbenchLayoutService.resolveLayoutIdForEntityType(code);
    }

    private List<DmDataTabLayoutSaveItemVO> dedupeSaveItems(List<DmDataTabLayoutSaveItemVO> layouts) {
        if (layouts == null || layouts.isEmpty()) {
            return List.of();
        }
        Map<String, DmDataTabLayoutSaveItemVO> deduped = new LinkedHashMap<>();
        for (DmDataTabLayoutSaveItemVO item : layouts) {
            String kind = item.getColumnKind() == null ? "" : item.getColumnKind().trim().toUpperCase();
            String tabId = normalizeTabId(item.getTabId());
            deduped.put(scopeKey(kind, tabId), item);
        }
        return new ArrayList<>(deduped.values());
    }

    private String scopeKey(String columnKind, String tabId) {
        String kind = columnKind == null ? "" : columnKind.trim().toUpperCase();
        String tab = tabId == null ? "" : tabId.trim();
        return kind + "|" + tab;
    }

    private void validateSaveItems(List<DmDataTabLayoutSaveItemVO> layouts) {
        Set<String> scopes = new HashSet<>();
        for (DmDataTabLayoutSaveItemVO item : layouts) {
            if (!DmDataTabLayoutKindEnum.isValid(item.getColumnKind())) {
                throw new ServiceException(400, "无效的 columnKind: " + item.getColumnKind());
            }
            String kind = item.getColumnKind().trim().toUpperCase();
            if (DmDataTabLayoutKindEnum.CATEGORY.getCode().equals(kind)
                    && !StringUtils.hasText(item.getTabId())) {
                throw new ServiceException(400, "分类列必须提供标签页编号");
            }
            boolean allowsTabId = DmDataTabLayoutKindEnum.CATEGORY.getCode().equals(kind)
                    || DmDataTabLayoutKindEnum.MODEL.getCode().equals(kind)
                    || DmDataTabLayoutKindEnum.ENTITY.getCode().equals(kind);
            if (!allowsTabId && StringUtils.hasText(item.getTabId())) {
                throw new ServiceException(400, kind + " 列不应设置标签页编号");
            }
            String scope = scopeKey(kind, normalizeTabId(item.getTabId()));
            if (!scopes.add(scope)) {
                throw new ServiceException(400, "数据 Tab 布局重复：" + kind
                        + (StringUtils.hasText(item.getTabId()) ? " / " + item.getTabId() : ""));
            }
        }
    }

    private DmDataTabLayoutRespVO toRespVO(DmDataTabLayoutDO row) {
        DmDataTabLayoutRespVO vo = new DmDataTabLayoutRespVO();
        vo.setId(row.getId());
        vo.setLayoutId(row.getLayoutId());
        vo.setEntityTypeCode(row.getEntityTypeCode());
        vo.setColumnKind(row.getColumnKind());
        vo.setTabId(row.getTabId());
        vo.setPropsId(row.getPropsId());
        vo.setEnabled(row.getEnabled());
        vo.setColumnMeta(row.getColumnMeta());
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

    private String normalizeTabId(String tabId) {
        if (!StringUtils.hasText(tabId)) {
            return null;
        }
        return tabId.trim();
    }
}
