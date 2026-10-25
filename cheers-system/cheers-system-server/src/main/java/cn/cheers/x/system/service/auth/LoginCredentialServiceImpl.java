package cn.cheers.x.system.service.auth;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.system.dal.dataobject.auth.LoginCredentialDO;
import cn.cheers.x.system.dal.mysql.auth.LoginCredentialMapper;
import jakarta.annotation.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 口令摘要只写 system_login_credential。
 * 不写系统用户表，也不写用户目录上的普通字段。
 */
@Service
public class LoginCredentialServiceImpl implements LoginCredentialService {

    @Resource
    private LoginCredentialMapper loginCredentialMapper;
    @Resource
    private PasswordEncoder passwordEncoder;

    @Override
    public void saveNew(Long userId, String rawPassword) {
        requireUserId(userId);
        requirePassword(rawPassword);
        if (loginCredentialMapper.selectByUserId(userId) != null) {
            throw new ServiceException(400, "这个人已经有口令");
        }
        LoginCredentialDO row = new LoginCredentialDO();
        row.setUserId(userId);
        row.setPassword(passwordEncoder.encode(rawPassword));
        loginCredentialMapper.insert(row);
    }

    @Override
    public void replace(Long userId, String rawPassword) {
        requireUserId(userId);
        requirePassword(rawPassword);
        LoginCredentialDO row = loginCredentialMapper.selectByUserId(userId);
        if (row == null) {
            throw new ServiceException(400, "这个人还没有口令，不能重置");
        }
        row.setPassword(passwordEncoder.encode(rawPassword));
        loginCredentialMapper.updateById(row);
    }

    @Override
    public void change(Long userId, String oldPassword, String newPassword) {
        requireUserId(userId);
        requirePassword(newPassword);
        LoginCredentialDO row = loginCredentialMapper.selectByUserId(userId);
        if (row == null || !StringUtils.hasText(row.getPassword())) {
            throw new ServiceException(400, "这个人还没有口令，不能重置");
        }
        if (!StringUtils.hasText(oldPassword) || !passwordEncoder.matches(oldPassword, row.getPassword())) {
            throw new ServiceException(400, "旧密码不对");
        }
        replace(userId, newPassword);
    }

    @Override
    public void deleteByUserId(Long userId) {
        if (userId == null) {
            return;
        }
        LoginCredentialDO row = loginCredentialMapper.selectByUserId(userId);
        if (row != null) {
            loginCredentialMapper.deleteById(row.getId());
        }
    }

    @Override
    public boolean matches(Long userId, String rawPassword) {
        if (userId == null || !StringUtils.hasText(rawPassword)) {
            return false;
        }
        LoginCredentialDO row = loginCredentialMapper.selectByUserId(userId);
        if (row == null || !StringUtils.hasText(row.getPassword())) {
            return false;
        }
        return passwordEncoder.matches(rawPassword, row.getPassword());
    }

    private static void requireUserId(Long userId) {
        if (userId == null) {
            throw new ServiceException(400, "用户编号不能为空");
        }
    }

    private static void requirePassword(String rawPassword) {
        if (!StringUtils.hasText(rawPassword)) {
            throw new ServiceException(400, "口令不能为空");
        }
    }
}
