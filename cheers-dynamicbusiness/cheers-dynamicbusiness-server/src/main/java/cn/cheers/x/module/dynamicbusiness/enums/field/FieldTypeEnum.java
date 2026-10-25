package cn.cheers.x.module.dynamicbusiness.enums.field;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 字段类型枚举
 * 
 * 定义系统支持的所有字段类型，包括：
 * - 基础类型：TEXT, NUMBER, DATE, DATETIME, BOOLEAN, ENUM, JSON, COORDINATE
 * - 关联类型：ENTITY_REF（单选关联字段）、ENTITY_REF_MULTI（多选关联字段）
 * - 引用型号字段：REF_MODEL（读某一个型号上点名的字段）
 * - 引用类型：REFERENCE（固定列字段使用，引用系统表）
 * 
 * @author yudao
 */
@Getter
@AllArgsConstructor
public enum FieldTypeEnum {

    /**
     * 文本类型
     */
    TEXT("TEXT", "文本", false),

    /**
     * 长文本类型
     */
    LONG_TEXT("LONG_TEXT", "长文本", false),

    /**
     * 数字类型
     */
    NUMBER("NUMBER", "数字", false),

    /**
     * 整数类型
     */
    INTEGER("INTEGER", "整数", false),

    /**
     * 日期类型
     */
    DATE("DATE", "日期", false),

    /**
     * 日期时间类型
     */
    DATETIME("DATETIME", "日期时间", false),

    /**
     * 布尔类型
     */
    BOOLEAN("BOOLEAN", "布尔", false),

    /**
     * 枚举类型
     */
    ENUM("ENUM", "枚举", false),

    /**
     * JSON类型
     */
    JSON("JSON", "JSON", false),

    /**
     * 地理坐标（值类型，不是插件用途）
     *
     * <p>一份结构化值，固定子项：经度 longitude、纬度 latitude、高程 height（GB/T 16831 / ISO 6709）。
     * 路网点位「坐标」、设备 GIS 坐标、设备三维坐标都是这个值类型；字段码可以不同，子项不能另造一套。</p>
     * <p>禁止：用多行文本冒充；拆成三条独立小数当主定义；对照读路径补子项。</p>
     */
    COORDINATE("COORDINATE", "地理坐标", false),

    /**
     * 数值范围（值类型）。
     *
     * <p>一份结构化值，固定子项：最小值 min、最大值 max。单位写在字段上，不写进每一份值。
     * 俯仰角、电压、工作温度、湿度都用这个类型。禁止用一段文字冒充范围。</p>
     */
    RANGE("RANGE", "范围", false),

    /**
     * 带范围的数值（值类型）。
     *
     * <p>一份值里有两组：要填的数字 value，以及范围 min、max。
     * 展示方式写在字段 options.display。value 是填数字，range 是看范围。
     * 页面按这个标识画。填数字时人填这个数。看范围时画出最小和最大。</p>
     */
    RANGED_NUMBER("RANGED_NUMBER", "带范围的数值", false),

    /**
     * 模型关联。
     *
     * <p>详情上选的是型号，不是某一台设备。名单写在型号—型号关联表。
     * 方向记在字段的 semanticType：向外列目标型号，向内反查来源型号。
     * 不写入这台设备的自定义字段。</p>
     */
    MODEL_RELATION("MODEL_RELATION", "模型关联", true),

    /**
     * 调用服务。这一项不写入记录。
     * 创建保存成功后，按字段 options 里的服务和操作，把新记录编号交给那个操作。
     */
    SERVICE_CALL("SERVICE_CALL", "调用服务", false),

    /**
 * 引用数据（单选）
 *
 * <p>用户在字段库只选「引用数据」并指定目标数据类型；挂到型号后由系统判定：</p>
 * <ul>
 *   <li>目标 = 当前型号所属类型 → 同类型引用（树选，值记在本字段；不参与组织树同步）</li>
 *   <li>目标 ≠ 当前型号所属类型 → 跨类型引用（关联选择）</li>
 * </ul>
 * <p>组织树上级不走本类型用户配置，见系统字段 {@link #ENTITY_SELF_REF}（TREE_PARENT）。</p>
 */
ENTITY_REF("ENTITY_REF", "引用数据", true),

    /**
     * 引用型号字段。
     *
     * <p>读某一个型号上点名的字段。配置记在字段 options 里，一份给详情、实时控制和生成航线指令参数共用。
     * 值留在被点名的型号上，不抄到每一台设备。</p>
     */
    REF_MODEL("REF_MODEL", "引用型号字段", false),

/**
 * 引用数据（多选）
 *
 * 支持关联多个目标实体，受 maxRelations 字段限制。
 * 跨/同类型同样按目标与当前型号是否一致由后台判定。
 */
ENTITY_REF_MULTI("ENTITY_REF_MULTI", "引用数据(多选)", true),

/**
 * 引用数据（批量）
 *
 * 语义上等同于多选引用，在关联处理上与 ENTITY_REF_MULTI 一致。
 */
BATCH_ENTITY_REF("BATCH_ENTITY_REF", "引用数据(批量)", true),

/**
 * 系统组织上级（内部类型，不对用户字段库展示）
 *
 * <p>仅高级分类 ensure 使用：编码 parentId、semanticType=TREE_PARENT；
 * 读写实体上级编号/树路径并同步分类树。用户手建引用一律用 {@link #ENTITY_REF}。</p>
 */
ENTITY_SELF_REF("ENTITY_SELF_REF", "系统组织上级", true),

    /**
     * 系统引用类型（固定列字段使用）
     *
     * 用于固定列字段引用系统表（如用户、部门等）。
     * 与 ENTITY_REF 的区别：
     * - REFERENCE: 引用系统内置表
     * - ENTITY_REF: 引用动态业务实体（对人展示为「引用数据」）
     */
    REFERENCE("REFERENCE", "系统引用", true);

    /**
     * 类型编码
     */
    private final String code;

    /**
     * 类型名称
     */
    private final String name;

    /**
     * 是否为关联类型
     */
    private final boolean relationType;

    /**
     * 根据编码获取枚举
     */
    public static FieldTypeEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(e -> e.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElse(null);
    }

    /**
     * 判断是否为关联类型
     */
    public static boolean isRelationType(String code) {
        FieldTypeEnum type = getByCode(code);
        return type != null && type.isRelationType();
    }

    /** 引用型号字段。读型号上点名的字段，不读某一条记录。 */
    public static boolean isRefModel(String code) {
        return REF_MODEL.getCode().equalsIgnoreCase(code);
    }

    /** 调用服务。值不落记录。 */
    public static boolean isServiceCall(String code) {
        return SERVICE_CALL.getCode().equalsIgnoreCase(code);
    }

    /**
     * 判断是否为引用数据（ENTITY_REF 族，含单选/多选/批量）。不含系统组织上级。
     */
    public static boolean isEntityRef(String code) {
        return ENTITY_REF.getCode().equalsIgnoreCase(code)
                || ENTITY_REF_MULTI.getCode().equalsIgnoreCase(code)
                || BATCH_ENTITY_REF.getCode().equalsIgnoreCase(code);
    }

    /** 系统组织上级（内部 ENTITY_SELF_REF）。 */
    public static boolean isEntitySelfRef(String code) {
        return ENTITY_SELF_REF.getCode().equalsIgnoreCase(code);
    }

    /**
     * 相对某型号是否为同类型引用：目标数据类型编码与型号所属类型一致。
     * 用户字段统一存 ENTITY_REF*，由此判定走树选还是跨类型关联 UI。
     */
    public static boolean isSameTypeRefTarget(String modelEntityTypeCode, String targetEntityTypeCode) {
        if (modelEntityTypeCode == null || modelEntityTypeCode.isBlank()
                || targetEntityTypeCode == null || targetEntityTypeCode.isBlank()) {
            return false;
        }
        return modelEntityTypeCode.trim().equalsIgnoreCase(targetEntityTypeCode.trim());
    }

    /**
     * 判断是否为单选实体引用类型（跨类型）
     */
    public static boolean isSingleEntityRef(String code) {
        return ENTITY_REF.getCode().equalsIgnoreCase(code);
    }

    /**
     * 判断是否为多选实体引用类型
     */
    public static boolean isMultiEntityRef(String code) {
        return ENTITY_REF_MULTI.getCode().equalsIgnoreCase(code)
                || BATCH_ENTITY_REF.getCode().equalsIgnoreCase(code);
    }

    /**
     * 地理坐标值类型。空对象或未填经纬度视为未填，不得当半成品坐标校验。
     */
    public static boolean isCoordinate(String code) {
        return COORDINATE.getCode().equalsIgnoreCase(code);
    }

    /** 数值范围。空对象或最小、最大都没填视为未填。 */
    public static boolean isRange(String code) {
        return RANGE.getCode().equalsIgnoreCase(code);
    }

    /** 带范围的数值。数字和范围都空视为未填。 */
    public static boolean isRangedNumber(String code) {
        return RANGED_NUMBER.getCode().equalsIgnoreCase(code);
    }
}
