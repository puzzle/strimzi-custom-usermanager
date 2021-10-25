# Custom User Manager PoC for Strimzi CR KafkaUser

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

Use the `.env` file to override configuration in the local environment.
```
KUBERNETES_CLIENT_TOKEN=token
KUBERNETES_CLIENT_MASTER_URL=http://kubernetes-proxy-url:8080
KUBERNETES_CLIENT_NAMESPACE=namespace
KAFKA_CLUSTER_NAME=strimzi-cluster-name
```

## How to use

List KafkaUsers
```shell script
curl --location --request GET 'http://localhost:8080/kafkausers'
```

Create new KafkaUser
```shell script
curl --location --request POST 'http://localhost:8080/kafkausers' \
   --header 'Content-Type: application/json' \ 
   --data-raw '{ "name": "my-user2" }'
```
