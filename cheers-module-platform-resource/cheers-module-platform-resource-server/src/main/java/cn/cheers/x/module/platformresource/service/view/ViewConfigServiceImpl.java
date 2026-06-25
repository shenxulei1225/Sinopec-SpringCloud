package cn.cheers.x.module.platformresource.service.view;

import cn.hutool.core.util.StrUtil;
import cn.cheers.x.module.platformresource.controller.admin.view.vo.*;
import cn.cheers.x.module.platformresource.dal.dataobject.view.ViewConfigDO;
import cn.cheers.x.module.platformresource.dal.mysql.view.ViewConfigMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.cheers.x.module.platformresource.enums.ErrorCodeConstants.*;

@Service
public class ViewConfigServiceImpl implements ViewConfigService {

    @Resource
    private ViewConfigMapper viewConfigMapper;

    @Resource
    private ObjectMapper objectMapper;

    // ─── 查询 ──────────────────────────────────────────────────────────────────

    @Override
    public ViewConfigRespVO getViewConfig(Long viewId) {
        return convertToRespVO(requireViewConfig(viewId));
    }

    @Override
    public ViewConfigRespVO getViewConfigByCode(String viewCode) {
        ViewConfigDO row = viewConfigMapper.selectByCode(viewCode);
        if (row == null) {
            throw exception(VIEW_CONFIG_NOT_EXISTS);
        }
        return convertToRespVO(row);
    }

    @Override
    public List<ViewConfigRespVO> getViewConfigList(ViewConfigListReqVO reqVO) {
        Boolean onlyEnabled = reqVO.getOnlyEnabled() != null ? reqVO.getOnlyEnabled() : Boolean.TRUE;
        return viewConfigMapper
                .selectList(reqVO.getViewType(), reqVO.getIsTemplate(), reqVO.getCategoryId(), onlyEnabled)
                .stream()
                .map(this::convertToRespVO)
                .collect(Collectors.toList());
    }

    // ─── 写入 ──────────────────────────────────────────────────────────────────

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createTemplate(ViewConfigCreateTemplateReqVO reqVO) {
        ViewConfigDO row = new ViewConfigDO();
        row.setIsTemplate(true);
        row.setTemplateId(null);
        row.setViewType(reqVO.getViewType());
        row.setViewCode(StrUtil.blankToDefault(reqVO.getViewCode(), null));
        row.setName(reqVO.getName());
        row.setDescription(reqVO.getDescription());
        row.setCategoryId(reqVO.getCategoryId());
        row.setConfigJson(toJson(reqVO.getConfigJson()));
        row.setConfigOverride(null);
        row.setStatus(reqVO.getStatus() != null ? reqVO.getStatus() : 1);
        row.setSort(reqVO.getSort() != null ? reqVO.getSort() : 0);
        viewConfigMapper.insert(row);
        return row.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createInstance(ViewConfigCreateInstanceReqVO reqVO) {
        ViewConfigDO template = requireTemplate(reqVO.getTemplateId());
        ViewConfigDO row = new ViewConfigDO();
        row.setIsTemplate(false);
        row.setTemplateId(template.getId());
        row.setViewType(template.getViewType());
        row.setViewCode(StrUtil.blankToDefault(reqVO.getViewCode(), null));
        row.setName(StrUtil.isNotBlank(reqVO.getName()) ? reqVO.getName() : template.getName());
        row.setDescription(reqVO.getDescription());
        row.setConfigJson("{}");
        row.setConfigOverride(toJson(reqVO.getConfigOverride() != null ? reqVO.getConfigOverride() : Map.of()));
        row.setStatus(1);
        row.setSort(0);
        viewConfigMapper.insert(row);
        return row.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveViewConfig(Long viewId, ViewConfigSaveReqVO reqVO) {
        ViewConfigDO row = requireViewConfig(viewId);
        applyMetadata(row, reqVO);
        if (Boolean.TRUE.equals(row.getIsTemplate())) {
            if (reqVO.getConfigJson() != null) {
                row.setConfigJson(toJson(reqVO.getConfigJson()));
            } else if (!hasMetadataPatch(reqVO)) {
                throw exception(VIEW_CONFIG_SAVE_JSON_REQUIRED);
            }
        } else {
            if (reqVO.getConfigOverride() != null) {
                row.setConfigOverride(toJson(reqVO.getConfigOverride()));
            } else if (!hasMetadataPatch(reqVO)) {
                throw exception(VIEW_CONFIG_SAVE_OVERRIDE_REQUIRED);
            }
        }
        viewConfigMapper.updateById(row);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteViewConfig(Long viewId) {
        ViewConfigDO row = requireViewConfig(viewId);
        if (Boolean.TRUE.equals(row.getIsTemplate())
                && viewConfigMapper.selectCountByTemplateId(viewId) > 0) {
            throw exception(VIEW_CONFIG_TEMPLATE_HAS_INSTANCES);
        }
        viewConfigMapper.deleteById(viewId);
    }

    // ─── 内部工具 ──────────────────────────────────────────────────────────────

    /**
     * 将模板 configJson 与实例 configOverride 深度合并，仅合并 slots 层。
     * relations 若实例有声明则覆盖整个数组，否则沿用模板。
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> resolveConfig(ViewConfigDO row) {
        if (Boolean.TRUE.equals(row.getIsTemplate())) {
            return parseJsonMap(row.getConfigJson());
        }
        if (row.getTemplateId() == null) {
            return parseJsonMap(row.getConfigJson());
        }
        ViewConfigDO template = viewConfigMapper.selectById(row.getTemplateId());
        Map<String, Object> base = template != null
                ? parseJsonMap(template.getConfigJson())
                : new LinkedHashMap<>();
        Map<String, Object> override = parseJsonMap(row.getConfigOverride());

        // 深度合并 slots
        Map<String, Object> merged = new LinkedHashMap<>(base);
        if (override.containsKey("slots")) {
            Map<String, Object> baseSlots = base.containsKey("slots")
                    ? new LinkedHashMap<>((Map<String, Object>) base.get("slots"))
                    : new LinkedHashMap<>();
            Map<String, Object> overrideSlots = (Map<String, Object>) override.get("slots");
            overrideSlots.forEach((key, val) -> {
                if (baseSlots.containsKey(key) && val instanceof Map) {
                    Map<String, Object> baseSlot = new LinkedHashMap<>((Map<String, Object>) baseSlots.get(key));
                    baseSlot.putAll((Map<String, Object>) val);
                    baseSlots.put(key, baseSlot);
                } else {
                    baseSlots.put(key, val);
                }
            });
            merged.put("slots", baseSlots);
        }
        // relations：实例整体覆盖
        if (override.containsKey("relations")) {
            merged.put("relations", override.get("relations"));
        }
        return merged;
    }

    private ViewConfigRespVO convertToRespVO(ViewConfigDO row) {
        ViewConfigRespVO vo = new ViewConfigRespVO();
        vo.setViewId(row.getId());
        vo.setIsTemplate(row.getIsTemplate());
        vo.setTemplateId(row.getTemplateId());
        vo.setViewType(row.getViewType());
        vo.setViewCode(row.getViewCode());
        vo.setName(row.getName());
        vo.setDescription(row.getDescription());
        vo.setCategoryId(row.getCategoryId());
        vo.setConfigJson(parseJsonMap(row.getConfigJson()));
        vo.setConfigOverride(parseJsonMap(row.getConfigOverride()));
        vo.setResolvedConfig(resolveConfig(row));
        vo.setStatus(row.getStatus());
        vo.setSort(row.getSort());
        return vo;
    }

    private ViewConfigDO requireViewConfig(Long viewId) {
        ViewConfigDO row = viewConfigMapper.selectById(viewId);
        if (row == null) {
            throw exception(VIEW_CONFIG_NOT_EXISTS);
        }
        return row;
    }

    private ViewConfigDO requireTemplate(Long templateId) {
        ViewConfigDO row = requireViewConfig(templateId);
        if (!Boolean.TRUE.equals(row.getIsTemplate())) {
            throw exception(VIEW_CONFIG_NOT_TEMPLATE);
        }
        return row;
    }

    private void applyMetadata(ViewConfigDO row, ViewConfigSaveReqVO reqVO) {
        if (reqVO.getName() != null) row.setName(reqVO.getName());
        if (reqVO.getDescription() != null) row.setDescription(reqVO.getDescription());
        if (reqVO.getViewCode() != null) row.setViewCode(StrUtil.blankToDefault(reqVO.getViewCode(), null));
        if (reqVO.getCategoryId() != null || Boolean.TRUE.equals(reqVO.getClearCategoryId())) {
            row.setCategoryId(Boolean.TRUE.equals(reqVO.getClearCategoryId()) ? null : reqVO.getCategoryId());
        }
        if (reqVO.getStatus() != null) row.setStatus(reqVO.getStatus());
        if (reqVO.getSort() != null) row.setSort(reqVO.getSort());
    }

    private boolean hasMetadataPatch(ViewConfigSaveReqVO reqVO) {
        return reqVO.getName() != null || reqVO.getDescription() != null
                || reqVO.getViewCode() != null || reqVO.getCategoryId() != null
                || Boolean.TRUE.equals(reqVO.getClearCategoryId())
                || reqVO.getStatus() != null || reqVO.getSort() != null;
    }

    private Map<String, Object> parseJsonMap(String json) {
        if (StrUtil.isBlank(json)) return new LinkedHashMap<>();
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            throw new RuntimeException("ViewConfig JSON 反序列化失败", e);
        }
    }

    private String toJson(Object obj) {
        if (obj == null) return null;
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("ViewConfig JSON 序列化失败", e);
        }
    }
}
