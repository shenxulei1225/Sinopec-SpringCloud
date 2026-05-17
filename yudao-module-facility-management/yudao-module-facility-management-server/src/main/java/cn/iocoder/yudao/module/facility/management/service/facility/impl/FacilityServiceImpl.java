package cn.iocoder.yudao.module.facility.management.service.facility.impl;

import cn.iocoder.yudao.module.facility.management.controller.admin.vo.facility.FacilitySpatialSaveReqVO;
import cn.iocoder.yudao.module.facility.management.dal.dataobject.FacilityDO;
import cn.iocoder.yudao.module.facility.management.dal.mysql.FacilityMapper;
import cn.iocoder.yudao.module.facility.management.service.facility.FacilityService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.facility.management.enums.ErrorCodeConstants.FACILITY_NOT_EXISTS;

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
