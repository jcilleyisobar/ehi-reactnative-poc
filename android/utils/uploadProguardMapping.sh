#!/bin/sh
. "$(cd "$(dirname "$0")" && pwd)/setup-env.sh"

#using Production App Key for crittercism
CrittercismAppID="52fb9401558d6a6ad3000006"
CrittercismAppKey="vuwwmql3zkdi5yzdaaonv1ebply7cs4i"

appVersion=$(python "$SCRIPT_DIR/getAppVersion.py" "$REPO_ROOT/app/build.gradle")

curl "https://app.crittercism.com/api_beta/proguard/$CrittercismAppID" -F proguard=@"$WORKSPACE/app/build/outputs/mapping/prod/release/mapping.txt" -F app_version="$(python "$SCRIPT_DIR/getAppVersion.py" "$REPO_ROOT/app/build.gradle")" -F key="$CrittercismAppKey"

