package cn.iocoder.yudao.module.emergency.controller.admin.response.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 应急响应响应体")
@Data
public class ResponseRespVO {

    private Long id;
    private String responseNo;
    private Long eventId;
    private String responseLevel;
    private Long planId;
    private String status;
}

















