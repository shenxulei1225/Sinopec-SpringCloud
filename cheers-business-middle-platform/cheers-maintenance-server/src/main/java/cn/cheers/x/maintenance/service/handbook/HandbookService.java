package cn.cheers.x.maintenance.service.handbook;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.maintenance.controller.admin.vo.handbook.*;

public interface HandbookService {
    Long createHandbook(HandbookCreateReqVO createReqVO);
    void updateHandbook(Long id, HandbookUpdateReqVO updateReqVO);
    HandbookRespVO getHandbook(Long id);
    PageResult<HandbookRespVO> getHandbookPage(HandbookPageReqVO pageReqVO);
    Long publishHandbook(Long id);
}
