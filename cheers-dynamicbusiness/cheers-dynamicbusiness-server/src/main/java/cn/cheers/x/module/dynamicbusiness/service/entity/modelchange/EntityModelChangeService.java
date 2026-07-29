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
 * 实体变更模型：预览 fieldCode 交集迁移，提交时保留共有字段并归档源专有字段。
 * 批量接口按实体 ID 列表处理（列表勾选），不使用「当前高亮单行」。
 */
public interface EntityModelChangeService {

    EntityChangeModelPreviewRespVO preview(EntityChangeModelPreviewReqVO reqVO);

    EntityChangeModelCommitRespVO commit(EntityChangeModelCommitReqVO reqVO);

    EntityChangeModelBatchPreviewRespVO batchPreview(EntityChangeModelBatchPreviewReqVO reqVO);

    EntityChangeModelBatchCommitRespVO batchCommit(EntityChangeModelBatchCommitReqVO reqVO);
}
