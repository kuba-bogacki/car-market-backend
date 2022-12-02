package com.carmarket.controller;

import com.carmarket.jwt.JwtConfiguration;
import com.carmarket.model.Article;
import com.carmarket.model.Customer;
import com.carmarket.service.ArticleService;
import com.carmarket.service.CustomerService;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class ArticleController {

    private final ArticleService articleService;
    private final CustomerService customerService;
    private final JwtConfiguration jwtConfiguration;

    @Autowired
    public ArticleController(ArticleService articleService, CustomerService customerService,
                             JwtConfiguration jwtConfiguration) {
        this.articleService = articleService;
        this.customerService = customerService;
        this.jwtConfiguration = jwtConfiguration;
    }

    @GetMapping(value = "/get-all-articles")
    public List<Article> getAllArticles() {
        return articleService.getAllArticles();
    }

    @GetMapping(value = "/get-individual-article/{articleId}")
    public Article getIndividualArticle(@PathVariable String articleId) {
        return articleService.getIndividualArticle(Long.valueOf(articleId));
    }

    @PostMapping(value = "/add-new-article")
    public ResponseEntity<?> addNewArticle(@RequestHeader("Authorization") String token, @RequestBody ObjectNode objectNode) {
        Optional<Customer> currentCustomer =
            customerService.selectCustomerByCustomerEmail(jwtConfiguration.getUsernameFromToken(token.substring(7)));
        if (currentCustomer.isPresent()) {
            articleService.addNewArticle(objectNode.get("articleHeader").asText(), objectNode.get("articleBody").asText(),
                    currentCustomer.get());
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
