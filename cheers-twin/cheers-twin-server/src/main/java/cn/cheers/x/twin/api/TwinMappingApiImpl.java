package cn.cheers.x.twin.api;

import cn.cheers.x.twin.api.dto.TwinMappingRespDTO;
import cn.cheers.x.twin.controller.admin.vo.TwinMappingRespVO;
import cn.cheers.x.twin.service.TwinMappingService;
import cn.cheers.x.framework.common.pojo.CommonResult;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

@RestController
@Validated
public class TwinMappingApiImpl implements TwinMappingApi {

    @Resource
    private TwinMappingService twinMappingService;

    @Override
    public CommonResult<TwinMappingRespDTO> getByFacilityId(Long facilityId) {
        TwinMappingRespVO source = twinMappingService.getByFacilityId(facilityId);
        if (source == null) {
            return success(null);
        }
        TwinMappingRespDTO dto = new TwinMappingRespDTO();
        dto.setId(source.getId());
        dto.setMappingCode(source.getMappingCode());
        dto.setMappingType(source.getMappingType());
        dto.setRelationMode(source.getRelationMode());
        dto.setFacilityId(source.getFacilityId());
        dto.setActorInstanceId(source.getActorInstanceId());
        dto.setSceneId(source.getSceneId());
        dto.setSceneCode(source.getSceneCode());
        dto.setBindSource(source.getBindSource());
        dto.setStatus(source.getStatus());
        dto.setVersion(source.getVersion());
        dto.setRemark(source.getRemark());
        dto.setExtJson(source.getExtJson());
        dto.setCreateTime(source.getCreateTime());
        dto.setUpdateTime(source.getUpdateTime());
        dto.setActorInstance(source.getActorInstance());
        return success(dto);
    }
}
