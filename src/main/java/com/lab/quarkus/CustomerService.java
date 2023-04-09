package com.lab.quarkus;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.validation.Valid;

import java.util.List;

import static javax.transaction.Transactional.TxType.REQUIRED;

@ApplicationScoped
@Transactional(REQUIRED)
public class CustomerService {


    public List<Customer> findAllCustomers() {
        return Customer.listAll();
    }

    public Customer findCustomerById(Long id) {
        return Customer.findById(id);
    }

    public Customer createCustomer(@Valid Customer customer) {
        Customer.persist(customer);
        return customer;
    }

    public Customer updateCustomer(@Valid Customer customer) {
        Customer entity = Customer.findById(customer.id);
        entity.mobile = customer.mobile;
        entity.address = customer.address;
        return entity;
    }

    public void deleteCustomer(Long id) {
        Customer customer = Customer.findById(id);
        customer.delete();
    }
}
