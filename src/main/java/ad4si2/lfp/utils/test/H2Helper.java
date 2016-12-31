package ad4si2.lfp.utils.test;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

@Service
public class H2Helper {

    @Inject
    private DataSource dataSource;

    @PersistenceContext
    private EntityManager entityManager;

    public void clearH2DB() {
        try {
            final Connection connection = dataSource.getConnection();
            final Statement s = connection.createStatement();

            // Вырубаем FK
            s.execute("SET REFERENTIAL_INTEGRITY FALSE");

            // Получим список всех таблиц
            final Set<String> tables = new HashSet<>();
            ResultSet rs = s.executeQuery("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES  where TABLE_SCHEMA='PUBLIC'");
            while (rs.next()) {
                tables.add(rs.getString(1));
            }
            rs.close();
            //Удалим из каждой таблицы данные
            for (final String table : tables) {
                s.executeUpdate("TRUNCATE TABLE " + table);
            }

            // Начнём считать ID с 0 снова
            final Set<String> sequences = new HashSet<>();
            rs = s.executeQuery("SELECT SEQUENCE_NAME FROM INFORMATION_SCHEMA.SEQUENCES WHERE SEQUENCE_SCHEMA='PUBLIC'");
            while (rs.next()) {
                sequences.add(rs.getString(1));
            }
            rs.close();
            for (final String seq : sequences) {
                s.executeUpdate("ALTER SEQUENCE " + seq + " RESTART WITH 1");
            }

            // Включим обратно FK
            s.execute("SET REFERENTIAL_INTEGRITY TRUE");
            s.close();

            connection.close();

            // Зачистим L2-кэш
            entityManager.getEntityManagerFactory().unwrap(SessionFactory.class).getCache().evictAllRegions();

        } catch (final SQLException e) {
            throw new RuntimeException("Failed to clear in-memory database");
        }
    }
}
