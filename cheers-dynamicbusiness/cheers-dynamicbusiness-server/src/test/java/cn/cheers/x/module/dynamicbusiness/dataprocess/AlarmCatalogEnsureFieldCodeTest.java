package cn.cheers.x.module.dynamicbusiness.dataprocess;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.tenant.core.context.TenantContextHolder;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeBaseFieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeBaseFieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.field.FieldMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * 告警上的「原因」以告警目录已经挂上的那一列为准。
 * 字段库里另有同名列时，不能报「有多条，对不上」，也不能改用另一条。
 */
@ExtendWith(MockitoExtension.class)
class AlarmCatalogEnsureFieldCodeTest {

    private static final String ALARM_CAUSE = "F-f1c520f1adfa4afd9f828d6eaa454aef";

    @Mock
    private FieldMapper fieldMapper;
    @Mock
    private EntityTypeBaseFieldMapper baseFieldMapper;
    @InjectMocks
    private AlarmCatalogEnsure alarmCatalogEnsure;

    @BeforeEach
    void setTenant() {
        TenantContextHolder.setTenantId(123L);
    }

    @AfterEach
    void clearTenant() {
        TenantContextHolder.clear();
    }

    @Test
    void causeUsesTheColumnAlreadyOnTheAlarmCatalog() {
        EntityTypeBaseFieldDO bound = new EntityTypeBaseFieldDO();
        bound.setFieldName(AlarmTypeCatalog.FIELD_CAUSE);
        bound.setFieldCode(ALARM_CAUSE);
        when(baseFieldMapper.selectByEntityTypeCode(AlarmTypeCatalog.DIRECTORY)).thenReturn(List.of(bound));
        FieldDO alarmCause = new FieldDO();
        alarmCause.setCode(ALARM_CAUSE);
        alarmCause.setName(AlarmTypeCatalog.FIELD_CAUSE);
        when(fieldMapper.selectOne(any())).thenReturn(alarmCause);

        assertEquals(ALARM_CAUSE, alarmCatalogEnsure.fieldCode(AlarmTypeCatalog.FIELD_CAUSE));
    }

    @Test
    void unboundDuplicateNameStillFails() {
        when(baseFieldMapper.selectByEntityTypeCode(AlarmTypeCatalog.DIRECTORY)).thenReturn(List.of());
        FieldDO alarmCause = new FieldDO();
        alarmCause.setCode(ALARM_CAUSE);
        alarmCause.setName(AlarmTypeCatalog.FIELD_CAUSE);
        FieldDO repairCause = new FieldDO();
        repairCause.setCode("F-9d3df844928d4c44bfec831a1e32dcb0");
        repairCause.setName(AlarmTypeCatalog.FIELD_CAUSE);
        when(fieldMapper.selectList(any())).thenReturn(List.of(alarmCause, repairCause));

        ServiceException ex = assertThrows(ServiceException.class,
                () -> alarmCatalogEnsure.fieldCode(AlarmTypeCatalog.FIELD_CAUSE));
        assertEquals("字段「原因」有多条，对不上", ex.getMessage());
    }
}
