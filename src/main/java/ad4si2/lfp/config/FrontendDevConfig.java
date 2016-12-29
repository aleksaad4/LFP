package ad4si2.lfp.config;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Map;
import java.util.Scanner;

@Configuration
@Profile(SpringProfileConstrants.SPRING_PROFILE_DEVELOPMENT)
public class FrontendDevConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(FrontendDevConfig.class);

    private static final String HOST = "0.0.0.0";

    private static final int MAGIC_THREE = 3;

    private static final int MAGIC_THOUSAND = 1000;

    /**
     * Запуск специального фронтенд-сервера и проксирование неизвестных запросов на tomcat-backend
     * (запускается на следующем за server.port свободном порту)
     */
    @Value("${dev.frontendServer:false}")
    private boolean serverMode;

    /**
     * Management endpoint HTTP port
     */
    @Value("${server.port}")
    private int serverPort;

    /**
     * Порт, на котором запушен java-backend
     */
    private int javaPort = serverPort;

    /**
     * Переопределение порта, на котором запускается java в случае serverMode-а
     */
    @Bean
    public EmbeddedServletContainerCustomizer containerCustomizer2() {
        javaPort = serverMode ? findFreePort(serverPort) : serverPort;
        if (serverMode) {
            LOGGER.warn("Server run in frontend dev mode, tomcat will be mapped on port " + javaPort);
        }
        return container -> container.setPort(javaPort);
    }

    /**
     * Запуск сборщика webpack
     */
    @Bean
    @ConditionalOnBean(value = EmbeddedServletContainerCustomizer.class)
    public WebpackRunner webpackRunner() {
        return new WebpackRunner();
    }

    /**
     * Последовательный поиск свободного порта
     *
     * @param sinceExcluded начальный порт (исключается из поиска)
     * @return следующий за sinceExcluded свободный порт
     */
    private static int findFreePort(final int sinceExcluded) {
        // findAvailableTcpPort(javaPort + 1)
        final int nextPort = sinceExcluded + 1;
        return available(nextPort) ? nextPort : findFreePort(nextPort);

    }

    /**
     * Проверка доступности порта
     *
     * @param port тестируемый порт
     * @return порт доступен
     */
    private static boolean available(final int port) {
        try (final Socket ignored = new Socket(HOST, port)) {
            return false;
        } catch (final IOException ignored) {
            return true;
        }
    }

    /**
     * Класс, отвечающий за запуск/остановку webpack
     */
    public class WebpackRunner {

        private volatile Process process = null;

        @PostConstruct
        public void init() throws Exception {
            LOGGER.info("Running Webpack");
            process = startWebpackDevServer();
            forwardTerminalMessages();
        }

        @PreDestroy
        public void destroy() {
            if (process != null) {
                LOGGER.info("Destroying Webpack");
                if (isWindows()) {
                    try {
                        final BufferedWriter out = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
                        out.write((char) MAGIC_THREE);
                        out.flush();
                        out.write((char) MAGIC_THREE + "\n");
                        out.flush();
                        Thread.sleep(MAGIC_THOUSAND);
                    } catch (final InterruptedException e) {
                        LOGGER.error("Webpack destroying has been interrupted");
                        Thread.currentThread().interrupt();
                    } catch (final IOException e) {
                        LOGGER.error("Can not destroy Webpack: " + e, e);
                    }
                }
                try {
                    process.destroyForcibly().waitFor();
                } catch (final InterruptedException e) {
                    LOGGER.error("Webpack destroying has been interrupted");
                    Thread.currentThread().interrupt();
                } finally {
                    process = null;
                }
            }
        }

        public boolean isWindows() {
            return System.getProperty("os.name").toLowerCase().contains("windows");
        }

        private Process startWebpackDevServer() throws IOException {
            final ImmutableList.Builder<String> commands = ImmutableList.builder();

            if (isWindows()) {
                commands.add("cmd");
                commands.add("/c");
                commands.add("npm.cmd", "run");
            } else {
                commands.add("./npm", "run");
            }

            if (serverMode) {

                // запускаем devServer, который будет обрабатывать статический контент, о котором он знает, а остальные
                // запросы проксировать на java-backend

                // порт, на котором будет висеть webpack-dev-server
                final String publicPort = String.valueOf(
                        serverPort == javaPort ? findFreePort(javaPort) : serverPort);

                // настройки webpack
                final Map<String, String> params = ImmutableMap.of(
                        "dev-server-host", HOST, // на каком хосте запускаем devServer
                        "dev-server-port", publicPort, // на каком порту запускаем devServer
                        "dev-server-target", "http://" + HOST + ":" + javaPort); // куда проксируем неизвестные запросы

                commands.add("dev-server");
                commands.add("--");
                for (final Map.Entry<String, String> entry : params.entrySet()) {
                    commands.add("--" + entry.getKey() + ":" + entry.getValue());
                }

            } else if (isWindows()) {
                // нет ручек - нет мороженого, однократный запуск webpack
                commands.add("build");

            } else {
                // запускаем webpack в режиме наблюдения за статическими ресурсами и автоматической перекомпиляцией
                // обновившихся ресурсов в /target/classes/static/
                commands.add("dev");
            }

            final ProcessBuilder builder = new ProcessBuilder(commands.build());
            builder.directory(new File("").getAbsoluteFile());
            LOGGER.debug("Webpack running dir: " + builder.directory());

            return builder.start();
        }

        private void forwardTerminalMessages() {
            new Thread(() -> {
                try (final Scanner inputStreamScanner = new Scanner(process.getInputStream())) {
                    while (isRun() && inputStreamScanner.hasNextLine()) {
                        final String line = inputStreamScanner.nextLine();
                        if (line.matches("\\s*chunk\\s+\\{.+\\}.*")) {
                            // chunk header
                            LOGGER.debug(line);
                        } else if (line.matches("\\s*\\[\\d+\\].*")) {
                            // chunk item
                            LOGGER.debug(line);
                        } else if (line.matches("webpack: bundle is now (\\w+)\\.")) {
                            // change state
                            LOGGER.warn(line);
                        } else {
                            LOGGER.info(line);
                        }

                    }
                } catch (final Throwable t) {
                    LOGGER.error("Webpack has failed: " + t.toString(), t);
                }
            }, "dev-server").start();

            new Thread(() -> {
                try (final Scanner errorStreamScanner = new Scanner(process.getErrorStream())) {
                    while (isRun() && errorStreamScanner.hasNextLine()) {
                        LOGGER.error(errorStreamScanner.nextLine());
                    }
                } catch (final Throwable t) {
                    LOGGER.error("Webpack has failed: " + t.toString(), t);
                }
            }, "dev-server-error").start();
        }

        private boolean isRun() {
            return process != null;
        }
    }
}
