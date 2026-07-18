package cn.cheers.x.module.dynamicbusiness.convert.relation;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.relation.vo.RefConstraintLibraryCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.relation.vo.RefConstraintLibraryPageReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.relation.vo.RefConstraintLibraryRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.relation.vo.RefConstraintLibraryUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.relation.RefConstraintLibraryDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface RefConstraintLibraryConvert {

    RefConstraintLibraryConvert INSTANCE = Mappers.getMapper(RefConstraintLibraryConvert.class);

    RefConstraintLibraryDO convert(RefConstraintLibraryCreateReqVO reqVO);

    RefConstraintLibraryDO convert(RefConstraintLibraryUpdateReqVO reqVO);

    RefConstraintLibraryRespVO convert(RefConstraintLibraryDO bean);

    List<RefConstraintLibraryRespVO> convertList(List<RefConstraintLibraryDO> list);

    default PageResult<RefConstraintLibraryRespVO> convertPage(PageResult<RefConstraintLibraryDO> page) {
        if (page == null) {
            return null;
        }
        return new PageResult<>(convertList(page.getList()), page.getTotal());
    }
}
