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
 * SOP 标准包适用范围行（V103 {@code dynamic_sop_scope_rule}）。
 *
 * <p><b>负责</b>：按 SOP 维护适用范围（分类 / 型号）。</p>
 * <p><b>不负责</b>：检查项动作细节；任务设备选择候选生成。</p>
 * <p><b>禁止</b>：在读路径补范围；跨表硬编码业务推断。</p>
 */
@TableName("dynamic_sop_scope_rule")
@KeySequence("dynamic_sop_scope_rule_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SopScopeRuleDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** SOP 实体 id */
    @TableField("sop_id")
    private Long sopId;

    /** 范围类型：CATEGORY / MODEL */
    private String scopeType;

    /** 目标 id：分类 id 或型号 id */
    private Long targetId;

    /** 同 SOP 内展示顺序 */
    private Integer sortNo;

    /** 人工备注 */
    private String note;
}
