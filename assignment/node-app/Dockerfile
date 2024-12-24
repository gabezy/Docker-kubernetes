# Pull the base image to run node 
FROM node:14-slim

# Set a workdir
WORKDIR /app

# Copy dependencies files (pom.xml, package.json)
COPY ./package.json /app

# run command to install dependencies
RUN npm install

COPY . /app

# Expose server port
EXPOSE 3000

CMD [ "node", "server.js" ]