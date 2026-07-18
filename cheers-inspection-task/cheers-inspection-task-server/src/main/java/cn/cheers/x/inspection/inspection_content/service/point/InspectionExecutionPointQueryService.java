package cn.cheers.x.inspection.inspection_content.service.point;

import cn.cheers.x.inspection.inspection_content.controller.admin.vo.point.InspectionExecutionPointPageReqVO;
import cn.cheers.x.inspection.inspection_content.service.point.model.InspectionExecutionPointView;

import java.util.List;

/**
 * 执行点位查询服务。
 */
public interface InspectionExecutionPointQueryService {

    /**
     * 获取执行点位详情读模型。
     *
     * @param pointId 点位 ID
     * @return 执行点位读模型
     */
    InspectionExecutionPointView getPointView(Long pointId);

    /**
     * 按条件获取执行点位列表读模型。
     *
     * @param reqVO 查询条件
     * @return 执行点位读模型列表
     */
    List<InspectionExecutionPointView> getPointViewList(InspectionExecutionPointPageReqVO reqVO);
}
