#!/bin/bash
set -e

echo "Running Script"

echo "Running Script From Branch"

echo $@

echo "After Param test"

echo %sonar.pullrequest.key%

echo %teamcity.pullRequest.number%

echo "EXtra Vars"

