FROM node:14-alpine

WORKDIR /app

COPY package.json .

RUN npm install

ENV TASKS_FOLDER=tasks

RUN mkdir /app/${TASKS_FOLDER}

COPY . .

EXPOSE 8000

CMD [ "node", "tasks-app.js" ]