package com.cacheframework.example;

import java.io.Serializable;

public class UserObject implements Serializable
{

    private static final long serialVersionUID = -7794550347872472486L;
    private String username;
    private String firstName;
    private String lastName;
    private String address;

    public UserObject( String username, String firstName, String lastName, String address )
    {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername( String username )
    {
        this.username = username;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName( String firstName )
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName( String lastName )
    {
        this.lastName = lastName;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress( String address )
    {
        this.address = address;
    }
}
