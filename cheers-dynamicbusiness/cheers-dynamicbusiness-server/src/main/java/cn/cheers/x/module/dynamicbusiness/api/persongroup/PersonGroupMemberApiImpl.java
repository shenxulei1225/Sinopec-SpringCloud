package cn.cheers.x.module.dynamicbusiness.api.persongroup;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.service.category.CategoryUserRelationService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 人员编组在册成员查询。
 * 只读分类和用户的关联，不写工单。
 */
@RestController
@Validated
public class PersonGroupMemberApiImpl implements PersonGroupMemberApi {

    @Resource
    private CategoryUserRelationService categoryUserRelationService;

    @Override
    public CommonResult<Boolean> contains(Long categoryId, Long userId) {
        if (categoryId == null || categoryId <= 0 || userId == null || userId <= 0) {
            return success(false);
        }
        List<Long> userIds = categoryUserRelationService.listUserIds(categoryId);
        return success(userIds != null && userIds.contains(userId));
    }
}
