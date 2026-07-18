package cn.cheers.x.inspection.inspection_content.dal.dataobject.item;

import cn.cheers.x.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 巡检项定义 DO。
 *
 * <p>统一管理所有业务实体类型的巡检项。</p>
 *
 * <p>设计说明：</p>
 * <ul>
 *     <li>sourceType 区分业务实体类型（设施/建筑/厂区/车间）</li>
 *     <li>categoryId 关联到具体业务实体分类</li>
 *     <li>objectModel 关联到设备型号/模型，同一分类下不同型号可能有不同的检查项</li>
 *     <li>用户选择巡检对象后，系统根据其 sourceType + categoryId + objectModel 自动关联对应的检查项</li>
 * </ul>
 *
 * <p>sourceType 可选值：</p>
 * <ul>
 *     <li>facility - 设施</li>
 *     <li>building - 建筑</li>
 *     <li>site - 厂区</li>
 *     <li>workshop - 车间</li>
 * </ul>
 */
@TableName("inspection_item")
@Data
@EqualsAndHashCode(callSuper = true)
public class InspectionItemDO extends BaseDO {

    /**
     * 主键 ID。
     */
    @TableId
    private Long id;

    /**
     * 巡检项编码。
     */
    private String itemCode;

    /**
     * 巡检项名称。
     */
    private String itemName;

    /**
     * 业务实体类型。
     *
     * <p>用于区分巡检项属于哪种业务实体：</p>
     * <ul>
     *     <li>facility - 设施</li>
     *     <li>building - 建筑</li>
     *     <li>site - 厂区</li>
     *     <li>workshop - 车间</li>
     * </ul>
     */
    private String sourceType;

    /**
     * 分类 ID。
     *
     * <p>关联到具体业务实体分类。</p>
     * <p>例如：设施分类下的"储罐类"、"泵类"等。</p>
     */
    private Long categoryId;

    /**
     * 设备型号/模型。
     *
     * <p>用于关联到设备类型/模型。</p>
     * <p>同一分类下不同型号有不同的检查项：</p>
     * <ul>
     *     <li>储罐类 - 立式储罐 - 检查项: 液位、压力...</li>
     *     <li>储罐类 - 卧式储罐 - 检查项: 液位、压力、支座...</li>
     *     <li>泵类 - 离心泵 - 检查项: 温度、振动...</li>
     *     <li>泵类 - 螺杆泵 - 检查项: 温度、密封...</li>
     * </ul>
     *
     * <p>注意：这里存储的是"类型"（如"立式储罐"），不是具体实体（如"1号储罐"）。</p>
     */
    private String objectModel;

    /**
     * 检查方法说明。
     */
    private String method;

    /**
     * 检查标准/判定依据。
     */
    private String standard;

    /**
     * 状态。
     */
    private String status;

    /**
     * 备注。
     */
    private String remark;
}
