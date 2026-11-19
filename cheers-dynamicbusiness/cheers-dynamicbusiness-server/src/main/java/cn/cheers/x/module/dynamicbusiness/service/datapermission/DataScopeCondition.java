package cn.cheers.x.module.dynamicbusiness.service.datapermission;

import java.util.Set;

/**
 * 一行授权分配在某本目录上换算出的条件：记录的某个归属字段取值必须落在这些编号里。
 *
 * @param fieldCode   归属字段编码；为 null 时表示记录本身的编号（这本目录就是范围所在目录）
 * @param multiValued 归属字段多值时按实体关系表反查，单值时按专用列比较
 * @param allowedIds  允许的取值；为空表示这一行在这本目录上什么都看不到
 */
record DataScopeCondition(String fieldCode, boolean multiValued, Set<Long> allowedIds) {

    boolean onRecordId() {
        return fieldCode == null;
    }
}
