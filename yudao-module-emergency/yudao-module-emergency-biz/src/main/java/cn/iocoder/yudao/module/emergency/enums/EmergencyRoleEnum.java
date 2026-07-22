package cn.iocoder.yudao.module.emergency.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 应急管理角色枚举
 * 
 * 定义应急管理系统中使用的角色类型
 */
@Getter
@AllArgsConstructor
public enum EmergencyRoleEnum {

    /**
     * 报告人：负责报告应急事件
     */
    REPORTER("reporter", "报告人"),

    /**
     * 执行人：负责执行应急任务
     */
    EXECUTOR("executor", "执行人"),

    /**
     * 指挥人员：负责指挥应急响应
     */
    COMMANDER("commander", "指挥人员"),

    /**
     * 评估人员：负责评估事件和响应效果
     */
    ASSESSOR("assessor", "评估人员"),

    /**
     * 资源管理员：负责管理应急资源
     */
    RESOURCE_MANAGER("resource_manager", "资源管理员"),

    /**
     * 预案管理员：负责管理应急预案
     */
    PLAN_MANAGER("plan_manager", "预案管理员"),

    /**
     * 系统管理员：负责系统管理
     */
    SYSTEM_ADMIN("system_admin", "系统管理员");

    /**
     * 角色代码
     */
    private final String code;

    /**
     * 角色名称
     */
    private final String name;

    /**
     * 根据代码获取角色枚举
     * 
     * @param code 角色代码
     * @return 角色枚举
     */
    public static EmergencyRoleEnum getByCode(String code) {
        for (EmergencyRoleEnum role : values()) {
            if (role.getCode().equals(code)) {
                return role;
            }
        }
        return null;
    }
}



