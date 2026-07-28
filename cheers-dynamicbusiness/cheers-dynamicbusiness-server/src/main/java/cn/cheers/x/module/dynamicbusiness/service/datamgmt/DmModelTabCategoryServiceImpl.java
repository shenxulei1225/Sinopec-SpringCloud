package cn.cheers.x.module.dynamicbusiness.service.datamgmt;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmModelTabCategoryRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmModelTabCategorySaveReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datamgmt.DmModelTabCategoryDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.datamgmt.DmModelTabCategoryMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class DmModelTabCategoryServiceImpl implements DmModelTabCategoryService {

    @Resource
    private DmModelTabCategoryMapper dmModelTabCategoryMapper;

    @Override
    public DmModelTabCategoryRespVO getByEntityTypeCode(String entityTypeCode) {
        String code = normalizeEntityTypeCode(entityTypeCode);
        DmModelTabCategoryDO row = dmModelTabCategoryMapper.selectByEntityTypeCode(code);
        if (row == null) {
            return emptyResp(code);
        }
        return toResp(row);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DmModelTabCategoryRespVO save(DmModelTabCategorySaveReqVO reqVO) {
        String code = normalizeEntityTypeCode(reqVO.getEntityTypeCode());
        DmModelTabCategoryDO row = dmModelTabCategoryMapper.selectByEntityTypeCode(code);
        if (row == null) {
            row = new DmModelTabCategoryDO();
            row.setEntityTypeCode(code);
            apply(row, reqVO);
            dmModelTabCategoryMapper.insert(row);
        } else {
            apply(row, reqVO);
            dmModelTabCategoryMapper.updateById(row);
        }
        return toResp(dmModelTabCategoryMapper.selectByEntityTypeCode(code));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clear(String entityTypeCode) {
        String code = normalizeEntityTypeCode(entityTypeCode);
        DmModelTabCategoryDO row = dmModelTabCategoryMapper.selectByEntityTypeCode(code);
        if (row != null) {
            dmModelTabCategoryMapper.deleteById(row.getId());
        }
    }

    private void apply(DmModelTabCategoryDO row, DmModelTabCategorySaveReqVO reqVO) {
        row.setEnabled(reqVO.getEnabled() == null || reqVO.getEnabled());
        row.setLabel(trimToNull(reqVO.getLabel()));
        row.setCategoryTypeCode(trimToNull(reqVO.getCategoryTypeCode()));
        row.setPropsId(reqVO.getPropsId());
    }

    private DmModelTabCategoryRespVO emptyResp(String entityTypeCode) {
        DmModelTabCategoryRespVO vo = new DmModelTabCategoryRespVO();
        vo.setEntityTypeCode(entityTypeCode);
        vo.setEnabled(false);
        return vo;
    }

    private DmModelTabCategoryRespVO toResp(DmModelTabCategoryDO row) {
        if (row == null) {
            return null;
        }
        DmModelTabCategoryRespVO vo = new DmModelTabCategoryRespVO();
        vo.setEntityTypeCode(row.getEntityTypeCode());
        vo.setEnabled(row.getEnabled() == null || row.getEnabled());
        vo.setLabel(row.getLabel());
        vo.setCategoryTypeCode(row.getCategoryTypeCode());
        vo.setPropsId(row.getPropsId());
        return vo;
    }

    private String normalizeEntityTypeCode(String entityTypeCode) {
        if (!StringUtils.hasText(entityTypeCode)) {
            throw new ServiceException(400, "entityTypeCode 不能为空");
        }
        return entityTypeCode.trim();
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }
}
