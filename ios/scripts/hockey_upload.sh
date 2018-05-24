#!/bin/bash

RELEASE_NOTES=$(git log --no-merges ${TRAVIS_COMMIT_RANGE} --pretty="tformat:**%h** (*%an*) %n* %s%n")
APP_FILE="${APP_NAME}.app"
APP_IPA="${APP_NAME}.ipa"

if test "${RELEASE_NOTES}" == ""
then
	RELEASE_NOTES="New Build"
fi

# Sanity check that we should be publishing
if test "${TRAVIS_PULL_REQUEST}" == "false"
then
	echo "Compressing archive..."
	cd Build/Products/${XCODE_CONFIGURATION}-iphoneos

	# clean up any old artifacts
	rm -rf Payload
	rm -f *.ipa
	
	mkdir Payload
	cp -a ${APP_FILE} Payload/.
	zip -r ${APP_IPA} Payload
	rm -rf Payload

	echo "Uploading to HockeyApp"
	curl -v \
	  -F "status=2" \
	  -F "notify=2" \
	  -F "notes=${RELEASE_NOTES}" \
	  -F "notes_type=1" \
	  -F "ipa=@${APP_IPA}" \
	  -H "X-HockeyAppToken: ${HOCKEY_API_KEY}" \
	  https://rink.hockeyapp.net/api/2/apps/upload
fi

echo "Done"