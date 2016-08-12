# Wizardsbay

Wizardsbay is a clone of ebay web services. It provides basic functionality such as creating an account, posting items, placing a bid.

* Application URL : https://wizards-ebayclone.herokuapp.com/wizardsbay
* Documentation : [https://wizards-ebayclone.herokuapp.com/wizardsbay/swagger](https://wizards-ebayclone.herokuapp.com/wizardsbay/swagger "Swagger")

## Outline

-	Store information about users
  *	Allow user to authenticate
-	Store information about items
-	Store/track information about bids, auctions
  *	Make bids
-	View/search for items that are available
-	Record and present feedback between buyers and sellers
-	Access & modify stored information via an API of some sort (in JSON format)
-	Email notification for customers
-	Implement testing to ensure everything is working together as expected

## More for nerds

+	Web Host: __Heroku__
+	Database: __Postgres__
+	Dropwizard framework
+	DB control: __Liquibase Migrations__
+	Jackson JSON/Java parser Library
+	Dependency manager: __Maven__
+	Testing Framework: __Junit__
+	Continuous Integration: __Codeship__
+	Documentation: __Swagger__
+	Email plugin: __Mailgun plugin__
+	Scheduler: __Quartz__

## Running The Application

* To package the application

        mvn package

* To setup the postgres database

        java -jar target/ebay-clone-0.9.2.jar db migrate configuration.yml

* To run the server

        java -jar target/ebay-clone-0.9.2.jar server configuration.yml


## End points

### Item

* Creates an item in the database given that item  
  POST /item  

* Finds all active items in the database  
  GET /item/active 

* Finds all matching items in the database  
  GET /item/search/{key} 

* Finds an item by id  
  GET /item/{id} 

* Updates an item in the database given the item with updated information  
  PUT /item/{id}  

* Deletes an item from the database by ID  
  DELETE /item/{itemId} 

### Bids

* Creates an bid in the database given the bid  
  POST /item/{itemId}/bids   

* Finds the current highest bid for an item by the item id  
  GET /item/{itemId}/bids/highest 

* Finds all bids for a specified item given the item id  
  GET /item/{itemId}/bids/history 

* Deletes a bid by it's id  
  DELETE /item/{itemId}/bids/{bidId} 

* Finds a bid by it's id  
  GET /item/{itemId}/bids/{id} 

### User

* Creates a user in the database given the user  
  POST /user    

* Deletes a specified user from the database by username  
  DELETE /user/{username} 

* Finds and returns a user from the database by username  
  GET /user/{username} 

* Updates a certain user with new information given the user with updated information  
  PUT /user/{username}   

* Finds all items linked to the specified user by username  
  GET /user/{username}/items 

### Feedback

* Creates a feedback in the database for specified user given the feedback  
  POST /user/{username}/feedback  

* Finds all feedback for a user by username  
  GET /user/{username}/feedback/all

* Deletes a feedback from the database by id  
  DELETE /user/{username}/feedback/{feedbackId} 

* Finds feedback by it's id  
  GET /user/{username}/feedback/{id} 



