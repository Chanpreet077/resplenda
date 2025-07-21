package com.example.resplenda.service;


import com.example.resplenda.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class MakeupApiService {
	
	private final RestTemplate restTemplate;
	
	private final String base_url = "http://makeup-api.herokuapp.com/api/v1/products.json";
	
	@Autowired
	public  MakeupApiService(RestTemplate restTemplate) {
		this.restTemplate =  restTemplate;
	}


	public List <Product> fetchProducts(){
	
		Product[] products = restTemplate.getForObject(base_url, Product[].class);//product[].class means "array of Product objects"
		return Arrays.asList(products);
	}
	
	public List <Product> getProductByCategory(String category) {
		String url = base_url + "?product_type=" + category;
		Product[] products = restTemplate.getForObject(url, Product[].class);
		return Arrays.asList(products);
	}
		
		
}
	
	


