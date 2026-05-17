package cn.iocoder.yudao.module.inspection.inspection_content.dal.dataobject.point;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 执行点位 DO。
 */
@TableName("inspection_execution_point")
@Data
@EqualsAndHashCode(callSuper = true)
public class InspectionExecutionPointDO extends BaseDO {

    /**
     * 主键 ID。
     */
    @TableId
    private Long id;
    /**
     * 点位编码。
     */
    private String pointCode;
    /**
     * 点位名称。
     */
    private String pointName;
    /**
     * 所属站场 ID。
     */
    private Long siteId;
    /**
     * 所属巡检对象 ID。
     */
    private Long objectId;
    /**
     * 经度。
     */
    private BigDecimal longitude;
    /**
     * 纬度。
     */
    private BigDecimal latitude;
    /**
     * 高度。
     */
    private BigDecimal height;
    /**
     * 偏航角。
     */
    private BigDecimal yaw;
    /**
     * 俯仰角。
     */
    private BigDecimal pitch;
    /**
     * 横滚角。
     */
    private BigDecimal roll;
    /**
     * 适配设备类型。
     */
    private String deviceType;
    /**
     * 排序权重。
     */
    private Integer weight;
    /**
     * 状态。
     */
    private String status;
    /**
     * 备注。
     */
    private String remark;
}
