package cn.cheers.x.module.dynamicbusiness.service.category;

import java.util.List;

public interface CategoryUserRelationService {

    void bindUsers(Long categoryId, List<Long> userIds);

    void unbindUsers(Long categoryId, List<Long> userIds);

    List<Long> listUserIds(Long categoryId);
}
