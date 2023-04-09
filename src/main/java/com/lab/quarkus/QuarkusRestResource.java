package com.lab.quarkus;

import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestResponse;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.List;

@Path("/api/customers")
public class QuarkusRestResource {


    Logger logger;
    CustomerService service;

    public QuarkusRestResource(Logger logger, CustomerService service) {
        this.service = service;
        this.logger = logger;
    }



    @GET
    public RestResponse<List<Customer>> getAllCustomers() {
        List<Customer> customers = service.findAllCustomers();
        logger.debug("Total number of Customers " + customers);
        return RestResponse.ok(customers);
    }

    @GET
    @Path("/{id}")
    public RestResponse<Customer> getCustomer(@RestPath Long id) {
        Customer customer = service.findCustomerById(id);
        if (customer != null) {
            logger.debug("Found customer " + customer);
            return RestResponse.ok(customer);
        } else {
            logger.debug("No customer found with id " + id);
            return RestResponse.noContent();
        }
    }

    @POST
    public RestResponse<Void> createCustomer(@Valid Customer customer, @Context UriInfo uriInfo) {
        customer = service.createCustomer(customer);
        UriBuilder builder = uriInfo.getAbsolutePathBuilder().path(Long.toString(customer.id));
        logger.debug("New customer created with URI " + builder.build().toString());
        return RestResponse.created(builder.build());
    }

    @PUT
    public RestResponse<Customer> updateCustomer(@Valid Customer customer) {
        customer = service.updateCustomer(customer);
        logger.debug("Customer updated with new valued " + customer);
        return RestResponse.ok(customer);
    }

    @DELETE
    @Path("/{id}")
    public RestResponse<Void> deleteCustomer(@RestPath Long id) {
        service.deleteCustomer(id);
        logger.debug("Customer deleted with " + id);
        return RestResponse.noContent();
    }

    @GET
    @Path("/hello")
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello from RESTEasy Reactive";
    }


}