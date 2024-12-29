## Type of Data Storage

- Volumes
	- Managed by Docker
- Bind Mounts
	- Managed by the user

## [Volumes](https://docs.docker.com/engine/storage/volumes/)

Volumes are a storage mechanism that provide the ability to persist data beyond the life cycle of an individual container. basically, **folders on the host machine** hard drive which are mounted (mapped) into containers. 
Docker handles volumes with two types:
- Anonymous Volumes
	- Create **automatically** and **specific** for a single container
	- Random name
	- **Removed automatically** when container is removed (because of `--rm` flag)
	- Best for temporary storage that doesn't need to persist
	- Used when `docker run` the container with `-v <mount-path>` flag
```bash
docker run -d -p 80:80 --name feedback -v /app/feedback feedback-node:volumes
```
- Named Volumes
	- Explicitly created with specify name (CLI or docker-compose.yaml)
	- Persist after container removal unless explicitly deleted
	- Used when `docker run` the container with `-v <volume-name>:<mount-path>` flag
```bash
docker run -d -p 80:80 --name feedback -v feedback:/app/feedback feedback-node:volumes
```
### Commands
- List all Volumes
```bash
docker volume ls
```
- Remove Volume
```bash
docker volume rm feedback
```
- Remove unused Volumes
```bash
docker volume prune
```

## [Bind Mounts](https://docs.docker.com/engine/storage/bind-mounts/)

A Bind mount is a file or directory mounted on the host machine into the container and managed by the host machine.

- Can be stored anywhere on the host system
- Host path must exist before mounting
- Less secure as they expose host filesystem structure
- Performance is better on Linux but slower on Windows/macOS
- Limited functionality compared to volumes
- Useful during development for hot reloading

```bash
docker run -d -p 80:80 --name feedback -v feedback:/app/feedback -v $(pwd):/app feedback-node:latest
```

To ensure that the bind mount will be read-only (in the container perspective), it is used the `:ro` flag after the mount-path
```
docker run -d -p 80:80 --name feedback -v feedback:/app/feedback -v $(pwd):/app:ro feedback-node:latest
```


## ARGments & ENVironment variables

![[args-and_env.png]]
#### ENV
A environment variable is declare in the dockerfile with `ENV <key>=<value` instruction and can be passed in the `docker run` with the `-e <key>=<value>` flag to **overwrite** the environment variable's default value. To use a **environment file**, the `--env-file path` is needed. The environment variables **can directly influence the execution of your build**, and the behavior or configuration of the application.

```Dockerfile
FROM node:14-slim

WORKDIR /app

COPY ./package.json .
 
RUN npm install

COPY . .

ENV PORT=80

EXPOSE ${PORT}

CMD [ "npm", "start" ]
```

```bash
docker run -p 3000:8000 -e PORT=8000 -d node-server
```
#### ARG
Only available inside Dockerfile, not influencing the execution of the build, and only can be used in instruction that don´t execute in runtime (like `CMD` or `ENTRYPOINT`). The arguments can be **overwrite** on the build stage with `--build-arg <key>=<value>`, like example below

```Dockerfile
ARG NODE_VERSION="14-slim"

FROM node:${NODE_VERSION}

WORKDIR /app

COPY ./package.json .

RUN npm install

COPY . .

ARG DEFAULT_PORT=80

EXPOSE ${DEFAULT_PORT}

CMD [ "npm", "start" ]
```

```bash
docker built -t node-server:latest --build-arg NODE_VERSION="14" --build-arg DEFAULT_PORT=8000 .
```

## Networking

### Container to WWW Communication

By default, container can send request to the WWW, like a API `https://swapi.py4e.com/api/`, no needed further configuration. 
### Container to Local Host Machine Communication
To be able the container communicates to application like a Database or REST API running in the local machine, **in the code** instead of references to a like `postgresql://localhost:5432/` uses `postgresql://host.docker.internal:5432/`. The `host.docker.internal` URL gives Docker a hint that will use a port in the local machine
### Container to Container Communication
- Inconvenient way
To communicate to other container like a MongoDB container, you can inspect the container with `docker container inspect <container-name>` to get the IP Address and Port than changes the code to that address, like `mongodb://172.17.0.2:27017/swfavorites`.
- Container Networks
 The `docker run --network my_network` set the container to use that network. **Within a Docker network**, all containers **can communicates with each other** and IPs are automatically resolved. *unlikely volumes, Docker doesn't create a network with `--network` flag, to create a network use `docker network create <network-name>`*.  After set all the container to the same network, to communicates to each other, just use the container name like `mongodb://mongo_container:27017/swfavorites`
 
![[network.png]]

### Drives

Docker Networks actually support different kinds of "**Drivers**" which influence the behavior of the Network.

The default driver is the "**bridge**" driver - it provides the behavior shown in this module (i.e. Containers can find each other by name if they are in the same Network).

The driver can be set when a Network is created, simply by adding the `--driver` option.
```bash
docker network create --driver bridge my-net
```

_Of course, if you want to use the "bridge" driver, you can simply omit the entire option since "bridge" is the default anyways._

Docker also supports these alternative drivers - though you will use the "bridge" driver in most cases:

- **host**: For standalone containers, isolation between container and host system is removed (i.e. they share localhost as a network)
- **overlay**: Multiple Docker daemons (i.e. Docker running on different machines) are able to connect with each other. Only works in "Swarm" mode which is a dated / almost deprecated way of connecting multiple containers
- **macvlan**: You can set a custom MAC address to a container - this address can then be used for communication with that container
- **none**: All networking is disabled.
- **Third-party plugins**: You can install third-party plugins which then may add all kinds of behaviors and functionalities
    
As mentioned, the "**bridge**" driver makes most sense in the vast majority of scenarios.

```bash
--mongo
docker run -d --name mongo --network goals-net --rm -v mongodb:/data/db -e MONGO_INITDB_ROOT_USERNAME=admin -e MONGO_INITDB_ROOT_PASSWORD=secret mongo

-- back
docker run -d --name goal-back --rm --network goals-net -p 3001:3001 -v $(pwd):/app:ro -v /app/node_modules -v logs:/app/logs goals-node

-- front
docker run --name goal-front --rm -p 3000:3000 -v $(pwd):/app -v /app/node_modules -d react-goals
```