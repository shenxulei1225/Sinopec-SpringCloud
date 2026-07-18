package cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 物理列配置
 * 
 * <p>用于配置 DEDICATED 存储类型中哪些字段存储到物理列。
 * 配置的字段将使用 B-Tree 索引,提供更高的查询性能。</p>
 * 
 * <h3>配置驱动行为</h3>
 * <ul>
 *   <li><b>有配置</b>：字段存储到物理列（高性能查询,原 DEDICATED_STATIC 行为）</li>
 *   <li><b>无配置</b>：字段存储到 JSONB（灵活扩展,原 DEDICATED_DYNAMIC 行为）</li>
 * </ul>
 * 
 * <h3>使用场景</h3>
 * <ul>
 *   <li>高频查询字段：如设备编号、品牌、型号等</li>
 *   <li>需要范围查询的字段：如功率、电压、日期等</li>
 *   <li>需要排序的字段：如安装日期、创建时间等</li>
 * </ul>
 * 
 * <h3>支持的数据类型</h3>
 * <ul>
 *   <li>VARCHAR - 字符串,需指定 length</li>
 *   <li>INTEGER - 整数</li>
 *   <li>BIGINT - 长整数</li>
 *   <li>DECIMAL - 小数,需指定 precision 和 scale</li>
 *   <li>DATE - 日期</li>
 *   <li>TIMESTAMP - 时间戳</li>
 *   <li>BOOLEAN - 布尔值</li>
 *   <li>TEXT - 长文本</li>
 * </ul>
 * 
 * <h3>配置示例</h3>
 * <pre>
 * {
 *   "manufacturer": {"column": "manufacturer", "type": "VARCHAR", "length": 200},
 *   "power": {"column": "power", "type": "DECIMAL", "precision": 18, "scale": 4},
 *   "install_date": {"column": "install_date", "type": "DATE"}
 * }
 * </pre>
 * 
 * @author 基础服务模块
 * @see EntityTypeDO#getPhysicalColumnMapping()
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhysicalColumnConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    // ==================== 数据类型常量 ====================

    /** VARCHAR 类型 - 变长字符串 */
    public static final String TYPE_VARCHAR = "VARCHAR";
    
    /** INTEGER 类型 - 整数 */
    public static final String TYPE_INTEGER = "INTEGER";
    
    /** BIGINT 类型 - 长整数 */
    public static final String TYPE_BIGINT = "BIGINT";
    
    /** DECIMAL 类型 - 精确小数 */
    public static final String TYPE_DECIMAL = "DECIMAL";
    
    /** DATE 类型 - 日期 */
    public static final String TYPE_DATE = "DATE";
    
    /** TIMESTAMP 类型 - 时间戳 */
    public static final String TYPE_TIMESTAMP = "TIMESTAMP";
    
    /** BOOLEAN 类型 - 布尔值 */
    public static final String TYPE_BOOLEAN = "BOOLEAN";
    
    /** TEXT 类型 - 长文本 */
    public static final String TYPE_TEXT = "TEXT";

    // ==================== 默认值常量 ====================

    /** VARCHAR 默认长度 */
    public static final int DEFAULT_VARCHAR_LENGTH = 255;
    
    /** DECIMAL 默认精度 */
    public static final int DEFAULT_DECIMAL_PRECISION = 18;
    
    /** DECIMAL 默认小数位 */
    public static final int DEFAULT_DECIMAL_SCALE = 4;

    // ==================== 配置字段 ====================

    /**
     * 物理列名
     * 
     * <p>数据库表中的实际列名,建议使用 snake_case 命名。</p>
     * 
     * <p>示例：manufacturer, install_date, power_rating</p>
     */
    private String column;

    /**
     * 数据类型
     * 
     * <p>支持的类型：VARCHAR, INTEGER, BIGINT, DECIMAL, DATE, TIMESTAMP, BOOLEAN, TEXT</p>
     * 
     * @see #TYPE_VARCHAR
     * @see #TYPE_INTEGER
     * @see #TYPE_BIGINT
     * @see #TYPE_DECIMAL
     * @see #TYPE_DATE
     * @see #TYPE_TIMESTAMP
     * @see #TYPE_BOOLEAN
     * @see #TYPE_TEXT
     */
    private String type;

    /**
     * VARCHAR 长度
     * 
     * <p>仅 VARCHAR 类型使用,指定字符串最大长度。</p>
     * <p>默认值：255</p>
     */
    private Integer length;

    /**
     * DECIMAL 精度
     * 
     * <p>仅 DECIMAL 类型使用,指定数字总位数（包括小数位）。</p>
     * <p>默认值：18</p>
     */
    private Integer precision;

    /**
     * DECIMAL 小数位
     * 
     * <p>仅 DECIMAL 类型使用,指定小数点后的位数。</p>
     * <p>默认值：4</p>
     */
    private Integer scale;

    // ==================== 便捷构造方法 ====================

    /**
     * 创建 VARCHAR 类型配置
     * 
     * @param column 列名
     * @param length 长度
     * @return 配置对象
     */
    public static PhysicalColumnConfig varchar(String column, int length) {
        return PhysicalColumnConfig.builder()
                .column(column)
                .type(TYPE_VARCHAR)
                .length(length)
                .build();
    }

    /**
     * 创建 VARCHAR 类型配置（使用默认长度 255）
     * 
     * @param column 列名
     * @return 配置对象
     */
    public static PhysicalColumnConfig varchar(String column) {
        return varchar(column, DEFAULT_VARCHAR_LENGTH);
    }

    /**
     * 创建 INTEGER 类型配置
     * 
     * @param column 列名
     * @return 配置对象
     */
    public static PhysicalColumnConfig integer(String column) {
        return PhysicalColumnConfig.builder()
                .column(column)
                .type(TYPE_INTEGER)
                .build();
    }

    /**
     * 创建 BIGINT 类型配置
     * 
     * @param column 列名
     * @return 配置对象
     */
    public static PhysicalColumnConfig bigint(String column) {
        return PhysicalColumnConfig.builder()
                .column(column)
                .type(TYPE_BIGINT)
                .build();
    }

    /**
     * 创建 DECIMAL 类型配置
     * 
     * @param column 列名
     * @param precision 精度
     * @param scale 小数位
     * @return 配置对象
     */
    public static PhysicalColumnConfig decimal(String column, int precision, int scale) {
        return PhysicalColumnConfig.builder()
                .column(column)
                .type(TYPE_DECIMAL)
                .precision(precision)
                .scale(scale)
                .build();
    }

    /**
     * 创建 DECIMAL 类型配置（使用默认精度 18,4）
     * 
     * @param column 列名
     * @return 配置对象
     */
    public static PhysicalColumnConfig decimal(String column) {
        return decimal(column, DEFAULT_DECIMAL_PRECISION, DEFAULT_DECIMAL_SCALE);
    }

    /**
     * 创建 DATE 类型配置
     * 
     * @param column 列名
     * @return 配置对象
     */
    public static PhysicalColumnConfig date(String column) {
        return PhysicalColumnConfig.builder()
                .column(column)
                .type(TYPE_DATE)
                .build();
    }

    /**
     * 创建 TIMESTAMP 类型配置
     * 
     * @param column 列名
     * @return 配置对象
     */
    public static PhysicalColumnConfig timestamp(String column) {
        return PhysicalColumnConfig.builder()
                .column(column)
                .type(TYPE_TIMESTAMP)
                .build();
    }

    /**
     * 创建 BOOLEAN 类型配置
     * 
     * @param column 列名
     * @return 配置对象
     */
    public static PhysicalColumnConfig bool(String column) {
        return PhysicalColumnConfig.builder()
                .column(column)
                .type(TYPE_BOOLEAN)
                .build();
    }

    /**
     * 创建 TEXT 类型配置
     * 
     * @param column 列名
     * @return 配置对象
     */
    public static PhysicalColumnConfig text(String column) {
        return PhysicalColumnConfig.builder()
                .column(column)
                .type(TYPE_TEXT)
                .build();
    }

    // ==================== 核心方法 ====================

    /**
     * 生成数据库类型字符串
     * 
     * <p>根据配置生成 PostgreSQL 兼容的数据类型定义。</p>
     * 
     * <h4>生成示例</h4>
     * <ul>
     *   <li>VARCHAR(200)</li>
     *   <li>INTEGER</li>
     *   <li>BIGINT</li>
     *   <li>DECIMAL(18,4)</li>
     *   <li>DATE</li>
     *   <li>TIMESTAMP</li>
     *   <li>BOOLEAN</li>
     *   <li>TEXT</li>
     * </ul>
     * 
     * @return 数据库类型字符串
     * @throws IllegalStateException 如果类型未设置或不支持
     */
    public String toDbType() {
        if (StrUtil.isBlank(type)) {
            throw new IllegalStateException("物理列类型未设置");
        }

        String upperType = type.toUpperCase();
        switch (upperType) {
            case TYPE_VARCHAR:
                int len = (length != null && length > 0) ? length : DEFAULT_VARCHAR_LENGTH;
                return String.format("VARCHAR(%d)", len);
                
            case TYPE_INTEGER:
                return "INTEGER";
                
            case TYPE_BIGINT:
                return "BIGINT";
                
            case TYPE_DECIMAL:
                int p = (precision != null && precision > 0) ? precision : DEFAULT_DECIMAL_PRECISION;
                int s = (scale != null && scale >= 0) ? scale : DEFAULT_DECIMAL_SCALE;
                return String.format("DECIMAL(%d,%d)", p, s);
                
            case TYPE_DATE:
                return "DATE";
                
            case TYPE_TIMESTAMP:
                return "TIMESTAMP";
                
            case TYPE_BOOLEAN:
                return "BOOLEAN";
                
            case TYPE_TEXT:
                return "TEXT";
                
            default:
                throw new IllegalStateException("不支持的物理列类型: " + type);
        }
    }

    /**
     * 验证配置有效性
     * 
     * <p>检查配置是否完整且有效。</p>
     * 
     * @return 验证结果,null 表示有效,否则返回错误信息
     */
    public String validate() {
        // 检查列名
        if (StrUtil.isBlank(column)) {
            return "物理列名不能为空";
        }
        
        // 检查列名格式（只允许字母、数字、下划线,且以字母开头）
        if (!column.matches("^[a-zA-Z][a-zA-Z0-9_]*$")) {
            return "物理列名格式无效,只允许字母、数字、下划线,且必须以字母开头: " + column;
        }
        
        // 检查类型
        if (StrUtil.isBlank(type)) {
            return "物理列类型不能为空";
        }
        
        String upperType = type.toUpperCase();
        
        // 检查类型是否支持
        if (!TYPE_VARCHAR.equals(upperType) && 
            !TYPE_INTEGER.equals(upperType) && 
            !TYPE_BIGINT.equals(upperType) && 
            !TYPE_DECIMAL.equals(upperType) && 
            !TYPE_DATE.equals(upperType) && 
            !TYPE_TIMESTAMP.equals(upperType) && 
            !TYPE_BOOLEAN.equals(upperType) && 
            !TYPE_TEXT.equals(upperType)) {
            return "不支持的物理列类型: " + type;
        }
        
        // VARCHAR 类型检查长度
        if (TYPE_VARCHAR.equals(upperType)) {
            if (length != null && length <= 0) {
                return "VARCHAR 长度必须大于 0";
            }
            if (length != null && length > 65535) {
                return "VARCHAR 长度不能超过 65535";
            }
        }
        
        // DECIMAL 类型检查精度和小数位
        if (TYPE_DECIMAL.equals(upperType)) {
            if (precision != null && precision <= 0) {
                return "DECIMAL 精度必须大于 0";
            }
            if (precision != null && precision > 38) {
                return "DECIMAL 精度不能超过 38";
            }
            if (scale != null && scale < 0) {
                return "DECIMAL 小数位不能为负数";
            }
            if (precision != null && scale != null && scale > precision) {
                return "DECIMAL 小数位不能大于精度";
            }
        }
        
        return null; // 验证通过
    }

    /**
     * 判断配置是否有效
     * 
     * @return true 如果配置有效
     */
    public boolean isValid() {
        return validate() == null;
    }

    /**
     * 判断是否为字符串类型
     * 
     * @return true 如果是 VARCHAR 或 TEXT 类型
     */
    public boolean isStringType() {
        if (StrUtil.isBlank(type)) {
            return false;
        }
        String upperType = type.toUpperCase();
        return TYPE_VARCHAR.equals(upperType) || TYPE_TEXT.equals(upperType);
    }

    /**
     * 判断是否为数值类型
     * 
     * @return true 如果是 INTEGER、BIGINT 或 DECIMAL 类型
     */
    public boolean isNumericType() {
        if (StrUtil.isBlank(type)) {
            return false;
        }
        String upperType = type.toUpperCase();
        return TYPE_INTEGER.equals(upperType) || 
               TYPE_BIGINT.equals(upperType) || 
               TYPE_DECIMAL.equals(upperType);
    }

    /**
     * 判断是否为日期类型
     * 
     * @return true 如果是 DATE 或 TIMESTAMP 类型
     */
    public boolean isDateType() {
        if (StrUtil.isBlank(type)) {
            return false;
        }
        String upperType = type.toUpperCase();
        return TYPE_DATE.equals(upperType) || TYPE_TIMESTAMP.equals(upperType);
    }

    /**
     * 判断是否为布尔类型
     * 
     * @return true 如果是 BOOLEAN 类型
     */
    public boolean isBooleanType() {
        if (StrUtil.isBlank(type)) {
            return false;
        }
        return TYPE_BOOLEAN.equals(type.toUpperCase());
    }

    @Override
    public String toString() {
        return String.format("PhysicalColumnConfig{column='%s', type='%s', dbType='%s'}", 
                column, type, isValid() ? toDbType() : "INVALID");
    }
}
