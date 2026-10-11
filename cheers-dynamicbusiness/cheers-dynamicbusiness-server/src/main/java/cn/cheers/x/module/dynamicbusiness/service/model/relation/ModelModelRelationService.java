package cn.cheers.x.module.dynamicbusiness.service.model.relation;

import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelModelAssociationRespVO;

import java.util.List;

/**
 * 模型—模型多对多。
 *
 * <p>同类型、异类型都在同一张表，用来源类型和目标类型区分。
 * 按来源列出目标型号，按目标反查来源型号。挂、卸只改这一张表。</p>
 *
 * <p>不负责：实体自定义字段、实体上级、型号—实体归属。</p>
 */
public interface ModelModelRelationService {

    ModelModelAssociationRespVO associate(Long sourceModelId, Long targetModelId,
                                          String sourceEntityTypeCode, String targetEntityTypeCode);

    ModelModelAssociationRespVO disassociate(Long sourceModelId, Long targetModelId,
                                             String sourceEntityTypeCode, String targetEntityTypeCode);

    /**
     * 这个来源模型指向哪些目标型号。
     */
    List<Long> listTargetModelIds(Long sourceModelId, String sourceEntityTypeCode, String targetEntityTypeCode);

    /**
     * 哪些来源模型指向这个目标型号。
     */
    List<Long> listSourceModelIds(Long targetModelId, String targetEntityTypeCode, String sourceEntityTypeCode);

    /**
     * 把这个来源型号指向的目标名单换成提交的这一份。同一数据类型。
     * 没提交的边卸掉。空名单表示这一侧清空。
     */
    void replaceTargets(String entityTypeCode, Long sourceModelId, List<Long> targetModelIds);

    /**
     * 把指向这个目标型号的来源名单换成提交的这一份。同一数据类型。
     * 没提交的边卸掉。空名单表示这一侧清空。
     */
    void replaceSources(String entityTypeCode, Long targetModelId, List<Long> sourceModelIds);
}
