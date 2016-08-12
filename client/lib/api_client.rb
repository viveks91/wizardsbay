require 'net/http'
require "uri"
require 'json'


class ApiClient

  HOST = "https://wizards-ebayclone.herokuapp.com/wizardsbay"

  def create_user(body)
    return httpCall("user", "POST", body)
  end

   def get_user_by_name(user_name)
    return httpCall("user/" + user_name, "GET")
  end 
  
  def delete_user(user_name, password)
    return httpCall("user/" + user_name, "DELETE", "", user_name, password)
  end
  
  def post_item(body, user_name, password)
    return httpCall("item", "POST", body, user_name, password)
  end
  
  def delete_item(item_id, user_name, password)
    return httpCall("item/" + item_id, "DELETE", "", user_name, password)
  end
  
  def make_bid(item_id, body, user_name, password)
    return httpCall("item/" + item_id + "/bids" , "POST", body, user_name, password)
  end
  
  def delete_bid(item_id, bid_id, user_name, password)
    return httpCall("item/" + item_id + "/bids/" + bid_id, "DELETE", "", user_name, password)
  end

  def httpCall(endPoint, type="GET", body="", user_name="", password="")
    url = "#{HOST}/" + endPoint
    uri = URI.parse(url)
    http = Net::HTTP.new(uri.host, uri.port)
    if type == "POST"
    req = Net::HTTP::Post.new(uri.request_uri)
    elsif type == "DELETE"
    req = Net::HTTP::Delete.new(uri.request_uri)
    elsif type == "PUT"
    req = Net::HTTP::Put.new(uri.request_uri)
    else
    req = Net::HTTP::Get.new(uri)
    end
    if body != ""
        req.body = body.to_json
    end
    if user_name != "" && password !=""
        req.basic_auth(user_name, password)
    end
    req["Content-Type"] = "application/json"
    http.use_ssl = (uri.scheme == "https")
    response = http.request(req)
    return response
  end

end
