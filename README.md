# Gamesight
Gamesight is a personal project which will host microservices supporting online games. Technologies used will include: a Spring-based application, REST services, Hibernate, PostgreSQL and Docker-based microservices deployed on an AWS cluster.

# To Download and Run this Application
This maven based Spring-boot appplication can be downloaded, started and tested in minutes.
1) Using your IDE, create a new project from a repository URL (https://github.com/pbg57/Gamesight);
2) From your project home directory (containing pom.xml), run 'mvn spring-boot:run'
3) Run tests from your maven tool window or by using 'mvn surefire:test' and 'mvn surefire-report:report'

# Project Goals 
I am developing this project to demonstrate my technical proficiency in a number of areas (and also because I love doing it!). My approach is to first develop an end-to-end 'steel thread' of execution across the entire application, and then iterate over the project to add additional functionality. This approach has worked well for me as it allows lessons-learned to be immediately and efficiently applied during each development iteration. 

# Available Functionality Update
To make it easier to review work-to-date, here are pointers:

Nov. 19th 2022
* Data model: Game, Player, Profile, User. Hibernate/JPA Entities to demo 1:1, 1:N, N:N relationships.
* Data persistence/Testing: In-memory H2 or PostgreSql integration. See JUnit UserAndProfileCrudTests and RepositoryPagingAndSortingTests.
* Restful controllers/Testing: My first iteration focuses on the Profile entity, only. See the Spring ProfileController and ProfileControllerTest.
