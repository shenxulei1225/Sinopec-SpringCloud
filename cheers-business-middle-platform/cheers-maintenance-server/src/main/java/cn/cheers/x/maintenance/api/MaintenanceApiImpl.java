package cn.cheers.x.maintenance.api;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.maintenance.api.dto.FieldWorkStandardRespDTO;
import cn.cheers.x.maintenance.dal.dataobject.FieldWorkStandardDO;
import cn.cheers.x.maintenance.dal.mysql.FieldWorkStandardMapper;
import cn.cheers.x.maintenance.enums.FieldWorkStandardStatusEnum;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import static cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.cheers.x.framework.common.pojo.CommonResult.success;
import static cn.cheers.x.maintenance.enums.ErrorCodeConstants.FIELD_WORK_STANDARD_NOT_EXISTS;
import static cn.cheers.x.maintenance.enums.ErrorCodeConstants.FIELD_WORK_STANDARD_NOT_PUBLISHED;

@RestController
@Validated
public class MaintenanceApiImpl implements MaintenanceApi {

    @Resource
    private FieldWorkStandardMapper fieldWorkStandardMapper;

    @Override
    public CommonResult<FieldWorkStandardRespDTO> getPublishedStandard(Long id) {
        FieldWorkStandardDO standard = fieldWorkStandardMapper.selectById(id);
        if (standard == null) {
            throw exception(FIELD_WORK_STANDARD_NOT_EXISTS);
        }
        if (!FieldWorkStandardStatusEnum.PUBLISHED.getStatus().equals(standard.getStatus())) {
            throw exception(FIELD_WORK_STANDARD_NOT_PUBLISHED);
        }
        FieldWorkStandardRespDTO dto = new FieldWorkStandardRespDTO();
        dto.setId(standard.getId());
        dto.setCode(standard.getCode());
        dto.setName(standard.getName());
        dto.setVersionNo(standard.getVersionNo());
        dto.setScope(standard.getScope());
        dto.setStepsJson(standard.getStepsJson());
        dto.setStatus(standard.getStatus());
        return success(dto);
    }
}
