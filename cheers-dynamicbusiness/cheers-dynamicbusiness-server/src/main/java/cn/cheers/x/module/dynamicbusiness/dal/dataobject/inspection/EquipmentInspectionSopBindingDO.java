package cn.cheers.x.module.dynamicbusiness.dal.dataobject.inspection;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 设备检查绑定（V75 {@code dynamic_equipment_inspection_sop_binding}）。
 *
 * <p><b>权威</b>：被检设备 + 检查项 + 执行手段 → 独占 SOP 实例 id。</p>
 * <p><b>禁止</b>：多设备共用同一 {@code sopInstanceId}；读路径静默换实例。</p>
 */
@TableName("dynamic_equipment_inspection_sop_binding")
@KeySequence("dynamic_equipment_inspection_sop_binding_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentInspectionSopBindingDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long equipmentId;

    private Long inspectionItemId;

    private String executionMeans;

    /** SOP 实例 id（is_template=false） */
    private Long sopInstanceId;
}
