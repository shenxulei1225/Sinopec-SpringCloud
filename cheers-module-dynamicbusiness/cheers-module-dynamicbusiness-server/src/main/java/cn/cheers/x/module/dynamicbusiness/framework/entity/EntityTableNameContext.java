package cn.cheers.x.module.dynamicbusiness.framework.entity;

/**
 * 实体表名上下文
 * 
 * <p>使用 ThreadLocal 传递当前操作的 businessTypeCode，
 * 供 MyBatis-Plus 动态表名拦截器使用。</p>
 * 
 * <h3>使用示例</h3>
 * <pre>{@code
 * try {
 *     EntityTableNameContext.set("equipment");
 *     // 执行数据库操作，表名会自动替换为 biz_equipment
 *     mapper.selectById(id);
 * } finally {
 *     EntityTableNameContext.clear();
 * }
 * }</pre>
 * 
 * @author 基础服务模块
 * @see EntityTableNameHandler
 */
public class EntityTableNameContext {

    private static final ThreadLocal<String> BUSINESS_TYPE_CODE = new ThreadLocal<>();

    /**
     * 设置当前线程的业务类型编码
     * 
     * @param businessTypeCode 业务类型编码
     */
    public static void set(String businessTypeCode) {
        BUSINESS_TYPE_CODE.set(businessTypeCode);
    }

    /**
     * 获取当前线程的业务类型编码
     * 
     * @return 业务类型编码，如果未设置则返回 null
     */
    public static String get() {
        return BUSINESS_TYPE_CODE.get();
    }

    /**
     * 清理当前线程的业务类型编码
     * 
     * <p><b>重要</b>：每次操作完成后必须调用此方法，防止内存泄漏。</p>
     */
    public static void clear() {
        BUSINESS_TYPE_CODE.remove();
    }
}
































