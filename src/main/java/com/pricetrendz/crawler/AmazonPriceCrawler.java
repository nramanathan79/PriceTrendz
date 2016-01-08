package com.pricetrendz.crawler;

import com.pricetrendz.bean.Product;
import com.sun.istack.internal.Nullable;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Set;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;

public class AmazonPriceCrawler extends PriceCrawler {
    private static final String DOMAIN_URL = "http://www.amazon.com";
    private static final int MAX_PRODUCTS = 1000;

    private String department = null;
    private Set<String> searchFilters = null;

    private static boolean isProductGood(final Element item) {
        // Check if the item has all necessary details present and a price advertised
        return item != null &&
                item.hasAttr("data-asin") &&
                !item.getElementsByClass("s-access-detail-page").isEmpty() &&
                !item.getElementsByClass("a-color-price").isEmpty();
    }

    private Product getProduct(final Element item) {
        final Element itemDetails = item.getElementsByClass("s-access-detail-page").first();
        final Element itemPrice = item.getElementsByClass("a-color-price").first();
        final Element itemMake = itemDetails.parent().nextElementSibling();
        final Element itemRating = (item.getElementsByClass("a-icon-alt").isEmpty() ? null : item.getElementsByClass("a-icon-alt").last());
        final Element itemNumberOfRatings = item.getElementsByTag("a").last();

        Product product = new Product();

        // Set the product id
        product.setId(item.attr("data-asin"));

        // Set the make
        if (itemMake != null && !itemMake.getElementsByTag("span").isEmpty()) {
            product.setMake(itemMake.getElementsByTag("span").last().html());
        }

        // Set the product description
        product.setDescription(itemDetails.attr("title"));

        // Set the product category
        product.setCategory(department);

        // Set the product filters from the given search filters and tags from the description
        Set<String> filters = stream(product.getDescription().split(" "))
                .map((tag) -> tag.replaceAll("[^a-zA-Z0-9-]+", "").toLowerCase()) // map lower case non-special character only
                .filter((tag) -> tag.length() > 1) // filter out single character tags as filter
                .collect(toSet());
        filters.addAll(searchFilters);
        product.setFilters(filters);

        // Set the product link
        String itemLink = (itemDetails.hasAttr("href") ? itemDetails.attr("href") : null);
        if (itemLink != null && itemLink.startsWith("http")) {
            product.setLink(itemLink.substring(0, itemLink.indexOf("/ref=")));
        }

        // Set the product price
        product.setPrice(Float.parseFloat(itemPrice.html().replaceAll("[^\\d.]", "")));

        // Set the product rating
        String rating = (itemRating != null ? itemRating.html() : null);
        if (rating != null && rating.endsWith("stars")) {
            product.setRating(Float.parseFloat(rating.split(" ")[0]) * 20.0f);
        }

        // Set the number of ratings
        if (itemNumberOfRatings.hasAttr("href") && itemNumberOfRatings.attr("href").endsWith("#customerReviews")) {
            product.setNumberOfRatings(Integer.parseInt(itemNumberOfRatings.html().replaceAll("[^\\d]", "")));
        }

        return product;
    }

    private String getAmazonSearchUrl() {
        // Construct the full URL using domain, department and search filters
        return getDomainUrl() +
                "/s?url=" +
                (department != null ? "search-alias=" + department + "&" : "") +
                "field-keywords=" +
                (searchFilters != null ? searchFilters.stream().collect(joining("+")) : "");
    }

    @Nullable
    private String getNextPageUrl() {
        // Get the next page link element
        final Element nextPage = (getHtmlDocument() != null ? getHtmlDocument().getElementById("pagnNextLink") : null);

        // Get the URL from the next page link, if exists
        String url = (nextPage != null && nextPage.hasAttr("href") ? nextPage.attr("href") : null);

        // String unwanted suffix in the url and return
        return (url != null ? url.substring(0, url.indexOf("&ie=")) : null);
    }

    public AmazonPriceCrawler(final Set<String> searchFilters) {
        // Set the default user agent and timeout with amazon domain URL
        super(PriceCrawler.DEFAULT_USER_AGENT, PriceCrawler.DEFAULT_TIMEOUT, DOMAIN_URL);

        // Set the search filters needed for crawling
        this.searchFilters = (searchFilters != null ? searchFilters.stream().map(String::toLowerCase).collect(toSet()) : null);
    }

    public AmazonPriceCrawler(final Set<String> searchFilters, final String department) {
        this(searchFilters);

        // Set the department and search filters needed for crawling
        this.department = (department != null ? department.toLowerCase() : null);
    }

    @Override
    public Set<Product> getProducts() {
        String url = getAmazonSearchUrl();
        Elements elements = new Elements();

        // Loop through all the pages of search results by iterating through next page link in the search results
        while (url != null && elements.size() <= MAX_PRODUCTS) {
            // Get the items as elements and append to the master list
            elements.addAll(getHtmlDocument(url).getElementsByClass("s-result-item"));

            // Get the next page Url
            url = getNextPageUrl();
        }

        // Go through the items and filter out bad ones and map to a Product bean set
        return elements
                .parallelStream()
                .filter(AmazonPriceCrawler::isProductGood)
                .map(this::getProduct)
                .collect(toSet());
    }
}
