package cn.cheers.x.module.dynamicbusiness.service.datapermission;

/**
 * 登录人能看的一条管线记录，带它在管线树上的上级编号。
 * parentId 为空表示它的上级不在这批结果里（管线根，或上级不在可见范围）。
 * 数据页用它嵌成树。不负责判断哪一段被选中。
 */
public record PipelineSegmentNode(Long id, String name, Long parentId) {
}
