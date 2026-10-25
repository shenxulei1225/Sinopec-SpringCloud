package cn.cheers.x.system.framework.datapermission.config;

import cn.cheers.x.system.dal.dataobject.user.AdminUserDO;
import cn.cheers.x.framework.datapermission.core.rule.dept.DeptDataPermissionRuleCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * system 模块的数据权限 Configuration
 *
 * 
 */
@Configuration(proxyBeanMethods = false)
public class DataPermissionConfiguration {

    @Bean
    public DeptDataPermissionRuleCustomizer sysDeptDataPermissionRuleCustomizer() {
        return rule -> {
            // 用户与部门已在动态目录中。system 表只保留系统自己的按用户编号权限列。
            rule.addUserColumn(AdminUserDO.class, "id");
        };
    }

}
