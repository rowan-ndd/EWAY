#!/bin/sh                                                                       

# usage: enter 4 directories                                                    
# the first directory is where the reports are stored                           
# the second directory is where you want the json files stored                  
# the third directory is where you want the predictions stored                  
# the fourth directory is where the annotations are stored                      


# converts the files to json
javac JSONConverter.java
java JSONConverter $1 $2
# predicts the laterality using a rule based system
javac LateralityPredictor.java
java LateralityPredictor $2 $3
# evaluates the rule based laterality predictions
javac Evaluation.java
java Evaluation $3 $4

