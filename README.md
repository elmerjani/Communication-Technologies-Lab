# Communication Technologies Lab

## Project Overview
This project is part of the Communication Technologies Lab.

---

## How to Run the Project

### 1. Prerequisites
-  **Java 8+**
-  **MongoDB database**

### 2. Clone the Repository
```sh
git clone https://github.com/elmerjani/Communication-Technologies-Lab.git
cd Communication-Technologies-Lab
```

### 3. Set Up Environment Variables
```sh
export SPRING_DATA_MONGODB_URI="your-mongodb-uri"
```
or on Windows (Command Prompt):
```sh
set SPRING_DATA_MONGODB_URI=your-mongodb-uri
```

### 4. Build project
```sh
./mvnw clean package
```

### 5. Run the Project

#### Using Maven
```sh
./mvnw spring-boot:run
```

#### Using JAR File
```sh
java -jar target/server-0.0.1-SNAPSHOT.jar
```

#### Using IntelliJ IDEA
- Open the project in IntelliJ IDEA.
- Set environment variable SPRING_DATA_MONGODB_URI in Run/Debug configuration
- Run the `ServerApplication` class.



---

