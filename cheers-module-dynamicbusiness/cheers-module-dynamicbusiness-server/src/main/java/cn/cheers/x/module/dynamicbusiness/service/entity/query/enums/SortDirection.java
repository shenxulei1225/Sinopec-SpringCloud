package cn.cheers.x.module.dynamicbusiness.service.entity.query.enums;

/**
 * 排序方向枚举
 *
 * 定义查询结果的排序方向
 *
 * @author 系统
 */
public enum SortDirection {

    /**
     * 升序
     */
    ASC("asc", "升序", "ASC"),

    /**
     * 降序
     */
    DESC("desc", "降序", "DESC");

    /**
     * 排序方向编码
     */
    private final String code;

    /**
     * 排序方向名称
     */
    private final String name;

    /**
     * SQL 关键字
     */
    private final String sqlKeyword;

    SortDirection(String code, String name, String sqlKeyword) {
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
     * 根据编码获取排序方向
     *
     * @param code 排序方向编码
     * @return 排序方向枚举，如果未找到返回 ASC（默认值）
     */
    public static SortDirection getByCode(String code) {
        if (code == null) {
            return ASC;
        }
        for (SortDirection direction : values()) {
            if (direction.getCode().equalsIgnoreCase(code)) {
                return direction;
            }
        }
        return ASC;
    }
}
