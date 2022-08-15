# Desafio Java Backend Pl/Sr

## Para rodar o projeto basta usar o seguinte comando 
>  ### <li>mvnw spring-boot:run</li>


## É necessário que tenha um banco Aws DynamoDb com uma tabela chamada noticias, que pode ser criada com o comando abaixo:

>  <li> aws dynamodb create-table --table-name noticias --attribute-definitions AttributeName=id,AttributeType=S --key-schema AttributeName=id,KeyType=HASH --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5 --endpoint-url http://localhost:8000 </li>

#### Obs: as configurações de acesso ao banco estão no arquivo "application.properties"