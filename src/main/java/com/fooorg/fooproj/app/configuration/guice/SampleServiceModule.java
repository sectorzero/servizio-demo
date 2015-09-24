package com.fooorg.fooproj.app.configuration.guice;

import com.fooorg.fooproj.app.configuration.SampleServiceConfiguration;

import com.fooorg.fooproj.core.FooDAO;

import io.dropwizard.db.DataSourceFactory;
import org.skife.jdbi.v2.DBI;

import com.google.inject.AbstractModule;
import com.google.inject.name.Named;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public class SampleServiceModule extends AbstractModule {

    @Override
    protected void configure() {
    }

    @Provides
    @Named("JdbcDataSourceName")
    public String getJdbcDataSourceName(SampleServiceConfiguration configuration) {
        return configuration.getDataSourceName();
    }

    @Provides
    @Singleton
    DataSourceFactory getDataSourceFactory(SampleServiceConfiguration configuration)  {
        return configuration.getDataSourceFactory();
    }

    @Provides
    public FooDAO getFooDAO(DBI jdbi) {
        return jdbi.onDemand(FooDAO.class);
    }

}
