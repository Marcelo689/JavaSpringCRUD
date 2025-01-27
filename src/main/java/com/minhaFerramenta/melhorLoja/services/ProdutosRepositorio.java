package com.minhaFerramenta.melhorLoja.services;

import org.springframework.data.jpa.repository.JpaRepository;

import com.minhaFerramenta.melhorLoja.Produto;

public interface ProdutosRepositorio extends JpaRepository<Produto, Integer> {

}
