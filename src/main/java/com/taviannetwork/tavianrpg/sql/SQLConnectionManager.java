package com.taviannetwork.tavianrpg.sql;

import com.taviannetwork.tavianrpg.TavianRPG;
import com.taviannetwork.tavianrpg.services.Service;
import com.taviannetwork.tavianrpg.services.ServiceInfo;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.File;
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
        dataSource = new HikariDataSource(new HikariConfig(new File(plugin.getDataFolder(), "datasource.properties").getAbsolutePath()));
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
