package com.taviannetwork.tavianrpg.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface SQLTypeAdapter<T> {
    T fromSQL(ResultSet rs) throws SQLException;
    String toSQL(T type);

    Class<T> getTypeClass();
}
