package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "多独立栏分类求交中的一组分类范围")
@Data
public class CategoryIdGroupReqVO {

    @Schema(description = "该栏激活标签页的分类体系编码", example = "region")
    private String categoryTypeCode;

    @Schema(description = "分类节点 ID；全选时传该体系用于求交的节点集（可由前端展开全部节点，或传根由服务端整树展开）",
            example = "[100, 101]")
    private List<Long> categoryIds;
}
