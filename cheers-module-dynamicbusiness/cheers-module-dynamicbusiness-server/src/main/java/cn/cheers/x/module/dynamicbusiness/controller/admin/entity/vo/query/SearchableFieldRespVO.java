package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.query;

import cn.cheers.x.module.dynamicbusiness.service.entity.query.enums.Operator;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 可查询字段响应 VO
 * 
 * <p>用于返回 Model 的可查询字段信息，供前端动态生成查询表单。</p>
 * 
 * <h3>使用场景</h3>
 * <ul>
 *   <li>前端根据此信息动态渲染查询条件输入框</li>
 *   <li>前端根据字段类型选择合适的输入组件</li>
 *   <li>前端根据支持的操作符生成操作符下拉选项</li>
 * </ul>
 * 
 * <h3>需求引用</h3>
 * <ul>
 *   <li>FR-022: 系统必须支持查询某个 Model 的所有可查询字段列表</li>
 *   <li>FR-053: 系统必须提供获取 Model 可查询字段列表的 API，供前端动态生成查询表单</li>
 * </ul>
 * 
 * @author 扩展字段查询服务
 */
@Schema(description = "管理后台 - 可查询字段响应")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchableFieldRespVO {

    /**
     * 字段 ID
     */
    @Schema(description = "字段 ID", example = "1")
    private Long fieldId;

    /**
     * 字段编码
     * 
     * <p>用于构建查询条件时指定字段。</p>
     */
    @Schema(description = "字段编码", example = "power")
    private String fieldCode;

    /**
     * 字段名称
     * 
     * <p>用于前端显示。</p>
     */
    @Schema(description = "字段名称", example = "功率")
    private String fieldName;

    /**
     * 字段类型
     * 
     * <p>用于前端选择合适的输入组件：</p>
     * <ul>
     *   <li>STRING: 文本输入框</li>
     *   <li>INTEGER/DECIMAL: 数字输入框</li>
     *   <li>DATE/DATETIME: 日期选择器</li>
     *   <li>BOOLEAN: 开关/复选框</li>
     *   <li>SELECT: 下拉选择框</li>
     *   <li>ENTITY_REF: 实体选择器</li>
     * </ul>
     */
    @Schema(description = "字段类型", example = "DECIMAL")
    private String fieldType;

    /**
     * 支持的操作符列表
     * 
     * <p>根据字段类型自动计算支持的操作符。</p>
     */
    @Schema(description = "支持的操作符列表")
    private List<Operator> supportedOperators;

    /**
     * 是否可排序
     */
    @Schema(description = "是否可排序", example = "true")
    private Boolean sortable;

    /**
     * 字段描述
     */
    @Schema(description = "字段描述", example = "设备功率，单位：千瓦")
    private String description;

    /**
     * 单位（数值类型字段）
     */
    @Schema(description = "单位", example = "kW")
    private String unit;

    /**
     * 选项列表（SELECT 类型字段）
     * 
     * <p>JSON 格式的选项列表。</p>
     */
    @Schema(description = "选项列表（SELECT 类型）")
    private List<FieldOptionVO> options;

    /**
     * 字段选项 VO
     */
    @Schema(description = "字段选项")
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FieldOptionVO {
        
        @Schema(description = "选项值", example = "1")
        private String value;
        
        @Schema(description = "选项标签", example = "选项一")
        private String label;
        
        @Schema(description = "选项颜色", example = "#1890ff")
        private String color;
    }
}
