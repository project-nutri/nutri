package com.nutri.preparation.dto;

/**
 * Created by katerinaglushchenko on 1/10/15.
 */
public class DocumentDto {
    String name;
    String ingredients;
    String url;
    String tags;
    String category;
    String portions;
    String time;
    String img;
    String instruction;
    String calories;
    String fats;
    String carbs;
    String proteins;

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }

    public String getFats() {
        return fats;
    }

    public void setFats(String fats) {
        this.fats = fats;
    }

    public String getCarbs() {
        return carbs;
    }

    public void setCarbs(String carbs) {
        this.carbs = carbs;
    }

    public String getProteins() {
        return proteins;
    }

    public void setProteins(String proteins) {
        this.proteins = proteins;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPortions() {
        return portions;
    }

    public void portions(String numberOfPortions) {
        this.portions = numberOfPortions;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "DocumentDto{" +
                "name='" + name + '\'' +
                ", ingredients='" + ingredients + '\'' +
                ", url='" + url + '\'' +
                ", tags='" + tags + '\'' +
                ", category='" + category + '\'' +
                ", portions='" + portions + '\'' +
                ", time='" + time + '\'' +
                ", img='" + img + '\'' +
//                ", instruction='" + instruction + '\'' +
                ", calories='" + calories + '\'' +
                ", fats='" + fats + '\'' +
                ", carbs='" + carbs + '\'' +
                ", proteins='" + proteins + '\'' +
                '}';
    }

}
