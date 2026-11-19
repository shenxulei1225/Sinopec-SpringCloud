package cn.cheers.x.module.dynamicbusiness.service.entity;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.IncomingRecordRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.framework.facility.FacilityOwningFieldCodes;
import cn.cheers.x.module.dynamicbusiness.service.entity.refroute.EntityRefLookupQueryService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 详情页签打开时，列出指向当前这条记录的对方。
 *
 * 管什么：找出引用了当前记录的对方（单值专用列查列，其余查实体关联表），并按型号名、所属场站收窄。
 * 不管什么：把对方编号写回当前这条；详情栏勾了哪些字段。
 * 禁止：没有引用时编一份名单。
 */
@Service
public class IncomingRecordQueryService {

    private static final int MAX_ROWS = 200;
    private static final int CANDIDATE_LIMIT = 2000;

    @Resource
    private EntityRefLookupQueryService entityRefLookupQueryService;
    @Resource
    private ModelMapper modelMapper;
    @Resource
    private EntityService entityService;

    public List<IncomingRecordRespVO> list(String registryCode,
                                            Long hostEntityId,
                                            String hostEntityTypeCode,
                                            String modelName,
                                            Long facilityId) {
        if (!StringUtils.hasText(registryCode)) {
            throw new ServiceException(400, "请选择要打开的目录");
        }
        if (hostEntityId == null || hostEntityId <= 0) {
            throw new ServiceException(400, "请先点中一条记录");
        }
        if (!StringUtils.hasText(hostEntityTypeCode)) {
            throw new ServiceException(400, "请说明当前记录属于哪本目录");
        }
        String sourceType = registryCode.trim();
        String hostType = hostEntityTypeCode.trim();
        String wantedModel = modelName == null ? "" : modelName.trim();
        List<IncomingRecordRespVO> out = new ArrayList<>();
        // 候选多取一些：后面还要按型号名、所属场站收窄
        for (Long sourceId : entityRefLookupQueryService.listReferencingSubjectIds(
                sourceType, hostType, hostEntityId, CANDIDATE_LIMIT)) {
            EntityRespVO entity = loadEntity(sourceId, sourceType);
            if (entity == null) {
                continue;
            }
            String rowModelName = modelNameOf(entity.getModelId());
            if (StringUtils.hasText(wantedModel) && !wantedModel.equals(rowModelName)) {
                continue;
            }
            if (facilityId != null && facilityId > 0 && !facilityMatches(entity, facilityId)) {
                continue;
            }
            IncomingRecordRespVO item = new IncomingRecordRespVO();
            item.setId(sourceId);
            item.setName(entity.getName());
            item.setModelName(rowModelName);
            out.add(item);
            if (out.size() >= MAX_ROWS) {
                break;
            }
        }
        return out;
    }

    private EntityRespVO loadEntity(Long id, String entityTypeCode) {
        try {
            return entityService.get(id, entityTypeCode);
        } catch (RuntimeException ex) {
            return null;
        }
    }

    private String modelNameOf(Long modelId) {
        if (modelId == null) {
            return "";
        }
        ModelDO model = modelMapper.selectById(modelId);
        if (model == null || !StringUtils.hasText(model.getName())) {
            return "";
        }
        return model.getName().trim();
    }

    private static boolean facilityMatches(EntityRespVO entity, Long facilityId) {
        Map<String, Object> base = entity.getBaseFields();
        if (base == null) {
            return false;
        }
        Long owning = FacilityOwningFieldCodes.extractId(base.get(FacilityOwningFieldCodes.FIELD_CODE));
        return facilityId.equals(owning);
    }

    private static String text(String value) {
        return value == null ? "" : value.trim();
    }
}
