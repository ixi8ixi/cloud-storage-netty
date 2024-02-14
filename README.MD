# Account Login Window for Cloud Storage

Instructions for running different parts of the project

## 1. Server

To start the server, run the following command from the project root: `gradlew server:run`. Logs will be displayed in the console.

## 2. Client

To start the client, run the following command from the project root: `gradlew client:run`. After executing the command, a window for entering your username and password will appear.

Currently, Downloading, Uploading and Moving files on the server are supported. To add support for a new operation, 
use the [@CommandProcessor](./common/src/main/java/com/ix8oio8xi/commands/CommandProcessor.java) annotation,
[ClientProcessor](./client/src/main/java/com/ix8oio8xi/client/commands/ClientProcessor.java) and 
[ServerProcessor](./server/src/main/java/com/ix8oio8xi/server/commands/ServerProcessor.java) interfaces. 
[CommandRegistry](./common/src/main/java/com/ix8oio8xi/commands/CommandRegistry.java) will index new processor 
automatically.