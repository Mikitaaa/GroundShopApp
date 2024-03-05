package com.groundshop.groundshopapp.ui.parser;

public class Order {
    private int id;
    private String name;
    private String phone;
    private String comment;

    public Order(int id, String name, String phone, String comment) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.comment = comment;
    }

    public int getId() {  return id; }

    public void setId(int id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }

    public void setPhone(String phone) { this.phone = phone; }

    public String getComment() { return comment; }

    public void setComment(String comment) { this.comment = comment; }
}

