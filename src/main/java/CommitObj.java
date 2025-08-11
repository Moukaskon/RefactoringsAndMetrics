package main.java;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CommitObj {

    private String sha;
    private Date date;
    private String fullDate;
    private String slimDate;

    public CommitObj(String sha, Date date) {
        this.sha = sha;
        this.date = date;
        this.fullDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(date);
        this.slimDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    public String getSha() {
        return sha;
    }

    public Date getDate() {
        return date;
    }
}