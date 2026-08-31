package cn.cheers.x.module.dynamicbusiness.service.sop;

import cn.cheers.x.module.dynamicbusiness.controller.admin.sop.vo.SopInstanceBindingRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.sop.vo.SopInstanceBindingUpsertReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.sop.vo.SopInstanceCreateFromTemplateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.sop.vo.SopMethodBindingRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.sop.vo.SopMethodBindingUpsertReqVO;

import java.util.List;

/**
 * 通用 SOP 方法选用 / 实例绑定。
 *
 * <p><b>负责</b>：按调用方传入的类型码与维度键读写绑定行；从模板建独占实例。</p>
 * <p><b>不负责</b>：解释业务域（检查/应急等）；编排 How UI。</p>
 * <p><b>禁止</b>：硬编码某一业务 subject/host 类型码；读路径补绑定。</p>
 */
public interface SopBindingService {

    List<SopMethodBindingRespVO> listMethods(String subjectType, long subjectId);

    Long upsertMethod(SopMethodBindingUpsertReqVO req);

    SopInstanceBindingRespVO getInstanceBinding(
            String hostType,
            long hostId,
            String subjectType,
            long subjectId,
            String dimensionKey,
            String dimensionValue);

    List<SopInstanceBindingRespVO> listInstanceBindings(
            String hostType, long hostId, String subjectType, long subjectId);

    void upsertInstanceBinding(SopInstanceBindingUpsertReqVO req);

    /**
     * 从 SOP 模板新建实例并 upsert 绑定；始终新建 {@code is_template=false} 行。
     *
     * @return 新 SOP 实例 id
     */
    long createInstanceFromTemplate(SopInstanceCreateFromTemplateReqVO req);
}
