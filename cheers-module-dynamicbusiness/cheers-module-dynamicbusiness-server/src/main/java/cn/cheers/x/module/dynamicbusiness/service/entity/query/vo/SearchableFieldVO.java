package cn.cheers.x.module.dynamicbusiness.service.entity.query.vo;

import cn.cheers.x.module.dynamicbusiness.service.entity.query.enums.Operator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 可查询字段 VO
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
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchableFieldVO {

    /**
     * 字段 ID
     */
    private Long fieldId;

    /**
     * 字段编码
     * 
     * <p>用于构建查询条件时指定字段</p>
     */
    private String fieldCode;

    /**
     * 字段名称
     * 
     * <p>用于前端显示</p>
     */
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
    private String fieldType;

    /**
     * 支持的操作符列表
     * 
     * <p>根据字段类型自动计算支持的操作符：</p>
     * <ul>
     *   <li>STRING: EQ, NE, LIKE, IN, NOT_IN, IS_NULL, IS_NOT_NULL</li>
     *   <li>INTEGER/DECIMAL: EQ, NE, GT, GE, LT, LE, BETWEEN, IN, NOT_IN, IS_NULL, IS_NOT_NULL</li>
     *   <li>DATE/DATETIME: EQ, NE, GT, GE, LT, LE, BETWEEN, IS_NULL, IS_NOT_NULL</li>
     *   <li>BOOLEAN: EQ, NE, IS_NULL, IS_NOT_NULL</li>
     *   <li>SELECT: EQ, NE, IN, NOT_IN, IS_NULL, IS_NOT_NULL</li>
     *   <li>ENTITY_REF: EQ, NE, IN, NOT_IN, IS_NULL, IS_NOT_NULL</li>
     * </ul>
     */
    private List<Operator> supportedOperators;

    /**
     * 是否可排序
     */
    private Boolean sortable;

    /**
     * 字段描述
     */
    private String description;

    /**
     * 单位（数值类型字段）
     */
    private String unit;

    /**
     * 选项列表（SELECT 类型字段）
     * 
     * <p>JSON 格式的选项列表，如：[{"value": "1", "label": "选项1"}]</p>
     */
    private String options;

    // ==================== 静态工厂方法 ====================

    /**
     * 从字段定义创建 SearchableFieldVO
     * 
     * @param fieldId 字段 ID
     * @param fieldCode 字段编码
     * @param fieldName 字段名称
     * @param fieldType 字段类型
     * @param sortable 是否可排序
     * @return SearchableFieldVO
     */
    public static SearchableFieldVO of(Long fieldId, String fieldCode, String fieldName, 
                                       String fieldType, Boolean sortable) {
        return SearchableFieldVO.builder()
            .fieldId(fieldId)
            .fieldCode(fieldCode)
            .fieldName(fieldName)
            .fieldType(fieldType)
            .sortable(sortable)
            .supportedOperators(getSupportedOperatorsForType(fieldType))
            .build();
    }

    /**
     * 根据字段类型获取支持的操作符列表
     */
    public static List<Operator> getSupportedOperatorsForType(String fieldType) {
        if (fieldType == null) {
            return List.of(Operator.EQ, Operator.NE, Operator.IS_NULL, Operator.IS_NOT_NULL);
        }
        
        return switch (fieldType.toUpperCase()) {
            case "STRING", "TEXT" -> List.of(
                Operator.EQ, Operator.NE, Operator.LIKE, 
                Operator.IN, Operator.NOT_IN, 
                Operator.IS_NULL, Operator.IS_NOT_NULL
            );
            case "INTEGER", "DECIMAL", "NUMBER" -> List.of(
                Operator.EQ, Operator.NE, 
                Operator.GT, Operator.GE, Operator.LT, Operator.LE, Operator.BETWEEN,
                Operator.IN, Operator.NOT_IN,
                Operator.IS_NULL, Operator.IS_NOT_NULL
            );
            case "DATE", "DATETIME" -> List.of(
                Operator.EQ, Operator.NE,
                Operator.GT, Operator.GE, Operator.LT, Operator.LE, Operator.BETWEEN,
                Operator.IS_NULL, Operator.IS_NOT_NULL
            );
            case "BOOLEAN" -> List.of(
                Operator.EQ, Operator.NE,
                Operator.IS_NULL, Operator.IS_NOT_NULL
            );
            case "SELECT", "MULTI_SELECT", "ENUM" -> List.of(
                Operator.EQ, Operator.NE,
                Operator.IN, Operator.NOT_IN,
                Operator.IS_NULL, Operator.IS_NOT_NULL
            );
            case "ENTITY_REF" -> List.of(
                Operator.EQ, Operator.NE,
                Operator.IN, Operator.NOT_IN,
                Operator.IS_NULL, Operator.IS_NOT_NULL
            );
            default -> List.of(
                Operator.EQ, Operator.NE,
                Operator.IS_NULL, Operator.IS_NOT_NULL
            );
        };
    }
}
