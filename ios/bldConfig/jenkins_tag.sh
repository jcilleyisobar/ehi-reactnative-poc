#!/bin/sh

versionName=$(/usr/libexec/PlistBuddy -c "print :CFBundleShortVersionString" "$WORKSPACE/Enterprise/Info.plist")

TAGNAME=v$versionName.$BUILD_NUMBER

echo Tagging Build with $TAGNAME
git tag $TAGNAME
git push origin $TAGNAME
