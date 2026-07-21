package cn.cheers.x.inspection.inspection_content.dal.dataobject.profile;

import cn.cheers.x.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 对象巡检类型台账 DO。
 */
@TableName("inspection_object_profile")
@Data
@EqualsAndHashCode(callSuper = true)
public class InspectionObjectProfileDO extends BaseDO {

    @TableId
    private Long id;

    private Long facilityId;

    private Long objectId;

    private String inspectionType;

    private Integer defaultWorkMinutes;
}
