package cn.cheers.x.maintenance.api.dto;

import lombok.Data;

@Data
public class FieldWorkStandardRespDTO {
    private Long id;
    private String code;
    private String name;
    private Integer versionNo;
    private String scope;
    private String stepsJson;
    private Integer status;
}
