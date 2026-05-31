package cn.iocoder.yudao.module.alarm.config;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.alarm.framework.cache.AlarmQueryCacheService;
import cn.iocoder.yudao.module.alarm.framework.cache.AlarmCacheService;
import cn.iocoder.yudao.framework.common.biz.infra.logger.ApiErrorLogCommonApi;
import cn.iocoder.yudao.framework.common.biz.infra.logger.dto.ApiErrorLogCreateReqDTO;
import cn.iocoder.yudao.framework.common.biz.system.dict.DictDataCommonApi;
import cn.iocoder.yudao.framework.common.biz.system.dict.dto.DictDataRespDTO;
import cn.iocoder.yudao.framework.common.biz.system.logger.OperateLogCommonApi;
import cn.iocoder.yudao.framework.common.biz.system.logger.dto.OperateLogCreateReqDTO;
import cn.iocoder.yudao.framework.common.biz.system.oauth2.OAuth2TokenCommonApi;
import cn.iocoder.yudao.framework.common.biz.system.oauth2.dto.OAuth2AccessTokenCheckRespDTO;
import cn.iocoder.yudao.framework.common.biz.system.oauth2.dto.OAuth2AccessTokenCreateReqDTO;
import cn.iocoder.yudao.framework.common.biz.system.oauth2.dto.OAuth2AccessTokenRespDTO;
import cn.iocoder.yudao.framework.common.biz.system.permission.PermissionCommonApi;
import cn.iocoder.yudao.framework.common.biz.system.permission.dto.DeptDataPermissionRespDTO;

import cn.iocoder.yudao.framework.tenant.core.service.TenantFrameworkService;
import cn.iocoder.yudao.framework.tenant.config.TenantProperties;
import cn.iocoder.yudao.framework.tenant.core.db.TenantDatabaseInterceptor;
import cn.iocoder.yudao.framework.mybatis.core.util.MyBatisUtils;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import cn.iocoder.yudao.module.system.api.notify.NotifyMessageSendApi;
import cn.iocoder.yudao.module.system.api.sms.SmsSendApi;
import cn.iocoder.yudao.module.system.api.mail.MailSendApi;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.xiaoymin.knife4j.spring.configuration.Knife4jProperties;
import org.mockito.Mockito;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * 告警模块测试统一配置类
 * 
 * <p>提供告警模块测试所需的通用 Mock Bean 配置。
 * 测试类可以通过 @Import(AlarmTestConfiguration.class) 引入这些 Mock。</p>
 * 
 * <h3>提供的 Mock Bean</h3>
 * <ul>
 *   <li>StringRedisTemplate - Redis 缓存（用于告警抑制等）</li>
 *   <li>RedissonClient - 分布式锁</li>
 *   <li>AdminUserApi - 用户服务 API</li>
 *   <li>NotifyMessageSendApi - 站内信通知 API</li>
 *   <li>SmsSendApi - 短信发送 API</li>
 *   <li>MailSendApi - 邮件发送 API</li>
 *   <li>TenantFrameworkService - 租户服务</li>
 *   <li>其他框架依赖的 Mock Bean</li>
 * </ul>
 * 
 * <h3>使用示例</h3>
 * <pre>
 * {@code
 * @Import({
 *     AlarmTestConfiguration.class,
 *     AlarmServiceImpl.class,
 *     // 其他需要的服务...
 * })
 * public class AlarmServiceTest extends BaseDbUnitTest {
 *     // 测试代码...
 * }
 * }
 * </pre>
 *
 * @author 告警管理模块
 */
@TestConfiguration
public class AlarmTestConfiguration {

    // ========== Redis Mock ==========

    /**
     * 提供 ObjectMapper 的 Bean
     * 
     * <p>用于 JSON 序列化和反序列化</p>
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }

    /**
     * 提供 StringRedisTemplate 的 Mock Bean
     * 
     * <p>用于告警抑制检查等需要 Redis 的场景</p>
     */
    @Bean
    @Primary
    public StringRedisTemplate stringRedisTemplate() {
        StringRedisTemplate template = mock(StringRedisTemplate.class);
        @SuppressWarnings("unchecked")
        ValueOperations<String, String> valueOps = mock(ValueOperations.class);
        when(template.opsForValue()).thenReturn(valueOps);
        // 默认返回 null，表示缓存中没有数据
        when(valueOps.get(any())).thenReturn(null);
        return template;
    }

    /**
     * 提供 AlarmQueryCacheService 的 Mock Bean
     * 
     * <p>用于告警查询缓存服务</p>
     */
    @Bean
    @Primary
    public AlarmQueryCacheService alarmQueryCacheService() {
        AlarmQueryCacheService service = mock(AlarmQueryCacheService.class);
        // 默认返回 null，表示缓存未命中
        when(service.getAlarmDetailCache(any())).thenReturn(null);
        when(service.getEnabledAlarmRulesCache()).thenReturn(null);
        when(service.getEnabledLinkageRulesCache()).thenReturn(null);
        when(service.getRealTimeAlarmsCache()).thenReturn(null);
        return service;
    }

    /**
     * 提供 AlarmCacheService 的 Mock Bean
     * 
     * <p>用于告警统计和类型缓存服务</p>
     */
    @Bean
    @Primary
    public AlarmCacheService alarmCacheService() {
        AlarmCacheService service = mock(AlarmCacheService.class);
        // 默认返回 null，表示缓存未命中
        when(service.getRealTimeStatisticsCache()).thenReturn(null);
        when(service.getTodayStatisticsCache()).thenReturn(null);
        when(service.getLevelCountCache()).thenReturn(null);
        when(service.getAlarmTypeTreeCache()).thenReturn(null);
        when(service.getAlarmTypePathCache(any())).thenReturn(null);
        when(service.getAlarmTypeEntityCache(any())).thenReturn(null);
        return service;
    }

    /**
     * 提供 RedisTemplate 的 Mock Bean
     */
    @Bean
    @Primary
    public RedisTemplate<String, Object> redisTemplate() {
        @SuppressWarnings("unchecked")
        RedisTemplate<String, Object> template = mock(RedisTemplate.class);
        @SuppressWarnings("unchecked")
        ValueOperations<String, Object> ops = mock(ValueOperations.class);
        when(template.opsForValue()).thenReturn(ops);
        return template;
    }

    /**
     * 提供 RedissonClient 的 Mock Bean
     * 
     * <p>用于分布式锁等场景</p>
     */
    @Bean
    @Primary
    public RedissonClient redissonClient() {
        return mock(RedissonClient.class);
    }

    // ========== 用户服务 Mock ==========

    /**
     * 提供 AdminUserApi 的 Mock Bean
     * 
     * <p>用于获取用户信息、通知接收人等场景</p>
     */
    @Bean
    @Primary
    public AdminUserApi adminUserApi() {
        return new AdminUserApi() {
            private AdminUserRespDTO mockUser(Long id) {
                AdminUserRespDTO dto = new AdminUserRespDTO();
                dto.setId(id);
                dto.setNickname("测试用户-" + id);
                dto.setStatus(0);
                dto.setDeptId(1L);
                dto.setPostIds(Set.of(1L));
                dto.setMobile("13800000000");
                dto.setAvatar("");
                return dto;
            }

            @Override
            public CommonResult<AdminUserRespDTO> getUser(Long id) {
                return CommonResult.success(mockUser(id));
            }

            @Override
            public CommonResult<List<AdminUserRespDTO>> getUserListBySubordinate(Long id) {
                return CommonResult.success(List.of(mockUser(id)));
            }

            @Override
            public CommonResult<List<AdminUserRespDTO>> getUserList(Collection<Long> ids) {
                if (ids == null || ids.isEmpty()) {
                    return CommonResult.success(Collections.emptyList());
                }
                return CommonResult.success(ids.stream().map(this::mockUser).toList());
            }

            @Override
            public CommonResult<List<AdminUserRespDTO>> getUserListByDeptIds(Collection<Long> deptIds) {
                return CommonResult.success(List.of(mockUser(1L)));
            }

            @Override
            public CommonResult<List<AdminUserRespDTO>> getUserListByPostIds(Collection<Long> postIds) {
                return CommonResult.success(List.of(mockUser(1L)));
            }

            @Override
            public CommonResult<List<AdminUserRespDTO>> getUserListByNickname(String nickname) {
                return CommonResult.success(List.of(mockUser(1L)));
            }

            @Override
            public CommonResult<Boolean> validateUserList(Collection<Long> ids) {
                return CommonResult.success(true);
            }
        };
    }

    // ========== 通知服务 Mock ==========

    /**
     * 提供 NotifyMessageSendApi 的 Mock Bean
     * 
     * <p>用于发送站内信通知</p>
     */
    @Bean
    @Primary
    public NotifyMessageSendApi notifyMessageSendApi() {
        NotifyMessageSendApi api = mock(NotifyMessageSendApi.class);
        when(api.sendSingleMessageToAdmin(any())).thenReturn(CommonResult.success(1L));
        when(api.sendSingleMessageToMember(any())).thenReturn(CommonResult.success(1L));
        return api;
    }

    /**
     * 提供 SmsSendApi 的 Mock Bean
     * 
     * <p>用于发送短信通知</p>
     */
    @Bean
    @Primary
    public SmsSendApi smsSendApi() {
        SmsSendApi api = mock(SmsSendApi.class);
        when(api.sendSingleSmsToAdmin(any())).thenReturn(CommonResult.success(1L));
        when(api.sendSingleSmsToMember(any())).thenReturn(CommonResult.success(1L));
        return api;
    }

    /**
     * 提供 MailSendApi 的 Mock Bean
     * 
     * <p>用于发送邮件通知</p>
     */
    @Bean
    @Primary
    public MailSendApi mailSendApi() {
        MailSendApi api = mock(MailSendApi.class);
        when(api.sendSingleMailToAdmin(any())).thenReturn(CommonResult.success(1L));
        when(api.sendSingleMailToMember(any())).thenReturn(CommonResult.success(1L));
        return api;
    }

    // ========== 框架服务 Mock ==========

    /**
     * 提供 Knife4jProperties 的 Bean
     */
    @Bean
    public Knife4jProperties knife4jProperties() {
        return new Knife4jProperties();
    }


    /**
     * 提供 ApiErrorLogCommonApi 的 Mock Bean
     */
    @Bean
    @Primary
    public ApiErrorLogCommonApi apiErrorLogCommonApi() {
        return createReqDTO -> CommonResult.success(true);
    }

    /**
     * 提供 OAuth2TokenCommonApi 的 Mock Bean
     */
    @Bean
    @Primary
    public OAuth2TokenCommonApi oAuth2TokenCommonApi() {
        return new OAuth2TokenCommonApi() {
            @Override
            public CommonResult<OAuth2AccessTokenRespDTO> createAccessToken(OAuth2AccessTokenCreateReqDTO createReqDTO) {
                OAuth2AccessTokenRespDTO respDTO = new OAuth2AccessTokenRespDTO();
                respDTO.setAccessToken("mock-token");
                respDTO.setRefreshToken("mock-refresh-token");
                return CommonResult.success(respDTO);
            }

            @Override
            public CommonResult<OAuth2AccessTokenRespDTO> refreshAccessToken(String refreshToken, String clientId) {
                OAuth2AccessTokenRespDTO respDTO = new OAuth2AccessTokenRespDTO();
                respDTO.setAccessToken("mock-refreshed-token");
                respDTO.setRefreshToken("mock-refresh-token");
                return CommonResult.success(respDTO);
            }

            @Override
            public CommonResult<OAuth2AccessTokenCheckRespDTO> checkAccessToken(String accessToken) {
                OAuth2AccessTokenCheckRespDTO respDTO = new OAuth2AccessTokenCheckRespDTO();
                respDTO.setUserId(1L);
                respDTO.setUserType(1);
                respDTO.setTenantId(1L);
                respDTO.setScopes(List.of("read", "write"));
                respDTO.setExpiresTime(LocalDateTime.now().plusHours(1));
                return CommonResult.success(respDTO);
            }

            @Override
            public CommonResult<OAuth2AccessTokenRespDTO> removeAccessToken(String accessToken) {
                OAuth2AccessTokenRespDTO respDTO = new OAuth2AccessTokenRespDTO();
                respDTO.setAccessToken(accessToken);
                respDTO.setRefreshToken("mock-refresh-token");
                return CommonResult.success(respDTO);
            }
        };
    }

    /**
     * 提供 TenantFrameworkService 的 Mock Bean
     */
    @Bean
    @Primary
    public TenantFrameworkService tenantFrameworkService() {
        return new TenantFrameworkService() {
            @Override
            public List<Long> getTenantIds() {
                return List.of(1L);
            }

            @Override
            public void validTenant(Long id) {
                // Mock 实现：测试环境总是验证通过
            }
        };
    }

    /**
     * 提供 OperateLogCommonApi 的 Mock Bean
     */
    @Bean
    @Primary
    public OperateLogCommonApi operateLogCommonApi() {
        return createReqDTO -> CommonResult.success(true);
    }

    /**
     * 提供 DictDataCommonApi 的 Mock Bean
     */
    @Bean
    @Primary
    public DictDataCommonApi dictDataCommonApi() {
        return dictType -> CommonResult.success(Collections.emptyList());
    }

    /**
     * 提供 PermissionCommonApi 的 Mock Bean
     */
    @Bean
    @Primary
    public PermissionCommonApi permissionCommonApi() {
        return new PermissionCommonApi() {
            @Override
            public CommonResult<Boolean> hasAnyPermissions(Long userId, String... permissions) {
                return CommonResult.success(true);
            }

            @Override
            public CommonResult<Boolean> hasAnyRoles(Long userId, String... roles) {
                return CommonResult.success(true);
            }

            @Override
            public CommonResult<DeptDataPermissionRespDTO> getDeptDataPermission(Long userId) {
                DeptDataPermissionRespDTO respDTO = new DeptDataPermissionRespDTO();
                respDTO.setSelf(true);
                respDTO.setDeptIds(Collections.emptySet());
                return CommonResult.success(respDTO);
            }
        };
    }

    /**
     * 提供 TenantLineInnerInterceptor 的 Bean
     */
    @Bean
    public TenantLineInnerInterceptor tenantLineInnerInterceptor(TenantProperties properties,
                                                                 MybatisPlusInterceptor interceptor) {
        TenantLineInnerInterceptor inner = new TenantLineInnerInterceptor(new TenantDatabaseInterceptor(properties));
        MyBatisUtils.addInterceptor(interceptor, inner, 0);
        return inner;
    }

    /**
     * 提供 TenantProperties 的 Bean
     */
    @Bean
    public TenantProperties tenantProperties() {
        TenantProperties properties = new TenantProperties();
        properties.setEnable(true);
        return properties;
    }

}
