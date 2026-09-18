package cn.cheers.x.module.dynamicbusiness.service.sop;

import cn.cheers.x.module.dynamicbusiness.controller.admin.sop.vo.RelationInstanceBindingRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.sop.vo.RelationInstanceBindingUpsertReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.sop.vo.RelationMethodBindingRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.sop.vo.RelationMethodBindingUpsertReqVO;

import java.util.List;

/**
 * 通用关系绑定。
 *
 * <p><b>负责</b>：按调用方传入的类型码与维度键读写绑定行（不附带业务语义）。</p>
 * <p><b>不负责</b>：解释业务域（检查/应急等）；编排 How UI。</p>
 * <p><b>禁止</b>：硬编码某一业务 subject/host 类型码；读路径补绑定。</p>
 */
public interface RelationBindingService {

    List<RelationMethodBindingRespVO> listMethods(String subjectType, long subjectId);

    Long upsertMethod(RelationMethodBindingUpsertReqVO req);

    RelationInstanceBindingRespVO getInstanceBinding(
            String hostType,
            long hostId,
            String subjectType,
            long subjectId,
            String dimensionKey,
            String dimensionValue);

    List<RelationInstanceBindingRespVO> listInstanceBindings(
            String hostType, long hostId, String subjectType, long subjectId);

    void upsertInstanceBinding(RelationInstanceBindingUpsertReqVO req);

}
