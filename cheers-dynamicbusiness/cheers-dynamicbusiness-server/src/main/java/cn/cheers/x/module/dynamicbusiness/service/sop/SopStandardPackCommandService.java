package cn.cheers.x.module.dynamicbusiness.service.sop;

import cn.cheers.x.module.dynamicbusiness.controller.admin.sop.vo.SopStandardPackUpsertReqVO;

/**
 * SOP 标准包命令服务。
 *
 * <p><b>负责</b>：写入模板的适用范围与标准检查项包。</p>
 * <p><b>不负责</b>：动作树正文写入（仍在 SOP/检查项自身）；任务候选生成。</p>
 */
public interface SopStandardPackCommandService {

    /**
     * 覆盖保存 SOP 标准包。
     */
    void saveStandardPack(long sopId, SopStandardPackUpsertReqVO reqVO);
}
