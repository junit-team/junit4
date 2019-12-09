*** Settings ***
Library  Process

*** Test Cases ***
CREATE CHANGE AND PUSH CHANGES TO PR
    Run Process  ./make_fakechange.sh