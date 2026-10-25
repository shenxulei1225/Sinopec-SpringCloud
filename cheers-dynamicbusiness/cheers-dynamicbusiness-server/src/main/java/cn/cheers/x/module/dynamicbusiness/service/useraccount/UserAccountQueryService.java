package cn.cheers.x.module.dynamicbusiness.service.useraccount;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.useraccount.vo.UserAccountDepartmentListRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.useraccount.vo.UserAccountRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.category.CategoryDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.category.CategoryMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeMapper;
import cn.cheers.x.module.dynamicbusiness.service.category.CategoryUserRelationService;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 组织架构页读用户目录。部门上挂着的编号如果不是用户目录里的记录，只计入人数，不另查系统用户。
 * 用户目录还没建时，不查不存在的用户表，部门上的编号全部计入还没进入目录的人数。
 */
@Service
public class UserAccountQueryService {

    @Resource
    private CategoryUserRelationService categoryUserRelationService;
    @Resource
    private CategoryMapper categoryMapper;
    @Resource
    private EntityTypeMapper entityTypeMapper;
    @Resource
    private EntityService entityService;

    public UserAccountDepartmentListRespVO listByDepartments(List<Long> categoryIds) {
        UserAccountDepartmentListRespVO result = new UserAccountDepartmentListRespVO();
        if (categoryIds == null || categoryIds.isEmpty()) {
            return result;
        }
        boolean catalogReady = entityTypeMapper.selectByCode(UserAccountCodes.ENTITY_TYPE_CODE) != null;
        for (Long categoryId : categoryIds) {
            if (categoryId == null) {
                continue;
            }
            String departmentName = departmentName(categoryId);
            List<Long> userIds = categoryUserRelationService.listUserIds(categoryId);
            if (userIds == null) {
                continue;
            }
            if (!catalogReady) {
                result.setLegacyCount(result.getLegacyCount() + userIds.size());
                continue;
            }
            for (Long userId : userIds) {
                EntityRespVO entity = entityService.get(userId, UserAccountCodes.ENTITY_TYPE_CODE);
                if (entity == null) {
                    result.setLegacyCount(result.getLegacyCount() + 1);
                    continue;
                }
                result.getPeople().add(toResp(entity, categoryId, departmentName));
            }
        }
        return result;
    }

    public EntityRespVO requireCatalogUser(Long id) {
        if (id == null) {
            throw new ServiceException(400, "用户编号不能为空");
        }
        EntityRespVO entity = entityService.get(id, UserAccountCodes.ENTITY_TYPE_CODE);
        if (entity == null) {
            throw new ServiceException(404, "这个人不是用户目录里的用户");
        }
        return entity;
    }

    private String departmentName(Long categoryId) {
        CategoryDO category = categoryMapper.selectById(categoryId);
        if (category == null || category.getName() == null) {
            return "";
        }
        return category.getName();
    }

    private static UserAccountRespVO toResp(EntityRespVO entity, Long categoryId, String departmentName) {
        Map<String, Object> base = entity.getBaseFields();
        Map<String, Object> custom = entity.getCustomFields();
        UserAccountRespVO row = new UserAccountRespVO();
        row.setId(entity.getId());
        row.setAccount(text(base, "code"));
        row.setNickname(entity.getName() == null ? "" : entity.getName());
        String mobile = text(base, UserAccountCodes.MOBILE);
        if (mobile.isEmpty()) {
            mobile = text(custom, UserAccountCodes.MOBILE);
        }
        row.setMobile(mobile);
        row.setStatus(entity.getStatus());
        row.setDepartmentCategoryId(categoryId);
        row.setDepartmentName(departmentName);
        return row;
    }

    private static String text(Map<String, Object> fields, String key) {
        if (fields == null || key == null) {
            return "";
        }
        Object value = fields.get(key);
        return value == null ? "" : String.valueOf(value).trim();
    }
}
