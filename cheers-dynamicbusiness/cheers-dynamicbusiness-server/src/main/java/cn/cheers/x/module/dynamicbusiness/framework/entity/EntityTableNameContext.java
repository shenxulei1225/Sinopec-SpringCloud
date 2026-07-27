package cn.cheers.x.module.dynamicbusiness.framework.entity;

/**
 * 实体表名上下文。
 *
 * <p>使用 ThreadLocal 传递当前操作的存储类型编码（如 {@code equipment}），
 * 供 MyBatis-Plus 动态表名拦截器把 EntityDO 占位表名改写为 {@code ent_*}。</p>
 *
 * <p><b>唯一合法用法</b>：由 {@code EntityRepository} 在访问前 {@link #set}、结束后 {@link #clear}。
 * 业务 Service 禁止自行 set 后直接调 EntityMapper。</p>
 *
 * @see EntityTableNameHandler
 */
public class EntityTableNameContext {

    private static final ThreadLocal<String> BUSINESS_TYPE_CODE = new ThreadLocal<>();

    /**
     * 设置当前线程的业务类型编码（实际存储类型）。
     *
     * @param entityTypeCode 业务类型编码
     */
    public static void set(String entityTypeCode) {
        BUSINESS_TYPE_CODE.set(entityTypeCode);
    }

    /**
     * 获取当前线程的业务类型编码。
     *
     * @return 业务类型编码；未设置则返回 null
     */
    public static String get() {
        return BUSINESS_TYPE_CODE.get();
    }

    /**
     * 清理当前线程的业务类型编码。
     *
     * <p><b>重要</b>：每次操作完成后必须调用，防止线程复用串表。</p>
     */
    public static void clear() {
        BUSINESS_TYPE_CODE.remove();
    }
}
