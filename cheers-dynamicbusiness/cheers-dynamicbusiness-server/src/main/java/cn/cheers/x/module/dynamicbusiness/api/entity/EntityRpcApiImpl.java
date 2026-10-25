package cn.cheers.x.module.dynamicbusiness.api.entity;

import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityPageReqDTO;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityWriteReqDTO;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityRpcFacadeService;
import cn.cheers.x.module.dynamicbusiness.service.entity.relation.EntityRelationService;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.framework.common.pojo.PageResult;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 动态业务通用实体读 RPC 实现。
 */
@RestController
@Validated
public class EntityRpcApiImpl implements EntityRpcApi {

    @Resource
    private EntityRpcFacadeService entityRpcFacadeService;

    @Resource
    private EntityRelationService entityRelationService;

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
    public CommonResult<PageResult<EntityRespDTO>> pageEntities(EntityPageReqDTO req) {
        return success(entityRpcFacadeService.page(req));
    }

    @Override
    public CommonResult<Boolean> isDeletedByCode(String code, String entityTypeCode) {
        return success(entityRpcFacadeService.isDeletedByCode(code, entityTypeCode));
    }

    @Override
    public CommonResult<EntityRespDTO> getEntityByCode(String code, String entityTypeCode) {
        return success(entityRpcFacadeService.getByCode(code, entityTypeCode));
    }

    @Override
    public CommonResult<List<EntityRespDTO>> listEntitiesByCodes(List<String> codes, String entityTypeCode) {
        return success(entityRpcFacadeService.listByCodes(codes, entityTypeCode));
    }

    @Override
    public CommonResult<Long> createEntity(EntityWriteReqDTO req) {
        return success(entityRpcFacadeService.create(req));
    }

    @Override
    public CommonResult<Boolean> updateEntityFields(EntityWriteReqDTO req) {
        entityRpcFacadeService.updateFields(req);
        return success(true);
    }

    @Override
    public CommonResult<Long> getModelIdByCode(String modelCode) {
        return success(entityRpcFacadeService.getModelIdByCode(modelCode));
    }

    @Override
    public CommonResult<Boolean> deleteEntity(Long id, String entityTypeCode) {
        entityRpcFacadeService.delete(id, entityTypeCode);
        return success(true);
    }

    @Override
    public CommonResult<List<Long>> listCounterpartEntityIds(
            List<Long> relatedEntityIds,
            String relatedEntityTypeCode,
            String listEntityTypeCode
    ) {
        return success(entityRelationService.listCounterpartEntityIds(
                relatedEntityIds, relatedEntityTypeCode, listEntityTypeCode));
    }

    @Override
    public CommonResult<Map<Long, List<Long>>> listCounterpartEntityIdsGrouped(
            List<Long> relatedEntityIds,
            String relatedEntityTypeCode,
            String listEntityTypeCode) {
        return success(entityRelationService.listCounterpartEntityIdsGrouped(
                relatedEntityIds, relatedEntityTypeCode, listEntityTypeCode));
    }

}
