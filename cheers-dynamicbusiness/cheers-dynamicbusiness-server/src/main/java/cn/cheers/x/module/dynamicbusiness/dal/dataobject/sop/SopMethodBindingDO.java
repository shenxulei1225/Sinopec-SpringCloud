package cn.cheers.x.module.dynamicbusiness.dal.dataobject.sop;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * SOP 方法选用行（V80 {@code dynamic_sop_method_binding}）。
 *
 * <p><b>负责</b>：按「对象类型 + 对象 id + 维度键/值」存指向的标准 SOP id。</p>
 * <p><b>不负责</b>：宿主侧实例绑定；业务类型码含义（由调用方传入）。</p>
 * <p><b>禁止</b>：读路径静默补方法行；在本表硬编码某一业务的 subject_type。</p>
 */
@TableName("dynamic_sop_method_binding")
@KeySequence("dynamic_sop_method_binding_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SopMethodBindingDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 对象实体类型码（请求入参） */
    private String subjectType;

    private Long subjectId;

    /** 维度字段名（如执行手段字段名） */
    private String dimensionKey;

    private String dimensionValue;

    /** 标准 SOP 实体 id（底层列名沿用 sop_template_id） */
    @TableField("sop_template_id")
    private Long sopId;
}
