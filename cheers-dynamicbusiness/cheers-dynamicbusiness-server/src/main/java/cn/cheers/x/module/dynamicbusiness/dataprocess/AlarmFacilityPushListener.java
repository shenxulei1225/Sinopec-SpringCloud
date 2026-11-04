package cn.cheers.x.module.dynamicbusiness.dataprocess;

import cn.cheers.x.module.dynamicbusiness.service.computed.precompute.EntityChangeEvent;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

/**
 * 告警记录保存或改处理之后，把这座站的统计推出去。
 * 删除由删除当时记下的场站来推，这里不再读已经删掉的记录。
 * 不负责分类挂接。挂接在分类关联保存完成时另推。
 */
@Component
public class AlarmFacilityPushListener {

    @Resource
    private AlarmFacilityNotice alarmFacilityNotice;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onAlarmRecordChanged(EntityChangeEvent event) {
        if (event == null || event.getEventType() == EntityChangeEvent.EventType.DELETED) {
            return;
        }
        if (event.getEntityId() == null) {
            return;
        }
        alarmFacilityNotice.schedule(event.getEntityTypeCode(), List.of(event.getEntityId()));
    }
}
