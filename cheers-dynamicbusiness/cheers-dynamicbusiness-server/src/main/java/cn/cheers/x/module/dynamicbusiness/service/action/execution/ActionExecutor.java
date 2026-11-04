package cn.cheers.x.module.dynamicbusiness.service.action.execution;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;

import java.util.Map;

/**
 * 一类动作的执行适配器。
 *
 * <p>执行适配器只接收动作定义、目标和参数，不读取联动、告警或巡检内部数据。</p>
 */
public interface ActionExecutor {

    boolean supports(String actionModelCode, String targetEntityTypeCode);

    void execute(EntityDO action, long targetEntityId, Map<String, Object> parameters);
}
