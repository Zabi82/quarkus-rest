package com.lab.quarkus;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
public class Customer extends PanacheEntity  {

    @NotNull
    @Size(min=4, max=50)
    public String name;

    @NotNull
    public String gender;

    @NotNull
    public Date dob;

    @NotNull
    @Size(min = 10, max = 10)
    public String mobile;

    @NotNull
    @Size(min=10, max=100)
    public String address;

    @Override
    public String toString() {
        return "Customer{" +
                "name='" + name + '\'' +
                ", gender=" + gender +
                ", dob=" + dob +
                ", mobile='" + mobile + '\'' +
                ", address='" + address + '\'' +
                ", id=" + id +
                '}';
    }
}
