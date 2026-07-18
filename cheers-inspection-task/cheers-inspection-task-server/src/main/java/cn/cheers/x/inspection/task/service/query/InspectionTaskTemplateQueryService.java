package cn.cheers.x.inspection.task.service.query;

import cn.cheers.x.inspection.task.controller.admin.vo.template.InspectionTaskTemplatePageReqVO;
import cn.cheers.x.inspection.task.service.query.model.InspectionTaskTemplateView;

import java.util.List;

/**
 * 巡检任务模板查询服务。
 */
public interface InspectionTaskTemplateQueryService {

    /**
     * 获取任务模板详情。
     *
     * @param id 模板ID
     * @return 模板视图
     */
    InspectionTaskTemplateView getTemplateView(Long id);

    /**
     * 获取任务模板列表。
     *
     * @param reqVO 查询条件
     * @return 模板视图列表
     */
    List<InspectionTaskTemplateView> getTemplateViewList(InspectionTaskTemplatePageReqVO reqVO);

    /**
     * 校验模板编码唯一性。
     *
     * @param templateCode 模板编码
     * @param id 排除的模板ID（用于更新时校验）
     * @return 是否唯一
     */
    boolean checkTemplateCodeUnique(String templateCode, Long id);
}
