## What is?

It is a tool that allows to replace multiples `docker run ...` and `docker build ...` commands. The main advantage of Docker compose is that all Docker containers configuration, network, volumes is declare in one file, making the building, creation, starting and stopping of the containers and images easier. **Docker Compose** gives orchestration commands (build, start, stop, ...) to multiple containers on the **same host**. 

notes: *creates the container(s) in attach mode and remove when stop by default. Also Docker compose adds all the containers inside the file in the same network*

## [Install Docker Compose](https://docs.docker.com/compose/install/standalone/#on-linux)

1. Install Docker Compose using the official method:
```bash
 Download the current stable release of Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose

# Apply executable permissions
sudo chmod +x /usr/local/bin/docker-compose
```
2. Verify the installation
```bash
docker-compose --version
```
3. (Optional) If you want to run Docker commands without sudo, add your user to the docker group:
```bash
sudo usermod -aG docker $USER
```

## [Writing Docker Compose](https://docs.docker.com/reference/compose-file/)

A Docker Compose file is divide in **core** configuration elements

- Services (Containers)
	- Published Ports
	- Environments variables
	- Volumes
	- Networks

```yaml
# ref: https://docs.docker.com/reference/compose-file/version-and-name/
# version: '' version of docker-compose file (features) - OBSOLETE

name: goalsapp

services:
  mongo:
    image: mongo
    container_name: 'mongodb'
    volumes:
      - data:/data/db
    environment:
      - MONGO_INITDB_ROOT_USERNAME=gabezy
      - MONGO_INITDB_ROOT_PASSWORD=secret
  backend:
    build: ./backend
      # context: ./backend
      # dockerfile: Dockerfile
      # args:
      #   - some-arg=value
    container_name: goals-backend
    ports:
      - '3001:3001'
    volumes:
      - logs:/app/logs
      - ./backend:/app      #bind mount
      - /app/node_modules   # anonymous volume
    environment:
      - PORT=3001
      - MONGODB_USERNAME=gabezy
      - MONGODB_PASSWORD=secret
    depends_on:             
      - mongo           # tells docker-componse that backend container needs the mongodb start first
  frontend:
    build: ./frontend
    container_name: goals-frontend
    ports:
      - '3000:3000'
    volumes:
      - ./frontend/src:/app/src
    # stdin_open: true    # -i
    # tty: true           # -t
    depends_on:
      - backend

# specify named volumes
volumes:
  data:
  logs:
```

### Commands
- Use Docker Compose to pull image(s) and start container(s). Use `-d` to start container(s) at detached mode
```bash
docker-compose up -d
```
- Use Docker Compose to stop and remove container(s) and network(s). Use `-v` to also remove volume(s)
```bash
docker-compose down #-v
```
- Force to rebuild images before start the container(s)
```bash
docker-compose up --build -d
```
- To build image(s) without start container(s)
```bash
docker-compose build
```
