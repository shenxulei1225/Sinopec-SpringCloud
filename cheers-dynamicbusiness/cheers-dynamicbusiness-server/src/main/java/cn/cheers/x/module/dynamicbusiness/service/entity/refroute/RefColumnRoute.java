package cn.cheers.x.module.dynamicbusiness.service.entity.refroute;

/**
 * 一个落在记录自己专用列上的单值引用字段。
 * 例：设备目录的「所属场站」（facility_id）存在设备表的 facility_id 列，指向场站目录。
 *
 * @param subjectTypeCode 引用方的存数类型（字段登记在这本目录上）
 * @param fieldCode       字段编码
 * @param column          引用方专用表上的列名，存被引用记录的编号
 * @param targetTypeCode  被引用的目录（字段声明里的指向）
 */
public record RefColumnRoute(String subjectTypeCode, String fieldCode, String column, String targetTypeCode) {
}
