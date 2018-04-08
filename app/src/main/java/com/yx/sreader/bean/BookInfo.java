package com.yx.sreader.bean;

import android.media.Image;
import android.widget.ImageView;

/**
 * Created by iss on 2018/4/8.
 */

public class BookInfo {
    private String bookname;
    private String bookintroduction;
    private Image bookimage;
    private String bookpath;
    private String author;

    public String getBookname() {
        return bookname;
    }

    public void setBookname(String bookname) {
        this.bookname = bookname;
    }

    public String getBookintroduction() {
        return bookintroduction;
    }

    public void setBookintroduction(String bookintroduction) {
        this.bookintroduction = bookintroduction;
    }

    public Image getBookimage() {
        return bookimage;
    }

    public void setBookimage(Image bookimage) {
        this.bookimage = bookimage;
    }

    public String getBookpath() {
        return bookpath;
    }

    public void setBookpath(String bookpath) {
        this.bookpath = bookpath;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
