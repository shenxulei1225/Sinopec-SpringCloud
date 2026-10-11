package cn.cheers.x.module.dynamicbusiness.service.action;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.intelligentanalysis.api.IntelligentAnalysisApi;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelFieldAssignmentDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelFieldAssignmentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * 动作保存的提交校验。
 *
 * <p>动作模型是执行类型权威。本校验把模型对应的执行类型写入物理投影，
 * 供跨模块任务快照使用；不接受页面另传一份动作类型，也不按名称猜类型。
 * 智能分析能力是否可用只问智能分析服务，不读分析表。</p>
 */
@Component
@RequiredArgsConstructor
public class ActionCreationValidator {

    private static final String ACTION_ENTITY_TYPE = "action";
    private static final String INTELLIGENT_ANALYSIS = "INTELLIGENT_ANALYSIS";

    private final IntelligentAnalysisApi intelligentAnalysisApi;
    private final ModelMapper modelMapper;
    private final ModelFieldAssignmentMapper modelFieldAssignmentMapper;

    public void validate(String entityTypeCode, Long modelId, Map<String, Object> physicalColumns) {
        if (!ACTION_ENTITY_TYPE.equalsIgnoreCase(entityTypeCode)) {
            return;
        }
        ModelDO model = modelMapper.selectById(modelId);
        if (model == null
                || Boolean.TRUE.equals(model.getDeleted())
                || !ACTION_ENTITY_TYPE.equalsIgnoreCase(model.getEntityTypeCode())) {
            throw new ServiceException(400, "请选择动作目录中的有效模型");
        }
        ModelFieldAssignmentDO domainAssignment = modelFieldAssignmentMapper
                .selectByModelCodeAndFieldCode(model.getCode(), ActionFieldCodes.ACTION_DOMAIN);
        String domain = trimmed(domainAssignment == null ? null : domainAssignment.getDefaultValue());
        if (domain == null && !"action_group".equals(model.getCode())) {
            throw new ServiceException(400, "动作模型没有配置字段库「动作类型」的默认值");
        }
        physicalColumns.put(ActionFieldCodes.ACTION_DOMAIN, domain);
        Long capabilityId = longValue(physicalColumns.get(ActionFieldCodes.ANALYSIS_CAPABILITY_ID));
        if (INTELLIGENT_ANALYSIS.equals(domain)) {
            if (capabilityId == null) {
                throw new ServiceException(400, "创建智能分析动作时必须选择分析能力");
            }
            CommonResult<Boolean> available = intelligentAnalysisApi.requireAvailable(capabilityId);
            if (available == null || !available.isSuccess()) {
                Integer code = available == null ? null : available.getCode();
                throw new ServiceException(code == null ? 500 : code,
                        available != null && StringUtils.hasText(available.getMsg())
                                ? available.getMsg()
                                : "分析服务没有确认这项能力可用");
            }
        } else {
            if (capabilityId != null) {
                throw new ServiceException(400, "只有智能分析动作可以选择分析能力");
            }
            physicalColumns.put(ActionFieldCodes.ANALYSIS_CAPABILITY_ID, null);
        }
    }

    private static String trimmed(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim().toUpperCase();
    }

    private static Long longValue(Object value) {
        if (value instanceof Number number) {
            return number.longValue();
        }
        try {
            String text = value == null ? "" : String.valueOf(value).trim();
            return text.isBlank() ? null : Long.parseLong(text);
        } catch (NumberFormatException ex) {
            throw new ServiceException(400, "智能分析能力编号无效");
        }
    }
}
