package cn.iocoder.yudao.module.inspection.inspection_content.dal.dataobject.spec;

import cn.cheers.x.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 检查规范 DO。
 */
@TableName("inspection_spec")
@Data
@EqualsAndHashCode(callSuper = true)
public class InspectionSpecDO extends BaseDO {

    /**
     * 主键 ID。
     */
    @TableId
    private Long id;
    /**
     * 规范编码。
     */
    private String specCode;
    /**
     * 规范名称。
     */
    private String specName;
    /**
     * 规范版本。
     */
    private String specVersion;
    /**
     * 适用对象类型编码。
     */
    private String objectTypeCode;
    /**
     * 引用标准。
     */
    private String refStandard;
    /**
     * 状态。
     */
    private String status;
    /**
     * 规范说明。
     */
    private String description;
}
