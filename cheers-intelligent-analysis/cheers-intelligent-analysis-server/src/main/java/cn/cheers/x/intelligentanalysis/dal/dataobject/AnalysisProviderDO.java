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
 * 一项智能分析服务接入。
 *
 * <p>本表持有厂商连接配置。动作和分析任务不得复制服务地址或厂商算法编号。</p>
 */
@TableName("intelligent_analysis_provider")
@KeySequence("intelligent_analysis_provider_id_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class AnalysisProviderDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String code;

    private String name;

    private String adapterType;

    private String baseUrl;

    private String credentialRef;

    @TableField(value = "config_json", jdbcType = JdbcType.OTHER, typeHandler = JsonbStringTypeHandler.class)
    private String configJson;

    private Boolean enabled;
}
