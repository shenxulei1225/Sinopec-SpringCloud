package cn.cheers.x.maintenance.service.handbook;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.maintenance.dal.dataobject.FieldWorkStandardDO;
import cn.cheers.x.maintenance.dal.dataobject.HandbookDO;
import cn.cheers.x.maintenance.dal.mysql.FieldWorkStandardMapper;
import cn.cheers.x.maintenance.dal.mysql.HandbookMapper;
import cn.cheers.x.maintenance.enums.FieldWorkStandardStatusEnum;
import cn.cheers.x.maintenance.enums.HandbookStatusEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HandbookServiceImplTest {

    @Mock private HandbookMapper handbookMapper;
    @Mock private FieldWorkStandardMapper fieldWorkStandardMapper;
    @InjectMocks private HandbookServiceImpl handbookService;

    @Test
    @DisplayName("publish：标准未发布则失败")
    void publish_standardNotPublished_throws() {
        when(handbookMapper.selectById(1L)).thenReturn(HandbookDO.builder()
                .id(1L).code("hb1").fieldStandardId(9L)
                .status(HandbookStatusEnum.DRAFT.getStatus()).build());
        when(fieldWorkStandardMapper.selectById(9L)).thenReturn(FieldWorkStandardDO.builder()
                .id(9L).status(FieldWorkStandardStatusEnum.DRAFT.getStatus()).build());
        assertThrows(ServiceException.class, () -> handbookService.publishHandbook(1L));
    }

    @Test
    @DisplayName("publish：标准已发布则插入发布版手册")
    void publish_ok() {
        when(handbookMapper.selectById(1L)).thenReturn(HandbookDO.builder()
                .id(1L).code("hb1").name("n").scope("inspection").fieldStandardId(9L)
                .status(HandbookStatusEnum.DRAFT.getStatus()).versionNo(1).build());
        when(fieldWorkStandardMapper.selectById(9L)).thenReturn(FieldWorkStandardDO.builder()
                .id(9L).status(FieldWorkStandardStatusEnum.PUBLISHED.getStatus()).build());
        when(handbookMapper.selectMaxVersionNoByCode("hb1")).thenReturn(1);
        doAnswer(inv -> { inv.getArgument(0, HandbookDO.class).setId(2L); return 1; })
                .when(handbookMapper).insert(any(HandbookDO.class));
        assertEquals(2L, handbookService.publishHandbook(1L));
    }
}
