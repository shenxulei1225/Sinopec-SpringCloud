package cn.cheers.x.module.dynamicbusiness.service.action;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.action.vo.ActionListItemRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.action.vo.ActionListReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.action.ActionEnablementDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.action.ActionEnablementMapper;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.EntityRepository;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.PhysicalColumnFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 动作库列表查询实现。
 *
 * <p><b>权威</b>：动作行来自 {@code ent_action*}；启用集来自 V77 {@code dynamic_action_enablement}。</p>
 * <p><b>禁止</b>：owner 过滤下启用为空时改拉全库；依赖 inspection 包。</p>
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
        List<ActionListItemRespVO> out = new ArrayList<>(entities.size());
        for (EntityDO entity : entities) {
            ActionListItemRespVO item = toItem(entity);
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
        vo.setExecutionMeans(asString(base.get(ActionFieldCodes.EXECUTION_MEANS)));
        vo.setParamSlots(parseStringList(base.get(ActionFieldCodes.PARAM_SLOTS_JSON)));
        vo.setChildActionIds(parseStringList(base.get(ActionFieldCodes.CHILD_ACTION_IDS_JSON)));
        vo.setComposite(readBool(base.get(ActionFieldCodes.IS_COMPOSITE)));
        return vo;
    }

    private static String normalizeMeans(String raw) {
        if (!StringUtils.hasText(raw)) {
            return null;
        }
        return raw.trim().toUpperCase(Locale.ROOT);
    }

    private static String asString(Object raw) {
        return raw == null ? null : String.valueOf(raw).trim();
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

    private static boolean readBool(Object raw) {
        if (raw instanceof Boolean b) {
            return b;
        }
        if (raw instanceof Number n) {
            return n.intValue() != 0;
        }
        if (raw instanceof String s) {
            String t = s.trim().toLowerCase(Locale.ROOT);
            return "true".equals(t) || "1".equals(t);
        }
        return false;
    }

    private static List<String> parseStringList(Object raw) {
        if (raw == null) {
            return List.of();
        }
        if (raw instanceof List<?> list) {
            List<String> out = new ArrayList<>(list.size());
            for (Object item : list) {
                if (item == null) {
                    continue;
                }
                if (item instanceof Map<?, ?> map) {
                    Object slot = map.get("slotKey");
                    if (slot == null) {
                        slot = map.get("code");
                    }
                    if (slot != null) {
                        out.add(String.valueOf(slot));
                    }
                    continue;
                }
                out.add(String.valueOf(item));
            }
            return List.copyOf(out);
        }
        if (raw instanceof String s) {
            String trimmed = s.trim();
            if (trimmed.isEmpty()) {
                return List.of();
            }
            if (trimmed.startsWith("[")) {
                try {
                    return parseStringList(JSON.readValue(trimmed, Object.class));
                } catch (Exception ignored) {
                    return List.of();
                }
            }
            return List.of(trimmed);
        }
        return List.of(Objects.toString(raw));
    }

    private record OwnerFilter(String kind, Long id) {
    }
}
