package com.grigore.mongo.service;

import com.grigore.mongo.exception.UserNotFoundException;
import com.grigore.mongo.model.Company;
import com.grigore.mongo.repository.CompanyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository){
        this.companyRepository=companyRepository;
    }

    public List<Company> findAllCompanies() {
        return companyRepository.findAll();
    }

    public Company addCompany(Company company) {
        return companyRepository.save(company);
    }

    public Company findCompanyById(String id) {
        return companyRepository.findCompanyById(id).orElseThrow(
                ()-> new UserNotFoundException("Company by id " + id + " not found"));
    }

    public Company updateCompany(Company company) {
        return companyRepository.save(company);
    }
}
