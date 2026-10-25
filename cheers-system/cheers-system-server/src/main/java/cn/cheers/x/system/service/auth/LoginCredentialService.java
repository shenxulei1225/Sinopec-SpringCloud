package cn.cheers.x.system.service.auth;

/**
 * 登录凭证。认证按用户编号写下口令摘要，并在登录时核对。
 * 不读取、不写入昵称、手机、部门、角色。
 */
public interface LoginCredentialService {

    /** 这个人还没有口令时写入。已有口令则拒绝。口令为空则拒绝。 */
    void saveNew(Long userId, String rawPassword);

    /** 重置口令。没有凭证时拒绝，不新建一条用户。 */
    void replace(Long userId, String rawPassword);

    /** 核对旧密码后再换成新密码。没有凭证或旧密码不对则拒绝，不新建口令。 */
    void change(Long userId, String oldPassword, String newPassword);

    /** 删掉这个人的口令摘要。没有记录时什么都不做。 */
    void deleteByUserId(Long userId);

    /** 核对口令。没有凭证、或对不上，都返回 false。不把缺凭证当成通过。 */
    boolean matches(Long userId, String rawPassword);
}
