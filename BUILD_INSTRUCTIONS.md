# Build Instructions for RHS Backend

## Prerequisites
- Java 17 (JDK 17)
- Maven 3.6+
- MongoDB running locally or connection string configured

## Steps to Fix and Build

### 1. Clean Previous Builds
```bash
cd rhs-backend
mvn clean
```

### 2. Install Dependencies
```bash
mvn install -DskipTests
```

### 3. Compile the Project
```bash
mvn compile
```

### 4. Run the Application
```bash
mvn spring-boot:run
```

## Troubleshooting

### Issue: "cannot find symbol" errors for getters/setters
**Solution**: Clean and rebuild to regenerate Lombok-generated code
```bash
mvn clean install -DskipTests
```

### Issue: Lombok not working in IDE
**Solution for IntelliJ IDEA**:
1. Install Lombok plugin: `File` → `Settings` → `Plugins` → Search "Lombok"
2. Enable annotation processing: `File` → `Settings` → `Build, Execution, Deployment` → `Compiler` → `Annotation Processors` → Check "Enable annotation processing"

**Solution for Eclipse**:
1. Download lombok.jar from https://projectlombok.org/download
2. Run: `java -jar lombok.jar`
3. Select your Eclipse installation and install

**Solution for VS Code**:
1. Install "Language Support for Java" extension
2. Clean workspace: `Ctrl+Shift+P` → "Java: Clean Java Language Server Workspace"

### Issue: Wrong Java version
Check your Java version:
```bash
java -version
```
Should show Java 17. If not, update JAVA_HOME environment variable.

## Fixed Issues in This Branch

1. ✅ **MaintenanceRepository.java**: Renamed interface from `MaintenanceQueryRepository` to `MaintenanceRepository` to match filename
2. ✅ **MaintenanceQueryDTO.java**: Fixed package declaration from `com.rhs.backend.model` to `com.rhs.backend.dto` and added `assignedTo` field
3. ✅ **pom.xml**: Improved Lombok configuration with explicit version and compiler settings

## After Build

Once the build is successful, you should be able to access:
- API endpoints at `http://localhost:8080`
- Swagger UI (if configured) at `http://localhost:8080/swagger-ui.html`

## Running Tests
```bash
mvn test
```

## Creating JAR File
```bash
mvn package
```
The JAR file will be in `target/rhs-backend-0.0.1-SNAPSHOT.jar`
