package com.minhaFerramenta.melhorLoja.controllers;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import com.minhaFerramenta.melhorLoja.Produto;
import com.minhaFerramenta.melhorLoja.ProdutoDto;
import com.minhaFerramenta.melhorLoja.services.ProdutosRepositorio;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/produtos")
public class ProdutosController {
	
	@Autowired
	private ProdutosRepositorio repo;
	
	
	@GetMapping({"", "/"})
	public String showProdutoList(Model model) {
			List<Produto> produtos = repo.findAll(Sort.by(Sort.Direction.DESC, "id"));
			model.addAttribute("produtos", produtos);
			return "produtos/index";
	}
	
	@GetMapping("/criar")
	public String showCreatePage(Model model) {
			ProdutoDto produtoDto = new ProdutoDto();
			model.addAttribute("produtoDto", produtoDto);
			return "produtos/CreateProduto";
	}
	
	@PostMapping("/criar")
	public String criarProduto(
			@Valid @ModelAttribute ProdutoDto produtoDto,
			BindingResult result) {
			if(produtoDto.getImageFile().isEmpty()) {
				result.addError(new FieldError("produtoDto", "imageFile", "É necessário conter imagem"));
			}
				
			if(result.hasErrors()) {
				return "produtos/CreateProduto";
			}
			
			MultipartFile image = produtoDto.getImageFile();
			Date createdAt = new Date();
			String storageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();
			
			try {
				String uploadDir = "public/images/";
				Path uploadPath = Paths.get(uploadDir);
				
				if(!Files.exists(uploadPath)) {
					Files.createDirectories(uploadPath);
				}
				
				try (InputStream inputStream = image.getInputStream()){
					Files.copy(inputStream, Paths.get(uploadDir + storageFileName),StandardCopyOption.REPLACE_EXISTING);
				}
			}catch(Exception ex) {
				System.out.println("Exception: " + ex.getMessage());
			}
			
			Produto produto = new Produto();
			produto.setName(produtoDto.getName());
			produto.setBrand(produtoDto.getBrand());
			produto.setCategory(produtoDto.getCategory());
			produto.setPrice(produtoDto.getPrice());
			produto.setDescription(produtoDto.getDescription());
			produto.setCreateAt(createdAt);
			produto.setImageFileName(storageFileName);
				
			repo.save(produto);
			return "redirect:/produtos";
	}
}

