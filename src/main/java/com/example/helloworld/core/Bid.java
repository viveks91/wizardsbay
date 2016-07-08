package com.example.helloworld.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.validator.constraints.NotEmpty;

import java.math.BigDecimal;
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
    private int bidder;

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

    public int getBidder() {
        return bidder;
    }

    public void setBidder(int bidder) {
        this.bidder = bidder;
    }

    public int getBidAmount() {
        return bidAmount;
    }

    public void setBidAmount(int bidAmount) {
        this.bidAmount = bidAmount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, itemId, bidder, bidAmount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Bid bid = (Bid) o;

        if (id != bid.id) return false;
        if (itemId != bid.itemId) return false;
        if (bidder != bid.bidder) return false;
        return bidAmount == bid.bidAmount;

    }

    @Override
    public String toString() {
        return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
    }

}
