package com.innovacode.inventopro.repository;

import com.innovacode.inventopro.model.Proveedor;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProveedorRepository extends MongoRepository<Proveedor, String> {}
