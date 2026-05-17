package cn.iocoder.yudao.module.inspection.task.service.task;

import cn.iocoder.yudao.module.inspection.task.controller.admin.vo.template.InspectionTaskTemplateCreateReqVO;
import cn.iocoder.yudao.module.inspection.task.controller.admin.vo.template.InspectionTaskTemplateSaveFromTaskReqVO;
import cn.iocoder.yudao.module.inspection.task.controller.admin.vo.template.InspectionTaskTemplateUpdateReqVO;
import cn.iocoder.yudao.module.inspection.task.dal.dataobject.task.InspectionTaskTemplateDO;

public interface InspectionTaskTemplateService {

    Long createTemplate(InspectionTaskTemplateCreateReqVO reqVO);

    Long saveTemplateFromTask(InspectionTaskTemplateSaveFromTaskReqVO reqVO);

    void updateTemplate(InspectionTaskTemplateUpdateReqVO reqVO);

    void enableTemplate(Long id);

    void disableTemplate(Long id);

    void deleteTemplate(Long id);

    InspectionTaskTemplateDO getTemplate(Long id);
}
