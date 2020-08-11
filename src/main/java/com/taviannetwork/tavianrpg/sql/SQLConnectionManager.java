package com.taviannetwork.tavianrpg.sql;

import com.taviannetwork.tavianrpg.TavianRPG;
import com.taviannetwork.tavianrpg.services.Service;
import com.taviannetwork.tavianrpg.services.ServiceInfo;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;

@Singleton
@ServiceInfo(serviceName = "SQLConnectionManager", serviceVersion = "1.0.0", serviceAuthor = "AtomIsHere")
public class SQLConnectionManager implements Service {
    @Inject
    private TavianRPG plugin;

    private HikariDataSource dataSource;

    @Override
    public void start() {
        // Preloading the connector...
        try {
            Class.forName("org.mariadb.jdbc.MariaDbDataSource", true, getClass().getClassLoader());
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
            return;
        }

        File propertiesFile = new File(plugin.getDataFolder(), "datasource.properties");
        if(!propertiesFile.exists()) {
            try(InputStream inputStream = plugin.getResource("datasource.properties")) {
                FileUtils.copyInputStreamToFile(inputStream, propertiesFile);
            } catch (IOException ioe) {
                ioe.printStackTrace();
                return;
            }
        }

        dataSource = new HikariDataSource(new HikariConfig(propertiesFile.getAbsolutePath()));
    }

    @Override
    public void stop() {
        dataSource.close();
        dataSource = null;
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
