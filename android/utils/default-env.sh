#!/bin/bash

#env Variables for Unix/Jenkins Box
#main Paths
export ANT_HOME=/opt/app/apache-ant-1.8.2
export JAVA_HOME=/usr/lib/jvm/java-1.6.0-openjdk-1.6.0.0.x86_64

export ANDROID_HOME=/opt/app/android/android-sdk-linux
export ANDROID_SDK=$ANDROID_HOME
export ANT_RELEASE_PROPERTIES_FILE=/opt/app/scripts/ant.properties
#Script used to upload to S3, Non-Zero exit code indicates Upload error
export UPLOAD_UTIL=$SCRIPT_DIR/s3-upload.sh
#Script to take stdout and return the link to downlaod the file
extract_s3_upload_links() {
	grep "^https"
}
export UPLOAD_EXTRACT_LINKS=extract_s3_upload_links

export TEMP_DIR="$REPO_ROOT/temp"
export UPLOAD_DIR="$REPO_ROOT/files_to_upload"

#Base Directory that will be used to uplaod the file
export UPLOAD_URL_BASE=s3.amazonaws.com/ehi-enterprise-mobile-client-app/android/${BUILD_ID}
export UPLOAD_LOG=$WORKSPACE/upload.log
export LINKS=$WORKSPACE/links.txt
export LINKS_HTML=$WORKSPACE/links.html
