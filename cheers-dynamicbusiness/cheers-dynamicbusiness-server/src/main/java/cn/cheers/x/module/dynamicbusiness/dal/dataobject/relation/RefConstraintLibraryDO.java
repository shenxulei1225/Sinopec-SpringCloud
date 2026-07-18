package cn.cheers.x.module.dynamicbusiness.dal.dataobject.relation;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * Ref 约束器库 DO
 */
@TableName("dynamic_ref_constraint_library")
@KeySequence("dynamic_ref_constraint_library_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefConstraintLibraryDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 业务类型编码
     */
    private String entityTypeCode;

    /**
     * Ref 目标类型（可选）
     */
    private String refTargetType;

    /**
     * 约束器类型
     */
    private String constraintType;

    /**
     * 约束器名称
     */
    private String constraintName;

    /**
     * 状态：0-开启，1-关闭
     */
    private Integer status;

    /**
     * 排序值
     */
    private Integer sort;
}
