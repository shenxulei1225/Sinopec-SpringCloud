package cn.iocoder.yudao.module.facility.management.service.sitetype.impl;

import cn.iocoder.yudao.framework.common.util.object.ObjectUtils;
import cn.iocoder.yudao.module.facility.management.controller.admin.vo.site.SiteTypeCreateReqVO;
import cn.iocoder.yudao.module.facility.management.controller.admin.vo.site.SiteTypeUpdateReqVO;
import cn.iocoder.yudao.module.facility.management.dal.dataobject.SiteTypeDO;
import cn.iocoder.yudao.module.facility.management.dal.mysql.SiteTypeMapper;
import cn.iocoder.yudao.module.facility.management.service.sitetype.SiteTypeService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.facility.management.enums.ErrorCodeConstants.*;

/**
 * 站场类型服务实现
 */
@Slf4j
@Service
public class SiteTypeServiceImpl implements SiteTypeService {

    @Resource
    private SiteTypeMapper siteTypeMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createSiteType(SiteTypeCreateReqVO createReqVO) {
        // 校验编码唯一性
        SiteTypeDO existing = siteTypeMapper.selectByTypeCode(createReqVO.getTypeCode());
        if (existing != null) {
            throw exception(SITE_TYPE_CODE_DUPLICATE);
        }

        // 创建站场类型
        SiteTypeDO siteType = new SiteTypeDO();
        siteType.setTypeCode(createReqVO.getTypeCode());
        siteType.setTypeName(createReqVO.getTypeName());
        siteType.setDescription(createReqVO.getDescription());
        siteType.setSortNo(ObjectUtils.defaultIfNull(createReqVO.getSortNo(), 0));
        siteType.setStatus(0); // 默认正常
        siteType.setRemark(createReqVO.getRemark());
        siteTypeMapper.insert(siteType);
        return siteType.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSiteType(SiteTypeUpdateReqVO updateReqVO) {
        // 校验站场类型是否存在
        SiteTypeDO siteType = siteTypeMapper.selectById(updateReqVO.getId());
        if (siteType == null) {
            throw exception(SITE_TYPE_NOT_EXISTS);
        }

        // 校验编码唯一性（排除自己）
        SiteTypeDO existing = siteTypeMapper.selectByTypeCode(updateReqVO.getTypeCode());
        if (existing != null && !existing.getId().equals(updateReqVO.getId())) {
            throw exception(SITE_TYPE_CODE_DUPLICATE);
        }

        // 更新站场类型
        siteType.setTypeCode(updateReqVO.getTypeCode());
        siteType.setTypeName(updateReqVO.getTypeName());
        siteType.setDescription(updateReqVO.getDescription());
        siteType.setSortNo(ObjectUtils.defaultIfNull(updateReqVO.getSortNo(), 0));
        siteType.setStatus(ObjectUtils.defaultIfNull(updateReqVO.getStatus(), 0));
        siteType.setRemark(updateReqVO.getRemark());
        siteTypeMapper.updateById(siteType);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSiteType(Long id) {
        // 校验站场类型是否存在
        SiteTypeDO siteType = siteTypeMapper.selectById(id);
        if (siteType == null) {
            throw exception(SITE_TYPE_NOT_EXISTS);
        }
        // 删除站场类型
        siteTypeMapper.deleteById(id);
    }

    @Override
    public SiteTypeDO getSiteType(Long id) {
        return siteTypeMapper.selectById(id);
    }

    @Override
    public List<SiteTypeDO> getNormalSiteTypeList() {
        return siteTypeMapper.selectNormalList();
    }

    @Override
    public List<SiteTypeDO> getAllSiteTypeList() {
        return siteTypeMapper.selectAllList();
    }

}
