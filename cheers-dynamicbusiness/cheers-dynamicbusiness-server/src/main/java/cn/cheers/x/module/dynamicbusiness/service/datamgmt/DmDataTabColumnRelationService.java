package cn.cheers.x.module.dynamicbusiness.service.datamgmt;

import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmDataTabColumnRelationRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmDataTabColumnRelationSaveReqVO;

import java.util.List;

public interface DmDataTabColumnRelationService {

    List<DmDataTabColumnRelationRespVO> listByLayoutId(Long layoutId);

    List<DmDataTabColumnRelationRespVO> listByEntityTypeCode(String entityTypeCode);

    void saveRelations(DmDataTabColumnRelationSaveReqVO reqVO);

    /**
     * 通用台账默认栏间关系：按启用栏补齐「分类→型号」「型号→实体」（browseFilter）。
     * 已有同向对不覆盖；用户另配的边（如分类→实体）保留。
     */
    void ensureDefaultLedgerBrowseRelations(Long layoutId, String entityTypeCode);
}
