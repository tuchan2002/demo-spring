package com.example.demospringboot.controllers;

import com.example.demospringboot.models.Category;
import com.example.demospringboot.models.Product;
import com.example.demospringboot.repositories.CategoryRepository;
import com.example.demospringboot.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping(path = "products")
public class ProductController {
    private ProductRepository productRepository;
    private CategoryRepository categoryRepository;

    @Autowired
    public ProductController(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @RequestMapping(value = "/getProductsByCategoryID/{categoryID}", method = RequestMethod.GET)
    public String getProductsByCategoryID(ModelMap modelMap, @PathVariable String categoryID) {
        Iterable<Product> products = productRepository.findByCategoryID(categoryID);
        modelMap.addAttribute("products", products);

        return "productList";
    }

    @RequestMapping(value = "/insertProduct", method = RequestMethod.GET)
    public String insertProduct(ModelMap modelMap) {
        modelMap.addAttribute("product", new Product());
        Iterable<Category> categories = categoryRepository.findAll();
        modelMap.addAttribute("categories", categories);

        return "insertProduct";
    }

    @RequestMapping(value = "/insertProduct", method = RequestMethod.POST)
    public String insertProduct(ModelMap modelMap, @Valid @ModelAttribute("product") Product product, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            Iterable<Category> categories = categoryRepository.findAll();
            modelMap.addAttribute("categories", categories);
            return "insertProduct";
        }

        try {
            product.setProductID(UUID.randomUUID().toString());
            productRepository.save(product);

            return "redirect:/categories";
        } catch (Exception e) {
            System.out.println(e.toString());
            modelMap.addAttribute("error", e.toString());
            return "insertProduct";

        }

    }

    @RequestMapping(value = "/changeCategory/{productID}", method = RequestMethod.GET)
    public String changeCategory(ModelMap modelMap, @PathVariable String productID) {
        Iterable<Category> categories = categoryRepository.findAll();
        Product product = productRepository.findById(productID).get();

        modelMap.addAttribute("categories", categories);
        modelMap.addAttribute("product", product);

        return "updateProduct";
    }

    @RequestMapping(value = "/updateProduct/{productID}", method = RequestMethod.POST)
    public String updateProduct(ModelMap modelMap, @Valid @ModelAttribute("product") Product product, BindingResult bindingResult, @PathVariable String productID) {
        if(bindingResult.hasErrors()) {
            Iterable<Category> categories = categoryRepository.findAll();
            modelMap.addAttribute("categories", categories);

            return "updateProduct";
        }

        try {
            Optional<Product> optionalProduct = productRepository.findById(product.getProductID());
            if(optionalProduct.isPresent()) {
                Product foundProduct = optionalProduct.get();

                if(!product.getProductName().trim().isEmpty()) {
                    foundProduct.setProductName(product.getProductName() );
                }
                if(!product.getCategoryID().trim().isEmpty()) {
                    foundProduct.setCategoryID(product.getCategoryID());
                }
                if(!product.getDescription().trim().isEmpty()) {
                    foundProduct.setDescription(product.getDescription());
                }
                if(product.getPrice() > 0) {
                    foundProduct.setPrice(product.getPrice());
                }
                productRepository.save(foundProduct);
            }
        } catch (Exception e) {
            System.out.println(e.toString());
            modelMap.addAttribute("error", e.toString());
            return "updateProduct";
        }

        return "redirect:/products/getProductsByCategoryID/" + product.getCategoryID();
    }

    @RequestMapping(value = "/deleteProduct/{productID}", method = RequestMethod.POST)
    public String deleteProduct(ModelMap modelMap, @PathVariable String productID) {
        productRepository.deleteById(productID);
        return "redirect:/categories";
    }

}
