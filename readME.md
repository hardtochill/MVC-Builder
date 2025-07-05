# SpringBoot架构代码生成器

## 项目概述
本程序是一个SpringBoot架构代码生成器，主要功能是解析数据库并自动生成Controller、Service、DAO三层架构与CRUD代码。它通过读取并解析YML配置文件，利用JDBC连接数据库，准确解析表字段、索引、注释等表信息，然后依据这些信息生成符合SpringBoot分层架构的代码，具有良好的可读性和可维护性。同时，还会生成MapperXML文件，支持依据联合索引对数据进行查改删操作，以及批量插入、分页查询等基本功能。此外，代码会自动依据表注释与业务逻辑生成注释，方便开发人员快速理解代码。

## 功能特性
1. **数据库解析**：通过JDBC连接数据库，解析表的字段、索引和注释等信息。
2. **代码生成**：自动生成Controller、Service、DAO三层架构的基本CRUD代码。
3. **MapperXML支持**：生成MapperXML文件，支持联合索引的查改删操作、批量插入和分页查询。
4. **注释生成**：根据表注释和业务逻辑为代码添加详细注释。

## 项目结构
```
MVC-Builder/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── easyjava/
│   │   │           └── builder/
│   │   │               ├── BuildCommon.java
│   │   │               ├── BuildMapper.java
│   │   │               ├── BuildComment.java
│   │   │               ├── BuildController.java
│   │   │               ├── BuildTable.java
│   │   │               ├── BuildService.java
│   │   │               └── BuildDTO.java
│   │   └── resources/
│   │       └── template/
│   │           └── Result.txt
└── .gitignore
```

## 使用方式

### 1. 环境准备
- 确保你已经安装了Java开发环境（JDK），推荐使用JDK 8及以上版本。
- 准备好目标项目，项目需要基于Spring Boot框架。

### 2. 依赖导入
手动为目标项目导入`pagehelper`依赖。在`pom.xml`文件中添加以下依赖：
```xml
<dependency>
    <groupId>com.github.pagehelper</groupId>
    <artifactId>pagehelper-spring-boot-starter</artifactId>
    <version>最新版本号</version>
</dependency>
```
请将`最新版本号`替换为实际的最新版本。

### 3. 配置文件设置
手动在配置文件（通常是`application.yml`或`application.properties`）中开启驼峰命名。

#### application.yml示例
```yaml
mybatis:
  configuration:
    map-underscore-to-camel-case: true
```

#### application.properties示例
```properties
mybatis.configuration.map-underscore-to-camel-case=true
```

### 4. 运行代码生成器
1. 打开代码生成器项目，确保数据库连接信息在YML配置文件中正确配置，包括数据库URL、用户名、密码等。
2. 运行代码生成器的入口类（通常是包含`main`方法的类），即可自动生成相应的代码。代码将生成到指定的目录下，例如Controller、Service、DAO等层的代码以及MapperXML文件。

### 5. 调整生成代码
- `dto`、`entity`、`vo`虽然会生成出来，但用户需根据需要更改对应信息。例如，根据实际业务需求调整属性的类型、添加或删除属性等。
- `PageQueryDTO`对象直接继承自对应`DTO`类，开发者可根据需求选择不继承`DTO`类，直接编写对应分页查询的属性。

## 代码生成说明

### 通用类生成
`BuildCommon.java`负责创建一些通用类，如消息转化器、`Result`对象、`PageResult`对象、`BaseException`对象和`ExceptionHandler`对象。

### Mapper类生成
`BuildMapper.java`根据表的索引信息生成Mapper接口，包含根据索引的查询、更新、删除方法，以及分页查询、单条插入和批量插入方法。

### Controller类生成
`BuildController.java`生成Controller类，包含根据索引的查询、更新、删除方法，以及分页查询、新增和批量新增方法。

### Service类生成
`BuildService.java`生成Service接口，包含根据索引的查询、更新、删除方法，以及分页查询、新增和批量新增方法。

### DTO类生成
`BuildDTO.java`根据表信息生成对应的DTO类，包含属性的声明、`set`和`get`方法，以及`toString`方法。

## 注意事项
- 请确保数据库连接信息在YML配置文件中正确配置。
- 生成的代码可能需要根据实际业务需求进行适当调整。
