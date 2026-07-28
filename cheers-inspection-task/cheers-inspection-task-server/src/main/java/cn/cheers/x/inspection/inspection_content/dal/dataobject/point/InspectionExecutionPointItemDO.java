package cn.cheers.x.inspection.inspection_content.dal.dataobject.point;

import cn.cheers.x.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 点位检查项关联 DO。
 */
@TableName("inspection_execution_point_item")
@Data
@EqualsAndHashCode(callSuper = true)
public class InspectionExecutionPointItemDO extends BaseDO {

    /**
     * 主键 ID。
     */
    @TableId
    private Long id;
    /**
     * 所属站场 ID。
     */
    @TableField("site_id")
    private Long facilityId;
    /**
     * 执行点位 ID。
     */
    private Long pointId;
    /**
     * 检查项 ID。
     */
    private Long itemId;
    /**
     * 检查参数 JSON。
     */
    private String paramsJson;
    /**
     * 排序号。
     */
    private Integer sortNo;
    /**
     * 是否启用。
     */
    private Boolean enabled;
    /**
     * 备注。
     */
    private String remark;
}
