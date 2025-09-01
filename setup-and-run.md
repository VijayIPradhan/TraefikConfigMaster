# Setup and Run Instructions

## Step 1: Install Java

You need Java 17 or higher to run this Spring Boot application.

### Option A: Download from Oracle/OpenJDK
1. Go to https://adoptium.net/temurin/releases/
2. Download Java 17 or higher for Windows
3. Install and make sure to check "Add to PATH" during installation

### Option B: Using Chocolatey (if installed)
```cmd
choco install openjdk17
```

### Option C: Using Winget
```cmd
winget install Microsoft.OpenJDK.17
```

## Step 2: Verify Java Installation

Open a new command prompt and run:
```cmd
java -version
```

You should see something like:
```
openjdk version "17.0.x" 2023-xx-xx
OpenJDK Runtime Environment (build 17.0.x+x)
OpenJDK 64-Bit Server VM (build 17.0.x+x, mixed mode, sharing)
```

## Step 3: Set JAVA_HOME (if needed)

If you get "JAVA_HOME not found" error:

1. Find your Java installation directory (usually `C:\Program Files\Eclipse Adoptium\jdk-17.x.x-hotspot\`)
2. Set JAVA_HOME environment variable:
   - Press Win + R, type `sysdm.cpl`, press Enter
   - Click "Environment Variables"
   - Click "New" under System Variables
   - Variable name: `JAVA_HOME`
   - Variable value: `C:\Program Files\Eclipse Adoptium\jdk-17.x.x-hotspot\` (your actual path)
   - Click OK

## Step 4: Run the Application

Once Java is properly installed:

```cmd
mvnw.cmd spring-boot:run
```

Or if you have Maven installed:
```cmd
mvn spring-boot:run
```

## Alternative: Run with IDE

1. Install IntelliJ IDEA Community (free) or VS Code with Java extensions
2. Open this project folder
3. Run the `TraefikConfigApplication.java` file

## Troubleshooting

### "mvnw.cmd is not recognized"
Make sure you're in the project directory where `mvnw.cmd` is located.

### "JAVA_HOME not found"
Follow Step 3 above to set the JAVA_HOME environment variable.

### Port 8080 already in use
Change the port in `src/main/resources/application.yml`:
```yaml
server:
  port: 8081  # or any other available port
```