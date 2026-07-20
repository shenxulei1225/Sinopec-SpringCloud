package cn.iocoder.yudao.module.emergency.dal.dataobject.plan;
import com.baomidou.mybatisplus.annotation.IdType;
import cn.iocoder.yudao.module.emergency.dal.dataobject.EmergencyBaseDO;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import cn.iocoder.yudao.module.emergency.framework.mybatis.PostgreSQLJsonbTypeHandler;
import lombok.*;

import java.util.List;
import java.util.Map;

/**
 * 应急预案 DO
 *
 * @author 芋道源码
 */
@TableName(value = "emergency_plan", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyPlanDO extends EmergencyBaseDO {

    /**
     * 编号
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    /**
     * 预案编号（唯一）
     */
    private String planNo;
    /**
     * 预案名称
     */
    private String planName;
    /**
     * 预案类型
     *
     * 枚举 {@link cn.iocoder.yudao.module.emergency.enums.dict.EmergencyDictTypeConstants#EMERGENCY_PLAN_TYPE}
     */
    private Integer planType;
    /**
     * 预案级别列表（JSONB数组）
     * 存储该预案支持的所有级别，如 ["I", "II", "III"]
     * 引用数据字典：emergency_plan_level，可选字段
     */
    @TableField(typeHandler = PostgreSQLJsonbTypeHandler.class)
    private List<String> planLevels;
    /**
     * 预案分组ID
     * 引用system_category表，business_type='emergency_plan_group'，可选字段
     */
    private Long planGroupId;
    /**
     * 自定义配置/扩展字段（JSONB）
     */
    @TableField(typeHandler = PostgreSQLJsonbTypeHandler.class)
    private Map<String, Object> customConfig;
    /**
     * 审核信息（JSONB）
     */
    @TableField(typeHandler = PostgreSQLJsonbTypeHandler.class)
    private Map<String, Object> reviewInfo;
    /**
     * 状态
     *
     * 枚举 {@link cn.iocoder.yudao.module.emergency.enums.dict.EmergencyDictTypeConstants#EMERGENCY_PLAN_STATUS}
     * DRAFT - 草稿
     * PUBLISHED - 已发布
     * DISABLED - 已停用
     */
    private String status;

    /**
     * 版本号（可选，用于版本管理）
     * 预案版本不等于预案修改，不是每次修改预案内容都会生成新版本，而是经过调试后统一发布一个新版本预案
     */
    private String versionNumber;

    /**
     * 版本是否锁定（锁定后创建响应时使用快照）
     * true - 版本已锁定，创建响应时保存预案快照
     * false - 版本未锁定，创建响应时实时引用最新版本
     */
    private Boolean isVersionLocked;

}

