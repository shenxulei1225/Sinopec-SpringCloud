package cn.iocoder.yudao.framework.mybatis.core.context;

/**
 * 逻辑删除忽略上下文（线程级）。
 *
 * <p>用于 withDeleted 查询场景：在一次查询调用范围内临时忽略逻辑删除过滤条件。</p>
 */
public final class LogicDeleteIgnoreContext {

    private static final ThreadLocal<Boolean> IGNORE = ThreadLocal.withInitial(() -> Boolean.FALSE);

    private LogicDeleteIgnoreContext() {
    }

    public static void setIgnore(boolean ignore) {
        IGNORE.set(ignore);
    }

    public static boolean isIgnore() {
        return Boolean.TRUE.equals(IGNORE.get());
    }

    public static void clear() {
        IGNORE.remove();
    }
}
