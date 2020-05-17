#!/bin/sh

# Author : Ilamah, Osho
# This Bash shell script performs the following tasks:
# a) @compile: Production NPM build of frontend (Vue.JS) and merge of results with spring boot backend java maven resources (in resources/public folder)
# a) @serve:   Development npm run serve of frontend. The frontend host port is 3500 (as configured in frontend/package.json - "serve": "vue-cli-service serve --port 3500"),

if [ $# -eq 0 ]
then
    echo "No arguments supplied"
    exit 1
elif [ "$1" == "compile" ]
then
	echo "Compiling MX4.0 via Maven"
	# the '-Dmaven.exec.skip=false' works with the property in the POM file to activate the exec-maven-plugin
    mvn -Dmaven.exec.skip=false install
elif [ "$1" == "serve" ]
then
	echo "Serving Hot Reload with Vue CLI service"
	npm run serve --prefix frontend
fi


