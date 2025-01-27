package com.minhaFerramenta.melhorLoja.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.minhaFerramenta.melhorLoja.Produto;
import com.minhaFerramenta.melhorLoja.services.ProdutosRepositorio;

@Controller
@RequestMapping("/produtos")
public class ProdutosController {
	
	@Autowired
	private ProdutosRepositorio repo;
	
	
	@GetMapping({"", "/"})
	public String showProdutoList(Model model) {
			List<Produto> produtos = repo.findAll();
			model.addAttribute("produtos", produtos);
			return "produtos/index";
	}
}

