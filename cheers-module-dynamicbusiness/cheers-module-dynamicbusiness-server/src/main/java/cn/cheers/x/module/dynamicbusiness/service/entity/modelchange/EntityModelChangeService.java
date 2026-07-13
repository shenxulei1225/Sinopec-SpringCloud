package cn.cheers.x.module.dynamicbusiness.service.entity.modelchange;

import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityChangeModelCommitReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityChangeModelCommitRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityChangeModelPreviewReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityChangeModelPreviewRespVO;

/**
 * 实体变更模型：预览 fieldCode 交集迁移，提交时保留共有字段并归档源专有字段。
 */
public interface EntityModelChangeService {

    EntityChangeModelPreviewRespVO preview(EntityChangeModelPreviewReqVO reqVO);

    EntityChangeModelCommitRespVO commit(EntityChangeModelCommitReqVO reqVO);
}
