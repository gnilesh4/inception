/*
 * Copyright 2019 Marcus Portmann
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package digital.inception.application;

//~--- non-JDK imports --------------------------------------------------------

import com.atomikos.jdbc.AtomikosDataSourceBean;

import digital.inception.core.util.JDBCUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.hibernate.SpringJtaPlatform;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.util.StringUtils;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.*;

import javax.sql.DataSource;
import javax.sql.XADataSource;

/**
 * The <code>ApplicationDatabaseConfiguration</code> class provides access to the application
 * database configuration and initialises the application data source and the application entity
 * manager factory bean associated with the application data source.
 *
 * @author Marcus Portmann
 */
@Configuration
@ConditionalOnProperty(value = "application.database.dataSource")
@SuppressWarnings("unused")
public class ApplicationDatabaseConfiguration
  implements DisposableBean
{
  /* Logger */
  private static final Logger logger = LoggerFactory.getLogger(
      ApplicationDatabaseConfiguration.class);

  /**
   * The Spring application context.
   */
  private ApplicationContext applicationContext;

  /**
   * The application data source.
   */
  private DataSource dataSource;

  /**
   * The fully qualified name of the data source class used to connect to the application
   * database.
   */
  @Value("${application.database.dataSource:#{null}}")
  private String dataSourceClass;

  /**
   * The resources on the classpath that contain the SQL statements used to initialize the in-memory
   * application database.
   */
  @Value("classpath*:**/*-h2.sql")
  private Resource[] databaseInitResources;

  /**
   * The optional list of paths to the resources on the classpath that contain the SQL statements
   * used to initialize the in-memory application database.
   */
  @Value("${application.database.inMemoryDatabaseInitResources:#{null}}")
  private String inMemoryDatabaseInitResources;

  /**
   * The maximum size of the database connection pool used to connect to the application database.
   */
  @Value("${application.database.maxPoolSize:#{5}}")
  private int maxPoolSize;

  /**
   * The minimum size of the database connection pool used to connect to the application database.
   */
  @Value("${application.database.minPoolSize:#{1}}")
  private int minPoolSize;

  /**
   * The optional list of comma-delimited packages on the classpath to scan for JPA entities.
   */
  @Value("${application.database.packagesToScanForEntities:#{null}}")
  private String packagesToScanForEntities;

  /**
   * The URL used to connect to the application database.
   */
  @Value("${application.database.url:#{null}}")
  private String url;

  /**
   * The XA password for the application database.
   */
  @Value("${application.database.xaPassword:#{null}}")
  private String xaPassword;

  /**
   * The XA server name for the application database.
   */
  @Value("${application.database.xaServerName:#{null}}")
  private String xaServerName;

  /**
   * The XA username for the application database.
   */
  @Value("${application.database.xaUsername:#{null}}")
  private String xaUsername;

  /**
   * Constructs a new <code>ApplicationDatabaseConfiguration</code>.
   *
   * @param applicationContext the Spring application context
   */
  public ApplicationDatabaseConfiguration(ApplicationContext applicationContext)
  {
    this.applicationContext = applicationContext;
  }

  /**
   * Returns the data source that can be used to interact with the application database.
   *
   * @return the data source that can be used to interact with the in-memory database
   */
  @Bean(name = "applicationDataSource")
  @DependsOn({ "transactionManager" })
  public DataSource dataSource()
  {
    try
    {
      if ((dataSourceClass == null) || (url == null))
      {
        throw new ApplicationException("Failed to retrieve the application database configuration");
      }

      /*
       * The SAP JDBC driver does not return a DataSource, instead it provides connections so we
       * make use of the DriverManagerDataSource.
       */
      if (dataSourceClass.equals("com.sap.db.jdbc.Driver"))
      {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName(dataSourceClass);
        ds.setUrl(url);
        dataSource = ds;
      }
      else
      {
        Class<? extends DataSource> dataSourceClass = Thread.currentThread().getContextClassLoader()
            .loadClass(this.dataSourceClass).asSubclass(DataSource.class);

        dataSource = DataSourceBuilder.create().type(dataSourceClass).url(url).build();
      }

      Database databaseVendor = Database.DEFAULT;

      try (Connection connection = dataSource.getConnection())
      {
        DatabaseMetaData metaData = connection.getMetaData();

        logger.info("Connected to the " + metaData.getDatabaseProductName()
            + " application database with version " + metaData.getDatabaseProductVersion());

        switch (metaData.getDatabaseProductName())
        {
          case "H2":

            databaseVendor = Database.H2;

            break;

          case "Microsoft SQL Server":

            databaseVendor = Database.SQL_SERVER;

          default:

            logger.info("The default database tables will not be populated for the database type ("
                + metaData.getDatabaseProductName() + ")");

            break;
        }
      }

      if (databaseVendor == Database.H2)
      {
        logger.info("Initializing the in-memory H2 database");

        /*
         * Initialize the in-memory database using the SQL statements contained in the resources
         * for the Inception framework.
         */
        for (Resource databaseInitResource : databaseInitResources)
        {
          if ((!StringUtils.isEmpty(databaseInitResource.getFilename()))
              && databaseInitResource.getFilename().contains("inception-"))
          {
            loadSQL(dataSource, databaseInitResource);
          }
        }

        /*
         * Initialize the in-memory database using the SQL statements contained in any other
         * resources for the application.
         */
        for (Resource databaseInitResource : databaseInitResources)
        {
          if ((!StringUtils.isEmpty(databaseInitResource.getFilename()))
              && (!databaseInitResource.getFilename().contains("inception-")))
          {
            loadSQL(dataSource, databaseInitResource);
          }
        }
      }

      if (dataSource instanceof XADataSource)
      {
        AtomikosDataSourceBean atomikosDataSourceBean = new AtomikosDataSourceBean();

        atomikosDataSourceBean.setUniqueResourceName("ApplicationDataSource");
        atomikosDataSourceBean.setXaDataSource((XADataSource) dataSource);

        if (databaseVendor == Database.SQL_SERVER)
        {
          // Set the test query
          atomikosDataSourceBean.setTestQuery("SELECT 1;");

          // Set the XA properties if they are available
          // See: https://www.atomikos.com/Documentation/ConfiguringSQLServer
          if ((!StringUtils.isEmpty(xaServerName))
              && (!StringUtils.isEmpty(xaUsername))
              && (!StringUtils.isEmpty(xaPassword)))
          {
            Properties p = new Properties();
            p.setProperty("serverName", xaServerName);
            p.setProperty("user", xaUsername);
            p.setProperty("password", xaPassword);

            atomikosDataSourceBean.setXaProperties(p);
          }
          else
          {
            throw new FatalBeanException(
                "No XA properties specified for the AtomikosDataSourceBean for Microsoft SQL Server");
          }
        }

        if (minPoolSize > 0)
        {
          atomikosDataSourceBean.setMinPoolSize(minPoolSize);
        }
        else
        {
          atomikosDataSourceBean.setMinPoolSize(1);
        }

        if (maxPoolSize > 0)
        {
          atomikosDataSourceBean.setMaxPoolSize(maxPoolSize);
        }
        else
        {
          atomikosDataSourceBean.setMinPoolSize(5);
        }

        return atomikosDataSourceBean;
      }
      else
      {
        return dataSource;
      }
    }
    catch (Throwable e)
    {
      throw new FatalBeanException("Failed to initialize the application data source", e);
    }
  }

  /**
   * Destroy.
   */
  @Override
  public void destroy()
  {
    shutdownInMemoryApplicationDatabase();
  }

  /**
   * Returns the application entity manager factory bean associated with the application data
   * source.
   *
   * @return the application entity manager factory bean associated with the application data source
   */
  @Bean(name = "applicationPersistenceUnit")
  @DependsOn("applicationDataSource")
  public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean()
  {
    try
    {
      DataSource dataSource = dataSource();

      LocalContainerEntityManagerFactoryBean entityManagerFactoryBean =
          new LocalContainerEntityManagerFactoryBean();

      HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
      jpaVendorAdapter.setGenerateDdl(false);

      try (Connection connection = dataSource.getConnection())
      {
        DatabaseMetaData metaData = connection.getMetaData();

        switch (metaData.getDatabaseProductName())
        {
          case "H2":

            jpaVendorAdapter.setDatabase(Database.H2);
            jpaVendorAdapter.setShowSql(true);

            break;

          case "Microsoft SQL Server":

            jpaVendorAdapter.setDatabase(Database.SQL_SERVER);
            jpaVendorAdapter.setDatabasePlatform("org.hibernate.dialect.SQLServer2012Dialect");
            jpaVendorAdapter.setShowSql(false);

            break;

          default:

            jpaVendorAdapter.setDatabase(Database.DEFAULT);
            jpaVendorAdapter.setShowSql(false);

            break;
        }
      }

      entityManagerFactoryBean.setPersistenceUnitName("applicationPersistenceUnit");
      entityManagerFactoryBean.setJtaDataSource(dataSource);
      entityManagerFactoryBean.setPackagesToScan(StringUtils.toStringArray(
          packagesToScanForEntities()));
      entityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter);

      PlatformTransactionManager platformTransactionManager = applicationContext.getBean(
          PlatformTransactionManager.class);

      if (platformTransactionManager instanceof JtaTransactionManager)
      {
        Map<String, Object> jpaPropertyMap = entityManagerFactoryBean.getJpaPropertyMap();

        jpaPropertyMap.put("hibernate.transaction.jta.platform", new SpringJtaPlatform(
            ((JtaTransactionManager) platformTransactionManager)));
      }

      return entityManagerFactoryBean;
    }
    catch (Throwable e)
    {
      throw new FatalBeanException(
          "Failed to initialize the application entity manager factory bean", e);
    }
  }

  private void loadSQL(DataSource dataSource, Resource databaseInitResource)
    throws IOException, SQLException
  {
    logger.info("Executing the SQL statements in the file '" + databaseInitResource.getFilename()
        + "'");

    try
    {
      // Load the SQL statements used to initialize the database tables
      List<String> sqlStatements = JDBCUtil.loadSQL(databaseInitResource.getURL());

      // Get a connection to the in-memory database
      try (Connection connection = dataSource.getConnection())
      {
        for (String sqlStatement : sqlStatements)
        {
          LoggerFactory.getLogger(Application.class).debug("Executing SQL statement: "
              + sqlStatement);

          try (Statement statement = connection.createStatement())
          {
            statement.execute(sqlStatement);
          }
        }
      }
    }
    catch (SQLException e)
    {
      try (Connection connection = dataSource.getConnection())
      {
        JDBCUtil.shutdownHsqlDatabase(connection);
      }
      catch (Throwable f)
      {
        LoggerFactory.getLogger(Application.class).error(
            "Failed to shutdown the in-memory application database: " + e.getMessage());
      }

      throw e;
    }
  }

  /**
   * Returns the names of the packages to scan for JPA entities.
   *
   * @return the names of the packages to scan for JPA entities
   */
  private List<String> packagesToScanForEntities()
  {
    List<String> packagesToScanForEntities = new ArrayList<>();

    packagesToScanForEntities.add("digital.inception");

    if (!StringUtils.isEmpty(this.packagesToScanForEntities))
    {
      String[] packagesToScan = this.packagesToScanForEntities.split(",");

      Collections.addAll(packagesToScanForEntities, StringUtils.trimArrayElements(packagesToScan));
    }

    return packagesToScanForEntities;
  }

  /**
   * Shutdown the in-memory application database if required.
   */
  private void shutdownInMemoryApplicationDatabase()
  {
    if (dataSource != null)
    {
      try
      {
        try (Connection connection = dataSource.getConnection();
          Statement statement = connection.createStatement())

        {
          var metaData = connection.getMetaData();

          if ("H2".equals(metaData.getDatabaseProductName()))
          {
            logger.info("Shutting down the in-memory " + metaData.getDatabaseProductName()
                + " application database with version " + metaData.getDatabaseProductVersion());

            JDBCUtil.shutdownHsqlDatabase(connection);
          }
        }
      }
      catch (Throwable e)
      {
        logger.error("Failed to shutdown the in-memory application database", e);
      }
    }
  }
}