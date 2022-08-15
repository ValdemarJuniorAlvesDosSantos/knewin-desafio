package com.valdemar.desafio.service.impl;

import org.springframework.stereotype.Service;

import com.valdemar.desafio.model.repository.NoticiaRepository;
import com.valdemar.desafio.service.IniciaPesquisa;

@Service
public class IniciaPesquisaImpl implements IniciaPesquisa{
	private final NoticiaRepository noticiaRepository;
	
	public IniciaPesquisaImpl(NoticiaRepository repo){
		noticiaRepository = repo;
		new Thread(new G1NoticiasImpl(noticiaRepository)).start();
		new Thread(new InfoMoneyNoticiasImpl(noticiaRepository)).start();
		
	}
}
