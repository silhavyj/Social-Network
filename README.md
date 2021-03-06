# KIV/PIA - Social Network Project 2021

This is a brief description of how I tackled this assignment. It walks you through how to compile the whole project and how to get it up and running. The entire description of the assignment can be found over at https://github.com/osvetlik/pia2020/tree/master/semester-project.

## Screenshots of the application

![Screenshot](doc/01.svg)
![Screenshot](doc/03.svg)
![Screenshot](doc/02.svg)
## How to get it up and running?

There are two dependencies you need to have installed on your system - `Maven` for compiling the java application and `Docker` for being able to spin up all the containers required for a smooth run of the whole project.

### Compilation of the Java application

Once you have installed all the dependencies, you can navigate to the root folder of the project structure and execute the following command in the terminal:

```
mvn clean package
```

This will create a `jar` file (package) of the `Java` application. The `jar` file itself should be located in the `target` folder which will be created upon successful termination of the command.

### Starting docker containers

Once the `jar` file has been created, all you're required to do is to execute the following command in the root folder of the project structure:

```
docker-compose up
```

Assuming you don't have any docker images previously downloaded, it will go ahead and pull down 3 docker images needed for starting the appropriate containers. All the images are defined in the `docker-compose.yml` file located in the root folder of the project, but basically, these are

- `postgres` my database of choice
- `mail-dev` an e-mail development server used for sending/receiving e-mails
- `openjdk11` so we can run the `jar` file

These three servers are sitting on the same subnet which was created within `Docker`. However, I needed to expose a few ports to the local machine, so we can interact with the application effortlessly using a web browser.

### Accessing the Java Spring application as well as the mail server

#### Testing the application on localhost

The web application itself is running over at http://127.0.0.1:8085 and the mail server can be found sitting at http://127.0.0.1:8086. The database server has its default port `5432` exposed to the local machine as well. So, it can be accessed using a database client such as `DBeaver`, for instance. The credentials can be found in `docker-compose.yml`

Every time you're asked to check your e-mail, all you have to do is to navigate to the address mentioned above. Since a mock e-mail server was used, it doesn't really matter what e-mail address you decide to use as the e-mails won't be sent anywhere but to the local e-mail server.

#### Testing the application among multiple computers on the same network

If you were to test the application using multiple computers sitting on the same network, the following modifications would need to be made:

- in `application.yml`, the url would need to be changed from `127.0.0.1` to the IP address of the server. In my case, it was `10.10.2.103`.
  ```
    application:
      url:  http://10.10.2.103:8085
  ```

- in `resources/static/js/chat.js`, the URL defined at the very top of the file should be changed from `127.0.0.1` to the server IP address. In my case, it's again `10.10.2.103`.
  ```
    const URL = "http://10.10.2.103:8085";
  ```

After both changes have been made, you need to re-compile and re-run the application by taking the steps which are described further below. Also, you might need to temporarily change your firewall rules, so you can access the required ports on the server.

### Making changes within the Java Spring application

If you want to make any changes within the `Java` application, you should first stop all the running containers by executing the following command:

```
docker-compose down
```

Be aware of the fact that all data in the database will be erased after executing this command (`application.yml`).

Also, the previous docker image of the Java Spring application should be deleted. This can be achieved using the following command.

```
docker image rmi social-network_app
```

Once all desired changes have been made, you can get the whole project up and running using the same steps as described above. 

## Default user accounts

Upon the first start of the application, all the default accounts are logged into the console, so you can use any of them to log in. For convenience, I also add the list of the users with their credentials here:

| Username | Password | Roles |
| :---: | :---: | :---: |
| user1@silhavyj.pia.kiv.zcu.cz | User1-123*  | User
| user2@silhavyj.pia.kiv.zcu.cz | User2-123*  | User
| user3@silhavyj.pia.kiv.zcu.cz | User3-123*  | User
| admin@silhavyj.pia.kiv.zcu.cz | Admin123*  | User, Admin

## Bonus features implemented in the project

Out of all the bonus feature I decided to pull off the following ones:

- entropy based password strength evaluation
- password reset using an e-mail (reset link)
  - The default amount of time the user has to reset their password was set to 5 minutes (see `application.yml` section `application`)
- instant check of the e-mail availability (not being used by an already registered user) on the registration screen (REST)
- likes
- Use your Git repo properly and regularly - your activity there should give me a clear idea about your progress (https://github.com/silhavyj/Social-Network)


## Features added of my own

- I decided to further extend the overall functionality of the project by adding a profile page that allows users to update their personal information as well as to change their default profile image. The format of the image is supposed to be either `.png`, `.jpg`, or `.jpeg` not exceeding the size of 1 MB.
- Also, when a user signs up, they're required to confirm their registration via e-mail. All e-mails are HTML e-mails.
- When signing up, the user is required to be at least 15 y.o.