package cn.cheers.x.module.platformresource.dal.dataobject.component;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 组件 DO
 *
 * 说明：
 * - 组件是一种可复用 UI 单元的默认定义
 * - 不包含 module / layout / 嵌套视图等概念
 */
@Data
@TableName("pr_component")
public class ComponentDO extends BaseDO {

    @TableId
    private Long id;

    /** 组件唯一标识 */
    @TableField(value = "key")
    private String key;

    /** 组件类型：tree | list | multi-tree | ... */
    private String type;

    /** 展示名称 */
    private String name;

    /** 图标 */
    private String icon;

    /** 默认 props(JSON) */
    private String props;

    /** 默认数据接口(JSON) */
    private String dataConfig;

    /** 默认 API 配置(JSON) */
    private String apiConfig;

    /** 默认扩展元数据(JSON) */
    private String uiConfig;

    /** 状态 (0-禁用, 1-启用) */
    private Integer status;

    /** 排序 */
    private Integer sort;

    /** 描述 */
    private String description;
}