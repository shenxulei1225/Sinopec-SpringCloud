package cn.cheers.x.module.dynamicbusiness.service.inspection;

import cn.cheers.x.module.dynamicbusiness.controller.admin.inspection.vo.EquipmentInspectionSopBindingRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.inspection.vo.EquipmentInspectionSopCreateInstanceReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.inspection.vo.EquipmentInspectionSopUpsertReqVO;

import java.util.List;

/**
 * 设备检查绑定：设备 + 检查项 + 手段 → SOP 实例。
 */
public interface EquipmentInspectionSopBindingService {

    EquipmentInspectionSopBindingRespVO getBinding(long equipmentId, long inspectionItemId, String executionMeans);

    List<EquipmentInspectionSopBindingRespVO> listByEquipmentAndItem(long equipmentId, long inspectionItemId);

    void upsertBinding(EquipmentInspectionSopUpsertReqVO req);

    /**
     * 从 SOP 模板新建实例并 upsert 绑定；始终新建 {@code is_template=false} 行。
     *
     * @return 新 SOP 实例 id
     */
    long createInstanceFromTemplate(EquipmentInspectionSopCreateInstanceReqVO req);
}
