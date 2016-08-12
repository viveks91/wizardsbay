require 'spec_helper'
 
describe ApiClient do

    seller = ({
        "firstName"=>"seller3", 
        "lastName"=>"r", 
        "username"=>"seller3", 
        "password"=>"pass", 
        "email"=>"meh@meh.com", 
        "address"=>"not for you"})
        
    bidder = ({
        "firstName"=>"mrBidder3", 
        "lastName"=>"Sir", 
        "username"=>"bidder3", 
        "password"=>"pass", 
        "email"=>"bid@meh.com", 
        "address"=>"Bidder Ave"})
        
    item = ({
      "itemName"=>"testItem4",
      "itemDescription"=>"description of TestItem4",
      "minBidAmount"=>1,
      "auctionEndTime"=>"2016-09-01 12:00:00 AM",
      "sellerUsername"=>seller['username']
            })
            
     bid = ({
  "bidAmount"=> 10
            })

  
  it "Should create a user (seller)" do 
    client = ApiClient.new
    response = client.create_user(seller)
    expect(JSON.parse(response.body)).to eql seller
  end 
  
    it "Should create a user (bidder)" do 
    client = ApiClient.new
    response = client.create_user(bidder)
    expect(JSON.parse(response.body)).to eql bidder
  end 


  it "Should get back a user" do 
    client = ApiClient.new
    response = client.get_user_by_name(seller["username"])
    seller_noPassNoEmail = seller.clone
    seller_noPassNoEmail.delete("password")
    seller_noPassNoEmail.delete("email")
    expect(JSON.parse(response.body)).to eql seller_noPassNoEmail
  end 
  
  it "Should create an item" do 
    client = ApiClient.new
    response = client.post_item(item, seller['username'], seller['password'])
    item['itemId'] = JSON.parse(response.body)['itemId'].to_s
    expect(response.code).to eql "200"
  end 
  
  it "Should place a bid" do 
    client = ApiClient.new
    bid["bidderUsername"] = bidder['username']
    response = client.make_bid(item['itemId'], bid, bidder['username'], bidder['password'])
    bid = JSON.parse(response.body)
    expect(response.code).to eql "200"
  end 
  
  it "Should delete a bid" do 
    client = ApiClient.new
    body = client.delete_bid(item['itemId'], bid['bidId'].to_s, bidder['username'], bidder['password'])
    expect(body.code).to eql "204"
  end 
  
  it "Should delete an item" do 
    client = ApiClient.new
    body = client.delete_item(item['itemId'], seller['username'], seller['password'])
    expect(body.code).to eql "204"
  end 
  
  it "Should delete a user" do 
    client = ApiClient.new
    body = client.delete_user(bidder['username'], bidder['password'])
    expect(body.code).to eql "204"
  end 
  
  it "Should delete a user" do 
    client = ApiClient.new
    body = client.delete_user(seller['username'], seller['password'])
    expect(body.code).to eql "204"
  end 

end
