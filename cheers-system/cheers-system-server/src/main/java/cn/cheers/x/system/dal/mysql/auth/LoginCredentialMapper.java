package cn.cheers.x.system.dal.mysql.auth;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.system.dal.dataobject.auth.LoginCredentialDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LoginCredentialMapper extends BaseMapperX<LoginCredentialDO> {

    default LoginCredentialDO selectByUserId(Long userId) {
        return selectOne(new LambdaQueryWrapperX<LoginCredentialDO>()
                .eq(LoginCredentialDO::getUserId, userId));
    }
}
