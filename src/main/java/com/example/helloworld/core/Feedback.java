package com.example.helloworld.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.Objects;

/**
 * Created by susannaedens on 6/20/16.
 */
public class Feedback {

    @JsonProperty
    @NotEmpty
    private int id;

    @JsonProperty
    @NotEmpty
    private int userId;

    @JsonProperty
    @NotEmpty
    private String desc;

    public Feedback(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, desc);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Feedback feedback = (Feedback) o;

        if (id != feedback.id) return false;
        if (userId != feedback.userId) return false;
        return desc != null ? desc.equals(feedback.desc) : feedback.desc == null;
    }

    @Override
    public String toString() {
        return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
    }
}
