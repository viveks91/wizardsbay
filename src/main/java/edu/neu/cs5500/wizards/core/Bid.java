package edu.neu.cs5500.wizards.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.validator.constraints.NotEmpty;

import java.sql.Timestamp;
import java.util.Objects;

/**
 * Created by susannaedens on 6/20/16.
 */
public class Bid {

    @JsonProperty
    @NotEmpty
    private int id;

    @JsonProperty
    @NotEmpty
    private int itemId;

    @JsonProperty
    @NotEmpty
    private int bidderId;

    @JsonProperty
    @NotEmpty
    private Timestamp bidTime;

    @JsonProperty
    @NotEmpty
    private int bidAmount;

    public Bid(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getBidderId() {
        return bidderId;
    }

    public void setBidderId(int bidderId) {
        this.bidderId = bidderId;
    }

    public Timestamp getBidTime() {
        return bidTime;
    }

    public void setBidTime(Timestamp bidTime) {
        this.bidTime= bidTime;
    }

    public int getBidAmount() {
        return bidAmount;
    }

    public void setBidAmount(int bidAmount) {
        this.bidAmount = bidAmount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Bid bid = (Bid) o;

        if (id != bid.id) return false;
        if (itemId != bid.itemId) return false;
        if (bidderId != bid.bidderId) return false;
        if (bidAmount != bid.bidAmount) return false;
        return bidTime.equals(bid.bidTime);

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + itemId;
        result = 31 * result + bidderId;
        result = 31 * result + bidTime.hashCode();
        result = 31 * result + bidAmount;
        return result;
    }

    @Override
    public String toString() {
        return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
    }

}
