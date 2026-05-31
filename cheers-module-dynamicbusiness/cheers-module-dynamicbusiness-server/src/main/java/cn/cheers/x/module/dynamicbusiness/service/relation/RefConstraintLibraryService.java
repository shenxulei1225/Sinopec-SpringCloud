package cn.cheers.x.module.dynamicbusiness.service.relation;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.relation.vo.RefConstraintLibraryCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.relation.vo.RefConstraintLibraryPageReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.relation.vo.RefConstraintLibraryRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.relation.vo.RefConstraintLibraryUpdateReqVO;

import java.util.List;

public interface RefConstraintLibraryService {

    Long create(RefConstraintLibraryCreateReqVO reqVO);

    void update(RefConstraintLibraryUpdateReqVO reqVO);

    void delete(Long id);

    RefConstraintLibraryRespVO get(Long id);

    PageResult<RefConstraintLibraryRespVO> getPage(RefConstraintLibraryPageReqVO reqVO);

    List<RefConstraintLibraryRespVO> listByBusinessType(String businessTypeCode, String refTargetType);

    void validateConstraintType(String businessTypeCode, String refTargetType, String constraintType);

    List<RefConstraintLibraryRespVO> listAll();
}
