# Azure setup for the server

## Login

Login - it will show you your subscriptions.
```
az login
```

## Setup a few environment variables
Follow Solita Azure sandbox conventions. Resource group name for instance janne_rintanen_rg.

Note. If the app name is bots-of-black-friday, the url will be https://bots-of-black-friday.azurewebsites.net
When running multiple instances of the game, use bots-of-black-friday-tampere, bots-of-black-friday-helsinki, ... Check WebSocketConfig.

```
export BOTS_OF_BLACK_FRIDAY_AZURE_APP_NAME=...<must be unique, for example bots-of-black-friday>
export BOTS_OF_BLACK_FRIDAY_AZURE_SID=...<use az login to find out the correct sid>
export BOTS_OF_BLACK_FRIDAY_AZURE_RG=...<use your name, for example janne_rintanen_rg>
export BOTS_OF_BLACK_FRIDAY_AZURE_OWNER=...<Solita convention, use you username>
export BOTS_OF_BLACK_FRIDAY_AZURE_DUE_DATE=...<Solita convention, for example 2023-05-05>
export BOTS_OF_BLACK_FRIDAY_AZURE_AUTHTYPE=...
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

Note: Free tier linux should support websockets: https://learn.microsoft.com/en-us/troubleshoot/azure/app-service/faqs-app-service-linux#web-sockets

Old information related to the websockets: https://learn.microsoft.com/en-us/answers/questions/492517/websocket-connection-failed-azure

Details of the Azure maven plugin details here: https://github.com/microsoft/azure-maven-plugins/tree/develop/azure-webapp-maven-plugin

## Production build

Replace endpoint addresses in the frontend configuration file: [frontend/.env.production](frontend/.env.production)

Note! The WebSocket may need to use SSL, in this case the prefix is `wss://`.

After configuring the frontend, build and run tests in the server directory with: 
```./mvnw clean package -pl server -P production```

Check that build starts fine on localhost:
```
java -jar server/target/server-1.0-SNAPSHOT.jar
```

And then open `http://localhost:8080/`, the frontend should fail because it tries to connect to the production addresses.

## Deploy to Azure

Deploy with:
```./mvnw azure-webapp:deploy -pl server```

If needed, the deployment will create two resources:
- an appservice plan 
- an app service named bots-of-black-friday 

After the first deployment, go to app service configuration -> General settings and turn Websockets on in https://portal.azure.com/.

Check the logs with
```
az webapp log tail --name $BOTS_OF_BLACK_FRIDAY_AZURE_APP_NAME --resource-group $BOTS_OF_BLACK_FRIDAY_AZURE_RG
```

## Clean up

Clean up with:
```az group delete --name $BOTS_OF_BLACK_FRIDAY_AZURE_RG```
