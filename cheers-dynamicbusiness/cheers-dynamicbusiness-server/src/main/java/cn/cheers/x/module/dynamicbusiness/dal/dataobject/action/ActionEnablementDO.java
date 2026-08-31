package cn.cheers.x.module.dynamicbusiness.dal.dataobject.action;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 动作启用行（V77 {@code dynamic_action_enablement}）。
 *
 * <p><b>负责</b>：记录某 owner（型号/实体等）启用了哪些动作 id。</p>
 * <p><b>不负责</b>：动作定义本身（见 {@code ent_action*}）；不写死业务类型码。</p>
 * <p><b>禁止</b>：读路径在启用为空时兜底返回全库动作。</p>
 */
@TableName("dynamic_action_enablement")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActionEnablementDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 启用主体种类（如 model / entity；由 API 入参约定） */
    private String ownerKind;

    /** 启用主体 id */
    private Long ownerId;

    /** 动作实体 id（ent_action*_t{tenant}.id） */
    private Long actionId;
}
