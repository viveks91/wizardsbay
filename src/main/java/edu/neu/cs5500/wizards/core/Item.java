package edu.neu.cs5500.wizards.core;

import com.fasterxml.jackson.annotation.*;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(value = { "sellerId", "buyerId" })
@JsonPropertyOrder({ "itemName", "itemDescription", "minBidAmount", "auctionStartTime", "auctionEndTime", "sellerUsername", "buyerUsername", "itemId"})
public class Item {

    @JsonProperty
    private Integer id;

    @JsonProperty
    @NotEmpty
    private String itemName;

    @JsonProperty
    @NotEmpty
    private String itemDescription;

    @JsonProperty
    private Integer sellerId;

    @JsonProperty
    @NotEmpty
    private String sellerUsername;

    @JsonProperty
    private Integer buyerId;

    @JsonProperty
    private String buyerUsername;

    @JsonProperty
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd hh:mm:ss a", timezone="PST")
    private Timestamp auctionStartTime;

    @JsonProperty
    @NotNull
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd hh:mm:ss a", timezone="PST")
    private Timestamp auctionEndTime;

    @JsonProperty
    @NotNull
    @Min(1)
    private Integer minBidAmount;


    public Item() {
    }

    public String getBuyerUsername() {
        return buyerUsername;
    }

    public void setBuyerUsername(String buyerUsername) {
        this.buyerUsername = buyerUsername;
    }

    public String getSellerUsername() {
        return sellerUsername;
    }

    public void setSellerUsername(String sellerUsername) {
        this.sellerUsername = sellerUsername;
    }

    public Item(String itemName) {
        this.itemName = itemName;
    }

    @JsonProperty("itemId")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public Integer getSellerId() {
        return sellerId;
    }

    public void setSellerId(Integer sellerId) {
        this.sellerId = sellerId;
    }

    public Integer getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Integer buyerId) {
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

    public Integer getMinBidAmount() {
        return minBidAmount;
    }

    public void setMinBidAmount(Integer minBidAmount) {
        this.minBidAmount = minBidAmount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Item item = (Item) o;

        if (id != null ? !id.equals(item.id) : item.id != null) return false;
        if (!itemName.equals(item.itemName)) return false;
        if (!itemDescription.equals(item.itemDescription)) return false;
        if (sellerId != null ? !sellerId.equals(item.sellerId) : item.sellerId != null) return false;
        if (!sellerUsername.equals(item.sellerUsername)) return false;
        if (buyerId != null ? !buyerId.equals(item.buyerId) : item.buyerId != null) return false;
        if (buyerUsername != null ? !buyerUsername.equals(item.buyerUsername) : item.buyerUsername != null)
            return false;
        if (auctionStartTime != null ? !auctionStartTime.equals(item.auctionStartTime) : item.auctionStartTime != null)
            return false;
        if (!auctionEndTime.equals(item.auctionEndTime)) return false;
        return minBidAmount.equals(item.minBidAmount);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + itemName.hashCode();
        result = 31 * result + itemDescription.hashCode();
        result = 31 * result + (sellerId != null ? sellerId.hashCode() : 0);
        result = 31 * result + sellerUsername.hashCode();
        result = 31 * result + (buyerId != null ? buyerId.hashCode() : 0);
        result = 31 * result + (buyerUsername != null ? buyerUsername.hashCode() : 0);
        result = 31 * result + (auctionStartTime != null ? auctionStartTime.hashCode() : 0);
        result = 31 * result + auctionEndTime.hashCode();
        result = 31 * result + minBidAmount.hashCode();
        return result;
    }
}
