package cn.cheers.x.maintenance.controller.admin.vo.handbook;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class HandbookRespVO {
    private Long id;
    private String code;
    private String name;
    private String scope;
    private String assetTypeCode;
    private String frequencyCode;
    private Long fieldStandardId;
    private String crewHint;
    private String materialHint;
    private Integer versionNo;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
