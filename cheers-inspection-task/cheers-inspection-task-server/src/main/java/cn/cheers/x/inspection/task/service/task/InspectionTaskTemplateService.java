package cn.cheers.x.inspection.task.service.task;

import cn.cheers.x.inspection.task.controller.admin.vo.template.InspectionTaskTemplateCreateReqVO;
import cn.cheers.x.inspection.task.controller.admin.vo.template.InspectionTaskTemplateSaveFromTaskReqVO;
import cn.cheers.x.inspection.task.controller.admin.vo.template.InspectionTaskTemplateUpdateReqVO;
import cn.cheers.x.inspection.task.dal.dataobject.task.InspectionTaskTemplateDO;

public interface InspectionTaskTemplateService {

    Long createTemplate(InspectionTaskTemplateCreateReqVO reqVO);

    Long saveTemplateFromTask(InspectionTaskTemplateSaveFromTaskReqVO reqVO);

    void updateTemplate(InspectionTaskTemplateUpdateReqVO reqVO);

    void enableTemplate(Long id);

    void disableTemplate(Long id);

    void deleteTemplate(Long id);

    InspectionTaskTemplateDO getTemplate(Long id);
}
