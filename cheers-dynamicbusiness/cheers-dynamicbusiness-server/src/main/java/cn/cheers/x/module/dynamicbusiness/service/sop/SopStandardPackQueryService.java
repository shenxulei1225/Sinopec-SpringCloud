package cn.cheers.x.module.dynamicbusiness.service.sop;

import cn.cheers.x.module.dynamicbusiness.controller.admin.sop.vo.SopStandardPackRespVO;

/**
 * SOP 标准包查询服务。
 *
 * <p><b>负责</b>：查询模板的适用范围与标准检查项包。</p>
 * <p><b>不负责</b>：标准包写入；设备候选派生。</p>
 */
public interface SopStandardPackQueryService {

    /**
     * 获取 SOP 的标准包。
     */
    SopStandardPackRespVO getStandardPack(long sopId);
}
