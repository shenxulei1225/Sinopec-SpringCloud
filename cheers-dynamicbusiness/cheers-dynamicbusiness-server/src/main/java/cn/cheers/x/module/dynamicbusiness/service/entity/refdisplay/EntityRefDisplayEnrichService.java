package cn.cheers.x.module.dynamicbusiness.service.entity.refdisplay;

import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;

import java.util.List;

/**
 * 实体列表与详情的查询增强：引用字段只存编号，展示名称在这里补上，不写库。
 *
 * <p>管什么：已经是「类型 + 编号」的引用，补上对方名称；上级分区这种纯编号，改成带编号和名称的对象。创建人编号同样在这次返回里带上用户目录里的昵称。</p>
 * <p>不负责：改库里的上级编号，把昵称写进报修单，或在页面上再查一次名称。</p>
 */
public interface EntityRefDisplayEnrichService {

    /**
     * 详情里引用和上级分区要显示名称。
     * 编号留在 {@code id}（上级分区的 {@code getParentId()} 仍是原来的数字）。
     * 查不到名称时，原值不动，页面上仍能看到编号。
     */
    void enrich(List<EntityRespVO> entities);
}
