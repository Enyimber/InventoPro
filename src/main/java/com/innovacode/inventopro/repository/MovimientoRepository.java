package com.innovacode.inventopro.repository;

import com.innovacode.inventopro.model.Movimiento;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MovimientoRepository extends MongoRepository<Movimiento, String> {}
