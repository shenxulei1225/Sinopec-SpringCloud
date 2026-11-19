package cn.cheers.x.module.dynamicbusiness.service.datapermission;

/** 范围里的一条记录：编号、名称、编码。右上角选站列表用。 */
public record ScopeRecord(Long id, String name, String code) {
}
