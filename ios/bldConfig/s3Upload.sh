#!/bin/bash

# Upload list of files to S3.

Usage() {
  echo "$0 <bucket> <file> [file...]"
  echo " ex. $0 myBucket/myDir foo.*"
  exit 1
}

INSTALL=$1; shift
LOG_FILE=$WORKSPACE/upload.log

rm -f $LOG_FILE

# 365 days 
let "EXPIRE_TIME=365 * 24 * 60 * 60"

if [[ $# -lt 2 ]]; then
  Usage
fi

BUCKET=http://s3.amazonaws.com/$1; shift

echo "Uploading to $BUCKET"

while [[ "$1" != "" ]]; do
  FILE=$1; shift
  FILE_NAME=`basename $FILE`
  python $INSTALL/s3util3.py put $BUCKET/$FILE_NAME $FILE url_expires_seconds=$EXPIRE_TIME | tee -a $LOG_FILE
  EXIT_CODE=$?
  if [[ $? -ne 0 ]]; then
    exit $EXIT_CODE
  fi
done

LINKS=$WORKSPACE/links.txt
grep "^https" $LOG_FILE > $LINKS
grep -F ".html" $LINKS > $WORKSPACE/link.txt

exit 0
