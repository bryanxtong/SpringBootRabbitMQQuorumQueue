package com.example.rabbitmq;

import java.io.Serializable;

/**
 * Test Model
 */
public class Email implements Serializable {

    private String from;
    private String to;
    private String body;

    public Email() {
    }

    public Email(String from, String to, String body) {
        this.from = from;
        this.to = to;
        this.body = body;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return String.format("Email{from=%s,to=%s, body=%s}", getFrom(), getTo(), getBody());
    }

}