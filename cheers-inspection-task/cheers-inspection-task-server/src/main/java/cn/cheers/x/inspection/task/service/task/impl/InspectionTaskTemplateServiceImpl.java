package cn.cheers.x.inspection.task.service.task.impl;

import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.framework.common.util.object.BeanUtils;
import cn.cheers.x.inspection.task.controller.admin.vo.template.InspectionTaskTemplateCreateReqVO;
import cn.cheers.x.inspection.task.controller.admin.vo.template.InspectionTaskTemplateSaveFromTaskReqVO;
import cn.cheers.x.inspection.task.controller.admin.vo.template.InspectionTaskTemplateUpdateReqVO;
import cn.cheers.x.inspection.task.dal.dataobject.task.InspectionTaskDO;
import cn.cheers.x.inspection.task.dal.dataobject.task.InspectionTaskTemplateDO;
import cn.cheers.x.inspection.task.dal.mysql.task.InspectionTaskMapper;
import cn.cheers.x.inspection.task.dal.mysql.task.InspectionTaskTemplateMapper;
import cn.cheers.x.inspection.task.service.task.InspectionTaskTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static cn.cheers.x.framework.common.exception.enums.GlobalErrorCodeConstants.BAD_REQUEST;

@Service
@RequiredArgsConstructor
public class InspectionTaskTemplateServiceImpl implements InspectionTaskTemplateService {

    private final InspectionTaskTemplateMapper inspectionTaskTemplateMapper;
    private final InspectionTaskMapper inspectionTaskMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createTemplate(InspectionTaskTemplateCreateReqVO reqVO) {
        validateTemplateCodeUnique(reqVO.getTemplateCode(), null);
        validateParentTemplate(reqVO.getParentId());
        InspectionTaskTemplateDO templateDO = BeanUtils.toBean(reqVO, InspectionTaskTemplateDO.class);
        if (templateDO.getEnabled() == null) {
            templateDO.setEnabled(Boolean.TRUE);
        }
        inspectionTaskTemplateMapper.insert(templateDO);
        return templateDO.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveTemplateFromTask(InspectionTaskTemplateSaveFromTaskReqVO reqVO) {
        InspectionTaskDO taskDO = validateTaskExists(reqVO.getTaskId());
        validateTemplateCodeUnique(reqVO.getTemplateCode(), null);

        InspectionTaskTemplateDO templateDO = BeanUtils.toBean(reqVO, InspectionTaskTemplateDO.class);
        templateDO.setTemplateName(taskDO.getTaskName());
        templateDO.setInspectionContent(taskDO.getInspectionContent());
        templateDO.setDefaultSchedulePolicyId(taskDO.getSchedulePolicyId());
        // Note: resourcePolicy is not copied because InspectionTaskTemplateDO does not have this field
        templateDO.setEnabled(Boolean.TRUE);
        inspectionTaskTemplateMapper.insert(templateDO);
        return templateDO.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTemplate(InspectionTaskTemplateUpdateReqVO reqVO) {
        validateTemplateExists(reqVO.getId());
        validateTemplateCodeUnique(reqVO.getTemplateCode(), reqVO.getId());
        validateParentTemplate(reqVO.getParentId());

        InspectionTaskTemplateDO updateDO = BeanUtils.toBean(reqVO, InspectionTaskTemplateDO.class);
        inspectionTaskTemplateMapper.updateById(updateDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enableTemplate(Long id) {
        validateTemplateExists(id);
        InspectionTaskTemplateDO update = new InspectionTaskTemplateDO();
        update.setId(id);
        update.setEnabled(Boolean.TRUE);
        inspectionTaskTemplateMapper.updateById(update);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disableTemplate(Long id) {
        validateTemplateExists(id);
        InspectionTaskTemplateDO update = new InspectionTaskTemplateDO();
        update.setId(id);
        update.setEnabled(Boolean.FALSE);
        inspectionTaskTemplateMapper.updateById(update);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTemplate(Long id) {
        validateTemplateExists(id);
        inspectionTaskTemplateMapper.deleteById(id);
    }

    @Override
    public InspectionTaskTemplateDO getTemplate(Long id) {
        return inspectionTaskTemplateMapper.selectById(id);
    }

    // ==================== 私有方法 ====================

    private InspectionTaskTemplateDO validateTemplateExists(Long id) {
        InspectionTaskTemplateDO template = inspectionTaskTemplateMapper.selectById(id);
        if (template == null) {
            throw ServiceExceptionUtil.exception(BAD_REQUEST, "模板不存在");
        }
        return template;
    }

    private InspectionTaskDO validateTaskExists(Long id) {
        InspectionTaskDO task = inspectionTaskMapper.selectById(id);
        if (task == null) {
            throw ServiceExceptionUtil.exception(BAD_REQUEST, "任务不存在");
        }
        return task;
    }

    private void validateTemplateCodeUnique(String templateCode, Long id) {
        if (templateCode == null) {
            return;
        }
        InspectionTaskTemplateDO existing = inspectionTaskTemplateMapper.selectByTemplateCode(templateCode);
        if (existing != null && !Objects.equals(existing.getId(), id)) {
            throw ServiceExceptionUtil.exception(BAD_REQUEST, "模板编码已存在");
        }
    }

    private void validateParentTemplate(Long parentId) {
        if (parentId == null) {
            return;
        }
        validateTemplateExists(parentId);
    }
}
