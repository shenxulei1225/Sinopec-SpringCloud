package cn.cheers.x.module.dynamicbusiness.service.sop;

import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntitySceneQueryRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.sop.vo.SopTemplatePageReqVO;

/**
 * SOP 模板库读模型：服务端固定 {@code is_template=true}，禁止调用方绕过。
 */
public interface SopTemplateQueryService {

    /**
     * 分页查询 SOP 模板行（仅 {@code ent_sop.is_template=true}）。
     *
     * @param reqVO 分类筛选 / 关键词 / 分页；不接受客户端 is_template 覆盖
     */
    EntitySceneQueryRespVO pageTemplates(SopTemplatePageReqVO reqVO);

}
