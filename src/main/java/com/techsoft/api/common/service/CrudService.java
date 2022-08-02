package com.techsoft.api.common.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CrudService<T, ID, F> {
    T findById(ID id);

    Page<T> list(Pageable pageable);

    List<T> list();

    T saveDto(F domainForm) ;

    T saveDomain(T domain);

    void delete(ID id);
}
