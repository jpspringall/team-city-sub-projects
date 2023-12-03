#!/bin/bash
set -e # Set to fail on error

echo "Running End To End Test Script"

echo $@

while getopts :i:s:u:p:c:r:n: flag
do
    echo "Processing flag ${flag}"
    case "${flag}" in
        i) isCI="${OPTARG}";;
        s) server="${OPTARG}";; 
        u) user="${OPTARG}";; 
        p) password="${OPTARG}";; 
        c) buildCounter="${OPTARG}";;
        r) pullRequestNumber="${OPTARG}";;
        n) buildNumber="${OPTARG}";;
        \?) echo "Invalid option: -$OPTARG"
            exit 1 
    esac
done

mkdir -p .batect/sqlvolume
sudo chown 10001:0 .batect/sqlvolume
prNumber="NOT_SET"
if [ -n "$pullRequestNumber" ]; then
    prNumber=$pullRequestNumber
fi

echo "isCI $isCI";
echo "Server $server";
echo "User $user";
echo "Password $password"
echo "BuildCounter $buildCounter"
echo "PullRequestNumber $pullRequestNumber"
echo "BuildNumber $buildNumber"
echo "PRNumber $prNumber"

#sudo apt update
#sudo apt install make

#make

echo "Setting Batect Env "

export BATECT_ENABLE_TELEMETRY=false

echo "Setting Batect Execute Wiyhout script "

chmod +x ./batect

echo "Running Batect"

./batect \
--config-var TC_SONAR_QUBE_USE=1 \
--config-var TC_SONAR_QUBE_SERVER=$server \
--config-var TC_SONAR_QUBE_USER=$user \
--config-var TC_SONAR_QUBE_PASSWORD=$password \
--config-var TC_SONAR_QUBE_VERSION=$buildCounter \
--config-var TC_SONAR_QUBE_NUMBER=$prNumber \
run-tests