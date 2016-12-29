package ad4si2.lfp.utils.jpa;

import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class HibernateDbSchemaPatchGenerator {

    private DataSource dataSource;

    private Configuration configuration;

    private Dialect dialect;

    public HibernateDbSchemaPatchGenerator(final DataSource dataSource, final Configuration configuration) {
        this.dataSource = dataSource;
        this.configuration = configuration;
    }

    public static HibernateDbSchemaPatchGenerator create(final DataSource dataSource, final Configuration configuration) {
        return new HibernateDbSchemaPatchGenerator(dataSource, configuration);
    }

    public HibernateDbSchemaPatchGenerator withDialect(final Dialect d) {
        this.dialect = d;
        return this;
    }

    public GenerationResult generate() throws SQLException, NoSuchFieldException, IllegalAccessException {
        try (final Connection connection = dataSource.getConnection()) {
            return null;

            /*
            // todo: generate sql
            final DatabaseMetadata metadata = new DatabaseMetadata(connection, dialect, configuration);
            final List<SchemaUpdateScript> scripts = configuration.generateSchemaUpdateScriptList(dialect, metadata);

            final Formatter formatter = FormatStyle.DDL.getFormatter();
            final List<String> list = scripts.stream()
                    .map(script -> formatter.format(script.getScript()))
                    .collect(Collectors.toList());

            return new GenerationResult(list);
            */
        }
    }

    public static class GenerationResult {

        private final List<String> patches;

        private GenerationResult(final List<String> patches) {
            this.patches = patches;
        }

        public GenerationResult printTo(final PrintStream out) {
            for (String patch : patches) {
                out.println(patch + ";");
            }

            return this;
        }

        public GenerationResult saveTo(final String filename, final boolean append) throws FileNotFoundException {
            return saveTo(new File(filename), append);
        }

        public GenerationResult saveTo(final File file, final boolean append) throws FileNotFoundException {
            try (final PrintStream out = new PrintStream(new FileOutputStream(file, append))) {
                printTo(out);
            }

            return this;
        }

        public GenerationResult apply(final DataSource dataSource) throws SQLException {
            try (final Connection connection = dataSource.getConnection();
                 final Statement stmt = connection.createStatement()) {

                for (String patch : patches) {
                    stmt.execute(patch);
                }
            }

            return this;
        }
    }
}