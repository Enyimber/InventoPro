package com.innovacode.inventopro.repository;

import com.innovacode.inventopro.model.Categoria;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CategoriaRepository extends MongoRepository<Categoria, String> {}
