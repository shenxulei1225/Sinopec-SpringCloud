package cn.cheers.x.maintenance.controller.admin.vo.standard;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class FieldWorkStandardRespVO {
    private Long id;
    private String code;
    private String name;
    private Integer versionNo;
    private String scope;
    private List<FieldWorkStandardStepVO> steps;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
