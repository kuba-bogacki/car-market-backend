package com.carmarket.service.implementation;

import com.carmarket.model.Article;
import com.carmarket.model.Customer;
import com.carmarket.repository.ArticleRepository;
import com.carmarket.service.ArticleService;
import com.carmarket.utils.FileWriterReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
class ArticleServiceImplementation implements ArticleService {

    private final ArticleRepository articleRepository;

    @Autowired
    public ArticleServiceImplementation(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @Override
    public List<Article> getAllArticles() {
        List<Article> articlesListToReturn = new ArrayList<>();
        List<Article> articleList = articleRepository.findAll().stream()
                .sorted(Comparator.comparing(Article::getArticleDate).reversed())
                .toList();
        for (Article article : articleList) {
            String singleArticle = FileWriterReader.readArticleFile(article.getArticleBody());
            article.setArticleBody(singleArticle);
            articlesListToReturn.add(article);
        }
        return articlesListToReturn;
    }

    @Override
    public void addNewArticle(String articleHeader, String articleBody, Customer customer) {
        FileWriterReader fileWriterReader = new FileWriterReader();
        fileWriterReader.saveNewFile(articleBody);

        Article article = Article.builder()
                .articleHeader(articleHeader)
                .articleBody(fileWriterReader.getFileName())
                .articleDate(LocalDate.now())
                .customer(customer)
                .build();
        articleRepository.save(article);
    }

    @Override
    public Article getIndividualArticle(Long articleId) {
        Article temporaryArticle = articleRepository.getArticleByArticleId(articleId);
        String articleText = FileWriterReader.readArticleFile(temporaryArticle.getArticleBody());
        temporaryArticle.setArticleBody(articleText);
        return temporaryArticle;
    }
}
