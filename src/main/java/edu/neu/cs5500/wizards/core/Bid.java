package edu.neu.cs5500.wizards.core;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.validator.constraints.NotEmpty;

import java.sql.Timestamp;
import java.util.Objects;

/**
 * Created by susannaedens on 6/20/16.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "itemId", "bidAmount", "bidderId", "bidTime", "bidId"})
public class Bid {

    @JsonProperty
    @NotEmpty
    private Integer id;

    @JsonProperty
    @NotEmpty
    private Integer itemId;

    @JsonProperty
    @NotEmpty
    private Integer bidderId;

    @JsonProperty
    @NotEmpty
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd hh:mm:ss a", timezone="PST")
    private Timestamp bidTime;

    @JsonProperty
    @NotEmpty
    private Integer bidAmount;

    public Bid(){}

    @JsonProperty("bidId")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Integer getBidderId() {
        return bidderId;
    }

    public void setBidderId(Integer bidderId) {
        this.bidderId = bidderId;
    }

    public Timestamp getBidTime() {
        return bidTime;
    }

    public void setBidTime(Timestamp bidTime) {
        this.bidTime= bidTime;
    }

    public Integer getBidAmount() {
        return bidAmount;
    }

    public void setBidAmount(Integer bidAmount) {
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
