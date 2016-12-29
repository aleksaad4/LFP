package ad4si2.lfp.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

import javax.inject.Inject;
import java.io.IOException;

@Configuration
@EnableCaching
@ComponentScan("ad4si2.lfp")
public class AppConfig {

    @Inject
    private Environment env;

    @Bean
    public MessageSource messageSource() throws IOException {
        final ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();

        final ClassLoader cl = this.getClass().getClassLoader();
        final ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(cl);
        final Resource[] resources = resolver.getResources("classpath*:/messages/**/*messages.properties");

        final String[] baseNames = new String[resources.length];

        int counter = 0;
        for (final Resource resource : resources) {
            String absolutePath = resource.getURL().toString();
            baseNames[counter++] = absolutePath.substring(absolutePath.indexOf("messages"), absolutePath.indexOf(".properties"));
        }

        messageSource.setBasenames(baseNames);
        messageSource.setUseCodeAsDefaultMessage(true);
        messageSource.setDefaultEncoding("utf-8");
        return messageSource;
    }

    @Bean
    public TaskScheduler taskScheduler() {
        return new ConcurrentTaskScheduler();
    }
}
