package cn.iocoder.yudao.module.inspection.task.service.query.impl;

import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.inspection.task.controller.admin.vo.template.InspectionTaskTemplatePageReqVO;
import cn.iocoder.yudao.module.inspection.task.dal.dataobject.schedule.InspectionTaskSchedulePolicyDO;
import cn.iocoder.yudao.module.inspection.task.dal.dataobject.task.InspectionTaskTemplateDO;
import cn.iocoder.yudao.module.inspection.task.dal.mysql.schedule.InspectionTaskSchedulePolicyMapper;
import cn.iocoder.yudao.module.inspection.task.dal.mysql.task.InspectionTaskTemplateMapper;
import cn.iocoder.yudao.module.inspection.task.service.query.InspectionTaskTemplateQueryService;
import cn.iocoder.yudao.module.inspection.task.service.query.model.InspectionTaskTemplateView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class InspectionTaskTemplateQueryServiceImpl implements InspectionTaskTemplateQueryService {

    private final InspectionTaskTemplateMapper inspectionTaskTemplateMapper;
    private final InspectionTaskSchedulePolicyMapper inspectionTaskSchedulePolicyMapper;

    @Override
    public InspectionTaskTemplateView getTemplateView(Long id) {
        InspectionTaskTemplateDO templateDO = validateTemplateExists(id);
        return buildTemplateViews(List.of(templateDO)).stream().findFirst().orElse(null);
    }

    @Override
    public List<InspectionTaskTemplateView> getTemplateViewList(InspectionTaskTemplatePageReqVO reqVO) {
        return buildTemplateViews(inspectionTaskTemplateMapper.selectListByCondition(reqVO));
    }

    @Override
    public boolean checkTemplateCodeUnique(String templateCode, Long id) {
        InspectionTaskTemplateDO exists = inspectionTaskTemplateMapper.selectByTemplateCode(templateCode);
        return exists == null || Objects.equals(exists.getId(), id);
    }

    private InspectionTaskTemplateDO validateTemplateExists(Long id) {
        InspectionTaskTemplateDO templateDO = inspectionTaskTemplateMapper.selectById(id);
        if (templateDO == null) {
            throw ServiceExceptionUtil.exception(NOT_FOUND, "巡检任务模板不存在");
        }
        return templateDO;
    }

    private List<InspectionTaskTemplateView> buildTemplateViews(List<InspectionTaskTemplateDO> templates) {
        if (templates == null || templates.isEmpty()) {
            return List.of();
        }

        // 查询关联的排期策略
        List<Long> policyIds = templates.stream()
                .map(InspectionTaskTemplateDO::getDefaultSchedulePolicyId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        Map<Long, InspectionTaskSchedulePolicyDO> policyMap = buildPolicyMap(policyIds);

        // 构建视图
        return templates.stream().map(template -> {
            InspectionTaskTemplateView view = BeanUtils.toBean(template, InspectionTaskTemplateView.class);

            // 补充排期策略名称
            InspectionTaskSchedulePolicyDO policy = policyMap.get(template.getDefaultSchedulePolicyId());
            if (policy != null) {
                view.setDefaultSchedulePolicyName(policy.getPolicyName());
            }

            // 统计对象和巡检项数量
            if (template.getInspectionContent() != null) {
                int objectCount = 0;
                int itemCount = 0;
                // 统计模板组中的对象
                if (template.getInspectionContent().getGroups() != null) {
                    for (var group : template.getInspectionContent().getGroups()) {
                        if (group.getObjects() != null) {
                            objectCount += group.getObjects().size();
                            for (var obj : group.getObjects()) {
                                if (obj.getItems() != null) {
                                    itemCount += obj.getItems().size();
                                }
                            }
                        }
                    }
                }
                // 统计自定义对象
                if (template.getInspectionContent().getCustomObjects() != null) {
                    objectCount += template.getInspectionContent().getCustomObjects().size();
                    for (var obj : template.getInspectionContent().getCustomObjects()) {
                        if (obj.getItems() != null) {
                            itemCount += obj.getItems().size();
                        }
                    }
                }
                view.setObjectCount(objectCount);
                view.setItemCount(itemCount);
            }

            return view;
        }).toList();
    }

    private Map<Long, InspectionTaskSchedulePolicyDO> buildPolicyMap(List<Long> policyIds) {
        if (policyIds == null || policyIds.isEmpty()) {
            return Map.of();
        }
        return inspectionTaskSchedulePolicyMapper.selectByIds(policyIds).stream()
                .collect(Collectors.toMap(InspectionTaskSchedulePolicyDO::getId, Function.identity(), (a, b) -> a));
    }
}
