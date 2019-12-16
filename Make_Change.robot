*** Settings ***
Library  Process

*** Test Cases ***
CREATE CHANGE AND PUSH CHANGES TO PR
    LOG  Run and create change
    Run Process  ./make_fakechange.sh