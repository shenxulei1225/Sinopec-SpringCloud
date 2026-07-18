package cn.cheers.x.inspection.inspection_content.dal.dataobject.spec;

import cn.cheers.x.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 规范引用关系 DO。
 */
@TableName("inspection_spec_ref")
@Data
@EqualsAndHashCode(callSuper = true)
public class InspectionSpecRefDO extends BaseDO {

    /**
     * 主键 ID。
     */
    @TableId
    private Long id;
    /**
     * 当前规范 ID。
     */
    private Long specId;
    /**
     * 被引用规范 ID。
     */
    private Long refSpecId;
    /**
     * 引用关系类型。
     */
    private String relationType;
    /**
     * 优先级。
     */
    private Integer priority;
    /**
     * 是否启用。
     */
    private Boolean enabled;
}
