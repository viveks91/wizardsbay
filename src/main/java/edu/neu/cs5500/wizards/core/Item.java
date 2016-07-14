package edu.neu.cs5500.wizards.core;

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
    private int buyerId;

    @JsonProperty
    @NotEmpty
    private Timestamp auctionStartTime;

    @JsonProperty
    @NotEmpty
    private Timestamp auctionEndTime;

    @JsonProperty
    @NotEmpty
    private int minBidAmount;


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

    public int getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(int buyerId) {
        this.buyerId = buyerId;
    }

    public Timestamp getAuctionStartTime() {
        return auctionStartTime;
    }

    public void setAuctionStartTime(Timestamp auctionStartTime) {
        this.auctionStartTime = auctionStartTime;
    }

    public Timestamp getAuctionEndTime() {
        return auctionEndTime;
    }

    public void setAuctionEndTime(Timestamp auctionEndTime) {
        this.auctionEndTime = auctionEndTime;
    }

    public int getMinBidAmount() {
        return minBidAmount;
    }

    public void setMinBidAmount(int minBidAmount) {
        this.minBidAmount = minBidAmount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Item item = (Item) o;

        if (id != item.id) return false;
        if (sellerId != item.sellerId) return false;
        if (buyerId != item.buyerId) return false;
        if (minBidAmount != item.minBidAmount) return false;
        if (!itemName.equals(item.itemName)) return false;
        if (!itemDescription.equals(item.itemDescription)) return false;
        if (!auctionStartTime.equals(item.auctionStartTime)) return false;
        return auctionEndTime.equals(item.auctionEndTime);

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + itemName.hashCode();
        result = 31 * result + itemDescription.hashCode();
        result = 31 * result + sellerId;
        result = 31 * result + buyerId;
        result = 31 * result + auctionStartTime.hashCode();
        result = 31 * result + auctionEndTime.hashCode();
        result = 31 * result + minBidAmount;
        return result;
    }
}
