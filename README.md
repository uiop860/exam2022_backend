# Documentation of backend code:

## Setup
1. Change <remote.server>{*server url*}</remote.server> in [pom.xml](pom.xml) with the correct server url
2. Change databases in [persistence.xlm](src/main/resources/META-INF/persistence.xml) to match your databases. Also change line 41 in [mavenworkflow.yml](.github/workflows/mavenworkflow.yml) to match the test database set in persistence.xml
3. Add sercrets in your github repository for *REMOTE_USER* and *REMOTE_PW*


## How to use
- Remember to only create EntityManagerFactories with the [EMF_Creator.java](src/main/java/utils/EMF_Creator.java)