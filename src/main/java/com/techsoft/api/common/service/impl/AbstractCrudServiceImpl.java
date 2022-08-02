package com.techsoft.api.common.service.impl;

import com.techsoft.api.common.error.HttpResponseException;
import com.techsoft.api.common.service.CrudService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import java.util.Optional;

@Slf4j
public class AbstractCrudServiceImpl<T, ID, R extends JpaRepository<T, ID>, F> implements CrudService<T, ID, F> {
    protected final R repository;
    protected final Class<T> domainClass;

    public AbstractCrudServiceImpl(R repository, Class<T> domainClass) {
        this.repository = repository;
        this.domainClass = domainClass;
    }

    private String getDomainName() {
        return domainClass.getSimpleName();
    }

    public T saveDto(F domainForm) {
        try {
            T domain = domainClass.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(domainForm, domain);
            return repository.save(domain);
        } catch (Exception e) {
            e.printStackTrace();
            throw new HttpResponseException(
                    HttpStatus.BAD_REQUEST,
                    String.format("Error create %s", getDomainName())
            );
        }
    }

    public T saveDomain(T domain) {
        try {
            return repository.save(domain);
        } catch (Exception e) {
            e.printStackTrace();
            throw new HttpResponseException(
                    HttpStatus.BAD_REQUEST,
                    String.format("Error create %s", getDomainName())
            );
        }
    }

    public T findById(ID id) {
        Optional<T> domain = repository.findById(id);

        if(domain.isPresent()) {
            return domain.get();
        }

        throw notFound();
    }

    public Page<T> list(Pageable pageable) {
        Page<T> page = repository.findAll(pageable);

        if(page.hasContent()) {
            return page;
        }

        throw new HttpResponseException(HttpStatus.NOT_FOUND, String.format("Not found list for domain %s", getDomainName()));
    }

    public List<T> list() {
        return repository.findAll();
    }

    public void delete(ID id) {
        Optional<T> domain = repository.findById(id);

        if(domain.isPresent()) {
            repository.delete(domain.get());
            return;
        }

        throw notFound();
    }

    private HttpResponseException notFound() {
        return new HttpResponseException(
                HttpStatus.NOT_FOUND,
                String.format("%s not found!", getDomainName())
        );
    }
}
