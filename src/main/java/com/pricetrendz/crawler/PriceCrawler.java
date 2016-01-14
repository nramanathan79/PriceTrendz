package com.pricetrendz.crawler;

import com.pricetrendz.bean.Product;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

abstract public class PriceCrawler {
    protected static final String DEFAULT_USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.106 Safari/537.36";
    protected static final int DEFAULT_TIMEOUT = 5000;

    private final String userAgent;
    private final int timeout;
    private final String domainUrl;
    private Optional<Document> htmlDocument;

    protected PriceCrawler(final String userAgent, final int timeout, final String domainUrl) {
        this.userAgent = userAgent;
        this.timeout = timeout;
        this.domainUrl = domainUrl;
    }

    protected String fullyQualifiedUrl(final String url) {
        return (url == null) ? domainUrl : (url.startsWith("http") ? url : domainUrl + url);
    }

    protected String getUserAgent() {
        return userAgent;
    }

    protected int getTimeout() {
        return timeout;
    }

    protected String getDomainUrl() {
        return domainUrl;
    }

    protected Optional<Document> getHtmlDocument() {
        return htmlDocument;
    }

    protected Optional<Document> getHtmlDocument(final String url) {
        try {
            htmlDocument = Optional.ofNullable(Jsoup.connect(fullyQualifiedUrl(url)).userAgent(userAgent).timeout(timeout).followRedirects(true).get());
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
            htmlDocument = Optional.empty();
        }

        return htmlDocument;
    }

    abstract public Set<Product> getProducts();
}
