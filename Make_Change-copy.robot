*** Settings ***
Library  Process
Resource  ./Make_Change.robot

*** Test Cases ***
Create Change
    Make_Change.CREATE CHANGE AND PUSH CHANGES TO PR
