#!/bin/bash
set -e

# just test PRs
if test "${TRAVIS_PULL_REQUEST}" == "false"
then
    echo "Skipping tests this time"
else
    xcodebuild clean build-for-testing \
        -IDEBuildOperationMaxNumberOfConcurrentCompileTasks=2 \
        -workspace Enterprise.xcworkspace \
        -scheme Enterprise \
        -sdk iphonesimulator \
        -destination 'platform=iOS Simulator,id='"${SIMULATOR_UDID}" \
        | xcpretty -c

echo "Executing tests..."

    xcodebuild test-without-building \
        -IDEBuildOperationMaxNumberOfConcurrentCompileTasks=2 \
        -workspace Enterprise.xcworkspace \
        -scheme Enterprise \
        -sdk iphonesimulator \
        -destination 'platform=iOS Simulator,id='"${SIMULATOR_UDID}" \
        | xcpretty -c && exit ${PIPESTATUS[0]}
fi