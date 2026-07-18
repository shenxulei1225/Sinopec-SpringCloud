package cn.cheers.x.system.dal.dataobject.view;

import cn.cheers.x.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 视图 DO
 */
@Data
@TableName("system_view")
public class ViewDO extends BaseDO {

    private Long id;

    /** 视图唯一标识 */
    private String key;

    /** 视图内容定义(JSON) */
    private String composition;

    /** 视图展示名 */
    private String label;

    /** 图标 */
    private String icon;

    /** 是否作为模板展示 */
    private Boolean isTemplate;

    /** 视图扩展元数据(JSON) */
    private String uiConfig;

    /** 布局配置(JSON) */
    private String layoutConfig;

    /** 布局模板数组(JSON) */
    private String layouts;

    /** 状态 (0-禁用, 1-启用) */
    private Integer status;

    /** 排序 */
    private Integer sort;

    /** 描述 */
    private String description;
}