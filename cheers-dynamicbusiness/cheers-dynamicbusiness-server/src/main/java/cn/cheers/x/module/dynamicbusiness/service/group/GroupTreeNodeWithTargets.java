package cn.cheers.x.module.dynamicbusiness.service.group;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class GroupTreeNodeWithTargets {

    private Long id;
    private String code;
    private String name;
    private String description;
    private Long parentId;
    private String path;
    private Integer level;
    private Integer sort;
    private Integer status;
    private LocalDateTime createTime;

    /**
     * 分组下目标列表（当前主要用于 FIELD 分组返回字段列表）
     */
    private List<Object> targets;

    private List<GroupTreeNodeWithTargets> children;
}
