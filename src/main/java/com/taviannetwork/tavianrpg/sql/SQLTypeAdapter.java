package com.taviannetwork.tavianrpg.sql;

import java.sql.ResultSet;
import java.util.List;

public interface SQLTypeAdapter<T> {
    List<T> fromSQL(ResultSet rs);
    String toSQL(T type);

    Class<T> getTypeClass();
}
