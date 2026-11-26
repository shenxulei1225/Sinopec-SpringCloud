package cn.cheers.x.module.dynamicbusiness.service.pipeline;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.framework.entity.EntityTableNameHandler;
import cn.cheers.x.module.dynamicbusiness.service.datapermission.DataScopeQueryService;
import cn.cheers.x.module.dynamicbusiness.service.datapermission.ScopeRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PipelineGpsExtentQueryServiceTest {

    @Mock
    private EntityTableNameHandler tables;
    @Mock
    private JdbcTemplate jdbc;
    @Mock
    private DataScopeQueryService scopes;

    @Test
    void facilityOutsidePostCoverageIsRejected() {
        when(scopes.listMyFacilities()).thenReturn(List.of(new ScopeRecord(2L, "另一座站", "other")));
        PipelineGpsExtentQueryService service = new PipelineGpsExtentQueryService(tables, jdbc, scopes);

        ServiceException error = assertThrows(ServiceException.class, () -> service.extentOfFacility(1L));

        assertEquals(403, error.getCode());
        verifyNoInteractions(jdbc);
    }

    @Test
    void noGpsPointReturnsNull() throws Exception {
        when(scopes.listMyFacilities()).thenReturn(List.of(new ScopeRecord(1L, "管廊", "gallery")));
        when(tables.resolvePhysicalTableName("pipeline")).thenReturn("ent_pipeline_t123");
        when(jdbc.query(anyString(), any(RowMapper.class), eq(1L))).thenAnswer(invocation -> {
            RowMapper<?> mapper = invocation.getArgument(1);
            ResultSet rs = mock(ResultSet.class);
            when(rs.getDouble(anyString())).thenReturn(0.0);
            when(rs.wasNull()).thenReturn(true);
            return Collections.singletonList(mapper.mapRow(rs, 0));
        });
        PipelineGpsExtentQueryService service = new PipelineGpsExtentQueryService(tables, jdbc, scopes);

        assertNull(service.extentOfFacility(1L));
    }

    @Test
    void savedGpsBecomesTheExtent() throws Exception {
        when(scopes.listMyFacilities()).thenReturn(List.of(new ScopeRecord(1L, "管廊", "gallery")));
        when(tables.resolvePhysicalTableName("pipeline")).thenReturn("ent_pipeline_t123");
        when(jdbc.query(anyString(), any(RowMapper.class), eq(1L))).thenAnswer(invocation -> {
            String sql = invocation.getArgument(0);
            if (!sql.contains("ent_pipeline_t123") || !sql.contains("facility_id = ?")) {
                throw new AssertionError(sql);
            }
            RowMapper<?> mapper = invocation.getArgument(1);
            ResultSet rs = mock(ResultSet.class);
            when(rs.getDouble("west")).thenReturn(114.47532467);
            when(rs.getDouble("east")).thenReturn(114.52862369);
            when(rs.getDouble("south")).thenReturn(30.44728087);
            when(rs.getDouble("north")).thenReturn(30.48519315);
            when(rs.wasNull()).thenReturn(false);
            return List.of(mapper.mapRow(rs, 0));
        });
        PipelineGpsExtentQueryService service = new PipelineGpsExtentQueryService(tables, jdbc, scopes);

        PipelineGpsExtent extent = service.extentOfFacility(1L);

        assertEquals(114.47532467, extent.west());
        assertEquals(114.52862369, extent.east());
        assertEquals(30.44728087, extent.south());
        assertEquals(30.48519315, extent.north());
    }

    @Test
    void zeroAreaIsNotAnExtent() {
        assertNull(PipelineGpsExtentQueryService.extentOrNull(114.5, 30.4, 114.5, 30.5));
    }
}
