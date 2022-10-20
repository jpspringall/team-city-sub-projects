#!/bin/bash
set -e

echo "Running Script"

while getopts :s:u:p:n: flag
do
    case "${flag}" in
        s) server=${OPTARG};; 
        u) user=${OPTARG};; 
        p) password=${OPTARG};; 
        n) number=${OPTARG};;
        \?) echo "Invalid option: -$OPTARG" >&2;; 
    esac
done

echo "Server $server";
echo "User $user";
echo "Password $password"
echo "Number $number"

