package cn.cheers.x.module.dynamicbusiness.service.action;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.action.vo.ActionListItemRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.action.vo.ActionListReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.action.ActionEnablementDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.action.ActionEnablementMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.EntityRepository;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.PhysicalColumnFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 动作库列表查询实现。
 *
 * <p><b>权威</b>：动作行来自 {@code ent_action*}；启用集来自 V77 {@code dynamic_action_enablement}；
 * 参数槽列表来自 {@code param_slots_json}（定稿外形 methods[].fields[].fieldCode，也认旧版顶层 fields）。</p>
 * <p><b>禁止</b>：owner 过滤下启用为空时改拉全库；依赖 inspection 包；按型号 LIBRARY 分配拼参数槽；
 * 把已删除动作补进列表。子动作编码里已删除的，查询时丢掉，不改父动作已保存内容。</p>
 */
@Service
public class ActionQueryServiceImpl implements ActionQueryService {

    /** 编 SOP / 选用器一次拉全库上限；超出需分页或加筛选（本接口不做静默截断语义外扩展） */
    private static final int ALL_LIBRARY_PAGE_SIZE = 500;

    private static final ObjectMapper JSON = new ObjectMapper();

    @Resource
    private ActionEnablementMapper actionEnablementMapper;

    @Resource
    private EntityRepository entityRepository;

    @Resource
    private ModelMapper modelMapper;

    @Resource
    private ActionDeletedChildQueryFilter actionDeletedChildQueryFilter;

    @Override
    public List<ActionListItemRespVO> listActions(ActionListReqVO reqVO) {
        ActionListReqVO req = reqVO != null ? reqVO : new ActionListReqVO();
        String means = normalizeMeans(req.getExecutionMeans());
        OwnerFilter owner = resolveOwnerFilter(req.getOwnerKind(), req.getOwnerId());

        List<Long> orderedIds;
        if (owner != null) {
            // 启用过滤：空启用 → 空列表（禁止兜底全库）
            orderedIds = loadEnabledActionIds(owner.kind(), owner.id());
            if (orderedIds.isEmpty()) {
                return List.of();
            }
        } else {
            orderedIds = loadAllActionIds(means);
            if (orderedIds.isEmpty()) {
                return List.of();
            }
        }

        List<EntityDO> entities = entityRepository.findByIdsWithDedicatedBaseFields(
                orderedIds, ActionFieldCodes.ENTITY_TYPE_CODE);
        Map<Long, String> modelCodes = new LinkedHashMap<>();
        List<Long> modelIds = entities.stream()
                .map(EntityDO::getModelId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (!modelIds.isEmpty()) {
            for (ModelDO model : modelMapper.selectByIds(modelIds)) {
                modelCodes.put(model.getId(), model.getCode());
            }
        }
        Set<String> deletedChildCodes = actionDeletedChildQueryFilter.deletedCodes();
        List<ActionListItemRespVO> out = new ArrayList<>(entities.size());
        for (EntityDO entity : entities) {
            ActionListItemRespVO item = toItem(entity);
            item.setModelCode(modelCodes.get(entity.getModelId()));
            item.setActionTree(ActionDeletedChildQueryFilter.omitValue(
                    item.getActionTree(), deletedChildCodes));
            if (means != null && !means.equalsIgnoreCase(nullToEmpty(item.getExecutionMeans()))) {
                continue;
            }
            out.add(item);
        }
        return List.copyOf(out);
    }

    private List<Long> loadEnabledActionIds(String ownerKind, Long ownerId) {
        List<ActionEnablementDO> rows = actionEnablementMapper.selectByOwner(ownerKind, ownerId);
        Set<Long> ids = new LinkedHashSet<>();
        for (ActionEnablementDO row : rows) {
            if (row.getActionId() != null && row.getActionId() > 0) {
                ids.add(row.getActionId());
            }
        }
        return List.copyOf(ids);
    }

    private List<Long> loadAllActionIds(String means) {
        EntityRepository.EntityQuery.EntityQueryBuilder builder = EntityRepository.EntityQuery.builder()
                .entityTypeCode(ActionFieldCodes.ENTITY_TYPE_CODE)
                .status(1)
                .pageNo(1)
                .pageSize(ALL_LIBRARY_PAGE_SIZE)
                .orderByColumn("sort")
                .orderAsc(true);
        if (means != null) {
            builder.physicalFilters(List.of(
                    new PhysicalColumnFilter(ActionFieldCodes.EXECUTION_MEANS, "EQ", means)));
        }
        PageResult<Long> page = entityRepository.findPageIds(builder.build());
        List<Long> list = page != null && page.getList() != null ? page.getList() : List.of();
        return List.copyOf(list);
    }

    private static OwnerFilter resolveOwnerFilter(String ownerKind, Long ownerId) {
        boolean hasKind = StringUtils.hasText(ownerKind);
        boolean hasId = ownerId != null && ownerId > 0;
        if (hasKind && hasId) {
            return new OwnerFilter(ownerKind.trim(), ownerId);
        }
        if (hasKind || hasId) {
            throw new ServiceException(400, "ownerKind 与 ownerId 须成对传入，不可只传其一");
        }
        return null;
    }

    private static ActionListItemRespVO toItem(EntityDO entity) {
        ActionListItemRespVO vo = new ActionListItemRespVO();
        vo.setId(entity.getId());
        vo.setCode(entity.getCode());
        vo.setName(entity.getName());
        vo.setModelId(entity.getModelId());
        Map<String, Object> base = entity.getDedicatedBaseFieldValues();
        if (base == null) {
            base = Map.of();
        }
        vo.setExecutionMeans(meansToApiString(resolveExecutionMeans(entity, base)));
        // 动作归属与分析能力引用由动作专用表持有。列表只透传，不按名称或执行手段猜值。
        vo.setActionDomain(asString(base.get(ActionFieldCodes.ACTION_DOMAIN)));
        vo.setAnalysisCapabilityId(asLong(base.get(ActionFieldCodes.ANALYSIS_CAPABILITY_ID)));
        ActionParamSlotsParser.Snapshot slots = ActionParamSlotsParser.parse(base.get(ActionFieldCodes.PARAM_SLOTS_JSON));
        vo.setParamSlots(slots.paramSlots());
        vo.setParamSlotsByMeans(slots.paramSlotsByMeans());
        vo.setParamSources(slots.paramSources().stream()
                .map(ActionQueryServiceImpl::toParamSourceVO)
                .toList());
        Map<String, List<ActionListItemRespVO.ParamSourceVO>> sourcesByMeans = new LinkedHashMap<>();
        slots.paramSourcesByMeans().forEach((means, sources) ->
                sourcesByMeans.put(means, sources.stream()
                        .map(ActionQueryServiceImpl::toParamSourceVO)
                        .toList()));
        vo.setParamSourcesByMeans(sourcesByMeans);
        vo.setActionTree(base.get(ActionFieldCodes.ACTION_TREE_JSON));
        return vo;
    }

    private static ActionListItemRespVO.ParamSourceVO toParamSourceVO(
            ActionParamSlotsParser.SourceField source
    ) {
        ActionListItemRespVO.ParamSourceVO vo = new ActionListItemRespVO.ParamSourceVO();
        vo.setFieldCode(source.fieldCode());
        vo.setSourceKind(source.sourceKind());
        vo.setDefaultValue(source.defaultValue());
        vo.setGenerator(source.generator());
        return vo;
    }

    private static String normalizeMeans(String raw) {
        if (!StringUtils.hasText(raw)) {
            return null;
        }
        return raw.trim().toUpperCase(Locale.ROOT);
    }

    private static Object resolveExecutionMeans(EntityDO entity, Map<String, Object> base) {
        if (entity.getCustomFields() != null) {
            Object fromCustom = entity.getCustomFields().get(ActionFieldCodes.EXECUTION_MEANS);
            if (fromCustom != null) {
                return fromCustom;
            }
        }
        return base.get(ActionFieldCodes.EXECUTION_MEANS);
    }

    /** 多选手段按 JSON 数组写出，避免列表接口只剩单个旧值。 */
    private static String meansToApiString(Object raw) {
        if (raw == null) {
            return null;
        }
        if (raw instanceof List<?> list) {
            try {
                return JSON.writeValueAsString(list);
            } catch (Exception ignored) {
                return asString(raw);
            }
        }
        return asString(raw);
    }

    private static String asString(Object raw) {
        return raw == null ? null : String.valueOf(raw).trim();
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

    private static Long asLong(Object raw) {
        if (raw instanceof Number number) {
            return number.longValue();
        }
        if (raw instanceof String text && StringUtils.hasText(text)) {
            try {
                return Long.parseLong(text.trim());
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    private record OwnerFilter(String kind, Long id) {
    }
}
