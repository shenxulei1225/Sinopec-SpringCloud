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
 * 检查项—SOP 模板方法行（V44 {@code dynamic_inspection_item_sop}）。
 *
 * <p><b>权威</b>：检查项 × 执行手段 → 默认引用的 SOP 模板 id（{@code sopId}，须 {@code is_template=true}）。</p>
 * <p><b>不负责</b>：设备侧实例绑定（见 {@link EquipmentInspectionSopBindingDO}）。</p>
 */
@TableName("dynamic_inspection_item_sop")
@KeySequence("dynamic_inspection_item_sop_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InspectionItemSopMethodDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long inspectionItemId;

    /** SOP 模板实体 id（ent_sop；业务上须为模板行） */
    private Long sopId;

    /** 执行手段：MANUAL / UAV / ROBOT / FIXED_CAMERA */
    private String executionMeans;

    private Integer sort;
}
