package cn.cheers.x.module.dynamicbusiness.dal.dataobject.analysis;

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

import java.time.LocalDateTime;

/**
 * 一次智能分析任务。
 *
 * <p>创建时冻结能力和参数。回调只更新本任务，不回读动作当前配置。</p>
 */
@TableName("intelligent_analysis_job")
@KeySequence("intelligent_analysis_job_id_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class AnalysisJobDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String jobNo;

    private Long capabilityId;

    private String capabilityCodeSnapshot;

    private String capabilityVersionSnapshot;

    private Long providerIdSnapshot;

    private String inputType;

    private String inputRef;

    @TableField(value = "parameter_snapshot", jdbcType = JdbcType.OTHER, typeHandler = JsonbStringTypeHandler.class)
    private String parameterSnapshot;

    private String businessRefType;

    private String businessRefId;

    private String status;

    private String providerJobId;

    @TableField(value = "normalized_result_json", jdbcType = JdbcType.OTHER, typeHandler = JsonbStringTypeHandler.class)
    private String normalizedResultJson;

    @TableField(value = "raw_result_json", jdbcType = JdbcType.OTHER, typeHandler = JsonbStringTypeHandler.class)
    private String rawResultJson;

    private String failureMessage;

    private LocalDateTime acceptedTime;

    private LocalDateTime completedTime;
}
