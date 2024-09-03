# NexusBackend-SDK-Java

# Installing the SDK in the NexusBackend Plugin

This document describes the steps necessary to install and use the SDK within the NexusBackend Plugin using the IntelliJ IDE.

## Step 1: Install the SDK Locally

### Option 1: Using Maven Goals
1. Once you have created the SDK, go to the Maven dependencies section in IntelliJ.
2. Execute the following Maven goals: `clean package install`.
3. This will install the SDK locally in the Java dependencies folder, making it easier to use within the plugin.

### Option 2: Manual Installation of the JAR File
1. Alternatively, you can manually install the SDK using the following Maven command:

   ```bash
   mvn install:install-file -Dfile="path/to/SDK.jar" -DgroupId="groupOfTheSdk" -DartifactId="artifactOfTheSdk" -Dversion="versionOfTheSdk" -Dpackaging="jar"
Make sure to replace `"path/to/SDK.jar"`, `"groupOfTheSdk"`, `"artifactOfTheSdk"`, and `"versionOfTheSdk"` with your SDK's corresponding values.

## Step 2: Add the Dependency in the Plugin
Once the SDK is installed locally, open the `pom.xml` file of the NexusBackend plugin.

Add the SDK dependency in the dependencies section, using the SDK's `groupId` and `artifactId`.

### Example:

```xml
<dependency>
    <groupId>groupOfTheSdk</groupId>
    <artifactId>artifactOfTheSdk</artifactId>
    <version>versionOfTheSdk</version>
</dependency>
```

## Step 3: Use the SDK in the Plugin
After following these steps, the SDK will be ready to use within the NexusBackend Plugin.

## Step 4: Deploy the Plugin to the Minecraft Server
To run the plugin on a Minecraft server, create an artifact that targets the plugin folder of the Minecraft server.

