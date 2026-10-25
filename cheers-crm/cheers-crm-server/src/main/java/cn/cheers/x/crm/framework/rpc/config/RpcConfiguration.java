package cn.cheers.x.crm.framework.rpc.config;

import cn.cheers.x.bpm.api.task.BpmProcessInstanceApi;
import cn.cheers.x.module.dynamicbusiness.api.directory.DepartmentDirectoryApi;
import cn.cheers.x.system.api.dept.PostApi;
import cn.cheers.x.system.api.logger.OperateLogApi;
import cn.cheers.x.system.api.user.AdminUserApi;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration(value = "crmRpcConfiguration", proxyBeanMethods = false)
@EnableFeignClients(clients = {AdminUserApi.class, DepartmentDirectoryApi.class, PostApi.class,
        OperateLogApi.class,
        BpmProcessInstanceApi.class})
public class RpcConfiguration {
}
