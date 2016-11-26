package info.novatec.testit.livingdoc.server.database.hibernate;

import java.util.EnumSet;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;

import info.novatec.testit.livingdoc.server.domain.Execution;
import info.novatec.testit.livingdoc.server.domain.Project;
import info.novatec.testit.livingdoc.server.domain.Reference;
import info.novatec.testit.livingdoc.server.domain.Repository;
import info.novatec.testit.livingdoc.server.domain.RepositoryType;
import info.novatec.testit.livingdoc.server.domain.Requirement;
import info.novatec.testit.livingdoc.server.domain.Runner;
import info.novatec.testit.livingdoc.server.domain.Specification;
import info.novatec.testit.livingdoc.server.domain.SystemInfo;
import info.novatec.testit.livingdoc.server.domain.SystemUnderTest;


public class HibernateDatabase {
    private final Properties properties;
    private final Metadata metadata;

    public HibernateDatabase(Properties properties) throws HibernateException {

        StandardServiceRegistry registry = new StandardServiceRegistryBuilder().applySettings(properties).build();
        MetadataSources metadataSources = new MetadataSources(registry);
        metadataSources.addAnnotatedClass(SystemInfo.class)
            .addAnnotatedClass(Project.class)
            .addAnnotatedClass(Runner.class)
            .addAnnotatedClass(Repository.class)
            .addAnnotatedClass(RepositoryType.class)
            .addAnnotatedClass(SystemUnderTest.class)
            .addAnnotatedClass(Requirement.class)
            .addAnnotatedClass(Specification.class)
            .addAnnotatedClass(Reference.class)
            .addAnnotatedClass(Execution.class);

        this.properties = properties;
        this.metadata = metadataSources.buildMetadata();

    }

    public void createDatabase() throws HibernateException {
        // executes a drop and a create!
        new SchemaExport().create(EnumSet.of(TargetType.DATABASE), metadata);
    }

    public void dropDatabase() throws HibernateException {
//        new SchemaExport().drop(EnumSet.of(TargetType.DATABASE), metadata);
    }

    public Properties getConfiguration() {
        return properties;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public SessionFactory getSessionFactory() throws HibernateException {
        return metadata.buildSessionFactory();
    }

}
