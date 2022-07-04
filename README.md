# Custom User Manager PoC for Strimzi CR KafkaUser

## Local Development

### Start a local KubeProxy
Start a KubeProxy using the oc binary

```
oc proxy --port 8081
```

### Get token for SA
```
oc get secrets <SA-NAME>-token-<RANDOM> --template '{{.data.token}}' | base64 -d
```

### Start Quarkus Application
You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

Use the `.env` file to override configuration in the local environment.
```
KUBERNETES_CLIENT_TOKEN=token
KUBERNETES_CLIENT_MASTER_URL=http://localhost:8081
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
