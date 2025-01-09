## What is Docker?

Docker is a container technology that can create and managing **containers**. **Container** is a standardized unit of software, basically a package of code and dependencies to run that code (e.g Java code + JDK and Maven). This allow the same container always yields the exact same application and execution behavior.
## Containers 

**Containers** are lightweight and contain everything needed to run the application and allows to have multiple "application package", so you can have the various applications running with their own setting and specifics dependencies version (e.g Java 8, Java 11 or Java 17) and different environments. The used of containers can be helpful in various scenarios providing reliability and reproducible, such as:
- Different Development and Production Environments
- Different Development Environments Within a Team/Company
- Clashing Tools/version between different projects and etc
## Virtual Machines x Docker Containers

A **virtual machine** (VM) is a software that emulates a operating system on top of your current operation system, basically emulating a new computer inside your current computer, using the hardware resource of the computer.

![VM architecture](https://www.techtarget.com/rms/onlineimages/virtual_machines-h_half_column_mobile.png)

- Pros
	- Separated environments
	- Environments-specific configurations are possible
	- Environment configurations con be shared and reproduced reliably
- Cons
	- Redundant duplication wasting space
	- Performance can be slow, boot time can be long
	- Reproducing on another computer/server is possible but still be tricky

A Docker **Containers** uses on the OS Built-in/Emulated container support to run the **Docker Engine** that will handle the creation and manage the containers. Usually the containers use a lightweight OS layer to run the application and dependencies.

<img src="https://www.altexsoft.com/static/blog-post/2023/11/50d965c7-b468-4de6-ad45-d8c8cb385a02.jpg" width="500"/>

- Pros
	- Low impact on OS, very fast, minimal disk space usage
	- Sharing, re-building and distribution is easy
	- Encapsulate apps/environments instead of "whole machines"
- Cons
	- Complexity Management, it is necessary to use Kubernetes to orchestrate the containers adding a significant complexity
	- Networking complexity
	- Storage Persistence challenges

## [Docker Architecture](https://docs.docker.com/get-started/docker-overview/#docker-architecture)

<img src="https://docs.docker.com/get-started/images/docker-architecture.webp" width="800"/>

- **Docker Daemon (`dockerd`)**: Is the responsible to build, run and distribute the Docker containers. The Docker daemon listens for Docker API requests and manages Docker objects such as images, containers, networks, and volumes. A daemon can also communicate with other daemons to manage Docker services.
- **Docker Client (`docker`)**: Is the primary way to interact with Docker. the commands such as `docker run`, the client sends the commands to `dockerd` (daemon) through the Docker API (REST API), which carries them out. The Docker client and daemon can run on the same system, or you can connect a Docker client to a remote Docker daemon. *The Docker client can communicate with more than one daemon.*
- **Docker Registries**: It stores Docker images. Docker Hub is a public registry that anyone can use, and Docker looks for images on Docker hub by default. When commands like `docker pull` or `docker run` is used, Docker pulls the required images from your configured registry. When `docker push` command is used, Docker pushes your image to your configured registry.
- **Docker Objects**: It is the images, containers, networks, volumes, plugins, and other objects that Docker can create and manager.
- **Docker Image**: Is a read-only template with instructions for create a Docker container. A **Docker image** is **layer-based**. In other, once build if some instruction or code changes, only that specific part will be built at scratch, all the rest will used the cache
- **Docker Container**: It is a runnable instance of an image than can be created, started, stopped, moved or deleted using the Docker API or CLI.

Example `docker run` command

```bash
docker run -i -t ubuntu /bin/bash
```

1. If you don't have the `ubuntu` image locally, Docker pulls it from your configured registry, as though you had run `docker pull ubuntu` manually.
2. Docker create a new container, equivalent to the `docker container create` command.
3. Docker allocates a read-write filesystem to the container, as its final layer. This allows a running container to create or modify files and directories in its local filesystem.
4. Docker creates a network interface to connect the container to the default network, since any network configuration aren't specifying.
5. Docker starts the container and executes `/bin/bash` because the container is running interactively and attached to your terminal (due to the `-i` and `-t` flags).
6. When you run `exit` to terminate the `/bin/bash` command, the container stops but isn't removed.

## Docker commands

- Get Docker manual
```
docker [command] --help
```
- List all containers
```bash
docker ps -a # ps stands for processes | -a for all
```
- Create a new container, by default creates the container in **attached mode**
```
docker run -p 80:80 image_name
```
- Create a new container in **detached** mode with `-d` flag
```
docker run -p 80:80 -d image_name
```
- Restart a container, by default starts the container in **detached mode**
```
docker start container_name
```
- Restart a container in **attached mode**
```
docker start -a container_name
```
- Attach to a running container
```
docker attach container_name
```
- Fetch the logs of a container
```
docker logs container_name
```
- Remove containers
```
docker rm container_name other_containers...
```
- Remove images
```
docker rmi image_name other_images...
```
- Remove all unused images (without containers created and don´t have tag)
```bash
docker image prune
```
- Remove all images 
```bash
docker image prune -a
```
- Remove automatically the container when exits with `--rm`
```bash
docker run -p 3000:80 -d --rm image_name
```
- Inspect image
```bash
docker inspect image_name
```
- Copy files to the container or out
```bash
# Copy to Docker container
docker cp directory/. container_name:/path # if the path doesn´t exist, Docker will create inside the container

# Copy from Docker container to local machine
docker cp container_name:/path local_path/ # if the path doesn´t exist, Docker will create inside the container
```
- Rename image (create a clone of the old image)
```bash
docker tag image_old_name:tag image_new_name:tag
```
- Execute commands inside a running the container
```bash
docker exec -it <container_name> [command]
```
- Overwrite the default command at container initialization
```bash
docker run -it <image> [command]

docker run -it node npm init
```
## Dockerfile

The `Dockerfile` is a special file that holds all the configuration, commands and images that will be used to build the desirable Docker Image using `dockerd`.
*note: which instruction in the Docker file is considered a layer and caches every layer*

```Dockerfile
# Set the base image and version to go to the image (by default search in the Docker hub)
FROM node:14

# Tells Docker that all the subsequent commands will be executed in the /app folder
WORKDIR /app

# Tells which files will be copied to the image
# the `. .` tells to copy all files, folders and subfolders from the base directory (excluding the Dockerfile) and paste to the /app folder
COPY . /app

# Execute a specific command (exceute when the image is installed)
RUN npm install

# Expose the container's 80 port to the local system
EXPOSE 80

# This line will be executed only when the container is started
CMD [ "node", "server.js" ]
```

- To build the image, use the following command
```bash
docker build . --tag image_name
```

- To run the container and expose a specific port, use the following command with `-p` flag to specify the port
```bash
docker run image_name -p port:application_running_port
```

![[docker_image_container.png]]

## Push image to Docker Hub

1. Create a repository in Docker Hub
2. Change the Docker image to the Docker repository name
	```bash
	docker tag local-image:tagname new-repo:tagname
	```
3. Login with Docker CLI
	```
	docker login
	```
4. Push the image
	```bash
	docker push new-repo:tagname
	```

## Multi-Stage Builds

When we have a React application, for example, the development build image process is different from the production build process. In React and front-end development, there is an embedded web server to serve the application content and JavaScript code, which helps during the development process. However, in the final build version of the code used in the production environment, there will be no embedded server. Therefore, it is necessary to provide a web server (e.g., Nginx).