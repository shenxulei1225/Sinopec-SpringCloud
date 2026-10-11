package cn.cheers.x.intelligentanalysis.dal.dataobject;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import cn.cheers.x.framework.mybatis.core.type.JsonbStringTypeHandler;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.JdbcType;

/**
 * 平台分析能力。
 *
 * <p>code 是稳定业务身份。providerCapabilityCode 只在执行适配器中使用。</p>
 */
@TableName("intelligent_analysis_capability")
@KeySequence("intelligent_analysis_capability_id_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class AnalysisCapabilityDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String code;

    private String name;

    private String description;

    private Long providerId;

    private String providerCapabilityCode;

    private String capabilityVersion;

    @TableField(value = "supported_media", jdbcType = JdbcType.OTHER, typeHandler = JsonbStringTypeHandler.class)
    private String supportedMedia;

    @TableField(value = "parameter_schema_json", jdbcType = JdbcType.OTHER, typeHandler = JsonbStringTypeHandler.class)
    private String parameterSchemaJson;

    @TableField(value = "result_schema_json", jdbcType = JdbcType.OTHER, typeHandler = JsonbStringTypeHandler.class)
    private String resultSchemaJson;

    private Boolean enabled;
}
