package cn.cheers.x.module.dynamicbusiness.service.linkage;

/**
 * 联动配置四格的稳定字段编码。
 *
 * <p>负责：让运行时按 V187 已经写下的那四格读取。</p>
 * <p>不负责：创建字段、按中文名找列、把这四格换成另一套编码。</p>
 */
public final class LinkageFieldCodes {

    public static final String DOMAIN_ALARM = "alarm";
    public static final String LINKAGE_DIRECTORY = "linkage";

    /** 联动事件。事件主题的标准多引用。 */
    public static final String EVENT_TOPICS = "F-52da82e319184f80b6cb40d030d5c017";
    /** 触发方式。当前只执行任一事件触发。 */
    public static final String TRIGGER_MODE = "F-4caa3bbacc99442b9cefdc192af6a362";
    /** 联动方式。一行一组条件和多个动作的 JSON。 */
    public static final String MODE = "F-471fe2c2cedc4181987e6fcdb5afc47c";
    /** 执行动作。详情画在联动方式每一行右边，值写在联动方式 JSON 里。 */
    public static final String ACTION = "F-59784b7cbf854b509a8624165a7f0cee";
    /** 执行方式。自动执行或人工确认后执行。空着按自动执行。 */
    public static final String EXECUTION = "F-6fb90a47274748a3b708ad32a7dc4ce0";

    public static final String TRIGGER_ANY = "ANY";
    public static final String EXECUTION_AUTO = "AUTO";
    public static final String EXECUTION_CONFIRM = "CONFIRM";
    public static final String TARGET_EQUIPMENT = "equipment";
    public static final String TARGET_ACTION = "action";

    private LinkageFieldCodes() {
    }
}
