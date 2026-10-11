package cn.cheers.x.module.dynamicbusiness.service.entity;

import cn.cheers.x.inspection.task.api.TaskUpdateHintMarkReqDTO;
import cn.cheers.x.inspection.task.api.TaskUpdateHintRpcApi;
import com.alibaba.fastjson2.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 动作库参数或检查项步骤保存成功后，通知巡检任务服务给用到这次改动的已生成任务记「需要更新」。
 * <p>不负责：判断哪些任务要记。那一步在巡检任务服务。
 * <p>通知失败只记日志。动作或检查项本身已经保存，不把这次保存回滚。
 */
@Component
@Slf4j
public class GeneratedTaskUpdateHintNotifier {

    @Resource
    private TaskUpdateHintRpcApi taskUpdateHintRpcApi;

    public void notifySaved(
            String entityTypeCode, Long entityId, String code, String name, Object before, Object after
    ) {
        if (taskUpdateHintRpcApi == null || entityId == null) {
            return;
        }
        TaskUpdateHintMarkReqDTO req = new TaskUpdateHintMarkReqDTO();
        req.setEntityTypeCode(entityTypeCode);
        req.setEntityId(entityId);
        req.setCode(code);
        req.setName(name);
        req.setBeforeJson(asJson(before));
        req.setAfterJson(asJson(after));
        if (req.getBeforeJson().equals(req.getAfterJson())) {
            return;
        }
        try {
            taskUpdateHintRpcApi.mark(req);
        } catch (RuntimeException ex) {
            log.error("已生成任务的更新标记没有写上 type={} id={}", entityTypeCode, entityId, ex);
        }
    }

    private static String asJson(Object raw) {
        if (raw == null) {
            return "";
        }
        if (raw instanceof String text) {
            return text.trim();
        }
        return JSON.toJSONString(raw);
    }
}
