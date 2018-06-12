case "${XCODE_CONFIGURATION}" in

    "UAT" | "Debug")
        if test "${TRAVIS_PULL_REQUEST}" == "false" 
        then
            APP_ID="${CRITTERCISM_APP_ID}"
            API_KEY="${CRITTERCISM_API_KEY}"
            REQUIRE_UPLOAD_SUCCESS=0
            DSYM_FOLDER="Build/Products/${XCODE_CONFIGURATION}-iphoneos"
            APP_FILE="${DSYM_FOLDER}/${APP_NAME}.app"
            DSYM_FILE="${APP_FILE}.dSYM"
            INFOPLIST_FILE="${APP_NAME}/Info.plist"
            
            source scripts/dsym_upload.sh -v "$DSYM_FILE"
        fi
    ;;
    
    *)
    ;;
esac