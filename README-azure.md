# Azure setup for the server

## Login

Login - it will show you your subscriptions.

```
az login
```

Subscriptions can also be seen on Azure portal: https://portal.azure.com/#view/Microsoft_Azure_Billing/SubscriptionsBlade

## Setup a few environment variables

Follow Solita Azure sandbox conventions.

NOTE! If the app name is bots-of-black-friday, the url will be https://bots-of-black-friday.azurewebsites.net
      When running multiple instances of the game, use bots-of-black-friday-tampere, bots-of-black-friday-helsinki.

```
export BOTS_OF_BLACK_FRIDAY_AZURE_APP_NAME=...<must be unique, for example "bots-of-black-friday">
export BOTS_OF_BLACK_FRIDAY_AZURE_SID=...<use az login to find out the correct subscription ID (id value for AzureADSandbox on the list)>
export BOTS_OF_BLACK_FRIDAY_AZURE_RG=...<use your name, for example "firstname_surname_rg">
export BOTS_OF_BLACK_FRIDAY_AZURE_OWNER=...<Solita convention, use your username, for example "firstname.surname@domain.tld">
export BOTS_OF_BLACK_FRIDAY_AZURE_DUE_DATE=...<Solita convention, for example "2023-05-05">
```

## Create a resource group

Create a resource group:

```
az group create --name $BOTS_OF_BLACK_FRIDAY_AZURE_RG \
    --location northeurope \
    --tags Owner=$BOTS_OF_BLACK_FRIDAY_AZURE_OWNER DueDate=$BOTS_OF_BLACK_FRIDAY_AZURE_DUE_DATE
```

## Configure Azure settings

See the settings in server/pom.xml. They should be fine and use the environment variables above. 

NOTE! Free tier linux should support WebSockets: https://learn.microsoft.com/en-us/troubleshoot/azure/app-service/faqs-app-service-linux#web-sockets

Old information related to the WebSockets: https://learn.microsoft.com/en-us/answers/questions/492517/websocket-connection-failed-azure

Details of the Azure maven plugin details here: https://github.com/microsoft/azure-maven-plugins/tree/develop/azure-webapp-maven-plugin

## Production build

Build and run tests in the server directory with:

```./mvnw clean package -pl server -P production```

Check that build starts fine on localhost:

```
java -jar server/target/server-1.0-SNAPSHOT.jar
```

and then open `http://localhost:8080/`

## Deploy to Azure

Deploy with:

```./mvnw azure-webapp:deploy -pl server```

If needed, the deployment will create two resources:

- an App Service plan 
- an App Service, named after BOTS_OF_BLACK_FRIDAY_AZURE_APP_NAME

NOTE! The application can only be accessed via HTTP, HTTPS does not currently work with WebSockets.

Check the logs with

```
az webapp log tail --name $BOTS_OF_BLACK_FRIDAY_AZURE_APP_NAME --resource-group $BOTS_OF_BLACK_FRIDAY_AZURE_RG
```

## Clean up

Clean up with:

```az group delete --name $BOTS_OF_BLACK_FRIDAY_AZURE_RG```
