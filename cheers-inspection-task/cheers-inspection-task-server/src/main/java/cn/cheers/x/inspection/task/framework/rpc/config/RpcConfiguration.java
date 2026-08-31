package cn.cheers.x.inspection.task.framework.rpc.config;

import cn.cheers.x.device.protocolgateway.api.DeviceProtocolMissionApi;
import cn.cheers.x.module.dynamicbusiness.api.entity.EntityRpcApi;
import cn.cheers.x.module.dynamicbusiness.api.execution.TaskExecutionSessionApi;
import cn.cheers.x.module.platform.orchestration.api.ScheduleRunApi;
import cn.cheers.x.module.platform.runtime.api.RuntimeQueryApi;
import cn.cheers.x.module.platform.runtime.api.RuntimeSlotWriteApi;
import cn.cheers.x.module.platform.topology.api.PathNetworkApi;
import cn.cheers.x.system.api.user.AdminUserApi;
import cn.cheers.x.infra.api.file.FileApi;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

/**
 * Inspection 模块 RPC 配置类
 *
 * <p>配置 Inspection 模块需要调用的远程服务接口（显式 clients 白名单，禁止空扫包）。</p>
 */
@Configuration(value = "inspectionTaskRpcConfiguration", proxyBeanMethods = false)
@EnableFeignClients(
        clients = {
                AdminUserApi.class,
                FileApi.class,
                EntityRpcApi.class,
                TaskExecutionSessionApi.class,
                DeviceProtocolMissionApi.class,
                ScheduleRunApi.class,
                RuntimeQueryApi.class,
                RuntimeSlotWriteApi.class,
                PathNetworkApi.class
        },
        basePackages = {} // 明确指定空包，避免自动扫描
)
public class RpcConfiguration {
}
