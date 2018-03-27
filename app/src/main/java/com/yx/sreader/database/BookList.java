package com.yx.sreader.database;

import org.litepal.crud.DataSupport;

/**
 * Created by iss on 2018/3/26.
 */

public class BookList extends DataSupport {
    private int id;
    private String bookname;
    private String bookpath;

    public String getBookname() {
        return this.bookname;
    }

    public void setBookname(String bookname) {
        this.bookname = bookname;
    }

    public String getBookpath() {
        return this.bookpath;
    }

    public void setBookpath(String bookpath) {
        this.bookpath = bookpath;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
