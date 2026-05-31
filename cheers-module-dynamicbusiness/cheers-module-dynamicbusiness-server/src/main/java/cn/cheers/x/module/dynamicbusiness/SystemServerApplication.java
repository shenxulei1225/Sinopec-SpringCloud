package cn.cheers.x.module.dynamicbusiness;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 项目的启动类
 *
 * 如果你碰到启动的问题，请认真阅读 https://cloud.iocoder.cn/quick-start/ 文章
 * 如果你碰到启动的问题，请认真阅读 https://cloud.iocoder.cn/quick-start/ 文章
 * 如果你碰到启动的问题，请认真阅读 https://cloud.iocoder.cn/quick-start/ 文章
 *
 * 
 */
@SpringBootApplication(scanBasePackages = {
        "cn.cheers.x.module.dynamicbusiness"
})
@EnableFeignClients(basePackages = {
        "cn.iocoder.yudao.module.system.api",
        "cn.iocoder.yudao.module.infra.api"
})
public class SystemServerApplication {

    public static void main(String[] args) {
        // 如果你碰到启动的问题，请认真阅读 https://cloud.iocoder.cn/quick-start/ 文章
        // 如果你碰到 启动的问题，请认真阅读 https://cloud.iocoder.cn/quick-start/ 文章
        // 如果你碰到启动的问题，请认真阅读 https://cloud.iocoder.cn/quick-start/ 文章

        SpringApplication.run(SystemServerApplication.class, args);

        // 如果你碰到启动的问题，请认真阅读 https://cloud.iocoder.cn/quick-start/ 文章
        // 如果你碰到启动的问题，请认真阅读 https://cloud.iocoder.cn/quick-start/ 文章
        // 如果你碰到启动的问题，请认真阅读 https://cloud.iocoder.cn/quick-start/ 文章
    }

}
