package cn.cheers.x.module.dynamicbusiness.dal.repository.entity;

/**
 * 实体表物理列筛选（列名须为已校验安全标识符；op 仅 EQ / IN）。
 */
public record PhysicalColumnFilter(String column, String op, Object value) {
}
