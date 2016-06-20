package com.example.helloworld.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import java.sql.Timestamp;
import java.util.Objects;

public class Item {

    @JsonProperty
    @NotEmpty
    private int id;

    @JsonProperty
    @NotEmpty
    private String itemName;

    @JsonProperty
    @NotEmpty
    private String itemDescription;

    @JsonProperty
    @NotEmpty
    private int sellerId;

    @JsonProperty
    @NotEmpty
    private String auctionStartTime;

    @JsonProperty
    @NotEmpty
    private String auctionEndTime;

    @JsonProperty
    @NotEmpty
    private int minBidAmount;

    @JsonProperty
    private int currentMaxBid;


    public Item() {
    }

    public Item(String itemName) {
        this.itemName = itemName;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public int getSellerId() {
        return sellerId;
    }

    public void setSellerId(int sellerId) {
        this.sellerId = sellerId;
    }

    public String getAuctionStartTime() {
        return auctionStartTime;
    }

    public void setAuctionStartTime(String auctionStartTime) {
        this.auctionStartTime = auctionStartTime;
    }

    public String getAuctionEndTime() {
        return auctionEndTime;
    }

    public void setAuctionEndTime(String auctionEndTime) {
        this.auctionEndTime = auctionEndTime;
    }

    public int getMinBidAmount() {
        return minBidAmount;
    }

    public void setMinBidAmount(int minBidAmount) {
        this.minBidAmount = minBidAmount;
    }

    public int getCurrentMaxBid() {
        return currentMaxBid;
    }

    public void setCurrentMaxBid(int currentMaxBid) {
        this.currentMaxBid = currentMaxBid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Item)) {
            return false;
        }

        final Item that = (Item) o;

        return Objects.equals(this.id, that.id) &&
                Objects.equals(this.itemName, that.itemName) &&
                Objects.equals(this.itemDescription, that.itemDescription) &&
                Objects.equals(this.sellerId, that.sellerId) &&
                Objects.equals(this.auctionStartTime, that.auctionStartTime) &&
                Objects.equals(this.auctionEndTime, that.auctionEndTime) &&
                Objects.equals(this.minBidAmount, that.minBidAmount) &&
                Objects.equals(this.currentMaxBid, that.currentMaxBid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, itemName, itemDescription,sellerId, auctionStartTime, auctionEndTime, minBidAmount, currentMaxBid);
    }
}
