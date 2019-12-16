*** Settings ***
Library  Process

*** Test Cases ***
CREATE CHANGE AND PUSH CHANGES TO PR
    LOG  Run and create change
    Run Process  ./make_fakechange.sh
    # Run Process  git checkout test-automation 
    # Run Process  git pull --ff-only 
    # Run Process  sed -i "s/applyValidators.errors.;/applyValidators(errors);\/\/FAKE/g" ./src/main/java/org/junit/runners/ParentRunner.java
    # Run Process  git add . 
    # Run Process  git commit -m "Automation" 
    # Run Process  git push -f origin test-automation