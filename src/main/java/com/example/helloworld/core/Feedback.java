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
    private int userid;

    @JsonProperty
    @NotEmpty
    private String feedbackdesc;

    public Feedback() {
    }

    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }


    public int getUserid() {
        return userid;
    }


    public void setUserid(int userid) {
        this.userid = userid;
    }


    public String getFeedbackdesc() {
        return feedbackdesc;
    }

    public void setFeedbackdesc(String feedbackdesc) {
        this.feedbackdesc = feedbackdesc;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userid, feedbackdesc);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Feedback feedback = (Feedback) o;

        if (id != feedback.id) return false;
        if (userid != feedback.userid) return false;
        return feedbackdesc != null ? feedbackdesc.equals(feedback.feedbackdesc) : feedback.feedbackdesc == null;
    }

    @Override
    public String toString() {
        return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
    }
}
