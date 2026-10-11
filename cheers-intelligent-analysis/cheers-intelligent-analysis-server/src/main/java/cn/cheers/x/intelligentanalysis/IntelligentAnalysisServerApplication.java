package cn.cheers.x.intelligentanalysis;

import cn.cheers.x.module.dynamicbusiness.api.execution.TaskExecutionSessionApi;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 智能分析服务启动类。
 *
 * <p>负责能力目录、服务接入和分析任务。分析结果通过任务执行会话的远程接口交回。
 * 不负责动作定义、巡检步骤生成和设备协议。</p>
 */
@SpringBootApplication(scanBasePackages = "cn.cheers.x.intelligentanalysis")
@MapperScan(value = "cn.cheers.x.intelligentanalysis.dal.mysql", annotationClass = Mapper.class)
@EnableFeignClients(clients = TaskExecutionSessionApi.class)
public class IntelligentAnalysisServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(IntelligentAnalysisServerApplication.class, args);
    }
}
