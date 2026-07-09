package cn.cheers.x.module.dynamicbusiness.dal.dataobject.pageconfig;

import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
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
 * 页面配置 DO
 * 
 * 用于存储每个页面的配置信息，支持不同页面类型的配置。
 * 
 * 设计说明：
 * 1. 每个菜单（页面）可以有一个配置记录
 * 2. 不同 page_type 有完全不同的配置结构，因此使用 JSONB 字段存储
 * 3. 数据管理页面的 config 包含 pattern、leftTreeType、rightContentType 等
 * 
 * @author yudao
 */
@TableName(value = "dynamic_page_config", autoResultMap = true)
@KeySequence("dynamic_page_config_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageConfigDO extends TenantBaseDO {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 所属门户业务 id（dynamic_business.id） */
    private Long businessId;

    /**
     * 配置代码（唯一标识）
     * 
     * 格式：{businessType}-{pageName} 或 page_config_{id}
     * 例如：equipment-data、equipment-stats
     */
    private String configCode;

    /**
     * 页面代码（唯一标识）
     * 
     * 用于页面的唯一定位与加载
     */
    private String pageCode;

    /**
     * 关联菜单ID（已废弃，保留用于兼容性）
     * 
     * @deprecated 使用 dynamic_menu.page_config_id 替代
     * 与 dynamic_menu 表的 id 关联
     */
    @Deprecated
    private Long menuId;

    /**
     * 页面类型
     * 
     * 可选值：
     * - data_management: 数据管理页面（使用 Pattern A/B/C/D）
     * - dashboard: 驾驶舱页面
     * - statistics: 统计页面
     * - monitor: 实时监控页面
     */
    private String pageType;

    /**
     * 配置内容（JSON格式）
     * 
     * 不同 page_type 有不同的配置结构：
     * 
     * data_management 类型的配置结构：
     * {
     *   "pattern": "A" | "B" | "C" | "D",           // 架构模式
     *   "leftTreeType": "category" | "category-model",  // 左侧树类型
     *   "rightContentType": "entity-list" | "entity-list-with-detail" | "relation-list",  // 右侧内容类型
     *   "dragBehavior": "entity-category" | "model-category" | "none",  // 拖拽行为
     *   "categoryDrawerEntityMode": boolean,       // 分类抽屉实体模式
     * }
     * 
     * dashboard 类型的配置结构：
     * {
     *   "layout": "grid" | "flex",
     *   "widgets": [...],
     *   "refreshInterval": number
     * }
     * 
     * 使用 PostgreSQLJsonbTypeHandler 处理 PostgreSQL 的 JSONB 类型
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Serializable> config;

    /**
     * 页面类型常量
     */
    public static final String PAGE_TYPE_DATA_MANAGEMENT = "data_management";
    public static final String PAGE_TYPE_DASHBOARD = "dashboard";
    public static final String PAGE_TYPE_STATISTICS = "statistics";
    public static final String PAGE_TYPE_MONITOR = "monitor";
}
