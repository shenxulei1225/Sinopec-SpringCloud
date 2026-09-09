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
 * SOP 标准检查项包行（V103 {@code dynamic_sop_item_pack}）。
 *
 * <p><b>负责</b>：维护 SOP 下标准检查项引用与执行顺序。</p>
 * <p><b>不负责</b>：检查项动作树正文；设备实例候选落地。</p>
 * <p><b>禁止</b>：在读路径补缺失检查项；在此表承载动作参数。</p>
 */
@TableName("dynamic_sop_item_pack")
@KeySequence("dynamic_sop_item_pack_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SopItemPackDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** SOP 实体 id */
    @TableField("sop_id")
    private Long sopId;

    /** 检查项实体 id（inspection_item） */
    private Long inspectionItemId;

    /** 是否必做 */
    private Boolean required;

    /** 同 SOP 内执行推荐顺序 */
    private Integer sortNo;

    /** 人工备注 */
    private String note;
}
