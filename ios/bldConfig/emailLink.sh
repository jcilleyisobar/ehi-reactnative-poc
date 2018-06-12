#!/bin/bash

# Send email link to list of recipents

if [[ $# -eq 0 ]]; then
  cat <<HEREDOC
Usage: $0 email_adress [email address..]
HEREDOC
exit 1
fi

LINKS=$WORKSPACE/links.txt

# get the list of ipa names (filenames only, not the full path)
IPA_ARRAY=(`ls ${WORKSPACE}/output/*.ipa | xargs -n 1 basename`)

COUNT=0
# loop through all the ipa files
for ipa_file_name in ${IPA_ARRAY[*]}; do

	# find the URL for the given filename
    IPA_URL=(`grep -F "${ipa_file_name}" $LINKS |head -1| sed "s/&amp;/&/g"`)
    # add a list item and anchor tag for the URL
    EMAIL_LIST+="<li><a href=\"${IPA_URL}\">${ipa_file_name}</a></li>"
    EMAIL_LIST+=$'\n'
    let COUNT++;

done

cat <<EMAILDOC > $WORKSPACE/email.html
<!DOCTYPE html>
<html>
<body>
<p>
The build has completed successfully.  It can be accessed at <a href="`cat $WORKSPACE/link.txt`">Version Link</a>
</p>
<p>
Direct Links:
<ul>
$EMAIL_LIST
</ul>
</p>
</body>
</html>
EMAILDOC


sendmail -t <<HEREDOC
To: $*
Subject: Build completed for $JOB_NAME
Content-Type: text/html
`cat $WORKSPACE/email.html`
HEREDOC
