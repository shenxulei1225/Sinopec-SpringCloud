package cn.iocoder.yudao.module.emergency.convert.plan;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.emergency.controller.admin.plan.vo.EmergencyPlanAttachmentBaseVO;
import cn.iocoder.yudao.module.emergency.controller.admin.plan.vo.EmergencyPlanAttachmentRespVO;
import cn.iocoder.yudao.module.emergency.controller.admin.plan.vo.EmergencyPlanCreateReqVO;
import cn.iocoder.yudao.module.emergency.controller.admin.plan.vo.EmergencyPlanRespVO;
import cn.iocoder.yudao.module.emergency.controller.admin.plan.vo.EmergencyPlanStepBaseVO;
import cn.iocoder.yudao.module.emergency.controller.admin.plan.vo.EmergencyPlanStepRespVO;
import cn.iocoder.yudao.module.emergency.controller.admin.plan.vo.EmergencyPlanUpdateReqVO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.plan.EmergencyPlanAttachmentDO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.plan.EmergencyPlanDO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.plan.EmergencyPlanStepDO;
import org.mapstruct.Mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE, componentModel = "spring")
public interface EmergencyPlanConvert {

    EmergencyPlanDO convert(EmergencyPlanCreateReqVO bean);

    EmergencyPlanDO convert(EmergencyPlanUpdateReqVO bean);

    EmergencyPlanRespVO convert(EmergencyPlanDO bean);

    List<EmergencyPlanRespVO> convertList(List<EmergencyPlanDO> list);

    PageResult<EmergencyPlanRespVO> convertPage(PageResult<EmergencyPlanDO> page);

    EmergencyPlanStepDO convert(EmergencyPlanStepBaseVO bean);

    EmergencyPlanStepRespVO convert(EmergencyPlanStepDO bean);

    EmergencyPlanAttachmentDO convert(EmergencyPlanAttachmentBaseVO bean);

    EmergencyPlanAttachmentRespVO convert(EmergencyPlanAttachmentDO bean);

    // ========== 聚合对象转换 ==========

    default EmergencyPlanRespVO convertFull(EmergencyPlanDO plan,
                                            List<EmergencyPlanStepDO> steps,
                                            List<EmergencyPlanAttachmentDO> attachments) {
        EmergencyPlanRespVO result = convert(plan);
        // 转换并构建步骤树
        if (steps != null && !steps.isEmpty()) {
            result.setSteps(buildStepTree(steps));
        }
        // 转换附件
        if (attachments != null && !attachments.isEmpty()) {
            result.setAttachments(convertAttachmentList(attachments));
        }
        return result;
    }

    default List<EmergencyPlanStepRespVO> buildStepTree(List<EmergencyPlanStepDO> allSteps) {
        // 先全部转为 VO
        List<EmergencyPlanStepRespVO> allStepVOs = allSteps.stream()
                .map(this::convert)
                .collect(Collectors.toList());

        // 按 ID 建立映射
        Map<Long, EmergencyPlanStepRespVO> map = allStepVOs.stream()
                .collect(Collectors.toMap(EmergencyPlanStepRespVO::getId, vo -> vo));

        List<EmergencyPlanStepRespVO> roots = new ArrayList<>();
        for (EmergencyPlanStepRespVO vo : allStepVOs) {
            Long parentId = vo.getParentId();
            if (parentId == null || parentId == 0) {
                roots.add(vo);
            } else {
                EmergencyPlanStepRespVO parent = map.get(parentId);
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(vo);
                } else {
                    // 孤儿节点，作为根处理或记录日志
                    roots.add(vo);
                }
            }
        }
        // 可选：对 roots 和 children 按 stepOrder 排序
        return roots;
    }

    default List<EmergencyPlanAttachmentRespVO> convertAttachmentList(List<EmergencyPlanAttachmentDO> list) {
        return list.stream().map(this::convert).collect(Collectors.toList());
    }
}










