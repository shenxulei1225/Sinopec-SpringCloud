package cn.cheers.x.module.dynamicbusiness.service.sop;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityService;
import org.springframework.util.StringUtils;

/**
 * SOP 标准包共享校验工具。
 *
 * <p>仅负责 SOP 存在性与基础类型值标准化，避免 query / command 各写一套。</p>
 */
final class SopStandardPackSupport {

    private SopStandardPackSupport() {
    }

    static EntityRespVO requireSop(EntityService entityService, long sopId) {
        EntityRespVO row = entityService.get(sopId, SopFieldCodes.ENTITY_TYPE_CODE);
        if (row == null || row.getId() == null) {
            throw new ServiceException(404, "SOP 不存在：" + sopId);
        }
        return row;
    }

    static String normalizeScopeType(String raw) {
        if (!StringUtils.hasText(raw)) {
            throw new ServiceException(400, "scopeType 不能为空");
        }
        String normalized = raw.trim().toUpperCase();
        if (!"CATEGORY".equals(normalized) && !"MODEL".equals(normalized)) {
            throw new ServiceException(400, "scopeType 仅支持 CATEGORY / MODEL");
        }
        return normalized;
    }

}
