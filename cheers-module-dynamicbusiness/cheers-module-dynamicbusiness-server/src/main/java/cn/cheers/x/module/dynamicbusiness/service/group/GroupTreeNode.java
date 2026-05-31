package cn.cheers.x.module.dynamicbusiness.service.group;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class GroupTreeNode {

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
    private List<GroupTreeNode> children;
}
