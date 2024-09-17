## Launch springboot 🚀

1. **Navigate to rest**

```cmd
cd rest
```

2. **Run the REST-API**

```cmd
mvn spring-boot:run
```

3. **Kill the server if already running (optional)**

This is only for when you are having issues with the server not auto-closing after use. Run these commands in the **powershell** terminal and follow the instructions:

```cmd
netstat -ano | findstr :8080
```

Replace `<PID-number>` by the value you find from the first command:

```cmd
taskkill /PID <PID-number> /F
```

4. **Go to web browser**
   http://localhost:8080/highscores
