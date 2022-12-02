package com.carmarket.service;

import com.carmarket.model.Article;
import com.carmarket.model.Customer;

import java.util.List;

public interface ArticleService {
    List<Article> getAllArticles();
    void addNewArticle(String articleHeader, String articleBody, Customer customer);
    Article getIndividualArticle(Long articleId);
}
