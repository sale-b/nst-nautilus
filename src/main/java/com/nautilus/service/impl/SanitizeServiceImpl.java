package com.nautilus.service.impl;

import com.nautilus.domain.Sanitize;
import com.nautilus.repository.SanitizeRepository;
import com.nautilus.service.SanitizeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SanitizeServiceImpl implements SanitizeService {

    final
    SanitizeRepository sanitizeRepository;

    public SanitizeServiceImpl(SanitizeRepository sanitizeRepository) {
        this.sanitizeRepository = sanitizeRepository;
    }

    @Override
    public Sanitize insert(Sanitize obj) {
        return sanitizeRepository.insert(obj);
    }

    @Override
    public List<Sanitize> getAll() {
        return sanitizeRepository.getAll();
    }

    @Override
    public Sanitize findById(Long id) {
        return sanitizeRepository.findById(id);
    }

    @Override
    public Optional<Sanitize> update(Sanitize obj) {
        return sanitizeRepository.update(obj);
    }

    @Override
    public void deleteAll(Iterable<Sanitize> sanitize) {
        sanitizeRepository.deleteAll(sanitize);
    }

}
