package org.example.engine.internal;

import javax.sql.DataSource;

import static org.example.engine.internal.Datasource.getDataSource;

public class SchedulerRepo {

    private static final DataSource dataSource = getDataSource();

}
