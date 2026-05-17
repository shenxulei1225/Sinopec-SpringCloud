package cn.iocoder.yudao.module.inspection.inspection_content.service.point;

import cn.iocoder.yudao.module.inspection.inspection_content.controller.admin.vo.point.InspectionExecutionPointItemPageReqVO;
import cn.iocoder.yudao.module.inspection.inspection_content.service.point.model.InspectionExecutionPointItemView;

import java.util.List;

/**
 * 点位检查项查询服务。
 */
public interface InspectionExecutionPointItemQueryService {

    /**
     * 获取点位检查项详情读模型。
     *
     * @param id 关联 ID
     * @return 点位检查项读模型
     */
    InspectionExecutionPointItemView getPointItemView(Long id);

    /**
     * 按条件获取点位检查项列表读模型。
     *
     * @param reqVO 查询条件
     * @return 点位检查项读模型列表
     */
    List<InspectionExecutionPointItemView> getPointItemViewList(InspectionExecutionPointItemPageReqVO reqVO);
}
