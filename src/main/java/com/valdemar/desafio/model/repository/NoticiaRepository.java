package com.valdemar.desafio.model.repository;

import java.util.Optional;

import org.socialsignin.spring.data.dynamodb.repository.DynamoDBCrudRepository;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.context.annotation.Bean;
import org.springframework.data.repository.CrudRepository;

import com.valdemar.desafio.model.entity.Noticia;

@EnableScan
public interface NoticiaRepository extends DynamoDBCrudRepository<Noticia, String>{
	
}
