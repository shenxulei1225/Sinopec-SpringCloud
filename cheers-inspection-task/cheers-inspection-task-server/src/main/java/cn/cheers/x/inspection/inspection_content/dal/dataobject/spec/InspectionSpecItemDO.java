package cn.cheers.x.inspection.inspection_content.dal.dataobject.spec;

import cn.cheers.x.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 检查规范条目 DO。
 */
@TableName("inspection_spec_item")
@Data
@EqualsAndHashCode(callSuper = true)
public class InspectionSpecItemDO extends BaseDO {

    /**
     * 主键 ID。
     */
    @TableId
    private Long id;
    /**
     * 所属规范 ID。
     */
    private Long specId;
    /**
     * 条目序号。
     */
    private Integer itemNo;
    /**
     * 检查内容编码。
     */
    private String checkContentCode;
    /**
     * 检查方式编码。
     */
    private String checkMethodCode;
    /**
     * 检查目标说明。
     */
    private String checkGoal;
    /**
     * 默认参数 JSON。
     */
    private String defaultParamsJson;
    /**
     * 是否启用。
     */
    private Boolean enabled;
}
