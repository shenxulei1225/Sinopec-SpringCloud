package cn.cheers.x.inspection.inspection_content.dal.dataobject.collection;

import cn.cheers.x.framework.mybatis.core.dataobject.BaseDO;
import cn.cheers.x.inspection.task.model.task.InspectionContent;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 巡检对象集合 DO（模板）。
 *
 * <p>用于维护用户常用的巡检配置模板，方便快速添加到巡检任务中。</p>
 *
 * <p>与 InspectionTask.inspectionContent 采用相同的统一结构：</p>
 * <ul>
 *     <li>name - 显示名称</li>
 *     <li>groups - 模板组列表</li>
 *     <li>customObjects - 自定义对象列表</li>
 * </ul>
 *
 * <p>使用场景：</p>
 * <ul>
 *     <li>用户创建"罐体检修模板"，包含罐组对象及巡检项</li>
 *     <li>用户创建"泵组巡检模板"，包含泵组对象及巡检项</li>
 *     <li>创建巡检任务时，选择模板复制到任务中</li>
 *     <li>用户可以修改模板名称，也可以修改其中的对象</li>
 *     <li>用户可以把手头配置另存为新模板</li>
 * </ul>
 *
 * <p>注意：采用复制模式，模板修改不会影响已使用该模板的任务。</p>
 */
@TableName(value = "inspection_object_collection", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class InspectionObjectCollectionDO extends BaseDO {

    /**
     * 主键 ID。
     */
    @TableId
    private Long id;

    /**
     * 集合编码。
     *
     * <p>用于唯一标识模板。</p>
     */
    private String collectionCode;

    /**
     * 集合名称。
     *
     * <p>用户可读名称，如"罐体检修模板"。</p>
     */
    private String collectionName;

    /**
     * 分类 ID。
     */
    private Long categoryId;

    /**
     * 描述。
     *
     * <p>模板用途说明。</p>
     */
    private String description;

    /**
     * 状态。
     *
     * <p>控制模板是否可用。</p>
     * <p>枚举值：enabled（启用）、disabled（禁用）</p>
     */
    private String status;

    /**
     * 备注。
     */
    private String remark;

    /**
     * 集合内容。
     *
     * <p>复用 InspectionContent 结构，存储完整的对象和巡检项配置。</p>
     * <p>使用 JacksonTypeHandler 自动序列化/反序列化为 JSON。</p>
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private InspectionContent content;
}
