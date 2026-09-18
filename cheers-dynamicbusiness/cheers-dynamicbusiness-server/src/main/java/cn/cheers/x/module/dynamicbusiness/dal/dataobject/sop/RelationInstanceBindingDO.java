package cn.cheers.x.module.dynamicbusiness.dal.dataobject.sop;

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
 * 通用宿主-对象-维度-目标绑定行（V109 {@code dynamic_relation_instance_binding}）。
 *
 * <p><b>负责</b>：按「宿主 + 对象 + 维度」存独占目标实体。</p>
 * <p><b>不负责</b>：方法选用（模板）；业务类型码含义。</p>
 * <p><b>禁止</b>：多宿主共用同一实例；读路径静默换实例或补绑定。</p>
 */
@TableName("dynamic_relation_instance_binding")
@KeySequence("dynamic_sop_instance_binding_id_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelationInstanceBindingDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 宿主实体类型码（请求入参） */
    private String hostType;

    private Long hostId;

    /** 对象实体类型码（请求入参） */
    private String subjectType;

    private Long subjectId;

    private String dimensionKey;

    private String dimensionValue;

    /** 绑定目标实体类型码（调用方传入） */
    private String targetType;

    /** 绑定目标实体 id */
    private Long targetId;
}
