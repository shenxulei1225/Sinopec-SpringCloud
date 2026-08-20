package cn.cheers.x.module.dynamicbusiness.dal.dataobject.datamgmt;

import cn.cheers.x.framework.mybatis.core.type.JsonbMapTypeHandler;
import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.util.Map;

@TableName(value = "dm_five_w_orchestration", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DmFiveWOrchestrationDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String entityTypeCode;

    private Boolean enabled;

    private String whatMode;

    @TableField(typeHandler = JsonbMapTypeHandler.class)
    private Map<String, Object> whatConfig;

    private String howMode;

    @TableField(typeHandler = JsonbMapTypeHandler.class)
    private Map<String, Object> howConfig;

    /** 当前对象从哪来：LIST_ROW=点列表这一行；CATEGORY_NODE=点树上这个节点 */
    private String objectPickFrom;
}
