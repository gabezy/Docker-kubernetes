# Set the base image and version to go to the image (by default search in the Docker hub)
FROM node:14 

# Tells Docker that all the subsequent commands will be executed in the /app folder
WORKDIR /app

COPY package.json /app

# Execute a specific command (exceute when the image is installed)
RUN npm install

# Tells which files will be copied to the image 
# the `. .` tells to copy all files, folders and subfolders from the base directory (excluding the Dockerfile) and paste to the /app folder
COPY . /app

# Expose the container's 80 port to the local system
EXPOSE 80

# This line will be executed only when the container is started
CMD [ "node", "server.js" ]