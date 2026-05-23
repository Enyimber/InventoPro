package com.innovacode.inventopro.repository;

import com.innovacode.inventopro.model.Auditoria;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AuditoriaRepository extends MongoRepository<Auditoria, String> {}
