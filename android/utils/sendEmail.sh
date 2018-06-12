#!/bin/sh
python $WORKSPACE/utils/formatEmail.py $WORKSPACE/build_info.txt $WORKSPACE/links.txt $WORKSPACE/jira_issue_list.txt > $WORKSPACE/emailWithLinks.txt

sendmail -t <<HEREDOC
`cat $WORKSPACE/emailWithLinks.txt`
HEREDOC
