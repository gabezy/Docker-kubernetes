# Setup
1. Save the Dockerfile as Dockerfile.dev
2. Save the docker-compose file as docker-compose.yml 
3. Add the properties to your application-dev.properties
4. Run with:
```bash
docker-compose up --build
```
## Features of this setup

1. Mounts your local source code directory into the container
2. Mounts your local Maven cache to speed up builds
3. Enables Spring Boot DevTools for live reload
4. Maps both the application port and live reload port
5. Uses development-specific properties
6. Preserves Maven dependencies between container restarts

For better live reload performance, you might want to add this to your IDE settings:
For IntelliJ IDEA:

- Enable "Build project automatically" under Settings > Build, Execution, Deployment > Compiler
- Enable "Allow auto-make to start even..." under Registry (Ctrl+Shift+A, type "registry")

For VS Code:

- Install the Spring Boot Extension Pack
- Enable "Java > Debug > Settings: Hot Code" in settings

