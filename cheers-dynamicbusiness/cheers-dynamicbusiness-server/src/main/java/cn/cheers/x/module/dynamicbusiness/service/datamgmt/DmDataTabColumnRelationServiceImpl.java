package cn.cheers.x.module.dynamicbusiness.service.datamgmt;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmDataTabColumnRelationRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmDataTabColumnRelationSaveItemVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmDataTabColumnRelationSaveReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datamgmt.DmDataTabColumnRelationDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.datamgmt.DmDataTabColumnRelationMapper;
import cn.cheers.x.module.dynamicbusiness.service.entitytype.EntityTypeCategoryBootstrapService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.HashMap;
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

    private static final Set<String> WRITE_INTERACTIONS = Set.of(
            "dragAssociate",
            "unbindChecked",
            "checkboxSet",
            "refFieldPick",
            "ownershipWrite"
    );

    private static final Set<String> ALLOWED_EDGE_ROLES = Set.of("filter", "write");

    @Resource
    private DmDataTabColumnRelationMapper dmDataTabColumnRelationMapper;

    @Resource
    private DmWorkbenchLayoutService dmWorkbenchLayoutService;

    @Resource
    private EntityTypeCategoryBootstrapService entityTypeCategoryBootstrapService;

    @Override
    public List<DmDataTabColumnRelationRespVO> listByLayoutId(Long layoutId) {
        dmWorkbenchLayoutService.requireLayout(layoutId);
        return dedupeByPairKey(dmDataTabColumnRelationMapper.selectListByLayoutId(layoutId)).stream()
                .map(this::toRespVO)
                .toList();
    }

    @Override
    public List<DmDataTabColumnRelationRespVO> listByEntityTypeCode(String entityTypeCode) {
        String code = normalizeEntityTypeCode(entityTypeCode);
        entityTypeCategoryBootstrapService.ensureForEntityTypeCode(code);
        Long layoutId = dmWorkbenchLayoutService.resolveLayoutIdForEntityType(code);
        return listByLayoutId(layoutId);
    }

    private static String edgeRoleFromMeta(Map<String, Object> meta) {
        if (meta == null) {
            return "filter";
        }
        Object role = meta.get("edgeRole");
        if (role != null) {
            String text = String.valueOf(role).trim();
            if ("write".equals(text) || "filter".equals(text)) {
                return text;
            }
        }
        Object interactions = meta.get("enabledInteractions");
        if (interactions instanceof List<?> list) {
            for (Object item : list) {
                if (item == null) {
                    continue;
                }
                String code = String.valueOf(item).trim();
                if (WRITE_INTERACTIONS.contains(code)) {
                    return "write";
                }
            }
        }
        return "filter";
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
            String edgeRole = resolveEdgeRole(item);
            String dedupeKey = ColumnRelationLayoutEndpoints.pairKey(from, to, edgeRole);
            if (!pairKeys.add(dedupeKey)) {
                throw new ServiceException(400, "重复的栏间关系（同两端同用途）：" + dedupeKey);
            }

            String kind = resolveRelationKind(item);
            String edgeId = StringUtils.hasText(item.getEdgeId())
                    ? item.getEdgeId().trim()
                    : "edge-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
            if (!savedEdgeIds.add(edgeId)) {
                throw new ServiceException(400, "重复的 edgeId: " + edgeId);
            }

            List<String> interactions = normalizeInteractions(item.getEnabledInteractions(), edgeRole);
            Map<String, Object> meta = buildMeta(
                    edgeRole, interactions, item.getLinkKeys(), item.getPresentation());

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

    private String resolveEdgeRole(DmDataTabColumnRelationSaveItemVO item) {
        if (StringUtils.hasText(item.getEdgeRole())) {
            String role = item.getEdgeRole().trim();
            if (!ALLOWED_EDGE_ROLES.contains(role)) {
                throw new ServiceException(400, "无效的边用途: " + item.getEdgeRole());
            }
            return role;
        }
        List<String> raw = item.getEnabledInteractions();
        if (raw != null) {
            for (String code : raw) {
                if (StringUtils.hasText(code) && WRITE_INTERACTIONS.contains(code.trim())) {
                    return "write";
                }
            }
        }
        return "filter";
    }

    private List<String> normalizeInteractions(List<String> raw, String edgeRole) {
        if ("filter".equals(edgeRole)) {
            return List.of();
        }
        if (raw == null || raw.isEmpty()) {
            throw new ServiceException(400, "写关联至少启用一种交互方式（勾选或拖入）");
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
            if ("browseFilter".equals(code) || "cascadeClip".equals(code)
                    || "associationFilterTriad".equals(code)) {
                continue;
            }
            out.add(code);
        }
        if (out.isEmpty()) {
            throw new ServiceException(400, "写关联至少启用一种交互方式（勾选或拖入）");
        }
        return new ArrayList<>(out);
    }

    private Map<String, Object> buildMeta(
            String edgeRole,
            List<String> interactions,
            List<String> linkKeys,
            Map<String, Object> presentation) {
        Map<String, Object> meta = new LinkedHashMap<>();
        meta.put("edgeRole", edgeRole);
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
        String edgeRole = edgeRoleFromMeta(meta);
        vo.setEdgeRole(edgeRole);
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

    /** 读路径去重：同两端同用途只保留一条（历史重复写入兼容）。 */
    private List<DmDataTabColumnRelationDO> dedupeByPairKey(List<DmDataTabColumnRelationDO> rows) {
        Map<String, DmDataTabColumnRelationDO> byPair = new LinkedHashMap<>();
        for (DmDataTabColumnRelationDO row : rows) {
            String from = row.getFromColumnIdentity() == null ? "" : row.getFromColumnIdentity().trim();
            String to = row.getToColumnIdentity() == null ? "" : row.getToColumnIdentity().trim();
            String role = edgeRoleFromMeta(row.getRelationMeta());
            String key = ColumnRelationLayoutEndpoints.pairKey(from, to, role);
            byPair.putIfAbsent(key, row);
        }
        return new ArrayList<>(byPair.values());
    }
}
