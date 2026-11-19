package cn.cheers.x.module.dynamicbusiness.controller.admin.datapermission.vo;

import cn.cheers.x.module.dynamicbusiness.service.datapermission.PostAssignmentQueryService.DutyPostRoleBinding;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "改权限弹窗：职务绑定的一个角色，以及绑定这个角色的全部职务")
@Data
public class DutyPostRoleBindingRespVO {

    private Long roleId;

    @Schema(description = "绑定这个角色的全部职务（含当前职务）。多于一个时，改这个角色会同时改它们的权限")
    private List<DutyPost> dutyPosts;

    @Data
    public static class DutyPost {
        private Long id;
        @Schema(description = "职务名称；职务已删除时为空")
        private String name;
    }

    public static DutyPostRoleBindingRespVO from(DutyPostRoleBinding binding) {
        DutyPostRoleBindingRespVO vo = new DutyPostRoleBindingRespVO();
        vo.setRoleId(binding.roleId());
        vo.setDutyPosts(binding.dutyPosts().stream().map(ref -> {
            DutyPost post = new DutyPost();
            post.setId(ref.id());
            post.setName(ref.name());
            return post;
        }).toList());
        return vo;
    }
}
