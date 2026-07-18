package cn.cheers.x.module.dynamicbusiness.controller.admin.group.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Schema(description = "管理后台 - 通用分组 Response VO")
public class GroupRespVO {

    private Long id;
    private String groupType;
    private String code;
    private String name;
    private String description;
    private Long parentId;
    private String path;
    private Integer level;
    private Integer sort;
    private Integer status;
    private LocalDateTime createTime;
    private List<Map<String, Object>> targets;
    private List<GroupRespVO> children;
}
