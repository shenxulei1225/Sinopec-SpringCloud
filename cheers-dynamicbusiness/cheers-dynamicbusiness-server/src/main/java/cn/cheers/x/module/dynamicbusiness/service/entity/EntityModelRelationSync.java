package cn.cheers.x.module.dynamicbusiness.service.entity;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelFieldAssignmentDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.field.FieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelFieldAssignmentMapper;
import cn.cheers.x.module.dynamicbusiness.enums.field.FieldTypeEnum;
import cn.cheers.x.module.dynamicbusiness.service.model.relation.ModelModelRelationService;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 详情上的模型关联：按字段方向读写型号—型号表。
 *
 * <p>管什么：保存前从即将落库的自定义字段里拿掉这些键；请求里带了该键时，按方向整份替换这一侧的型号名单。
 * 打开详情时把名单放进返回值，只为这一格能画出来。</p>
 * <p>不负责：挂载点、设备上级、引用选某一台设备。</p>
 * <p>禁止：把名单写进设备自定义字段；方向空着时按字段名猜。</p>
 */
@Component
public class EntityModelRelationSync {

    /** 这个型号是来源，名单是它指向的目标型号。 */
    public static final String DIRECTION_OUT = "MODEL_RELATION_OUT";
    /** 这个型号是目标，名单是指向它的来源型号。 */
    public static final String DIRECTION_IN = "MODEL_RELATION_IN";

    @Resource
    private ModelFieldAssignmentMapper modelFieldAssignmentMapper;
    @Resource
    private FieldMapper fieldMapper;
    @Resource
    private ModelModelRelationService modelModelRelationService;

    /**
     * 从即将写入设备的自定义字段里去掉模型关联的键。
     * 调用前先另留一份提交值，替换名单用那一份。
     */
    public void stripPersisted(Long modelId, Map<String, Object> customFields) {
        if (customFields == null) {
            return;
        }
        for (FieldDO field : modelRelationFields(modelId)) {
            customFields.remove(field.getCode());
        }
    }

    /**
     * 请求里出现的模型关联键，按字段方向整份替换这一侧。
     * 请求没带这个键时不动已有关联。方向没写则拒绝。
     */
    public void syncSubmitted(String entityTypeCode, Long modelId, Map<String, Object> submitted) {
        if (submitted == null || modelId == null || !StringUtils.hasText(entityTypeCode)) {
            return;
        }
        for (FieldDO field : modelRelationFields(modelId)) {
            if (!submitted.containsKey(field.getCode())) {
                continue;
            }
            String direction = field.getSemanticType() == null ? "" : field.getSemanticType().trim();
            List<Long> ids = parseModelIds(submitted.get(field.getCode()));
            if (DIRECTION_OUT.equals(direction)) {
                modelModelRelationService.replaceTargets(entityTypeCode, modelId, ids);
            } else if (DIRECTION_IN.equals(direction)) {
                modelModelRelationService.replaceSources(entityTypeCode, modelId, ids);
            } else {
                throw new ServiceException(400, "模型关联字段缺少方向");
            }
        }
    }

    /**
     * 详情显示用的型号编号。不写库。方向没写的字段不出现在结果里。
     */
    public Map<String, List<Long>> readForModel(String entityTypeCode, Long modelId) {
        Map<String, List<Long>> values = new LinkedHashMap<>();
        if (modelId == null || !StringUtils.hasText(entityTypeCode)) {
            return values;
        }
        for (FieldDO field : modelRelationFields(modelId)) {
            String direction = field.getSemanticType() == null ? "" : field.getSemanticType().trim();
            if (DIRECTION_OUT.equals(direction)) {
                values.put(field.getCode(), modelModelRelationService.listTargetModelIds(
                        modelId, entityTypeCode, entityTypeCode));
            } else if (DIRECTION_IN.equals(direction)) {
                values.put(field.getCode(), modelModelRelationService.listSourceModelIds(
                        modelId, entityTypeCode, entityTypeCode));
            }
        }
        return values;
    }

    private List<FieldDO> modelRelationFields(Long modelId) {
        if (modelId == null) {
            return List.of();
        }
        List<ModelFieldAssignmentDO> assignments = modelFieldAssignmentMapper.selectByModelId(modelId);
        if (assignments == null || assignments.isEmpty()) {
            return List.of();
        }
        List<FieldDO> fields = new ArrayList<>();
        for (ModelFieldAssignmentDO assignment : assignments) {
            if (assignment.getFieldId() == null) {
                continue;
            }
            FieldDO field = fieldMapper.selectById(assignment.getFieldId());
            if (field == null || !StringUtils.hasText(field.getCode())) {
                continue;
            }
            if (!FieldTypeEnum.MODEL_RELATION.getCode().equalsIgnoreCase(field.getType())) {
                continue;
            }
            fields.add(field);
        }
        return fields;
    }

    static List<Long> parseModelIds(Object value) {
        if (value == null) {
            return List.of();
        }
        if (value instanceof Number number) {
            return List.of(requiredId(number.longValue()));
        }
        if (value instanceof Collection<?> collection) {
            List<Long> ids = new ArrayList<>();
            for (Object item : collection) {
                ids.add(requiredId(readId(item)));
            }
            return ids;
        }
        if (value instanceof String text) {
            String trimmed = text.trim();
            if (!StringUtils.hasText(trimmed)) {
                return List.of();
            }
            if (trimmed.startsWith("[")) {
                if (!JSONUtil.isTypeJSONArray(trimmed)) {
                    throw new ServiceException(400, "模型关联的值不是型号编号列表");
                }
                JSONArray array = JSONUtil.parseArray(trimmed);
                List<Long> ids = new ArrayList<>();
                for (Object item : array) {
                    ids.add(requiredId(readId(item)));
                }
                return ids;
            }
            return List.of(requiredId(readId(trimmed)));
        }
        throw new ServiceException(400, "模型关联的值不是型号编号列表");
    }

    private static long readId(Object item) {
        if (item instanceof Number number) {
            return number.longValue();
        }
        if (item instanceof String text && StringUtils.hasText(text)) {
            try {
                return Long.parseLong(text.trim());
            } catch (NumberFormatException ex) {
                throw new ServiceException(400, "模型关联的值不是型号编号列表");
            }
        }
        throw new ServiceException(400, "模型关联的值不是型号编号列表");
    }

    private static long requiredId(long id) {
        if (id <= 0) {
            throw new ServiceException(400, "型号编号不正确");
        }
        return id;
    }
}
