package cn.cheers.x.module.dynamicbusiness.dal.dataobject.orchestration;

import cn.cheers.x.framework.mybatis.core.type.JsonbStringTypeHandler;
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
import org.apache.ibatis.type.JdbcType;

/**
 * 业务编排（5W）DO。
 *
 * <p>负责：按租户 + businessCode 持久化编排权威 JSON 与画布坐标。</p>
 * <p>不负责：功能点内部细配；不与门户 business / 目录编排混表。</p>
 */
@TableName(value = "dynamic_business_orchestration", autoResultMap = true)
@KeySequence("dynamic_business_orchestration_id_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessOrchestrationDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("business_code")
    private String businessCode;

    @TableField("business_name")
    private String businessName;

    @TableField("task_domain")
    private String taskDomain;

    /** 编排权威 JSON 文本 */
    @TableField(value = "orchestration_json", jdbcType = JdbcType.OTHER, typeHandler = JsonbStringTypeHandler.class)
    private String orchestrationJson;

    /** 画布展示 JSON（坐标等）；可空 */
    @TableField(value = "canvas_json", jdbcType = JdbcType.OTHER, typeHandler = JsonbStringTypeHandler.class)
    private String canvasJson;
}
