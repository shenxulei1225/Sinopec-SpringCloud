package cn.cheers.x.module.dynamicbusiness.api.entity;

import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityRpcFacadeService;
import cn.cheers.x.framework.common.pojo.CommonResult;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 动态业务通用实体读 RPC 实现。
 */
@RestController
@Validated
public class EntityRpcApiImpl implements EntityRpcApi {

    @Resource
    private EntityRpcFacadeService entityRpcFacadeService;

    @Override
    public CommonResult<EntityRespDTO> getEntity(Long id, String entityTypeCode) {
        return success(entityRpcFacadeService.get(id, entityTypeCode));
    }

    @Override
    public CommonResult<List<EntityRespDTO>> listEntitiesByIds(List<Long> ids, String entityTypeCode) {
        return success(entityRpcFacadeService.listByIds(ids, entityTypeCode));
    }

    @Override
    public CommonResult<Boolean> existsEntity(Long id, String entityTypeCode) {
        return success(entityRpcFacadeService.exists(id, entityTypeCode));
    }

    @Override
    public CommonResult<List<EntityRespDTO>> listEntities(String entityTypeCode, Long modelId) {
        return success(entityRpcFacadeService.list(entityTypeCode, modelId));
    }

    @Override
    public CommonResult<EntityRespDTO> getEntityByCode(String code, String entityTypeCode) {
        return success(entityRpcFacadeService.getByCode(code, entityTypeCode));
    }

    @Override
    public CommonResult<List<EntityRespDTO>> listEntitiesByCodes(List<String> codes, String entityTypeCode) {
        return success(entityRpcFacadeService.listByCodes(codes, entityTypeCode));
    }

}
