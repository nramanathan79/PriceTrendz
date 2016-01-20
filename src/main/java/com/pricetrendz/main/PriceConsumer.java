package com.pricetrendz.main;

import com.pricetrendz.crawler.AmazonPriceCrawler;
import com.pricetrendz.crawler.PriceCrawler;

import java.util.HashSet;

import static java.util.Arrays.asList;

public class PriceConsumer {
    public static void main(final String[] args) {
        PriceCrawler crawler = new AmazonPriceCrawler(new HashSet<>(asList("Television", "4K")), "Electronics");

        crawler.getProducts().forEach((item) -> System.out.println(item.toJSON()));
    }
}
