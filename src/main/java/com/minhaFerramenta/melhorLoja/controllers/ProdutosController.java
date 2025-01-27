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
import org.springframework.web.bind.annotation.RequestParam;
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
			produto.setCreatedAt(createdAt);
			produto.setImageFileName(storageFileName);
				
			repo.save(produto);
			return "redirect:/produtos";
	}
	

	@GetMapping("/editar")
	public String showEditarPage(Model model, @RequestParam int id) {
		try {
			Produto produto = repo.findById(id).get();
			model.addAttribute("produto", produto);
			
			ProdutoDto produtoDto = new ProdutoDto();
			produtoDto.setName(produto.getName());
			produtoDto.setBrand(produto.getBrand());
			produtoDto.setDescription(produto.getDescription());
			produtoDto.setCategory(produto.getCategory());
			produtoDto.setPrice(produto.getPrice());
			
			model.addAttribute("produtoDto", produtoDto);
			
		}catch(Exception ex) {
			System.out.println("Exception: " + ex.getMessage());
			return "redirect:/produtos";
		}
	
		return "produtos/EditProduto";
	}
	
	@PostMapping("/editar")
	public String editarProduto(Model model, @RequestParam int id, @Valid @ModelAttribute ProdutoDto produtoDto, BindingResult result) {
		try {
			Produto produto = repo.findById(id).get();
			model.addAttribute("produto", produto);
			
			if(result.hasErrors()) {
				return "produtos/EditProduto";
			}
			
			if(!produtoDto.getImageFile().isEmpty()) {
				String uploadDir = "public/images/";
				Path oldImagePath = Paths.get(uploadDir + produto.getImageFileName());
				
				try {
					Files.delete(oldImagePath);
				}catch(Exception ex) {
					System.out.println("Exception: " + ex.getMessage());
				}
				
				MultipartFile image = produtoDto.getImageFile();
				Date createdAt = new Date();
				
				String storageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();
				try (InputStream inputStream = image.getInputStream()){
					Files.copy(inputStream, Paths.get(uploadDir + storageFileName), StandardCopyOption.REPLACE_EXISTING);
				}
				
				produto.setImageFileName(storageFileName);
			}
			
			produto.setName(produtoDto.getName());
			produto.setBrand(produtoDto.getBrand());
			produto.setDescription(produtoDto.getDescription());
			produto.setCategory(produtoDto.getCategory());
			produto.setPrice(produtoDto.getPrice());
			
			repo.save(produto);
		}catch(Exception ex) {
			System.out.println("Exception: " + ex.getMessage());
		}
	
		return "redirect:/produtos";
	}
	
	@GetMapping("/delete")
	public String deleteProduto(@RequestParam int id) {
		Produto produto = repo.findById(id).get();
		repo.delete(produto);
		return "redirect:/produtos";
	}
	
	
}

