## components
    1. Minio s3 storage
    2. posgressql server
    3. pgadmin
    4. ngnix cdn and proxy
    5. angular client
    6. angular-cities-service-client
    7. cities-service

## codes

### admin

   1. https://github.com/YaredNegede/city-listing-admin.git

### backend

   2. https://github.com/YaredNegede/city-listing.git
 
## to run the application
## precondition
  
    1. docker deamon
    2. docker compose > 3

 docker images have been hosted on my docker hub account public repository,
so running the following should lounch
 
```bash
docker-compose up
```

then go to 
    
    http://localhost:9000
create a bucket with name **city**

then go to

    http://localhost:9000
logind with username:password
create user
login
create city
edit city
inside edit upload images


for more look into docker-compose file to see usernames and password configured
for some componenst
    