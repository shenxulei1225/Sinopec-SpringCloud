package cn.cheers.x.module.dynamicbusiness.service.category;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.category.CategoryUserRelationMapper;
import cn.cheers.x.system.api.user.AdminUserApi;
import cn.cheers.x.system.api.user.dto.AdminUserRespDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static cn.cheers.x.module.dynamicbusiness.enums.ErrorCodeConstants.CATEGORY_NOT_EXISTS;
import static cn.cheers.x.module.dynamicbusiness.enums.ErrorCodeConstants.CATEGORY_USER_IDS_EMPTY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.category.CategoryUserRelationDO;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryUserRelationServiceImplTest {

    @Mock
    private CategoryUserRelationMapper categoryUserRelationMapper;
    @Mock
    private CategoryService categoryService;
    @Mock
    private AdminUserApi adminUserApi;

    @InjectMocks
    private CategoryUserRelationServiceImpl service;

    @Test
    void bindUsers_categoryMissing_rejects() {
        when(categoryService.existsById(9L)).thenReturn(false);
        ServiceException ex = assertThrows(ServiceException.class,
                () -> service.bindUsers(9L, List.of(1L)));
        assertEquals(CATEGORY_NOT_EXISTS.getCode(), ex.getCode());
        verify(categoryUserRelationMapper, never()).insert(any(CategoryUserRelationDO.class));
    }

    @Test
    void bindUsers_emptyIds_rejects() {
        when(categoryService.existsById(1L)).thenReturn(true);
        ServiceException ex = assertThrows(ServiceException.class,
                () -> service.bindUsers(1L, List.of()));
        assertEquals(CATEGORY_USER_IDS_EMPTY.getCode(), ex.getCode());
    }

    @Test
    void bindUsers_valid_inserts() {
        when(categoryService.existsById(1L)).thenReturn(true);
        AdminUserRespDTO user = new AdminUserRespDTO();
        user.setId(10L);
        user.setNickname("张三");
        user.setStatus(0);
        when(adminUserApi.getUser(10L)).thenReturn(CommonResult.success(user));
        when(categoryUserRelationMapper.selectByCategoryAndUser(1L, 10L)).thenReturn(null);

        service.bindUsers(1L, List.of(10L));

        verify(categoryUserRelationMapper).insert(any(CategoryUserRelationDO.class));
    }
}
