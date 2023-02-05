package com.example.exchangelibrary;

public class PostFeed {
    String userId, username, title, author, summary, genre, review, rating, status, location, coverPage;

    public PostFeed(String userId, String username, String title, String author, String summary, String genre, String review, String rating, String status, String location, String coverPage) {
        this.userId = userId;
        this.username = username;
        this.title = title;
        this.author = author;
        this.summary = summary;
        this.genre = genre;
        this.review = review;
        this.rating = rating;
        this.status = status;
        this.location = location;
        this.coverPage = coverPage;
    }

    public PostFeed() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getStatus(){ return status;}

    public void setStatus(String status) {this.status = status;}

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCoverPage() {
        return coverPage;
    }

    public void setCoverPage(String coverPage) {
        this.coverPage = coverPage;
    }
}
