package cn.cheers.x.module.dynamicbusiness.service.datamgmt;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmFiveWOrchestrationBundleRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmFiveWOrchestrationBundleSaveReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datamgmt.DmFiveWOrchestrationDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.datamgmt.DmFiveWOrchestrationMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Service
@Validated
public class DmFiveWOrchestrationServiceImpl implements DmFiveWOrchestrationService {

    private static final Set<String> WHAT_MODES = Set.of(
            "VIEW_DETAIL", "PICK_ENTITY", "PICK_MODEL", "PICK_CATEGORY", "SITE_PREP", "NONE");
    private static final Set<String> HOW_MODES = Set.of("NONE", "FOLLOW_WHAT");
    private static final Set<String> OBJECT_PICK_FROM = Set.of("LIST_ROW", "CATEGORY_NODE");

    @Resource
    private DmFiveWOrchestrationMapper orchestrationMapper;

    @Override
    public DmFiveWOrchestrationBundleRespVO getBundle(String registryCode) {
        String code = normalizeCode(registryCode);
        DmFiveWOrchestrationDO head = orchestrationMapper.selectByEntityTypeCode(code);
        if (head == null) {
            throw new ServiceException(404, "五维编排 bundle 未配置：" + code);
        }
        return assembleBundle(code, code, head);
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
        head.setObjectPickFrom(normalizeObjectPickFrom(reqVO.getObjectPickFrom()));
        head.setWhatMode(reqVO.getWhatSlot().getMode().trim().toUpperCase());
        head.setWhatConfig(toWhatConfigMap(reqVO.getWhatSlot()));
        head.setHowMode(reqVO.getHowSlot().getMode() == null
                ? "NONE" : reqVO.getHowSlot().getMode().trim().toUpperCase());
        head.setHowConfig(Collections.emptyMap());

        if (head.getId() == null) {
            orchestrationMapper.insert(head);
        } else {
            orchestrationMapper.updateById(head);
        }
    }

    private DmFiveWOrchestrationBundleRespVO assembleBundle(
            String registryCode,
            String storageCode,
            DmFiveWOrchestrationDO head) {
        DmFiveWOrchestrationBundleRespVO bundle = new DmFiveWOrchestrationBundleRespVO();
        bundle.setRegistryCode(registryCode);
        bundle.setStorageEntityTypeCode(storageCode);

        DmFiveWOrchestrationBundleRespVO.Semantic semantic = new DmFiveWOrchestrationBundleRespVO.Semantic();
        semantic.setEnabled(head.getEnabled());
        bundle.setSemantic(semantic);
        bundle.setObjectPickFrom(normalizeObjectPickFrom(head.getObjectPickFrom()));
        bundle.setWhatSlot(toWhatSlotVO(head));
        bundle.setHowSlot(toHowSlotVO(head));
        return bundle;
    }

    private void validateBundle(DmFiveWOrchestrationBundleSaveReqVO reqVO) {
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
        String pickFrom = normalizeObjectPickFrom(reqVO.getObjectPickFrom());
        if (!OBJECT_PICK_FROM.contains(pickFrom)) {
            throw new ServiceException(400, "无效的 objectPickFrom: " + pickFrom);
        }
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

    private String normalizeCode(String code) {
        if (!StringUtils.hasText(code)) {
            throw new ServiceException(400, "registryCode 不能为空");
        }
        return code.trim();
    }

    private String normalizeObjectPickFrom(String value) {
        if (!StringUtils.hasText(value)) {
            return "LIST_ROW";
        }
        return value.trim().toUpperCase();
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
