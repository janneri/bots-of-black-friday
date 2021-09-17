# Azure setup for the server

## Login

Login - it will show you your subscriptions.
```
az login
```


## Create a resource group
Create a resource group:
```
az group create --name janne_rintanen_rg \
    --location northeurope \
    --tags Owner=janneri DueDate=2021-09-18
```

## Configure Azure settings

Configure settings to server/pom.xml. 

Note: free tier linux does not support websockets: https://docs.microsoft.com/en-us/answers/questions/492517/websocket-connection-failed-azure.html

Details of the Azure maven plugin details here: 
https://github.com/microsoft/azure-maven-plugins/tree/develop/azure-webapp-maven-plugin


## Build

Build and run tests with: 
```mvn clean package```

Probably a good idea to check it's working fine in localhost:
```
java -jar target/bots-of-black-friday-0.1.0.jar
```

And then open `http://localhost:8080/`

## Deploy to Azure

Deploy with:
```mvn azure-webapp:deploy```

If needed, the deployment will create two resources:
- an appservice plan 
- an app service named bots-of-black-friday 

After the first deployment, go to app service configuration -> General settings and turn Websockets on.
Also turn on File System logging from App Service logs.

## Clean up

Clean up with:
```az group delete --name janne_rintanen_rg```