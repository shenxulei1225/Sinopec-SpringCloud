package cn.cheers.x.facility.management.service.facility.impl;

import cn.cheers.x.module.dynamicbusiness.api.category.CategoryApi;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.framework.common.util.object.BeanUtils;
import cn.cheers.x.facility.management.controller.admin.vo.facility.FacilityCreateReqVO;
import cn.cheers.x.facility.management.controller.admin.vo.facility.FacilitySpatialSaveReqVO;
import cn.cheers.x.facility.management.controller.admin.vo.facility.FacilityUpdateReqVO;
import cn.cheers.x.facility.management.dal.dataobject.FacilityDO;
import cn.cheers.x.facility.management.dal.mysql.FacilityMapper;
import cn.cheers.x.facility.management.service.facility.FacilityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.cheers.x.facility.management.enums.ErrorCodeConstants.*;

/**
 * 设施服务实现
 */
@Slf4j
@Service
public class FacilityServiceImpl implements FacilityService {

    @Resource
    private FacilityMapper facilityMapper;

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    @Lazy
    private CategoryApi categoryApi;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createFacility(FacilityCreateReqVO reqVO) {
        // 校验编码唯一性
        FacilityDO existingByCode = facilityMapper.selectByCode(reqVO.getFacilityCode());
        if (existingByCode != null) {
            throw exception(FACILITY_CODE_DUPLICATE);
        }

        // 获取分类名称
        String categoryName = reqVO.getCategoryName();
        if (categoryName == null && reqVO.getCategoryId() != null) {
            try {
                CommonResult<java.util.Map<String, Object>> categoryResult = categoryApi.getCategory(reqVO.getCategoryId(), "facility");
                if (categoryResult.isSuccess() && categoryResult.getData() != null) {
                    Object name = categoryResult.getData().get("name");
                    categoryName = name != null ? name.toString() : null;
                }
            } catch (Exception e) {
                log.warn("获取分类名称失败, categoryId={}", reqVO.getCategoryId(), e);
            }
        }

        // 创建设施
        FacilityDO facility = BeanUtils.toBean(reqVO, FacilityDO.class);
        facility.setCategoryName(categoryName);
        facility.setStatus(reqVO.getStatus() != null ? reqVO.getStatus() : 0);
        facility.setSortNo(reqVO.getSortNo() != null ? reqVO.getSortNo() : 0);
        facilityMapper.insert(facility);

        log.info("创建设施成功: id={}, code={}", facility.getId(), facility.getFacilityCode());
        return facility.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateFacility(FacilityUpdateReqVO reqVO) {
        // 校验设施存在
        FacilityDO existing = facilityMapper.selectById(reqVO.getId());
        if (existing == null) {
            throw exception(FACILITY_NOT_EXISTS);
        }

        // 校验编码唯一性（排除自身）
        FacilityDO existingByCode = facilityMapper.selectByCode(reqVO.getFacilityCode());
        if (existingByCode != null && !existingByCode.getId().equals(reqVO.getId())) {
            throw exception(FACILITY_CODE_DUPLICATE);
        }

        // 获取分类名称
        String categoryName = reqVO.getCategoryName();
        if (categoryName == null && reqVO.getCategoryId() != null) {
            try {
                CommonResult<java.util.Map<String, Object>> categoryResult = categoryApi.getCategory(reqVO.getCategoryId(), "facility");
                if (categoryResult.isSuccess() && categoryResult.getData() != null) {
                    Object name = categoryResult.getData().get("name");
                    categoryName = name != null ? name.toString() : null;
                }
            } catch (Exception e) {
                log.warn("获取分类名称失败, categoryId={}", reqVO.getCategoryId(), e);
            }
        }

        // 更新设施
        FacilityDO facility = BeanUtils.toBean(reqVO, FacilityDO.class);
        facility.setCategoryName(categoryName);
        facilityMapper.updateById(facility);

        log.info("更新设施成功: id={}, code={}", facility.getId(), facility.getFacilityCode());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFacility(Long id) {
        // 校验设施存在
        FacilityDO existing = facilityMapper.selectById(id);
        if (existing == null) {
            throw exception(FACILITY_NOT_EXISTS);
        }

        // 删除设施
        facilityMapper.deleteById(id);

        log.info("删除设施成功: id={}, code={}", id, existing.getFacilityCode());
    }

    @Override
    public FacilityDO getFacility(Long id) {
        return facilityMapper.selectById(id);
    }

    @Override
    public FacilityDO getFacilityByCode(String facilityCode) {
        return facilityMapper.selectByCode(facilityCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveSpatialInfo(FacilitySpatialSaveReqVO reqVO) {
        FacilityDO facility = facilityMapper.selectById(reqVO.getFacilityId());
        if (facility == null) {
            throw exception(FACILITY_NOT_EXISTS);
        }
        // TODO: 后续扩展设施的空间信息字段后实现保存逻辑
        log.info("保存设施空间信息: facilityId={}", reqVO.getFacilityId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateFacilityStatus(Long facilityId, Integer status) {
        FacilityDO facility = facilityMapper.selectById(facilityId);
        if (facility == null) {
            throw exception(FACILITY_NOT_EXISTS);
        }
        facility.setStatus(status);
        facilityMapper.updateById(facility);
    }

}
