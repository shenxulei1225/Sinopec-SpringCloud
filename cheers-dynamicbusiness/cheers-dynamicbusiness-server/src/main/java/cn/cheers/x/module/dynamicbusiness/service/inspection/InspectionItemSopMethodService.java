package cn.cheers.x.module.dynamicbusiness.service.inspection;

import cn.cheers.x.module.dynamicbusiness.controller.admin.inspection.vo.InspectionItemSopMethodRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.inspection.vo.InspectionItemSopMethodUpsertReqVO;

import java.util.List;

/**
 * 检查项方法：检查项 × 执行手段 → SOP 模板。
 */
public interface InspectionItemSopMethodService {

    List<InspectionItemSopMethodRespVO> listByInspectionItemId(long inspectionItemId);

    /**
     * 按 (inspectionItemId, executionMeans) upsert；同一手段只保留一条模板引用。
     */
    Long upsert(InspectionItemSopMethodUpsertReqVO req);
}
