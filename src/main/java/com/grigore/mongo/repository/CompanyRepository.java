package com.grigore.mongo.repository;

import com.grigore.mongo.model.Company;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CompanyRepository extends MongoRepository<Company, String> {
    Optional<Company> findCompanyById(String id);
}
