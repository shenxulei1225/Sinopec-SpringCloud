package cn.cheers.x.gis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

/**
 * GIS 标准服务：坐标/CRS/图层。过渡期 Java 包仍沿用 scene.platform.coordinate/geo。
 */
@SpringBootApplication(scanBasePackages = {
        "cn.cheers.x.gis",
        "cn.iocoder.yudao.module.scene.platform"
})
@MapperScan("cn.iocoder.yudao.module.scene.platform.dal.mysql")
public class GisServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GisServerApplication.class, args);
    }

}
