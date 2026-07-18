package cn.cheers.x.system.controller.admin.view.vo;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 创建 View 请求 VO
 */
@Data
public class ViewCreateReqVO {

    /** 视图唯一标识 */
    @NotBlank(message = "视图 key 不能为空")
    private String key;

    /** 视图展示名 */
    @NotBlank(message = "显示名称不能为空")
    private String label;

    /** 图标 */
    private String icon;

    /**
     * 视图内容定义（JSON）
     *
     * 结构建议：
     * {
     *   layoutSchema: {...},
     *   items: [ ... ]
     * }
     */
    @NotNull(message = "composition 不能为空")
    private Object composition;

    /** 是否作为模板展示 */
    private Boolean isTemplate;

    /** 视图扩展元数据 */
    private Object uiConfig;

    /** 布局配置 */
    private Object layoutConfig;

    /** 可用布局列表 */
    private Object layouts;

    /** 排序 */
    private Integer sort;

    /** 描述 */
    private String description;
}