FROM node:18-alpine

WORKDIR /app

COPY ./package.json .

RUN npm install

COPY . .

ENV PORT=3001

EXPOSE ${PORT}

ENV MONGODB_USERNAME=gabezy

ENV MONGODB_PASSWORD=secret

CMD [ "npm", "start" ]