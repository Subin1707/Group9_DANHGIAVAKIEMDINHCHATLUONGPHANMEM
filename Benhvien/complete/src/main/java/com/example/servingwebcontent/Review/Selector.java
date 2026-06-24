package com.example.servingwebcontent.Review;

public interface Selector {
    boolean end();
    Object current();
    void next();
}
