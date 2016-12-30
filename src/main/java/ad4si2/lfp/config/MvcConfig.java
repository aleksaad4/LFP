package ad4si2.lfp.config;

import com.google.gson.Gson;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.util.ResourceUtils;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.FileNotFoundException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

@Configuration
public class MvcConfig {

    @Inject
    @Named("gsonConfiguration")
    private Gson gsonConfiguration;

    @Value("${server.http.port:-1}")
    private int httpPort;

    @Value("${server.ssl.key-store:}")
    private String keyStorePath;

    @Bean
    @Order(value = 1)
    public WebMvcConfigurerAdapter config() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addInterceptors(final InterceptorRegistry registry) {
                // registry.addInterceptor(adminLoggingInterceptor)
                //         .addPathPatterns("/admin/**");
            }

            @Override
            public void configureMessageConverters(@Nonnull final List<HttpMessageConverter<?>> converters) {
                converters.add(new ByteArrayHttpMessageConverter());
                converters.add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
                converters.add(new ResourceHttpMessageConverter());

                // настроим GSON
                final GsonHttpMessageConverter msgConverter = new GsonHttpMessageConverter();
                msgConverter.setGson(gsonConfiguration);
                converters.add(msgConverter);
            }
        };
    }

    @Bean
    public EmbeddedServletContainerCustomizer containerCustomizer() {
        return container -> {
            if (httpPort != -1) {
                if (container instanceof TomcatEmbeddedServletContainerFactory) {
                    TomcatEmbeddedServletContainerFactory containerFactory =
                            (TomcatEmbeddedServletContainerFactory) container;
                    containerFactory.addConnectorCustomizers((TomcatConnectorCustomizer) connector -> {
                        // иначе генерится неправильный путь к файлу keystore (с file: в начале)
                        URL url = null;
                        try {
                            url = ResourceUtils.getURL(keyStorePath);
                        } catch (FileNotFoundException e) {
                        }
                        if (url != null) {
                            final Http11NioProtocol protocolHandler = (Http11NioProtocol) connector.getProtocolHandler();
                            protocolHandler.setKeystoreFile(url.getFile());
                        }
                    });
                    Connector connector = new Connector(TomcatEmbeddedServletContainerFactory.DEFAULT_PROTOCOL);
                    connector.setPort(httpPort);
                    containerFactory.addAdditionalTomcatConnectors(connector);
                }
            }
        };
    }
}
