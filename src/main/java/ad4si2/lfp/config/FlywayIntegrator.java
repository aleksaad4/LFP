package ad4si2.lfp.config;

import org.hibernate.boot.Metadata;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

public class FlywayIntegrator implements Integrator {

    @Override
    public void integrate(final Metadata metadata, final SessionFactoryImplementor sessionFactory, final SessionFactoryServiceRegistry serviceRegistry) {
    }

    @Override

    public void disintegrate(final SessionFactoryImplementor sessionFactory, final SessionFactoryServiceRegistry serviceRegistry) {
    }

}
