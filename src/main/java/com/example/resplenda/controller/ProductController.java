package com.example.resplenda.controller;

import com.example.resplenda.model.Product;
import com.example.resplenda.service.MakeupApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

private final MakeupApiService makeupApiService;

@Autowired
public ProductController(MakeupApiService makeupApiService) {
	this.makeupApiService = makeupApiService;
}

@GetMapping
public List<Product> getAllProducts(){
	return makeupApiService.fetchProducts();
}

@GetMapping(params = "category")
public List<Product> getProductByCategory(@RequestParam String cateogory){
	return makeupApiService.getProductByCategory(cateogory);
}


}
