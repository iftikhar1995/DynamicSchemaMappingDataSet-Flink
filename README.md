# Dynamic Schema Mapping on Flink DataSet
The project contains the logic to add schema, provided in the configuration, to a dataset.

# Getting Started
Following instructions will get you a copy of the project up and running on your local machine for development and 
testing purposes.

1. Clone the repository using below command:\
   ```git clone <https://github.com/iftikhar1995/DynamicSchemaMappingDataSet-Flink.git>```

2. Create a Maven project.
3. In `pom.xml` file add the `<properties>`, `<dependencies>`, and `<build>` tags from the `pom.xml` file provided in
the repository.
4. Add content from folder **resources** and **java** into there respective folder.
5. Change the path to the configuration in the **Main** class, if necessary. To change the configuration file path,
update the value of **configFilePath** variable. For example:
   ```java
      static String configFilePath = "path to config.json";
   ``` 
6. Now run **Main.java** file to start the flink job locally.

# Overview of Configuration File
The structure of the configuration file is as follow:
```json
{
  "fields":[
        {
          "name" : "name of the field w.r.t data file",
          "type" : "the type of the field"
        },
        .
        .
        .
        {
          "name" : "name of the field w.r.t data file",
          "type" : "the type of the field"
         },

  ],
  "source" : "path to the data file",
  "PojoName" : "The name of the POJO that is going to be created on fly"
}
```

The config file contains  the `fields` key. This is a `JSONArray`. It contains the schema information. The array consist
of objects, which will provide the information of the columns like name and it's type.

The `source` key represents the location where data file resides.

The `PojoName` represents the name of the POJO that is going to be created on fly.

## Currently Supported Types
Following are the supported types of fields:
1. Integer
2. String
3. Double
4. Float
5. Short

But you can add support for more to it.

# NOTE
This is the first step. We can do a lot using this technique e.g. using TableAPI and SQL you can query any dataset. 