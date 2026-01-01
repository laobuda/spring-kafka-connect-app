# Kafka datapipeline with confluent
> A Spring boot application that consumes the Confluent connector service API to create JDBC Source/Sink Connectors

## Table of contents
* [General info](#General_Info)
* [Technologies](#Technologies)
* [Setup](#Setup)
* [Useful Docker Commands](#Useful_Docker_Commands)
* [Status](#Status)
* [Contact](#Contact)

## General_info
This is a POC to show the possibility ocf creating the JDBC connectors from a web UI if you are using the open source confluent platform. 
The objective of this POC has been to create JDBC connectors from a web UI instead of starting
multiple terminal windows to do the job.There two ways to run the application either installing the confluent open source platform locally 
or running the platform through the use of docker as explained in the setup sections bellow.
   

## Technologies
* Spring Boot framework
* Thymeleaf template engine
* Confluent open source 5.5.1
* Docker

## Setup 
* Clone this repository
* Install a docker image of the confluent platform by following the link https://docs.confluent.io/current/quickstart/cos-docker-quickstart.html
* You have to start the docker image from a terminal more on useful commands in the next section ## Useful Docker Commands
* Run the application by issuing the mvn spring-boot:run command while in the main directory of the project.
* Start a web browser and type in http://localhost:9001 nad hit enter.

## Useful_Docker_Commands
* After installing the docker image by running "sudo docker-compose up -d"
* Make sure that all the service are up and running, and none of them is still starting by issuing the command "sudo docker-compose ps"
* Find out the container Id of the connector service by running the command "sudo docker ps "
* Start a shell command into that container by issuing "sudo docker exec -it container_id_from_above /bin/bash"
* Copy the JDBC jar files for the databases you'll be using into the right directory by issuing the following command from a terminal within 
  your local machine "sudo docker cp The_Path_to_your_jars/mysql-connector-java-5.1.46.jar  container_id_from_above:/usr/share/java/kafka-connect-jdbc".
* Restart the docker image by running "sudo docker-compose restart"

## Status
Project is: POC, this is by no mean a production ready application but just a way to show how to use a UI instead of terminals to create
            JDBC connectors.


## Contact
Created by Samman Jamal laobuda@gmail.com - feel free to contact me!
