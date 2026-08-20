package cn.cheers.x.module.dynamicbusiness.service.datamgmt;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmDataTabColumnRelationRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmDataTabColumnRelationSaveItemVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmDataTabColumnRelationSaveReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datamgmt.DmDataTabColumnRelationDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datamgmt.DmDataTabLayoutDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.datamgmt.DmDataTabColumnRelationMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.datamgmt.DmDataTabLayoutMapper;
import cn.cheers.x.module.dynamicbusiness.enums.datamgmt.DmDataTabLayoutKindEnum;
import cn.cheers.x.module.dynamicbusiness.service.entitytype.EntityTypeCategoryBootstrapService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@Validated
public class DmDataTabColumnRelationServiceImpl implements DmDataTabColumnRelationService {

    private static final Set<String> ALLOWED_KINDS = Set.of(
            "CATEGORY_CATEGORY",
            "CATEGORY_MODEL",
            "CATEGORY_ENTITY",
            "MODEL_MODEL",
            "MODEL_ENTITY",
            "ENTITY_ENTITY"
    );

    private static final Set<String> ALLOWED_INTERACTIONS = Set.of(
            "browseFilter",
            "dragAssociate",
            "unbindChecked",
            "checkboxSet",
            "refFieldPick",
            "associationFilterTriad",
            "cascadeClip",
            "ownershipWrite"
    );

    @Resource
    private DmDataTabColumnRelationMapper dmDataTabColumnRelationMapper;

    @Resource
    private DmDataTabLayoutMapper dmDataTabLayoutMapper;

    @Resource
    private DmWorkbenchLayoutService dmWorkbenchLayoutService;

    @Resource
    private EntityTypeCategoryBootstrapService entityTypeCategoryBootstrapService;

    @Override
    public List<DmDataTabColumnRelationRespVO> listByLayoutId(Long layoutId) {
        dmWorkbenchLayoutService.requireLayout(layoutId);
        String code = resolveEntityTypeCodeForLayout(layoutId);
        ensureDefaultLedgerBrowseRelations(layoutId, code);
        return dmDataTabColumnRelationMapper.selectListByLayoutId(layoutId).stream()
                .map(this::toRespVO)
                .toList();
    }

    private String resolveEntityTypeCodeForLayout(Long layoutId) {
        List<DmDataTabLayoutDO> layouts = dmDataTabLayoutMapper.selectListByLayoutId(layoutId);
        for (DmDataTabLayoutDO row : layouts) {
            if (StringUtils.hasText(row.getEntityTypeCode())) {
                return row.getEntityTypeCode().trim();
            }
        }
        return null;
    }

    @Override
    public List<DmDataTabColumnRelationRespVO> listByEntityTypeCode(String entityTypeCode) {
        String code = normalizeEntityTypeCode(entityTypeCode);
        entityTypeCategoryBootstrapService.ensureForEntityTypeCode(code);
        Long layoutId = dmWorkbenchLayoutService.resolveLayoutIdForEntityType(code);
        ensureDefaultLedgerBrowseRelations(layoutId, code);
        return listByLayoutId(layoutId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void ensureDefaultLedgerBrowseRelations(Long layoutId, String entityTypeCode) {
        if (layoutId == null) {
            return;
        }
        dmWorkbenchLayoutService.requireLayout(layoutId);
        List<DmDataTabLayoutDO> layouts = dmDataTabLayoutMapper.selectListByLayoutId(layoutId);
        if (layouts.isEmpty()) {
            return;
        }

        String pageCode = StringUtils.hasText(entityTypeCode) ? entityTypeCode.trim() : null;
        List<ColumnEndpoint> categories = new ArrayList<>();
        List<ColumnEndpoint> models = new ArrayList<>();
        List<ColumnEndpoint> entities = new ArrayList<>();
        for (DmDataTabLayoutDO row : layouts) {
            if (Boolean.FALSE.equals(row.getEnabled())) {
                continue;
            }
            ColumnEndpoint endpoint = toEndpoint(row, pageCode);
            if (endpoint == null) {
                continue;
            }
            switch (endpoint.kind()) {
                case CATEGORY -> categories.add(endpoint);
                case MODEL -> models.add(endpoint);
                case ENTITY -> entities.add(endpoint);
                default -> {
                }
            }
        }

        Set<String> existingPairs = new HashSet<>();
        for (DmDataTabColumnRelationDO row :
                dmDataTabColumnRelationMapper.selectListByLayoutId(layoutId)) {
            existingPairs.add(pairKey(row.getFromColumnIdentity(), row.getToColumnIdentity()));
        }

        // 只补通用台账缺的「分类→型号」「型号→实体」；已有用户边（如分类→实体）不删不改
        List<DmDataTabColumnRelationDO> toInsert = new ArrayList<>();
        for (ColumnEndpoint category : categories) {
            for (ColumnEndpoint model : models) {
                if (existingPairs.contains(pairKey(category.identity(), model.identity()))) {
                    continue;
                }
                toInsert.add(newBrowseRelation(
                        layoutId, pageCode, category, model, "CATEGORY_MODEL"));
            }
        }
        for (ColumnEndpoint model : models) {
            for (ColumnEndpoint entity : entities) {
                if (existingPairs.contains(pairKey(model.identity(), entity.identity()))) {
                    continue;
                }
                toInsert.add(newBrowseRelation(
                        layoutId, pageCode, model, entity, "MODEL_ENTITY"));
            }
        }
        for (DmDataTabColumnRelationDO row : toInsert) {
            dmDataTabColumnRelationMapper.insert(row);
        }
    }

    private static String pairKey(String from, String to) {
        return (from == null ? "" : from.trim()) + "=>" + (to == null ? "" : to.trim());
    }

    private DmDataTabColumnRelationDO newBrowseRelation(
            Long layoutId,
            String entityTypeCode,
            ColumnEndpoint from,
            ColumnEndpoint to,
            String relationKind) {
        DmDataTabColumnRelationDO row = new DmDataTabColumnRelationDO();
        row.setLayoutId(layoutId);
        row.setEntityTypeCode(entityTypeCode);
        row.setEdgeId("edge-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12));
        row.setFromColumnIdentity(from.identity());
        row.setToColumnIdentity(to.identity());
        row.setRelationKind(relationKind);
        row.setFromTypeCode(from.typeCode());
        row.setToTypeCode(to.typeCode());
        Map<String, Object> meta = new LinkedHashMap<>();
        meta.put("enabledInteractions", List.of("browseFilter"));
        row.setRelationMeta(meta);
        return row;
    }

    private ColumnEndpoint toEndpoint(DmDataTabLayoutDO row, String pageCode) {
        String kindCode = row.getColumnKind() == null ? "" : row.getColumnKind().trim().toUpperCase();
        DmDataTabLayoutKindEnum kind = DmDataTabLayoutKindEnum.getByCode(kindCode);
        if (kind == null) {
            return null;
        }
        Map<String, Object> meta = row.getColumnMeta() != null ? row.getColumnMeta() : Map.of();
        String tab = StringUtils.hasText(row.getTabId()) ? row.getTabId().trim() : "default";
        return switch (kind) {
            case CATEGORY -> {
                String typeCode = metaString(meta, "categoryTypeCode");
                if (!StringUtils.hasText(typeCode)) {
                    yield null;
                }
                String columnKey = metaString(meta, "columnKey");
                if (!StringUtils.hasText(columnKey)) {
                    columnKey = "default";
                }
                yield new ColumnEndpoint(
                        DmDataTabLayoutKindEnum.CATEGORY,
                        "CATEGORY:" + columnKey + ":" + tab,
                        typeCode.trim());
            }
            case MODEL -> {
                String typeCode = metaString(meta, "modelEntityTypeCode");
                if (!StringUtils.hasText(typeCode)) {
                    typeCode = pageCode;
                }
                if (!StringUtils.hasText(typeCode)) {
                    yield null;
                }
                yield new ColumnEndpoint(
                        DmDataTabLayoutKindEnum.MODEL,
                        "MODEL:" + tab,
                        typeCode.trim());
            }
            case ENTITY -> {
                String typeCode = metaString(meta, "entityEntityTypeCode");
                if (!StringUtils.hasText(typeCode)) {
                    typeCode = pageCode;
                }
                if (!StringUtils.hasText(typeCode)) {
                    yield null;
                }
                yield new ColumnEndpoint(
                        DmDataTabLayoutKindEnum.ENTITY,
                        "ENTITY:" + tab,
                        typeCode.trim());
            }
            default -> null;
        };
    }

    private static String metaString(Map<String, Object> meta, String key) {
        Object value = meta.get(key);
        if (value == null) {
            return null;
        }
        String text = String.valueOf(value).trim();
        return text.isEmpty() || "null".equals(text) ? null : text;
    }

    private record ColumnEndpoint(DmDataTabLayoutKindEnum kind, String identity, String typeCode) {
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveRelations(DmDataTabColumnRelationSaveReqVO reqVO) {
        Long layoutId = resolveSaveLayoutId(reqVO);
        String code = StringUtils.hasText(reqVO.getEntityTypeCode())
                ? reqVO.getEntityTypeCode().trim()
                : null;
        List<DmDataTabColumnRelationSaveItemVO> items =
                reqVO.getRelations() == null ? List.of() : reqVO.getRelations();

        List<DmDataTabColumnRelationDO> existing =
                dmDataTabColumnRelationMapper.selectListByLayoutId(layoutId);
        Map<String, DmDataTabColumnRelationDO> existingByEdge = new LinkedHashMap<>();
        for (DmDataTabColumnRelationDO row : existing) {
            existingByEdge.put(row.getEdgeId(), row);
        }

        Set<String> savedEdgeIds = new HashSet<>();
        Set<String> pairKeys = new HashSet<>();
        for (DmDataTabColumnRelationSaveItemVO item : items) {
            validateItem(item);
            String from = item.getFromColumnIdentity().trim();
            String to = item.getToColumnIdentity().trim();
            if (from.equals(to)) {
                throw new ServiceException(400, "栏间关系两端列身份不能相同");
            }
            String pairKey = from + "=>" + to;
            if (!pairKeys.add(pairKey)) {
                throw new ServiceException(400, "重复的栏间关系：" + pairKey);
            }

            String kind = resolveRelationKind(item);
            String edgeId = StringUtils.hasText(item.getEdgeId())
                    ? item.getEdgeId().trim()
                    : "edge-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
            if (!savedEdgeIds.add(edgeId)) {
                throw new ServiceException(400, "重复的 edgeId: " + edgeId);
            }

            List<String> interactions = normalizeInteractions(item.getEnabledInteractions());
            Map<String, Object> meta = buildMeta(interactions, item.getLinkKeys(), item.getPresentation());

            DmDataTabColumnRelationDO row = existingByEdge.get(edgeId);
            if (row != null) {
                row.setFromColumnIdentity(from);
                row.setToColumnIdentity(to);
                row.setRelationKind(kind);
                row.setFromTypeCode(item.getFromTypeCode().trim());
                row.setToTypeCode(item.getToTypeCode().trim());
                row.setRelationMeta(meta);
                if (code != null) {
                    row.setEntityTypeCode(code);
                }
                dmDataTabColumnRelationMapper.updateById(row);
                continue;
            }

            DmDataTabColumnRelationDO insert = new DmDataTabColumnRelationDO();
            insert.setLayoutId(layoutId);
            insert.setEntityTypeCode(code);
            insert.setEdgeId(edgeId);
            insert.setFromColumnIdentity(from);
            insert.setToColumnIdentity(to);
            insert.setRelationKind(kind);
            insert.setFromTypeCode(item.getFromTypeCode().trim());
            insert.setToTypeCode(item.getToTypeCode().trim());
            insert.setRelationMeta(meta);
            dmDataTabColumnRelationMapper.insert(insert);
        }

        for (DmDataTabColumnRelationDO row : existing) {
            if (!savedEdgeIds.contains(row.getEdgeId())) {
                dmDataTabColumnRelationMapper.deleteById(row.getId());
            }
        }
        dmDataTabColumnRelationMapper.deletePhysicalSoftDeletedByLayoutId(layoutId);
    }

    private Long resolveSaveLayoutId(DmDataTabColumnRelationSaveReqVO reqVO) {
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

    private void validateItem(DmDataTabColumnRelationSaveItemVO item) {
        if (!StringUtils.hasText(item.getFromColumnIdentity())
                || !StringUtils.hasText(item.getToColumnIdentity())) {
            throw new ServiceException(400, "栏间关系必须指定从列与到列");
        }
        if (!StringUtils.hasText(item.getFromTypeCode()) || !StringUtils.hasText(item.getToTypeCode())) {
            throw new ServiceException(400, "栏间关系必须指定两端类型编码");
        }
        if (StringUtils.hasText(item.getRelationKind())
                && !ALLOWED_KINDS.contains(item.getRelationKind().trim().toUpperCase())) {
            throw new ServiceException(400, "无效的关联种类: " + item.getRelationKind());
        }
    }

    private String resolveRelationKind(DmDataTabColumnRelationSaveItemVO item) {
        if (StringUtils.hasText(item.getRelationKind())) {
            return item.getRelationKind().trim().toUpperCase();
        }
        String inferred = inferRelationKind(item.getFromColumnIdentity(), item.getToColumnIdentity());
        if (inferred == null) {
            throw new ServiceException(400, "无法从列身份推断关联种类，请显式传入 relationKind");
        }
        return inferred;
    }

    static String inferRelationKind(String fromIdentity, String toIdentity) {
        String from = columnKindPrefix(fromIdentity);
        String to = columnKindPrefix(toIdentity);
        if (from == null || to == null) {
            return null;
        }
        String key = from + "_" + to;
        return switch (key) {
            case "CATEGORY_CATEGORY" -> "CATEGORY_CATEGORY";
            case "CATEGORY_MODEL" -> "CATEGORY_MODEL";
            case "CATEGORY_ENTITY" -> "CATEGORY_ENTITY";
            case "MODEL_MODEL" -> "MODEL_MODEL";
            case "MODEL_ENTITY" -> "MODEL_ENTITY";
            case "ENTITY_ENTITY" -> "ENTITY_ENTITY";
            default -> null;
        };
    }

    private static String columnKindPrefix(String identity) {
        if (!StringUtils.hasText(identity)) {
            return null;
        }
        String text = identity.trim();
        int idx = text.indexOf(':');
        if (idx <= 0) {
            return null;
        }
        return text.substring(0, idx).trim().toUpperCase();
    }

    private List<String> normalizeInteractions(List<String> raw) {
        if (raw == null || raw.isEmpty()) {
            throw new ServiceException(400, "至少启用一种交互方式");
        }
        LinkedHashSet<String> out = new LinkedHashSet<>();
        for (String item : raw) {
            if (!StringUtils.hasText(item)) {
                continue;
            }
            String code = item.trim();
            if (!ALLOWED_INTERACTIONS.contains(code)) {
                throw new ServiceException(400, "无效的交互方式: " + code);
            }
            out.add(code);
        }
        if (out.isEmpty()) {
            throw new ServiceException(400, "至少启用一种交互方式");
        }
        return new ArrayList<>(out);
    }

    private Map<String, Object> buildMeta(
            List<String> interactions, List<String> linkKeys, Map<String, Object> presentation) {
        Map<String, Object> meta = new LinkedHashMap<>();
        meta.put("enabledInteractions", interactions);
        if (linkKeys != null && !linkKeys.isEmpty()) {
            List<String> keys = new ArrayList<>();
            for (String key : linkKeys) {
                if (StringUtils.hasText(key)) {
                    keys.add(key.trim());
                }
            }
            if (!keys.isEmpty()) {
                meta.put("linkKeys", keys);
            }
        }
        if (presentation != null && !presentation.isEmpty()) {
            meta.put("presentation", presentation);
        }
        return meta;
    }

    @SuppressWarnings("unchecked")
    private DmDataTabColumnRelationRespVO toRespVO(DmDataTabColumnRelationDO row) {
        DmDataTabColumnRelationRespVO vo = new DmDataTabColumnRelationRespVO();
        vo.setId(row.getId());
        vo.setLayoutId(row.getLayoutId());
        vo.setEntityTypeCode(row.getEntityTypeCode());
        vo.setEdgeId(row.getEdgeId());
        vo.setFromColumnIdentity(row.getFromColumnIdentity());
        vo.setToColumnIdentity(row.getToColumnIdentity());
        vo.setRelationKind(row.getRelationKind());
        vo.setFromTypeCode(row.getFromTypeCode());
        vo.setToTypeCode(row.getToTypeCode());
        Map<String, Object> meta = row.getRelationMeta();
        if (meta != null) {
            Object interactions = meta.get("enabledInteractions");
            if (interactions instanceof List<?> list) {
                List<String> codes = new ArrayList<>();
                for (Object item : list) {
                    if (item != null && StringUtils.hasText(String.valueOf(item))) {
                        codes.add(String.valueOf(item).trim());
                    }
                }
                vo.setEnabledInteractions(codes);
            }
            Object linkKeys = meta.get("linkKeys");
            if (linkKeys instanceof List<?> list) {
                List<String> keys = new ArrayList<>();
                for (Object item : list) {
                    if (item != null && StringUtils.hasText(String.valueOf(item))) {
                        keys.add(String.valueOf(item).trim());
                    }
                }
                vo.setLinkKeys(keys);
            }
            Object presentation = meta.get("presentation");
            if (presentation instanceof Map<?, ?> map) {
                vo.setPresentation((Map<String, Object>) map);
            }
        }
        if (vo.getEnabledInteractions() == null) {
            vo.setEnabledInteractions(List.of());
        }
        return vo;
    }

    private String normalizeEntityTypeCode(String entityTypeCode) {
        if (!StringUtils.hasText(entityTypeCode)) {
            throw new ServiceException(400, "entityTypeCode 不能为空");
        }
        return entityTypeCode.trim();
    }
}
