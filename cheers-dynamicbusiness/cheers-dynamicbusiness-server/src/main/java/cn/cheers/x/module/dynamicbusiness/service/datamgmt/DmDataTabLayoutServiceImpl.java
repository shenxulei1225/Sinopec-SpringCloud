package cn.cheers.x.module.dynamicbusiness.service.datamgmt;

import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmDataTabLayoutBundleRespVO;
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
    private DmDataTabColumnRelationService dmDataTabColumnRelationService;

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
        // 读路径不 bootstrap 写库；缺 dataLayoutId 直接报错。
        String code = normalizeEntityTypeCode(entityTypeCode);
        EntityTypeDO entityType = entityTypeMapper.selectByCode(code);
        if (entityType == null || entityType.getDataLayoutId() == null) {
            throw new ServiceException(400, "数据类型尚未挂载 dataLayoutId：" + code);
        }
        return listByLayoutId(entityType.getDataLayoutId());
    }

    @Override
    public DmDataTabLayoutBundleRespVO listBundleByLayoutId(Long layoutId) {
        dmWorkbenchLayoutService.requireLayout(layoutId);
        DmDataTabLayoutBundleRespVO bundle = new DmDataTabLayoutBundleRespVO();
        bundle.setLayouts(listByLayoutId(layoutId));
        bundle.setColumnRelations(dmDataTabColumnRelationService.listByLayoutId(layoutId));
        return bundle;
    }

    @Override
    public DmDataTabLayoutBundleRespVO listBundleByEntityTypeCode(String entityTypeCode) {
        String code = normalizeEntityTypeCode(entityTypeCode);
        EntityTypeDO entityType = entityTypeMapper.selectByCode(code);
        if (entityType == null || entityType.getDataLayoutId() == null) {
            throw new ServiceException(400, "数据类型尚未挂载 dataLayoutId：" + code);
        }
        return listBundleByLayoutId(entityType.getDataLayoutId());
    }

    /**
     * 保存本页布局行（全量：请求体有的 upsert，没有的删行）。
     * <p>
     * ## 与栏间关系的关系（死规矩）
     * <ul>
     *   <li>优先按布局行 <strong>id</strong> 更新（含改 tabId）；列身份变了只
     *       {@link DmDataTabColumnRelationService#renameColumnIdentities}，不删边。</li>
     *   <li>仅当本次<strong>确实删掉布局行</strong>时：算出这些行的列身份，调用
     *       {@link DmDataTabColumnRelationService#removeRelationsTouchingIdentities} 清挂在上面的边。</li>
     *   <li>加栏之后：只 <strong>insert</strong> 缺的同类型默认 filter，不覆盖、不删用户边。</li>
     *   <li>禁止再调用「按当前状态猜孤儿边」的全表扫描删除。</li>
     * </ul>
     */
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
        Map<Long, DmDataTabLayoutDO> existingById = new LinkedHashMap<>();
        Map<String, DmDataTabLayoutDO> existingByScope = new LinkedHashMap<>();
        for (DmDataTabLayoutDO row : existing) {
            if (row.getId() != null) {
                existingById.put(row.getId(), row);
            }
            existingByScope.put(scopeKey(row.getColumnKind(), row.getTabId()), row);
        }

        Set<String> savedScopes = new HashSet<>();
        Set<Long> keptIds = new HashSet<>();
        Map<String, String> identityRenames = new LinkedHashMap<>();

        for (DmDataTabLayoutSaveItemVO item : items) {
            String kind = item.getColumnKind().trim().toUpperCase();
            String tabId = normalizeTabId(kind, item.getTabId());
            String scope = scopeKey(kind, tabId);
            savedScopes.add(scope);

            DmDataTabLayoutDO row = null;
            if (item.getId() != null) {
                row = existingById.get(item.getId());
                if (row != null && row.getLayoutId() != null && !row.getLayoutId().equals(layoutId)) {
                    throw new ServiceException(400, "布局行 id 不属于本页：" + item.getId());
                }
            }
            if (row == null) {
                row = existingByScope.get(scope);
            }

            if (row != null) {
                String oldIdentity = ColumnRelationLayoutEndpoints.columnIdentityOf(row);
                row.setColumnKind(kind);
                row.setTabId(tabId);
                row.setPropsId(item.getPropsId());
                row.setEnabled(item.getEnabled() == null || item.getEnabled());
                row.setColumnMeta(toMetaMap(item.getColumnMeta()));
                if (entityTypeCode != null) {
                    row.setEntityTypeCode(entityTypeCode);
                }
                dmDataTabLayoutMapper.updateById(row);
                keptIds.add(row.getId());
                String newIdentity = ColumnRelationLayoutEndpoints.columnIdentityOf(row);
                if (StringUtils.hasText(oldIdentity)
                        && StringUtils.hasText(newIdentity)
                        && !oldIdentity.equals(newIdentity)) {
                    identityRenames.put(oldIdentity.trim(), newIdentity.trim());
                }
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
            if (insert.getId() != null) {
                keptIds.add(insert.getId());
            }
        }

        Set<String> removedIdentities = new HashSet<>();
        for (DmDataTabLayoutDO row : existing) {
            if (row.getId() != null && keptIds.contains(row.getId())) {
                continue;
            }
            String scope = scopeKey(row.getColumnKind(), row.getTabId());
            if (!savedScopes.contains(scope)) {
                String identity = ColumnRelationLayoutEndpoints.columnIdentityOf(row);
                if (StringUtils.hasText(identity)) {
                    removedIdentities.add(identity.trim());
                }
                dmDataTabLayoutMapper.deleteById(row.getId());
            }
        }

        dmDataTabLayoutMapper.deletePhysicalSoftDeletedByLayoutId(layoutId);
        // 先改名边端点，再按「真删栏」清边，避免改 tabId 被误当成删栏
        dmDataTabColumnRelationService.renameColumnIdentities(layoutId, identityRenames);
        dmDataTabColumnRelationService.removeRelationsTouchingIdentities(layoutId, removedIdentities);

        String registryCode = resolveRegistryCodeAfterSave(layoutId, entityTypeCode);
        String storageCode = resolveStorageBaseCode(registryCode);
        dmDataTabColumnRelationBootstrapService.applyInitialDefaultRelations(
                layoutId, registryCode, storageCode);
    }

    /** 目录注册编码：请求优先，否则从本页任一栏行上的 entity_type_code 取。 */
    private String resolveRegistryCodeAfterSave(Long layoutId, String requestEntityTypeCode) {
        if (StringUtils.hasText(requestEntityTypeCode)) {
            return requestEntityTypeCode.trim();
        }
        List<DmDataTabLayoutDO> rows = dmDataTabLayoutMapper.selectListByLayoutId(layoutId);
        for (DmDataTabLayoutDO row : rows) {
            if (StringUtils.hasText(row.getEntityTypeCode())) {
                return row.getEntityTypeCode().trim();
            }
        }
        return null;
    }

    /** 底座类型编码（存数）；无 base 则与注册码相同。 */
    private String resolveStorageBaseCode(String registryEntityTypeCode) {
        if (!StringUtils.hasText(registryEntityTypeCode)) {
            return null;
        }
        EntityTypeDO type = entityTypeMapper.selectByCode(registryEntityTypeCode.trim());
        if (type != null && StringUtils.hasText(type.getBaseEntityTypeCode())) {
            return type.getBaseEntityTypeCode().trim();
        }
        return registryEntityTypeCode.trim();
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
            String tabId = normalizeTabId(kind, item.getTabId());
            deduped.put(scopeKey(kind, tabId), item);
        }
        return new ArrayList<>(deduped.values());
    }

    private String scopeKey(String columnKind, String tabId) {
        String kind = columnKind == null ? "" : columnKind.trim().toUpperCase();
        String tab = tabId == null ? "" : tabId.trim();
        return kind + "|" + tab;
    }

    /**
     * 落库前归一 Tab 编号。
     * 分类 / 型号 / 实体：必须非空；禁止字面 default。
     */
    private String normalizeTabId(String columnKind, String tabId) {
        String kind = columnKind == null ? "" : columnKind.trim().toUpperCase();
        boolean needsTab = DmDataTabLayoutKindEnum.CATEGORY.getCode().equals(kind)
                || DmDataTabLayoutKindEnum.MODEL.getCode().equals(kind)
                || DmDataTabLayoutKindEnum.ENTITY.getCode().equals(kind);
        if (!StringUtils.hasText(tabId)) {
            if (needsTab) {
                throw new ServiceException(400, kind + " 栏必须提供 tabId，禁止为空");
            }
            return null;
        }
        String trimmed = tabId.trim();
        if (needsTab && "default".equalsIgnoreCase(trimmed)) {
            throw new ServiceException(400, kind + " 栏 tabId 禁止字面 default");
        }
        if (needsTab && trimmed.toLowerCase().endsWith("-default")) {
            throw new ServiceException(400, kind + " 栏 tabId 禁止 -default 后缀");
        }
        return trimmed;
    }

    private void validateSaveItems(List<DmDataTabLayoutSaveItemVO> layouts) {
        Set<String> scopes = new HashSet<>();
        for (DmDataTabLayoutSaveItemVO item : layouts) {
            if (!DmDataTabLayoutKindEnum.isValid(item.getColumnKind())) {
                throw new ServiceException(400, "无效的 columnKind: " + item.getColumnKind());
            }
            String kind = item.getColumnKind().trim().toUpperCase();
            if (DmDataTabLayoutKindEnum.CATEGORY.getCode().equals(kind)
                    || DmDataTabLayoutKindEnum.MODEL.getCode().equals(kind)
                    || DmDataTabLayoutKindEnum.ENTITY.getCode().equals(kind)) {
                if (!StringUtils.hasText(item.getTabId())) {
                    throw new ServiceException(400, kind + " 栏必须提供 tabId");
                }
                String tab = item.getTabId().trim();
                if ("default".equalsIgnoreCase(tab)) {
                    throw new ServiceException(400, kind + " 栏 tabId 禁止字面 default");
                }
                if (tab.toLowerCase().endsWith("-default")) {
                    throw new ServiceException(400, kind + " 栏 tabId 禁止 -default 后缀");
                }
            }
            boolean allowsTabId = DmDataTabLayoutKindEnum.CATEGORY.getCode().equals(kind)
                    || DmDataTabLayoutKindEnum.MODEL.getCode().equals(kind)
                    || DmDataTabLayoutKindEnum.ENTITY.getCode().equals(kind);
            if (!allowsTabId && StringUtils.hasText(item.getTabId())) {
                throw new ServiceException(400, kind + " 列不应设置标签页编号");
            }
            // 启用中的分类/型号/实体必须带类型码，禁止空壳落库再让用户学怎么补
            if (item.getEnabled() == null || Boolean.TRUE.equals(item.getEnabled())) {
                requireColumnTypeCodeOnSave(kind, item.getColumnMeta());
                if (DmDataTabLayoutKindEnum.CATEGORY.getCode().equals(kind)) {
                    requireCategoryColumnKeyOnSave(item.getColumnMeta());
                }
            }
            String scope = scopeKey(kind, normalizeTabId(kind, item.getTabId()));
            if (!scopes.add(scope)) {
                throw new ServiceException(400, "数据 Tab 布局重复：" + kind
                        + (StringUtils.hasText(item.getTabId()) ? " / " + item.getTabId() : ""));
            }
        }
    }

    /**
     * 分类栏分组键 columnKey：启用栏必须非空，禁止字面 default 与 *-default 后缀。
     */
    private void requireCategoryColumnKeyOnSave(Object columnMeta) {
        Map<String, Object> meta = toMetaMapOrEmpty(columnMeta);
        if (!metaHasText(meta, "columnKey")) {
            throw new ServiceException(400, "分类栏缺少分组键 columnKey");
        }
        rejectInvalidCategoryScopePart("columnKey", String.valueOf(meta.get("columnKey")).trim());
    }

    private static void rejectInvalidCategoryScopePart(String field, String value) {
        if (!StringUtils.hasText(value)) {
            throw new ServiceException(400, "分类栏 " + field + " 禁止为空");
        }
        if ("default".equalsIgnoreCase(value)) {
            throw new ServiceException(400, "分类栏 " + field + " 禁止字面 default");
        }
        if (value.toLowerCase().endsWith("-default")) {
            throw new ServiceException(400, "分类栏 " + field + " 禁止 -default 后缀，须用种类码或稳定编号");
        }
    }

    /**
     * 保存闸门：启用栏的 columnMeta 必须含对应类型码。
     * 配置隐藏（enabled=false）允许暂无类型码；重新启用时须先配好。
     */
    private void requireColumnTypeCodeOnSave(String kind, Object columnMeta) {
        Map<String, Object> meta = toMetaMapOrEmpty(columnMeta);
        if (DmDataTabLayoutKindEnum.CATEGORY.getCode().equals(kind)) {
            if (!metaHasText(meta, "categoryTypeCode")) {
                throw new ServiceException(400, "分类栏缺少种类码 categoryTypeCode，请先选择分类种类");
            }
        } else if (DmDataTabLayoutKindEnum.MODEL.getCode().equals(kind)) {
            if (!metaHasText(meta, "modelEntityTypeCode")) {
                throw new ServiceException(400, "型号栏缺少类型码 modelEntityTypeCode，请先选择型号所属类型");
            }
        } else if (DmDataTabLayoutKindEnum.ENTITY.getCode().equals(kind)) {
            if (!metaHasText(meta, "entityEntityTypeCode")) {
                throw new ServiceException(400, "实体栏缺少类型码 entityEntityTypeCode，请先选择实体所属类型");
            }
        }
    }

    private Map<String, Object> toMetaMapOrEmpty(Object meta) {
        if (meta == null) {
            return Map.of();
        }
        Map<String, Object> mapped = toMetaMap(meta);
        return mapped != null ? mapped : Map.of();
    }

    private static boolean metaHasText(Map<String, Object> meta, String key) {
        if (meta == null || !StringUtils.hasText(key)) {
            return false;
        }
        Object value = meta.get(key);
        return value != null && StringUtils.hasText(String.valueOf(value).trim());
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
}
