package cn.cheers.x.module.dynamicbusiness.service.entity.modelchange;

import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityChangeModelBatchCommitReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityChangeModelBatchCommitRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityChangeModelBatchPreviewReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityChangeModelBatchPreviewRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityChangeModelCommitReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityChangeModelCommitRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityChangeModelPreviewReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityChangeModelPreviewRespVO;

/**
 * 实体换型号：扩展字段按编码差分；预览与提交同一算法。
 * 批量接口共享目标型号字段方案并批量读实体，不按条重复加载元数据。
 */
public interface EntityModelChangeService {

    EntityChangeModelPreviewRespVO preview(EntityChangeModelPreviewReqVO reqVO);

    EntityChangeModelCommitRespVO commit(EntityChangeModelCommitReqVO reqVO);

    EntityChangeModelBatchPreviewRespVO batchPreview(EntityChangeModelBatchPreviewReqVO reqVO);

    EntityChangeModelBatchCommitRespVO batchCommit(EntityChangeModelBatchCommitReqVO reqVO);
}
