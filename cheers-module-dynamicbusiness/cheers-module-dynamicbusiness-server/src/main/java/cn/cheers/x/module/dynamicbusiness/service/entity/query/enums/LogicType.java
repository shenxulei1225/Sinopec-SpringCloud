package cn.cheers.x.module.dynamicbusiness.service.entity.query.enums;

/**
 * 逻辑类型枚举
 *
 * 定义多条件组合查询时的逻辑关系
 *
 * @author 系统
 */
public enum LogicType {

    /**
     * 与逻辑（所有条件都必须满足）
     */
    AND("and", "与", "AND"),

    /**
     * 或逻辑（满足任一条件即可）
     */
    OR("or", "或", "OR");

    /**
     * 逻辑类型编码
     */
    private final String code;

    /**
     * 逻辑类型名称
     */
    private final String name;

    /**
     * SQL 关键字
     */
    private final String sqlKeyword;

    LogicType(String code, String name, String sqlKeyword) {
        this.code = code;
        this.name = name;
        this.sqlKeyword = sqlKeyword;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getSqlKeyword() {
        return sqlKeyword;
    }

    /**
     * 根据编码获取逻辑类型
     *
     * @param code 逻辑类型编码
     * @return 逻辑类型枚举，如果未找到返回 AND（默认值）
     */
    public static LogicType getByCode(String code) {
        if (code == null) {
            return AND;
        }
        for (LogicType logicType : values()) {
            if (logicType.getCode().equalsIgnoreCase(code)) {
                return logicType;
            }
        }
        return AND;
    }
}
