package cn.cheers.x.module.dynamicbusiness.dal.dataobject.page;

import cn.iocoder.yudao.framework.mybatis.core.type.JsonbJsonTypeHandler;
import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Map;

/**
 * 页面管理 DO
 *
 * 用于维护页面元信息与 A2UI 相关配置，页面配置本体仍存储在 dynamic_page_config。
 */
@TableName(value = "dynamic_page", autoResultMap = true)
@KeySequence("dynamic_page_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageDO extends TenantBaseDO {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 页面代码（唯一标识）
     */
    private String pageCode;

    /**
     * 页面名称
     */
    private String pageName;

    /**
     * 页面类型
     */
    private String pageType;

    /**
     * 页面描述
     */
    private String description;

    /**
     * 页面状态（1-发布，0-停用）
     */
    private Integer status;

    /**
     * 业务归属的父菜单ID
     */
    private Long parentMenuId;

    /**
     * 页面挂载菜单ID
     */
    private Long menuId;

    /**
     * 关联页面配置ID
     */
    private Long pageConfigId;

    /**
     * 页面图标
     */
    private String icon;

    /**
     * 页面标签（逗号分隔）
     */
    private String tags;

    /**
     * 路由地址
     */
    private String routePath;

    /**
     * 前端组件路径
     */
    private String component;

    /**
     * 页面布局
     */
    private String layout;

    /**
     * A2UI Schema
     */
    @TableField(typeHandler = JsonbJsonTypeHandler.class)
    private Map<String, Serializable> uiSchema;

    /**
     * A2UI Schema 版本
     */
    private String uiVersion;

    /**
     * A2UI 数据源配置
     */
    @TableField(typeHandler = JsonbJsonTypeHandler.class)
    private Map<String, Serializable> dataSource;

    /**
     * 备注
     */
    private String remark;
}
