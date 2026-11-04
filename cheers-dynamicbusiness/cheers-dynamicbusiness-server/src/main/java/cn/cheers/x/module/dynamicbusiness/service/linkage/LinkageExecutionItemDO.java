package cn.cheers.x.module.dynamicbusiness.service.linkage;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 一次联动中一对设备和动作的调用结果。
 */
@TableName("dynamic_linkage_execution_item")
@Data
@EqualsAndHashCode(callSuper = true)
public class LinkageExecutionItemDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long linkageExecutionId;
    /** 联动方式里这一对的位置。不是绑定子实体编号。 */
    private Long pairIndex;
    private Long actionExecutionId;
    private String status;
    private String lastError;
}
