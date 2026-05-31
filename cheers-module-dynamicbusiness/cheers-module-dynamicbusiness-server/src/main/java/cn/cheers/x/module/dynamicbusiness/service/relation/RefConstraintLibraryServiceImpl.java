package cn.cheers.x.module.dynamicbusiness.service.relation;

import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.relation.vo.RefConstraintLibraryCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.relation.vo.RefConstraintLibraryPageReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.relation.vo.RefConstraintLibraryRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.relation.vo.RefConstraintLibraryUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.convert.relation.RefConstraintLibraryConvert;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.relation.RefConstraintLibraryDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.relation.RefConstraintLibraryMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.util.List;

import static cn.cheers.x.module.dynamicbusiness.enums.ErrorCodeConstants.REF_CONSTRAINT_LIBRARY_NOT_EXISTS;
import static cn.cheers.x.module.dynamicbusiness.enums.ErrorCodeConstants.REF_CONSTRAINT_TYPE_NOT_CONFIGURED;

@Service
@Validated
@Slf4j
public class RefConstraintLibraryServiceImpl implements RefConstraintLibraryService {

    @Resource
    private RefConstraintLibraryMapper refConstraintLibraryMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(RefConstraintLibraryCreateReqVO reqVO) {
        RefConstraintLibraryDO data = RefConstraintLibraryConvert.INSTANCE.convert(reqVO);
        refConstraintLibraryMapper.insert(data);
        return data.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(RefConstraintLibraryUpdateReqVO reqVO) {
        RefConstraintLibraryDO exist = refConstraintLibraryMapper.selectById(reqVO.getId());
        if (exist == null) {
            throw ServiceExceptionUtil.exception(REF_CONSTRAINT_LIBRARY_NOT_EXISTS);
        }
        RefConstraintLibraryDO update = RefConstraintLibraryConvert.INSTANCE.convert(reqVO);
        refConstraintLibraryMapper.updateById(update);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        RefConstraintLibraryDO exist = refConstraintLibraryMapper.selectById(id);
        if (exist == null) {
            throw ServiceExceptionUtil.exception(REF_CONSTRAINT_LIBRARY_NOT_EXISTS);
        }
        refConstraintLibraryMapper.deleteById(id);
    }

    @Override
    public RefConstraintLibraryRespVO get(Long id) {
        return RefConstraintLibraryConvert.INSTANCE.convert(refConstraintLibraryMapper.selectById(id));
    }

    @Override
    public PageResult<RefConstraintLibraryRespVO> getPage(RefConstraintLibraryPageReqVO reqVO) {
        PageResult<RefConstraintLibraryDO> page = refConstraintLibraryMapper.selectPage(reqVO);
        return RefConstraintLibraryConvert.INSTANCE.convertPage(page);
    }

    @Override
    public List<RefConstraintLibraryRespVO> listByBusinessType(String businessTypeCode, String refTargetType) {
        return RefConstraintLibraryConvert.INSTANCE.convertList(
                refConstraintLibraryMapper.selectByBusinessType(businessTypeCode, refTargetType));
    }

    @Override
    public void validateConstraintType(String businessTypeCode, String refTargetType, String constraintType) {
        if (!refConstraintLibraryMapper.existsEnabled(businessTypeCode, refTargetType, constraintType)) {
            throw ServiceExceptionUtil.exception(REF_CONSTRAINT_TYPE_NOT_CONFIGURED, constraintType);
        }
    }

    @Override
    public List<RefConstraintLibraryRespVO> listAll() {
        return RefConstraintLibraryConvert.INSTANCE.convertList(refConstraintLibraryMapper.selectAll());
    }
}
