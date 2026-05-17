package cn.iocoder.yudao.module.inspection.inspection_content.dal.dataobject.object;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 巡检对象来源适配 DO。
 *
 * <p>定义巡检模块可接入的业务系统对象类型。</p>
 *
 * <p>设计说明：</p>
 * <ul>
 *     <li>本表只定义"能接入哪些系统"，不存储具体业务对象数据</li>
 *     <li>具体对象查询由各业务系统提供，巡检模块通过适配接口获取</li>
 *     <li>新增业务系统只需在此注册，并实现对应的适配器</li>
 * </ul>
 *
 * <p>sourceType 枚举（示例）：</p>
 * <ul>
 *     <li>facility - 设施系统</li>
 *     <li>building - 建筑系统</li>
 *     <li>site - 厂区系统</li>
 *     <li>workshop - 车间系统</li>
 *     <li>pipeline - 管线系统</li>
 *     <li>process - 工艺系统</li>
 * </ul>
 */
@TableName("inspection_object_source")
@Data
@EqualsAndHashCode(callSuper = true)
public class InspectionObjectSourceDO extends BaseDO {

    /**
     * 主键 ID。
     */
    @TableId
    private Long id;

    /**
     * 来源编码。
     *
     * <p>唯一标识业务系统，如：facility、building、pipeline 等。</p>
     */
    private String sourceCode;

    /**
     * 来源名称。
     *
     * <p>用户可读名称，如：设施系统、建筑系统、管线系统。</p>
     */
    private String sourceName;

    /**
     * 来源类型。
     *
     * <p>用于区分对象类型的层级：</p>
     * <ul>
     *     <li>facility - 设施</li>
     *     <li>equipment - 设备</li>
     *     <li>building - 建筑</li>
     *     <li>area - 区域</li>
     * </ul>
     */
    private String sourceType;

    /**
     * 适配器实现类。
     *
     * <p>实现 {@link cn.iocoder.yudao.module.inspection.inspection_content.service.source.ObjectSourceAdapter}</p>
     * <p>用于查询该来源的具体对象列表。</p>
     */
    private String adapterClass;

    /**
     * 对象模型列表。
     *
     * <p>支持的对象模型/型号，JSON 数组存储。</p>
     * <p>例如：["立式储罐", "卧式储罐", "球罐"]</p>
     *
     * <p>对象模型用于匹配检查项模板。</p>
     */
    private String objectModels;

    /**
     * 状态。
     *
     * <p>控制该来源是否可用。</p>
     */
    private String status;

    /**
     * 备注。
     */
    private String remark;

    /**
     * 排序号。
     */
    private Integer sortNo;
}
