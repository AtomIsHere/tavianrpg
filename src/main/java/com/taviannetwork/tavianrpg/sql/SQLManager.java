package com.taviannetwork.tavianrpg.sql;

import com.taviannetwork.tavianrpg.services.Service;
import com.taviannetwork.tavianrpg.services.ServiceInfo;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
@ServiceInfo(serviceName = "SQLManager", serviceVersion = "1.0.0", serviceAuthor = "AtomIsHere")
public class SQLManager implements Service {
    private final Map<Class<?>, SQLTypeAdapter<?>> typeAdapters = new HashMap<>();

    @Inject
    private SQLConnectionManager sqlConnectionManager;

    public <T> List<T> fromSQL(Class<T> type, String query) throws SQLException {
        SQLTypeAdapter<T> typeAdapter = getTypeAdapter(type);
        if(typeAdapter == null) {
            return null;
        }

        try(Connection con = sqlConnectionManager.getConnection()) {
            PreparedStatement statement = con.prepareStatement(query);
            ResultSet rs = statement.executeQuery();

            return typeAdapter.fromSQL(rs);
        }
    }

    public <T> void toSQL(T type, Class<T> typeClass) throws SQLException {
        SQLTypeAdapter<T> typeAdapter = getTypeAdapter(typeClass);
        if(typeAdapter == null) {
            return;
        }

        try(Connection con = sqlConnectionManager.getConnection()) {
            PreparedStatement statement = con.prepareStatement(typeAdapter.toSQL(type));
            statement.execute();
        }
    }

    public <T> SQLTypeAdapter<T> getTypeAdapter(Class<T> clazz) {
        if(!typeAdapters.containsKey(clazz)) {
            return null;
        }

        return (SQLTypeAdapter<T>) typeAdapters.get(clazz);
    }

    public void addTypeAdapter(SQLTypeAdapter<?> typeAdapter) {
        typeAdapters.put(typeAdapter.getTypeClass(), typeAdapter);
    }

    @Override
    public List<Class<? extends Service>> getDependencies() {
        return Collections.singletonList(SQLConnectionManager.class);
    }
}
