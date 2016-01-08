package com.pricetrendz.bean;

import java.util.Set;

import static java.util.stream.Collectors.joining;

public class Product extends BaseBean {
    private String category;
    private String description;
    private String id;
    private String link;
    private String make;
    private int numberOfRatings;
    private float price;
    private float rating; // expressed in % (0.0 to 100.0)
    private Set<String> filters;

    public String getCategory() {
        return category;
    }

    public void setCategory(final String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(final String link) {
        this.link = link;
    }

    public String getMake() {
        return make;
    }

    public void setMake(final String make) {
        this.make = make;
    }

    public int getNumberOfRatings() {
        return numberOfRatings;
    }

    public void setNumberOfRatings(final int numberOfRatings) {
        this.numberOfRatings = numberOfRatings;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(final float price) {
        this.price = price;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(final float rating) {
        this.rating = rating;
    }

    public Set<String> getFilters() {
        return filters;
    }

    public void setFilters(final Set<String> filters) {
        this.filters = filters;
    }

    @Override
    public String toString() {
        return id + "~" + category + "~" + make + "~" + description + "~" + price + "~" + rating + "~" + numberOfRatings + "~[" + filters.stream().collect(joining(", ")) + "]~" + link;
    }

    @Override
    public String getUniqueKey() {
        return id;
    }
}
