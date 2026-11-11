package cn.cheers.x.module.dynamicbusiness.dal.dataobject.tenantcapability;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 某个业务租户已开通的业务能力包一行。
 * openedPageCodes 是这一包里勾上的页面编码，逗号分隔。空值表示这一包的全部页面。
 * 目录仍跟包走，不在这里按页面拆。
 */
@TableName("tenant_capability_pack")
@KeySequence("tenant_capability_pack_id_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class TenantCapabilityPackDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 业务能力包编码 */
    private String packCode;

    /**
     * 本租户在这一包下开通的页面编码，逗号分隔。
     * null：这一包的全部页面（尚未按页面保存过）。
     * 空字符串：包已开通，但没有单独页面（只开通数据目录）。
     */
    private String openedPageCodes;
}
