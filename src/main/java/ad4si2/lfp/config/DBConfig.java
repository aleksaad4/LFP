package ad4si2.lfp.config;

import ad4si2.lfp.utils.jpa.HibernateDbSchemaPatchGenerator;
import com.google.common.collect.ImmutableMap;
import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.PostgreSQL9Dialect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.util.StringUtils;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(basePackages = {"ad4si2.lfp"})
public class DBConfig {

    @Inject
    private Environment env;

    @Bean
    @Primary
    public DataSource dataSource(@Value("${spring.datasource.url}") final String url,
                                 @Value("${spring.datasource.username}") final String userName,
                                 @Value("${spring.datasource.password}") final String password) {
        final String driverClass = env.getProperty("spring.datasource.driver-class-name");
        return getDataSource(url, userName, password, driverClass);
    }

    @Bean
    @Inject
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(final DataSource dataSource,
                                                                       @Value("${hibernate.hbm2ddl.auto}") final String hbMddlAuto) {

        final LocalContainerEntityManagerFactoryBean efmBean = getEntityManagerFactoryBean(dataSource);

        // дополнительные проперти
        efmBean.getJpaPropertyMap().putAll(ImmutableMap.<String, Object>builder()
                // валидация hibernate
                .put("hibernate.hbm2ddl.auto", hbMddlAuto)
                // кэш запросов, включается на каждом методе с запросом отдельно аннотацией
                // @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
                // .put("hibernate.cache.use_query_cache", true)
                .build());

        return efmBean;
    }

    /**
     * Стратегия миграции с автогенерацией патч файла и обновлением базы (аналогично ddl-auto=update)
     * Только для DEV профиля!
     */
    // @Bean
    // @Profile(SpringProfileConstrants.SPRING_PROFILE_DEVELOPMENT)
    public FlywayMigrationStrategy migrationStrategyWithAutoPatching() {
        return flyway -> {
            try {
                // миграция (если схема пустая, создаем её)
                flyway.migrate();

                // создадим локально entity manager factory bean
                final LocalContainerEntityManagerFactoryBean emfBean = getEntityManagerFactoryBean(flyway.getDataSource());
                emfBean.afterPropertiesSet();

                // создаём патч содержащий миграцию к новой схеме hibernate
                // с автоприменением к базе
                // final Dialect dialect = new MySQL5Dialect();
                final Dialect dialect = new PostgreSQL9Dialect();

                // генерируем файл с текущими изменениями в БД
                // todo: generate sql
                HibernateDbSchemaPatchGenerator.create(flyway.getDataSource(), null)
                        .withDialect(dialect)
                        .generate()
                        .printTo(System.out)
                        .saveTo("src/main/resources/db/migration/current.sql", true);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    @Nonnull
    private DataSource getDataSource(@Nonnull final String url,
                                     @Nonnull final String userName,
                                     @Nonnull final String password, @Nonnull final String driverClass) {
        final DataSourceBuilder dataSourceBuilder = DataSourceBuilder
                .create(env.getClass().getClassLoader())
                .type(HikariDataSource.class)
                .url(url)
                .username(userName)
                .password(password);

        if (!StringUtils.isEmpty(driverClass)) {
            dataSourceBuilder.driverClassName(driverClass);
        }

        return dataSourceBuilder.build();
    }

    @Nonnull
    private LocalContainerEntityManagerFactoryBean getEntityManagerFactoryBean(final DataSource dataSource) {
        final LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();

        // зададим вендора
        final HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        // автогенерация таблиц для entity - отключаем, таблицы теперь будет генерировать flyway
        vendorAdapter.setGenerateDdl(false);
        factory.setJpaVendorAdapter(vendorAdapter);

        // датасорс
        factory.setDataSource(dataSource);

        // где искать энтити
        factory.setPackagesToScan("ad4si2.lfp");

        // проперти
        factory.setJpaPropertyMap(ImmutableMap.<String, Object>builder()
                .put("hibernate.ejb.naming_strategy", "org.hibernate.cfg.ImprovedNamingStrategy")
                .build());

        return factory;
    }
}