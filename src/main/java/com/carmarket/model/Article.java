package com.carmarket.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "article")
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "article_id")
    private Long articleId;
    @Column(name = "article_header")
    private String articleHeader;
    @Column(name = "article_body")
    private String articleBody;
    @Column(name = "article_date")
    private LocalDate articleDate;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Builder
    public Article(String articleHeader, String articleBody, LocalDate articleDate, Customer customer) {
        super();
        this.articleHeader = articleHeader;
        this.articleBody = articleBody;
        this.articleDate = articleDate;
        this.customer = customer;
    }
}
