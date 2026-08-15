package cn.cheers.x.module.dynamicbusiness.service.datamgmt;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmFiveWOrchestrationBundleRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmFiveWOrchestrationBundleSaveReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datamgmt.DmFiveWFilterLayoutDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datamgmt.DmFiveWOrchestrationDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datamgmt.DmFiveWWhoLayoutDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.datamgmt.DmFiveWFilterLayoutMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.datamgmt.DmFiveWOrchestrationMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.datamgmt.DmFiveWWhoLayoutMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.util.*;

@Service
@Validated
public class DmFiveWOrchestrationServiceImpl implements DmFiveWOrchestrationService {

    private static final Set<String> SELECTION_LEVELS = Set.of("MODEL", "ENTITY");
    private static final Set<String> WHAT_MODES = Set.of(
            "VIEW_DETAIL", "PICK_ENTITY", "PICK_MODEL", "PICK_CATEGORY", "SITE_PREP", "NONE");
    private static final Set<String> HOW_MODES = Set.of("NONE", "AFTER_WHAT_ITEM");
    private static final Set<String> WHO_KINDS = Set.of("MODEL", "ENTITY");
    private static final Set<String> FILTER_KINDS = Set.of("CATEGORY", "MODEL", "ENTITY");
    private static final Set<String> CONTEXT_OUTPUT_KEYS = Set.of("categoryId", "modelId", "entityId");

    @Resource
    private DmFiveWOrchestrationMapper orchestrationMapper;

    @Resource
    private DmFiveWWhoLayoutMapper whoLayoutMapper;

    @Resource
    private DmFiveWFilterLayoutMapper filterLayoutMapper;

    @Override
    public DmFiveWOrchestrationBundleRespVO getBundle(String registryCode) {
        String code = normalizeCode(registryCode);
        DmFiveWOrchestrationDO head = orchestrationMapper.selectByEntityTypeCode(code);
        List<DmFiveWWhoLayoutDO> whoRows = whoLayoutMapper.selectListByEntityTypeCode(code);
        List<DmFiveWFilterLayoutDO> filterRows = filterLayoutMapper.selectListByEntityTypeCode(code);
        if (head == null && whoRows.isEmpty() && filterRows.isEmpty()) {
            throw new ServiceException(404, "五维编排 bundle 未配置：" + code);
        }
        if (head == null) {
            throw new ServiceException(500, "五维编排槽位存在但语义块缺失：" + code);
        }
        return assembleBundle(code, code, head, whoRows, filterRows);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBundle(DmFiveWOrchestrationBundleSaveReqVO reqVO) {
        String code = normalizeCode(reqVO.getRegistryCode());
        validateBundle(reqVO);

        DmFiveWOrchestrationDO head = orchestrationMapper.selectByEntityTypeCode(code);
        if (head == null) {
            head = new DmFiveWOrchestrationDO();
            head.setEntityTypeCode(code);
        }
        head.setEnabled(reqVO.getSemantic().getEnabled() == null || reqVO.getSemantic().getEnabled());
        head.setSelectionLevel(reqVO.getSemantic().getSelectionLevel().trim().toUpperCase());
        head.setWhatMode(reqVO.getWhatSlot().getMode().trim().toUpperCase());
        head.setWhatConfig(toWhatConfigMap(reqVO.getWhatSlot()));
        head.setHowMode(reqVO.getHowSlot().getMode() == null ? "NONE" : reqVO.getHowSlot().getMode().trim().toUpperCase());
        head.setHowConfig(Collections.emptyMap());

        if (head.getId() == null) {
            orchestrationMapper.insert(head);
        } else {
            orchestrationMapper.updateById(head);
        }

        upsertWhoSlots(code, reqVO.getWhoSlots());
        upsertFilterSlots(code, reqVO.getFilterSlots() == null ? List.of() : reqVO.getFilterSlots());
    }

    private void upsertWhoSlots(String code, List<DmFiveWOrchestrationBundleRespVO.WhoSlot> slots) {
        List<DmFiveWWhoLayoutDO> existing = whoLayoutMapper.selectListByEntityTypeCode(code);
        Map<String, DmFiveWWhoLayoutDO> existingByScope = new LinkedHashMap<>();
        for (DmFiveWWhoLayoutDO row : existing) {
            existingByScope.put(scopeKey(row.getColumnKind(), row.getPerspectiveId(), row.getSlotRef()), row);
        }
        Set<String> savedScopes = new HashSet<>();
        for (DmFiveWOrchestrationBundleRespVO.WhoSlot slot : slots) {
            String kind = slot.getColumnKind().trim().toUpperCase();
            String perspectiveId = normalizeOptional(slot.getPerspectiveId());
            String slotRef = normalizeOptional(slot.getSlotRef());
            String scope = scopeKey(kind, perspectiveId, slotRef);
            savedScopes.add(scope);
            DmFiveWWhoLayoutDO row = existingByScope.get(scope);
            if (row == null) {
                row = new DmFiveWWhoLayoutDO();
                row.setEntityTypeCode(code);
                row.setColumnKind(kind);
                row.setPerspectiveId(perspectiveId);
                row.setSlotRef(slotRef);
            }
            row.setPropsId(slot.getPropsId());
            row.setEnabled(slot.getEnabled() == null || slot.getEnabled());
            row.setContextOutputs(normalizeOutputs(slot.getContextOutputs()));
            row.setEntityIdRule(normalizeOptional(slot.getEntityIdRule()));
            row.setCategoryColumn(slot.getCategoryColumn());
            if (row.getId() == null) {
                whoLayoutMapper.insert(row);
            } else {
                whoLayoutMapper.updateById(row);
            }
        }
        for (DmFiveWWhoLayoutDO row : existing) {
            String scope = scopeKey(row.getColumnKind(), row.getPerspectiveId(), row.getSlotRef());
            if (!savedScopes.contains(scope)) {
                whoLayoutMapper.deleteById(row.getId());
            }
        }
    }

    private void upsertFilterSlots(String code, List<DmFiveWOrchestrationBundleRespVO.FilterSlot> slots) {
        List<DmFiveWFilterLayoutDO> existing = filterLayoutMapper.selectListByEntityTypeCode(code);
        Map<String, DmFiveWFilterLayoutDO> existingByScope = new LinkedHashMap<>();
        for (DmFiveWFilterLayoutDO row : existing) {
            existingByScope.put(scopeKey(row.getColumnKind(), row.getPerspectiveId(), row.getSlotRef()), row);
        }
        Set<String> savedScopes = new HashSet<>();
        for (DmFiveWOrchestrationBundleRespVO.FilterSlot slot : slots) {
            String kind = slot.getColumnKind().trim().toUpperCase();
            String perspectiveId = normalizeOptional(slot.getPerspectiveId());
            String slotRef = normalizeOptional(slot.getSlotRef());
            String scope = scopeKey(kind, perspectiveId, slotRef);
            savedScopes.add(scope);
            DmFiveWFilterLayoutDO row = existingByScope.get(scope);
            if (row == null) {
                row = new DmFiveWFilterLayoutDO();
                row.setEntityTypeCode(code);
                row.setColumnKind(kind);
                row.setPerspectiveId(perspectiveId);
                row.setSlotRef(slotRef);
            }
            row.setPropsId(slot.getPropsId());
            row.setEnabled(slot.getEnabled() == null || slot.getEnabled());
            row.setContextOutputs(normalizeOutputs(slot.getContextOutputs()));
            row.setEntityIdRule(normalizeOptional(slot.getEntityIdRule()));
            row.setCategoryColumn(slot.getCategoryColumn());
            if (row.getId() == null) {
                filterLayoutMapper.insert(row);
            } else {
                filterLayoutMapper.updateById(row);
            }
        }
        for (DmFiveWFilterLayoutDO row : existing) {
            String scope = scopeKey(row.getColumnKind(), row.getPerspectiveId(), row.getSlotRef());
            if (!savedScopes.contains(scope)) {
                filterLayoutMapper.deleteById(row.getId());
            }
        }
    }

    private DmFiveWOrchestrationBundleRespVO assembleBundle(
            String registryCode,
            String storageCode,
            DmFiveWOrchestrationDO head,
            List<DmFiveWWhoLayoutDO> whoRows,
            List<DmFiveWFilterLayoutDO> filterRows) {
        DmFiveWOrchestrationBundleRespVO bundle = new DmFiveWOrchestrationBundleRespVO();
        bundle.setRegistryCode(registryCode);
        bundle.setStorageEntityTypeCode(storageCode);

        DmFiveWOrchestrationBundleRespVO.Semantic semantic = new DmFiveWOrchestrationBundleRespVO.Semantic();
        semantic.setEnabled(head.getEnabled());
        semantic.setSelectionLevel(head.getSelectionLevel());
        bundle.setSemantic(semantic);

        bundle.setFilterSlots(filterRows.stream().map(this::toFilterSlotVO).toList());
        bundle.setWhoSlots(whoRows.stream().map(this::toWhoSlotVO).toList());
        bundle.setWhatSlot(toWhatSlotVO(head));
        bundle.setHowSlot(toHowSlotVO(head));
        return bundle;
    }

    private void validateBundle(DmFiveWOrchestrationBundleSaveReqVO reqVO) {
        String selectionLevel = reqVO.getSemantic().getSelectionLevel().trim().toUpperCase();
        if (!SELECTION_LEVELS.contains(selectionLevel)) {
            throw new ServiceException(400, "无效的 selectionLevel: " + selectionLevel);
        }
        String whatMode = reqVO.getWhatSlot().getMode().trim().toUpperCase();
        if (!WHAT_MODES.contains(whatMode)) {
            throw new ServiceException(400, "无效的 what mode: " + whatMode);
        }
        String howMode = reqVO.getHowSlot().getMode() == null
                ? "NONE"
                : reqVO.getHowSlot().getMode().trim().toUpperCase();
        if (!HOW_MODES.contains(howMode)) {
            throw new ServiceException(400, "无效的 how mode: " + howMode);
        }

        boolean anyEntityIdOutput = false;
        boolean categoryLinkedEntity = false;

        List<DmFiveWOrchestrationBundleRespVO.FilterSlot> filterSlots =
                reqVO.getFilterSlots() == null ? List.of() : reqVO.getFilterSlots();
        for (DmFiveWOrchestrationBundleRespVO.FilterSlot slot : filterSlots) {
            if (slot.getEnabled() != null && !slot.getEnabled()) {
                continue;
            }
            String kind = slot.getColumnKind().trim().toUpperCase();
            if (!FILTER_KINDS.contains(kind)) {
                throw new ServiceException(400, "筛选槽 columnKind 无效: " + kind);
            }
            List<String> outputs = normalizeOutputs(slot.getContextOutputs());
            for (String key : outputs) {
                if ("CATEGORY".equals(kind) && !"categoryId".equals(key)) {
                    throw new ServiceException(400, "分类筛选槽只能输出 categoryId: " + key);
                }
                if ("MODEL".equals(kind) && !"modelId".equals(key)) {
                    throw new ServiceException(400, "型号筛选槽只能输出 modelId: " + key);
                }
                if ("ENTITY".equals(kind) && !"entityId".equals(key)) {
                    throw new ServiceException(400, "实体筛选槽只能输出 entityId: " + key);
                }
            }
            String rule = normalizeOptional(slot.getEntityIdRule());
            if (rule != null) {
                if (!"CATEGORY".equals(kind) || !"categoryLinkedEntity".equals(rule)) {
                    throw new ServiceException(400, "筛选槽 entityIdRule 仅允许分类槽 categoryLinkedEntity");
                }
                categoryLinkedEntity = true;
            }
        }

        for (DmFiveWOrchestrationBundleRespVO.WhoSlot slot : reqVO.getWhoSlots()) {
            if (slot.getEnabled() != null && !slot.getEnabled()) {
                continue;
            }
            String kind = slot.getColumnKind().trim().toUpperCase();
            if (!WHO_KINDS.contains(kind)) {
                throw new ServiceException(400, "Who 槽位 columnKind 无效（分类请写入 filterSlots）: " + kind);
            }
            List<String> outputs = normalizeOutputs(slot.getContextOutputs());
            for (String key : outputs) {
                if (!CONTEXT_OUTPUT_KEYS.contains(key) || "categoryId".equals(key)) {
                    throw new ServiceException(400, "Who 槽位无效的 contextOutput: " + key);
                }
            }
            if (outputs.contains("entityId")) {
                anyEntityIdOutput = true;
                String rule = normalizeOptional(slot.getEntityIdRule());
                if (!"rowSelection".equals(rule)) {
                    throw new ServiceException(400, "Who 实体槽 entityIdRule 须为 rowSelection");
                }
            }
        }

        if ("ENTITY".equals(selectionLevel) && !anyEntityIdOutput && !categoryLinkedEntity) {
            throw new ServiceException(400, "selectionLevel=ENTITY 时须 Who 实体槽输出 entityId，或筛选槽设置 categoryLinkedEntity");
        }
        if ("MODEL".equals(selectionLevel) && anyEntityIdOutput) {
            throw new ServiceException(400, "selectionLevel=MODEL 时不得有 Who 槽输出 entityId");
        }
    }

    private DmFiveWOrchestrationBundleRespVO.FilterSlot toFilterSlotVO(DmFiveWFilterLayoutDO row) {
        DmFiveWOrchestrationBundleRespVO.FilterSlot vo = new DmFiveWOrchestrationBundleRespVO.FilterSlot();
        vo.setId(row.getId());
        vo.setSlotRef(row.getSlotRef());
        vo.setColumnKind(row.getColumnKind());
        vo.setPerspectiveId(row.getPerspectiveId());
        vo.setPropsId(row.getPropsId());
        vo.setEnabled(row.getEnabled());
        vo.setContextOutputs(row.getContextOutputs());
        vo.setEntityIdRule(row.getEntityIdRule());
        vo.setCategoryColumn(row.getCategoryColumn());
        return vo;
    }

    private DmFiveWOrchestrationBundleRespVO.WhoSlot toWhoSlotVO(DmFiveWWhoLayoutDO row) {
        DmFiveWOrchestrationBundleRespVO.WhoSlot vo = new DmFiveWOrchestrationBundleRespVO.WhoSlot();
        vo.setId(row.getId());
        vo.setSlotRef(row.getSlotRef());
        vo.setColumnKind(row.getColumnKind());
        vo.setPerspectiveId(row.getPerspectiveId());
        vo.setPropsId(row.getPropsId());
        vo.setEnabled(row.getEnabled());
        vo.setContextOutputs(row.getContextOutputs());
        vo.setEntityIdRule(row.getEntityIdRule());
        vo.setCategoryColumn(row.getCategoryColumn());
        return vo;
    }

    private DmFiveWOrchestrationBundleRespVO.WhatSlot toWhatSlotVO(DmFiveWOrchestrationDO head) {
        DmFiveWOrchestrationBundleRespVO.WhatSlot vo = new DmFiveWOrchestrationBundleRespVO.WhatSlot();
        vo.setMode(head.getWhatMode());
        Map<String, Object> cfg = head.getWhatConfig() == null ? Map.of() : head.getWhatConfig();
        vo.setBindLayer(stringVal(cfg.get("bindLayer")));
        vo.setDetailPropsId(longVal(cfg.get("detailPropsId")));
        vo.setListPropsId(longVal(cfg.get("listPropsId")));
        vo.setCandidateEntityTypeCode(stringVal(cfg.get("candidateEntityTypeCode")));
        vo.setCandidateCategoryTypeCode(stringVal(cfg.get("candidateCategoryTypeCode")));
        vo.setDetailReadonly(boolVal(cfg.get("detailReadonly")));
        vo.setPanelPropsId(longVal(cfg.get("panelPropsId")));
        vo.setDomain(stringVal(cfg.get("domain")));
        vo.setRelationKind(stringVal(cfg.get("relationKind")));
        return vo;
    }

    private DmFiveWOrchestrationBundleRespVO.HowSlot toHowSlotVO(DmFiveWOrchestrationDO head) {
        DmFiveWOrchestrationBundleRespVO.HowSlot vo = new DmFiveWOrchestrationBundleRespVO.HowSlot();
        vo.setMode(head.getHowMode() == null ? "NONE" : head.getHowMode());
        return vo;
    }

    private Map<String, Object> toWhatConfigMap(DmFiveWOrchestrationBundleRespVO.WhatSlot slot) {
        Map<String, Object> map = new LinkedHashMap<>();
        putIfText(map, "bindLayer", slot.getBindLayer());
        putIfNonNull(map, "detailPropsId", slot.getDetailPropsId());
        putIfNonNull(map, "listPropsId", slot.getListPropsId());
        putIfText(map, "candidateEntityTypeCode", slot.getCandidateEntityTypeCode());
        putIfText(map, "candidateCategoryTypeCode", slot.getCandidateCategoryTypeCode());
        putIfNonNull(map, "detailReadonly", slot.getDetailReadonly());
        putIfNonNull(map, "panelPropsId", slot.getPanelPropsId());
        putIfText(map, "domain", slot.getDomain());
        putIfText(map, "relationKind", slot.getRelationKind());
        return map;
    }

    private List<String> normalizeOutputs(List<String> outputs) {
        if (outputs == null || outputs.isEmpty()) {
            return List.of();
        }
        List<String> list = new ArrayList<>();
        for (String raw : outputs) {
            if (!StringUtils.hasText(raw)) {
                continue;
            }
            list.add(raw.trim());
        }
        return list;
    }

    private String scopeKey(String columnKind, String perspectiveId, String slotRef) {
        return (columnKind == null ? "" : columnKind.trim().toUpperCase())
                + "|"
                + (perspectiveId == null ? "" : perspectiveId.trim())
                + "|"
                + (slotRef == null ? "" : slotRef.trim());
    }

    private String normalizeCode(String code) {
        if (!StringUtils.hasText(code)) {
            throw new ServiceException(400, "registryCode 不能为空");
        }
        return code.trim();
    }

    private String normalizeOptional(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private void putIfText(Map<String, Object> map, String key, String value) {
        if (StringUtils.hasText(value)) {
            map.put(key, value.trim());
        }
    }

    private void putIfNonNull(Map<String, Object> map, String key, Object value) {
        if (value != null) {
            map.put(key, value);
        }
    }

    private String stringVal(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private Long longVal(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        return Long.parseLong(String.valueOf(value));
    }

    private Boolean boolVal(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Boolean b) {
            return b;
        }
        return Boolean.parseBoolean(String.valueOf(value));
    }
}
