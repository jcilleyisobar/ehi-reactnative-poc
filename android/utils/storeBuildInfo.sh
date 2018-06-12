#!/bin/bash
# Send email link to list of recipents

if [[ $# -lt 2 ]]; then
  cat <<HEREDOC
Usage: $0 [repo-root] [buildNumber] email_adress [email address..]
HEREDOC
exit 1
fi
WORKSPACE=$1
BUILD_NUMBER=$2
shift 2

# Copied from utils/jenkins-tag
echo init Props
#this will do for now, since it might not be the previous # value wise

# get the list of ipa names (filenames only, not the full path)
#JIRA_LINKS=$(git log --oneline $PREVTAGNAME..$TAGNAME | grep "/NMA-" | sed 's|[^/]*/\(NMA-[0-9]*\)[_\-]*\([^\n\r]*\)|<li><a href="https://tasks-ehi.ctmsp.com/browse/\1">https://tasks-ehi.ctmsp.com/browse/\1</a></li>|g')

git log --oneline $PREVTAGNAME..$TAGNAME | grep "/NMA-" > $WORKSPACE/jira_issue_list.txt
cat "$WORKSPACE/National Mobile/version_info.properties" > $WORKSPACE/build_info.txt
cat <<HEREDOC >> $WORKSPACE/build_info.txt

buildNumber=$BUILD_NUMBER
jobName=$JOB_NAME
sendTo=$*
HEREDOC


#sendmail -t "$(cat $WORKSPACE/mail.txt)"
