package edu.neu.cs5500.wizards.core;

import com.fasterxml.jackson.annotation.*;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

/**
 * Created by susannaedens on 6/20/16.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(value = { "bidderId" })
@JsonPropertyOrder({ "itemId", "bidAmount", "bidderUsername", "bidTime", "bidId"})
public class Bid {

    @JsonProperty
    private Integer id;

    @JsonProperty
    private Integer itemId;

    @JsonProperty
    private Integer bidderId;

    @JsonProperty
    @NotEmpty
    private String bidderUsername;

    @JsonProperty
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd hh:mm:ss a", timezone="PST")
    private Timestamp bidTime;

    @JsonProperty
    @NotNull
    @Min(1)
    private Integer bidAmount;

    public Bid(){}

    @JsonProperty("bidId")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBidderUsername() {
        return bidderUsername;
    }

    public void setBidderUsername(String bidderUsername) {
        this.bidderUsername = bidderUsername;
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

        if (id != null ? !id.equals(bid.id) : bid.id != null) return false;
        if (itemId != null ? !itemId.equals(bid.itemId) : bid.itemId != null) return false;
        if (bidderId != null ? !bidderId.equals(bid.bidderId) : bid.bidderId != null) return false;
        if (!bidderUsername.equals(bid.bidderUsername)) return false;
        if (bidTime != null ? !bidTime.equals(bid.bidTime) : bid.bidTime != null) return false;
        return bidAmount.equals(bid.bidAmount);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (itemId != null ? itemId.hashCode() : 0);
        result = 31 * result + (bidderId != null ? bidderId.hashCode() : 0);
        result = 31 * result + bidderUsername.hashCode();
        result = 31 * result + (bidTime != null ? bidTime.hashCode() : 0);
        result = 31 * result + bidAmount.hashCode();
        return result;
    }
}
