#!/bin/bash

RELEASE_NOTES=$(git log --no-merges ${TRAVIS_COMMIT_RANGE} --pretty="tformat:**%h** (*%an*) %n* %s%n")
APP_PATH="app/build/outputs/apk/${APP_FILE}"

if test "${RELEASE_NOTES}" == ""
then
	RELEASE_NOTES="New Build"
fi

# Sanity check that we should be publishing
if test "${TRAVIS_PULL_REQUEST}" == "false"
then
	curl -v \
	  -F "status=2" \
	  -F "notify=2" \
	  -F "notes=${RELEASE_NOTES}" \
	  -F "notes_type=1" \
	  -F "ipa=@${APP_PATH}" \
	  -H "X-HockeyAppToken: ${HOCKEY_API_KEY}" \
	  https://rink.hockeyapp.net/api/2/apps/upload
fi
