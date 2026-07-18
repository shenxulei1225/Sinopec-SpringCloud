package cn.cheers.x.system.controller.admin.view.vo;

import lombok.Data;

/**
 * 更新视图配置 Request VO
 */
@Data
public class ViewUpdateReqVO {

    /**
     * 显示名称
     */
    private String label;

    /**
     * 图标
     */
    private String icon;

    /**
     * 视图内容定义（JSON）
     */
    private Object composition;

    private Boolean isTemplate;

    /**
     * 视图业务元数据
     */
    private Object uiConfig;

    /**
     * 布局配置
     */
    private Object layoutConfig;

    /**
     * 布局模板数组
     */
    private Object layouts;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 描述
     */
    private String description;
}
