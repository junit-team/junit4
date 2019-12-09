*** Settings ***
Library  Process

*** Keywords ***
CREATE CHANGE AND PUSH CHANGES TO PR
    Run Process  ./make_fakechange.sh