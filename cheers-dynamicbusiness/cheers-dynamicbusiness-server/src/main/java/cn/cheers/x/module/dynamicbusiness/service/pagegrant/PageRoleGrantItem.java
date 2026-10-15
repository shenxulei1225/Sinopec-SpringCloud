package cn.cheers.x.module.dynamicbusiness.service.pagegrant;

import java.util.List;

/**
 * 一个页面上已勾选的动作。
 * actions 只含角色页上这个页面列出的那些项。
 */
public record PageRoleGrantItem(String pageCode, List<String> actions) {
}
