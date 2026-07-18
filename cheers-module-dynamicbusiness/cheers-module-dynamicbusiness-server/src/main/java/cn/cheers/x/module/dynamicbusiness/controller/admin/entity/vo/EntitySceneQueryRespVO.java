package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo;

import cn.cheers.x.framework.common.pojo.PageResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 实体场景查询响应")
@Data
public class EntitySceneQueryRespVO {

    @Schema(description = "结果形态：PAGE/LIST/TREE", requiredMode = Schema.RequiredMode.REQUIRED, example = "PAGE")
    private String resultShape;

    @Schema(description = "结果明细粒度：FULL/LIGHT", requiredMode = Schema.RequiredMode.REQUIRED, example = "FULL")
    private String resultDetail;

    @Schema(description = "分页结果（resultShape=PAGE 时有值）; LIGHT 时仅保证返回 id/name 等轻量字段")
    private PageResult<EntityRespVO> page;

    @Schema(description = "全量列表（resultShape=LIST 时有值）; LIGHT 时仅保证返回 id/name 等轻量字段")
    private List<EntityRespVO> list;

    @Schema(description = "树结构（resultShape=TREE 时有值）; LIGHT 时仅保证返回 id/name 等轻量字段")
    private List<EntityRespVO> tree;

    public static EntitySceneQueryRespVO page(PageResult<EntityRespVO> page) {
        return page(page, "FULL");
    }

    public static EntitySceneQueryRespVO page(PageResult<EntityRespVO> page, String resultDetail) {
        EntitySceneQueryRespVO resp = new EntitySceneQueryRespVO();
        resp.setResultShape("PAGE");
        resp.setResultDetail(resultDetail);
        resp.setPage(page);
        return resp;
    }

    public static EntitySceneQueryRespVO list(List<EntityRespVO> list) {
        return list(list, "FULL");
    }

    public static EntitySceneQueryRespVO list(List<EntityRespVO> list, String resultDetail) {
        EntitySceneQueryRespVO resp = new EntitySceneQueryRespVO();
        resp.setResultShape("LIST");
        resp.setResultDetail(resultDetail);
        resp.setList(list);
        return resp;
    }

    public static EntitySceneQueryRespVO tree(List<EntityRespVO> tree) {
        return tree(tree, "FULL");
    }

    public static EntitySceneQueryRespVO tree(List<EntityRespVO> tree, String resultDetail) {
        EntitySceneQueryRespVO resp = new EntitySceneQueryRespVO();
        resp.setResultShape("TREE");
        resp.setResultDetail(resultDetail);
        resp.setTree(tree);
        return resp;
    }
}

