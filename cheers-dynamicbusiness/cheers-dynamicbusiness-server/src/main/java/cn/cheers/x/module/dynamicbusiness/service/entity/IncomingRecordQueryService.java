package cn.cheers.x.module.dynamicbusiness.service.entity;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.IncomingRecordRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityRelationDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entity.EntityRelationMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.framework.facility.FacilityOwningFieldCodes;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 详情页签打开时，列出指向当前这条记录的对方。
 *
 * 管什么：按关联表找出源记录，并按型号名、所属场站收窄。
 * 不管什么：把对方编号写回当前这条；详情栏勾了哪些字段。
 * 禁止：没有关联行时编一份名单。
 */
@Service
public class IncomingRecordQueryService {

    private static final int MAX_ROWS = 200;

    @Resource
    private EntityRelationMapper entityRelationMapper;
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
        Set<Long> seen = new LinkedHashSet<>();
        for (EntityRelationDO row : entityRelationMapper.selectByTargetEntityIdAndSourceEntityTypeCode(
                hostEntityId, sourceType)) {
            if (row == null || row.getSourceEntityId() == null || seen.contains(row.getSourceEntityId())) {
                continue;
            }
            if (!sourceType.equals(text(row.getSourceEntityTypeCode()))) {
                continue;
            }
            if (!hostType.equals(text(row.getTargetEntityTypeCode()))) {
                continue;
            }
            String rowModelName = modelNameOf(row.getSourceModelCode());
            if (StringUtils.hasText(wantedModel) && !wantedModel.equals(rowModelName)) {
                continue;
            }
            EntityRespVO entity = loadEntity(row.getSourceEntityId(), sourceType);
            if (entity == null) {
                continue;
            }
            if (facilityId != null && facilityId > 0 && !facilityMatches(entity, facilityId)) {
                continue;
            }
            seen.add(row.getSourceEntityId());
            IncomingRecordRespVO item = new IncomingRecordRespVO();
            item.setId(row.getSourceEntityId());
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

    private String modelNameOf(String modelCode) {
        if (!StringUtils.hasText(modelCode)) {
            return "";
        }
        ModelDO model = modelMapper.selectByCode(modelCode.trim());
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
