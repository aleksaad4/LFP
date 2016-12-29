package ad4si2.lfp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement(mode = AdviceMode.ASPECTJ)
@EnableAsync(mode = AdviceMode.ASPECTJ)
@Configuration
@EnableAutoConfiguration
@ComponentScan(value = {"ad4si2.lfp.config"})
public class LfpApplication {

    public static void main(String[] args) {
        SpringApplication.run(LfpApplication.class, args);
    }
}
