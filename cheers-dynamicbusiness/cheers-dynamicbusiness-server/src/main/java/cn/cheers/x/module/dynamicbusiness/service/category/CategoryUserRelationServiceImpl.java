package cn.cheers.x.module.dynamicbusiness.service.category;

import cn.cheers.x.framework.common.enums.CommonStatusEnum;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.category.CategoryUserRelationDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.category.CategoryUserRelationMapper;
import cn.cheers.x.system.api.user.AdminUserApi;
import cn.cheers.x.system.api.user.dto.AdminUserRespDTO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import static cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.cheers.x.module.dynamicbusiness.enums.ErrorCodeConstants.*;

@Service
@Validated
public class CategoryUserRelationServiceImpl implements CategoryUserRelationService {

    @Resource
    private CategoryUserRelationMapper categoryUserRelationMapper;
    @Resource
    private CategoryService categoryService;
    @Resource
    private AdminUserApi adminUserApi;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindUsers(Long categoryId, List<Long> userIds) {
        assertCategory(categoryId);
        List<Long> normalized = normalizeUserIds(userIds);
        for (Long userId : normalized) {
            assertUserEnabled(userId);
            CategoryUserRelationDO exist = categoryUserRelationMapper.selectByCategoryAndUser(categoryId, userId);
            if (exist != null) {
                continue;
            }
            CategoryUserRelationDO row = CategoryUserRelationDO.builder()
                    .categoryId(categoryId)
                    .userId(userId)
                    .sort(0)
                    .build();
            categoryUserRelationMapper.insert(row);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unbindUsers(Long categoryId, List<Long> userIds) {
        assertCategory(categoryId);
        List<Long> normalized = normalizeUserIds(userIds);
        for (Long userId : normalized) {
            CategoryUserRelationDO exist = categoryUserRelationMapper.selectByCategoryAndUser(categoryId, userId);
            if (exist == null) {
                continue;
            }
            categoryUserRelationMapper.deleteById(exist.getId());
        }
    }

    @Override
    public List<Long> listUserIds(Long categoryId) {
        assertCategory(categoryId);
        return categoryUserRelationMapper.selectUserIdsByCategoryId(categoryId);
    }

    private void assertCategory(Long categoryId) {
        if (categoryId == null || !categoryService.existsById(categoryId)) {
            throw exception(CATEGORY_NOT_EXISTS);
        }
    }

    private List<Long> normalizeUserIds(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            throw exception(CATEGORY_USER_IDS_EMPTY);
        }
        LinkedHashSet<Long> set = new LinkedHashSet<>();
        for (Long id : userIds) {
            if (id != null) {
                set.add(id);
            }
        }
        if (set.isEmpty()) {
            throw exception(CATEGORY_USER_IDS_EMPTY);
        }
        return new ArrayList<>(set);
    }

    private void assertUserEnabled(Long userId) {
        CommonResult<AdminUserRespDTO> result = adminUserApi.getUser(userId);
        AdminUserRespDTO user = result == null ? null : result.getCheckedData();
        if (user == null) {
            throw exception(USER_NOT_EXISTS);
        }
        if (CommonStatusEnum.isDisable(user.getStatus())) {
            throw exception(USER_IS_DISABLE, user.getNickname());
        }
    }
}
