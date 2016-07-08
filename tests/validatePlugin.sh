#!/bin/bash

# Define color for after.
COLOR_NORMAL="\\033[0;39m";
COLOR_GREEN="\\033[1;32m";
COLOR_YELLOW="\\033[1;33m";
COLOR_RED="\\033[1;31m";

# Get the platform to test and check if it's existed.
PLATFORM="android";
if [[ $PLATFORM != "android" && $PLATFORM != "ios" ]];
then
    echo -e $COLOR_RED"$PLATFORM is an unknown platform."$COLOR_NORMAL;
    exit 1;
fi;

# Create the temporary directory.
BASEDIR=$(dirname $0);
cd $BASEDIR;
PATHPLUGIN=$(pwd)/../;
PATH_DIR_TEST=$PATHPLUGIN".tmp_tests";
mkdir $PATH_DIR_TEST &> /dev/null;

# Install plugman.
cd $PATH_DIR_TEST;
echo -e $COLOR_YELLOW"Install plugman"$COLOR_NORMAL;
npm install plugman;

# Create test application.
echo -e $COLOR_YELLOW"Create a test application"$COLOR_NORMAL;
cordova create validCustomCamera org.geneanet.customcamera.valid validCustomCamera &> /dev/null;
cd validCustomCamera;
cordova platform add $PLATFORM &> /dev/null;
cd $PATHPLUGIN/;

# Generate project path for the command plugman.
if [[ $PLATFORM == "android" ]];
then
    PATH_PROJECT_TEST=$PATH_DIR_TEST"/validCustomCamera/platforms/android";
elif [[ $PLATFORM == "ios" ]];
then
    PATH_PROJECT_TEST=$PATH_DIR_TEST"/validCustomCamera/platforms/ios";
fi;

# Run plugman and test the success.
echo -e $COLOR_YELLOW"Run plugman"$COLOR_NORMAL;
plugman install --platform $PLATFORM --project $PATH_PROJECT_TEST --plugin ./;
RETURN_PLUGMAN=$?;
if [[ $RETURN_PLUGMAN == 0 ]];
then
    echo -e $COLOR_GREEN"The plugin is validated."$COLOR_NORMAL;
else
    echo -e $COLOR_RED"The plugin isn't validated."$COLOR_NORMAL;
fi

# Delete the temporary directory.
echo -e $COLOR_YELLOW"Delete tempory directory"$COLOR_NORMAL;
rm -rf $PATH_DIR_TEST;