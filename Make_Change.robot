*** Settings ***
Library  Process

*** Test Cases ***
CREATE CHANGE AND PUSH CHANGES TO PR
    LOG  ./make_fakechange.sh
    Run Process  ./make_fakechange.sh