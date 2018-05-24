#!/bin/sh


#S3 Uplaod Script

##########################
# Help
##########################
Help() {
  cat <<HEREDOC
Upload a file to S3
Usage: $0 [path/fileName on server] [local Filename/path]
	ex. $0 http://SomeSite.com/bin/android build233/some-project-v1_1_1_200-release.apk ./opt/release/release-aligned.apk

HEREDOC
}
echo "--upload ran with: $*"
#60 Days
let "EXPIRE_TIME=365 * 24 * 60 * 60"
export S3UTIL=/var/jenkinsslave/s3util3.py
if [  $# -ne 2 ]; then
  Help
  exit 1
fi
#in case a dev forgot to setup the env.sh, we dont want randon uploads
if [ -e ${S3UTIL} ]; then
	python ${S3UTIL} put $1 $2 url_expires_seconds=$EXPIRE_TIME
else
	echo "Skipping Upload of: $2 , there is no ${S3UTIL} to execute."
fi
