package cn.cheers.x.module.dynamicbusiness.service.datamgmt;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmDataTabColumnRelationRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmDataTabColumnRelationSaveItemVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmDataTabColumnRelationSaveReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datamgmt.DmDataTabColumnRelationDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datamgmt.DmDataTabLayoutDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.datamgmt.DmDataTabColumnRelationMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.datamgmt.DmDataTabLayoutMapper;
import cn.cheers.x.module.dynamicbusiness.service.entitytype.EntityTypeCategoryBootstrapService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * 栏间关系声明实现。
 * <p>
 * ## 权威与边界
 * 本类负责关系表的读，以及<strong>仅两种</strong>删/写边路径：
 * <ul>
 *   <li>{@link #saveRelations}：用户保存关系全集（全量替换）</li>
 *   <li>{@link #removeRelationsTouchingIdentities}：删栏时按作废列身份清边</li>
 * </ul>
 * 禁止：按 enabled / 缺类型码 /「当前看起来不像有效端点」反扫全表删边。
 * 默认补 filter 边在 {@link DmDataTabColumnRelationBootstrapService}，只 insert 不删。
 */
@Service
@Validated
public class DmDataTabColumnRelationServiceImpl implements DmDataTabColumnRelationService {

    private static final Set<String> ALLOWED_KINDS = Set.of(
            "CATEGORY_CATEGORY",
            "CATEGORY_MODEL",
            "CATEGORY_ENTITY",
            "CATEGORY_DETAIL",
            "MODEL_MODEL",
            "MODEL_ENTITY",
            "MODEL_DETAIL",
            "ENTITY_DETAIL",
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

    private static final Set<String> ALLOWED_EDGE_ACTIONS = Set.of("filter", "write", "detail_follow");
    private static final Set<String> DETAIL_FOLLOW_KINDS = Set.of(
            "CATEGORY_DETAIL",
            "MODEL_DETAIL",
            "ENTITY_DETAIL"
    );

    /**
     * 读路径只认 meta.edgeAction；禁止从 enabledInteractions 推断。
     * 缺则抛错暴露缺口（须由 saveRelations / bootstrap / 迁移写出）。
     */
    private static String requireEdgeActionFromMeta(Map<String, Object> meta, String edgeId) {
        if (meta == null) {
            throw new ServiceException(400,
                    "栏间关系缺少 edgeAction（edgeId=" + edgeId + "）。须为 filter / write / detail_follow，禁止推断");
        }
        Object role = meta.get("edgeAction");
        if (role != null) {
            String text = String.valueOf(role).trim();
            if (ALLOWED_EDGE_ACTIONS.contains(text)) {
                return text;
            }
        }
        throw new ServiceException(400,
                "栏间关系缺少或无效 edgeAction（edgeId=" + edgeId + "）。须为 filter / write / detail_follow，禁止推断");
    }

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
        return dedupeByPairKey(dmDataTabColumnRelationMapper.selectListByLayoutId(layoutId)).stream()
                .map(this::toRespVO)
                .toList();
    }

    @Override
    public List<DmDataTabColumnRelationRespVO> listByEntityTypeCode(String entityTypeCode) {
        // 读路径禁止 bootstrap 写库；缺布局由创建类型时写出，此处只解析已挂载 layoutId。
        String code = normalizeEntityTypeCode(entityTypeCode);
        Long layoutId = dmWorkbenchLayoutService.resolveLayoutIdForEntityType(code);
        return listByLayoutId(layoutId);
    }

    /**
     * 触发 1：全量替换。
     * <p>
     * 请求体 relations = 本页最终边集合；库里有、请求里没有的 edgeId → 删除。
     * 这是用户在数据关系图点「保存关系」时的权威路径；与布局保存无关。
     * <p>
     * 输入假设：调用方提交的是意图中的完整集合（可为空，空即清空本页全部边）。
     * 禁止在布局改名/隐藏/调序等路径调用本方法「顺便清边」。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveRelations(DmDataTabColumnRelationSaveReqVO reqVO) {
        Long layoutId = resolveSaveLayoutId(reqVO);
        String code = resolveEntityTypeCodeForSave(layoutId, reqVO.getEntityTypeCode());
        List<DmDataTabColumnRelationSaveItemVO> items =
                reqVO.getRelations() == null ? List.of() : reqVO.getRelations();

        Set<String> savedEdgeIds = new HashSet<>();
        Set<String> pairKeys = new HashSet<>();
        Set<String> validIdentities = currentLayoutIdentities(layoutId);
        List<DmDataTabColumnRelationDO> toInsert = new ArrayList<>();
        for (DmDataTabColumnRelationSaveItemVO item : items) {
            validateItem(item);
            String from = item.getFromColumnIdentity().trim();
            String to = item.getToColumnIdentity().trim();
            if (from.equals(to)) {
                throw new ServiceException(400, "栏间关系两端列身份不能相同");
            }
            if (!validIdentities.contains(from)) {
                throw new ServiceException(400,
                        "栏间关系 from 端点不在当前布局栏身份中：" + from);
            }
            if (!validIdentities.contains(to)) {
                throw new ServiceException(400,
                        "栏间关系 to 端点不在当前布局栏身份中：" + to);
            }
            String edgeAction = resolveEdgeAction(item);
            String dedupeKey = ColumnRelationLayoutEndpoints.pairKey(from, to, edgeAction);
            if (!pairKeys.add(dedupeKey)) {
                throw new ServiceException(400, "重复的栏间关系（同两端同用途）：" + dedupeKey);
            }

            String kind = resolveRelationKind(item);
            validateEdgeActionAgainstKind(edgeAction, kind);
            String edgeId = StringUtils.hasText(item.getEdgeId())
                    ? item.getEdgeId().trim()
                    : "edge-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
            if (!savedEdgeIds.add(edgeId)) {
                throw new ServiceException(400, "重复的 edgeId: " + edgeId);
            }

            List<String> interactions = normalizeInteractions(item.getEnabledInteractions(), edgeAction);
            Map<String, Object> meta = buildMeta(
                    edgeAction, interactions, item.getLinkKeys(), item.getPresentation());

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
            toInsert.add(insert);
        }

        // 整页替换：先清空该 layout 已有关系，再按请求全集重建，杜绝历史旧边残留回流。
        List<DmDataTabColumnRelationDO> existing =
                dmDataTabColumnRelationMapper.selectListByLayoutId(layoutId);
        for (DmDataTabColumnRelationDO row : existing) {
            dmDataTabColumnRelationMapper.deleteById(row.getId());
        }
        dmDataTabColumnRelationMapper.deletePhysicalSoftDeletedByLayoutId(layoutId);
        for (DmDataTabColumnRelationDO row : toInsert) {
            dmDataTabColumnRelationMapper.insert(row);
        }
    }

    /**
     * 触发 2：删栏连带清边。
     * <p>
     * 权威入参：本次<strong>已经从布局删掉</strong>（或即将作废）的列身份集合。
     * 只删 from / to 碰到该集合的边；集合为空则不动关系表。
     * <p>
     * 谁调用：仅 {@link DmDataTabLayoutServiceImpl#saveLayouts} 在确有布局行被删时。
     * 配置隐藏、改名、调序、加栏 → 不得调用。
     * <p>
     * 删光后物理清除软删行，避免与全量替换路径残留混淆。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeRelationsTouchingIdentities(
            Long layoutId, Collection<String> removedIdentities) {
        if (layoutId == null || removedIdentities == null || removedIdentities.isEmpty()) {
            return;
        }
        Set<String> removed = new HashSet<>();
        for (String raw : removedIdentities) {
            if (StringUtils.hasText(raw)) {
                removed.add(raw.trim());
            }
        }
        if (removed.isEmpty()) {
            return;
        }
        dmWorkbenchLayoutService.requireLayout(layoutId);
        for (DmDataTabColumnRelationDO row :
                dmDataTabColumnRelationMapper.selectListByLayoutId(layoutId)) {
            String from = row.getFromColumnIdentity() == null ? "" : row.getFromColumnIdentity().trim();
            String to = row.getToColumnIdentity() == null ? "" : row.getToColumnIdentity().trim();
            if (removed.contains(from) || removed.contains(to)) {
                dmDataTabColumnRelationMapper.deleteById(row.getId());
            }
        }
        dmDataTabColumnRelationMapper.deletePhysicalSoftDeletedByLayoutId(layoutId);
    }

    /**
     * 布局 tabId 变更：只改名边端点，不删边。
     * <p>
     * 谁调用：仅 {@link DmDataTabLayoutServiceImpl#saveLayouts} 在按行 id 更新且列身份变化时。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void renameColumnIdentities(Long layoutId, Map<String, String> fromTo) {
        if (layoutId == null || fromTo == null || fromTo.isEmpty()) {
            return;
        }
        Map<String, String> renames = new LinkedHashMap<>();
        for (Map.Entry<String, String> e : fromTo.entrySet()) {
            String from = e.getKey() == null ? "" : e.getKey().trim();
            String to = e.getValue() == null ? "" : e.getValue().trim();
            if (!StringUtils.hasText(from) || !StringUtils.hasText(to) || from.equals(to)) {
                continue;
            }
            renames.put(from, to);
        }
        if (renames.isEmpty()) {
            return;
        }
        dmWorkbenchLayoutService.requireLayout(layoutId);
        for (DmDataTabColumnRelationDO row :
                dmDataTabColumnRelationMapper.selectListByLayoutId(layoutId)) {
            boolean changed = false;
            String from = row.getFromColumnIdentity() == null ? "" : row.getFromColumnIdentity().trim();
            String to = row.getToColumnIdentity() == null ? "" : row.getToColumnIdentity().trim();
            if (renames.containsKey(from)) {
                row.setFromColumnIdentity(renames.get(from));
                changed = true;
            }
            if (renames.containsKey(to)) {
                row.setToColumnIdentity(renames.get(to));
                changed = true;
            }
            if (changed) {
                dmDataTabColumnRelationMapper.updateById(row);
            }
        }
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

    /**
     * 关系行 entity_type_code 非空：请求优先，否则从本页布局行取目录注册编码。
     * 仍缺则报错，禁止插入 null（会整笔事务失败或落成脏数据）。
     */
    private String resolveEntityTypeCodeForSave(Long layoutId, String requestEntityTypeCode) {
        if (StringUtils.hasText(requestEntityTypeCode)) {
            return requestEntityTypeCode.trim();
        }
        List<DmDataTabLayoutDO> rows = dmDataTabLayoutMapper.selectListByLayoutId(layoutId);
        for (DmDataTabLayoutDO row : rows) {
            if (StringUtils.hasText(row.getEntityTypeCode())) {
                return row.getEntityTypeCode().trim();
            }
        }
        throw new ServiceException(400,
                "保存栏间关系缺少 entityTypeCode，且布局行上也没有目录注册编码（layoutId="
                        + layoutId + "）");
    }

    private Set<String> currentLayoutIdentities(Long layoutId) {
        Set<String> out = new HashSet<>();
        for (DmDataTabLayoutDO row : dmDataTabLayoutMapper.selectListByLayoutId(layoutId)) {
            String identity = ColumnRelationLayoutEndpoints.columnIdentityOf(row);
            if (StringUtils.hasText(identity)) {
                out.add(identity.trim());
            }
        }
        return out;
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
            case "CATEGORY_DETAIL" -> "CATEGORY_DETAIL";
            case "MODEL_MODEL" -> "MODEL_MODEL";
            case "MODEL_ENTITY" -> "MODEL_ENTITY";
            case "MODEL_DETAIL" -> "MODEL_DETAIL";
            case "ENTITY_DETAIL" -> "ENTITY_DETAIL";
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

    /**
     * 写路径：edgeAction 必须显式传入；禁止从交互列表猜 filter/write/detail_follow。
     */
    private String resolveEdgeAction(DmDataTabColumnRelationSaveItemVO item) {
        String raw = item.getEdgeAction();
        if (!StringUtils.hasText(raw)) {
            throw new ServiceException(400, "栏间关系必须指定 edgeAction（filter、write 或 detail_follow）");
        }
        String edgeAction = raw.trim();
        if (!ALLOWED_EDGE_ACTIONS.contains(edgeAction)) {
            throw new ServiceException(400, "无效的边用途: " + raw);
        }
        return edgeAction;
    }

    private void validateEdgeActionAgainstKind(String edgeAction, String relationKind) {
        if ("write".equals(edgeAction) && DETAIL_FOLLOW_KINDS.contains(relationKind)) {
            throw new ServiceException(400, "详情栏不支持修改关联 write，请改用筛选连线（内部会记为详情跟随）");
        }
        if ("detail_follow".equals(edgeAction) && !DETAIL_FOLLOW_KINDS.contains(relationKind)) {
            throw new ServiceException(400, "detail_follow 仅允许 CATEGORY_DETAIL / MODEL_DETAIL / ENTITY_DETAIL");
        }
        if ("filter".equals(edgeAction) && DETAIL_FOLLOW_KINDS.contains(relationKind)) {
            throw new ServiceException(400, "详情跟随请使用 detail_follow，禁止再用 filter 复用语义");
        }
    }

    private List<String> normalizeInteractions(List<String> raw, String edgeAction) {
        if ("filter".equals(edgeAction) || "detail_follow".equals(edgeAction)) {
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
            String edgeAction,
            List<String> interactions,
            List<String> linkKeys,
            Map<String, Object> presentation) {
        Map<String, Object> meta = new LinkedHashMap<>();
        meta.put("edgeAction", edgeAction);
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
        String edgeAction = requireEdgeActionFromMeta(meta, row.getEdgeId());
        vo.setEdgeAction(edgeAction);
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
            String edgeAction = requireEdgeActionFromMeta(row.getRelationMeta(), row.getEdgeId());
            String key = ColumnRelationLayoutEndpoints.pairKey(from, to, edgeAction);
            byPair.putIfAbsent(key, row);
        }
        return new ArrayList<>(byPair.values());
    }
}
