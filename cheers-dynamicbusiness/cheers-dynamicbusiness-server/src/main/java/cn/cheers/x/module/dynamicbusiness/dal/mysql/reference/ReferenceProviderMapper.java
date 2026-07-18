package cn.cheers.x.module.dynamicbusiness.dal.mysql.reference;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.reference.ReferenceProviderDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReferenceProviderMapper extends BaseMapper<ReferenceProviderDO> {

    ReferenceProviderDO selectByProviderCode(@Param("providerCode") String providerCode);

    List<ReferenceProviderDO> selectBySemanticTypeAndTenantScope(@Param("semanticType") String semanticType,
                                                                 @Param("tenantId") Long tenantId);
}
