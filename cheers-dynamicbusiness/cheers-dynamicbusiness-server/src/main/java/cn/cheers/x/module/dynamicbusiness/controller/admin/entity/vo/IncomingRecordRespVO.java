package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo;

import lombok.Data;

/**
 * 详情页签里的一条对方记录。
 * 里程、顺序、经纬度只在对方是场站、阀室时有值，页签名单可以不画这几项。
 */
@Data
public class IncomingRecordRespVO {

    private Long id;

    private String name;

    private String modelName;

    /** 在当前这条管线上的里程。没有实测里程则为空。 */
    private Double lineMileage;

    /** 在当前这条管线上的顺序编号。没有里程时详情按它排。 */
    private Double segmentSeq;

    private Double longitude;

    private Double latitude;

    /** 场站地址。地图上的点按这个地址译出的坐标来标。 */
    private String address;
}
