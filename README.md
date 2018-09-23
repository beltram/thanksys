# thanksys test by Beltram
To run tests ```./gradlew clean test```  
To run app ```./gradlew clean build```  
# Tools
* SpringBoot 2.0.5
* Kotlin
* Mongo
* Reactor
* Junit5
# Issues
* Don't know if updating an already existing rank leads to other rank downgrading or failure. Done with failure
* Accordingly, sample scenario leads to failure ```documentService.add("newDoc", 2)``` must fail as there's already a document with rank 2.
# Improvements
* Expose service as a resource (REST endpoints)
* Add resource tests
* Add Spring Cloud Contract


