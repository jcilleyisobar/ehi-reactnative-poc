#!/bin/sh
. "$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/setup-env.sh"
#Jenkins -push job
cd "$1/upload_me"

echo "Cleaning up temp files (if any)"
#rm "$TEMP_DIR/temp_log.log"
rm -r "$TEMP_DIR"
mkdir "$TEMP_DIR"
rm $LINKS_HTML
rm $LINKS
echo "uploading..."
for file in *
do
	echo pushing: ${UPLOAD_URL_BASE}/$file
	#echo --Running: $UPLOAD_UTIL ${UPLOAD_URL_BASE}/$file $file
	$UPLOAD_UTIL ${UPLOAD_URL_BASE}/$file $file | tee $TEMP_DIR/temp_log.log
	RETVAL=$?
	cat $TEMP_DIR/temp_log.log >> $UPLOAD_LOG
	LINK_URL=$(cat $TEMP_DIR/temp_log.log | $UPLOAD_EXTRACT_LINKS)
	echo "<li><a href=\"$LINK_URL\">$1</a></li>" >> $LINKS_HTML
	echo $file - $(grep "^https" $TEMP_DIR/temp_log.log) >> $LINKS

	if [ $RETVAL -ne 0 ]; then
		echo "Failed To Push $1 build"
		exit $RETVAL
	fi
done

echo "Done!"