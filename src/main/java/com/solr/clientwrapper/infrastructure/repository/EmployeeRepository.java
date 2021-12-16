package com.solr.clientwrapper.infrastructure.repository;


import com.solr.clientwrapper.infrastructure.entity.Employee;
import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;

import java.util.List;


public interface EmployeeRepository extends SolrCrudRepository<Employee, Integer> {

    Employee findByName(String name);

    Employee findByAddress(String name);

    List<Employee> findByNameLike(String name);

    @Query(fields = { "name", "id" })
    List<Employee> findById(int id);

    List<Employee> findByNameAndAddress(String name, String address);



}