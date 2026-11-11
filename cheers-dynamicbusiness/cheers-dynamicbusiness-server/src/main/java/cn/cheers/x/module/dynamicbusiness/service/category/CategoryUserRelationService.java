package cn.cheers.x.module.dynamicbusiness.service.category;

import java.util.List;

public interface CategoryUserRelationService {

    void bindUsers(Long categoryId, List<Long> userIds);

    void unbindUsers(Long categoryId, List<Long> userIds);

    List<Long> listUserIds(Long categoryId);

    /** 这个人挂在哪些分类上。执行人用它判断本班待领的工单。 */
    List<Long> listCategoryIds(Long userId);
}
