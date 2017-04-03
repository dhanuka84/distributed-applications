package org.reactor.monitoring.util;

public class HibernateConfiguration
{
    private String hibernateFactoryName;
    private String hibernateServerConfigFile;

    public HibernateConfiguration(String hibernateFactoryName, String hibernateServerConfigFile)
    {
        this.hibernateFactoryName = hibernateFactoryName;
        this.hibernateServerConfigFile = hibernateServerConfigFile;
    }

    public String getHibernateFactoryName()
    {
        return hibernateFactoryName;
    }

    public String getHibernateServerConfigFile()
    {
        return hibernateServerConfigFile;
    }
}