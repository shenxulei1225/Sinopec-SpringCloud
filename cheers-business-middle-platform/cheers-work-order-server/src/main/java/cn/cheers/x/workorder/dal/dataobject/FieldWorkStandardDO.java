package cn.cheers.x.workorder.dal.dataobject;

import cn.cheers.x.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 现场作业标准 DO
 *
 * @author 工单标准服务
 */
@TableName("wo_field_work_standard")
@KeySequence("wo_field_work_standard_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldWorkStandardDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 标准编码
     */
    private String code;

    /**
     * 标准名称
     */
    private String name;

    /**
     * 版本号
     */
    private Integer versionNo;

    /**
     * 业务域范围（scope）
     */
    private String scope;

    /**
     * 步骤定义（JSON）
     */
    private String stepsJson;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 租户 ID
     */
    private Long tenantId;

}
