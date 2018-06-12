import sys
import re
import string

#Usage: pyhton formatEmail.py [version_info.txt file] [links.txt file] [jira_issues.txt file]
#Send result to std-out
f_version_info = open(sys.argv[1], "r");
f_download_links = open(sys.argv[2], "r");
f_jira_issues = open(sys.argv[3], "r");

version_info = {}

for v in f_version_info:
	vv = re.split("=", v)
	if(len(vv) >= 2): 
		version_info[vv[0].strip("\n\r ")] = vv[1].strip("\n\r ")

uploadFileLinkTemplate = """<li><a href="{url}">{fileName}</a> - {url} </li>"""
downloadLinkTemplate = """<li>{fileName} - <a href="{url}">{url}</a></li>"""
jiraLinkTemplate = """<li><a href="https://tasks-ehi.ctmsp.com/browse/NMA-{jiraIssueNumber}">https://tasks-ehi.ctmsp.com/browse/NMA-{jiraIssueNumber}</a></li>"""
downloadLinks = ""
uploadedFiles = ""
for lk in f_download_links:
	p = re.split(" - ", lk)
	uploadedFiles = uploadedFiles + uploadFileLinkTemplate.format(url=p[1].strip("\n\r "), fileName=p[0].strip("\n\r "))
	if(string.find(p[0],".apk") != -1):
		downloadLinks = downloadLinks + downloadLinkTemplate.format(url=p[1].strip("\n\r "), fileName=p[0].strip("\n\r "))

jiraIssueLinks = ""
for lk in f_jira_issues:
	#f03677b Merge pull request #2481 from ehi-dev/NMA-5300_entypo_font_base_code
	p = re.match(r"([A-Fa-f0-9]+) Merge pull request #([0-9]+) from ehi-dev/NMA-([0-9]+)_([^ \n\r]*)", lk)
	if p != None:
		jiraIssueLinks = jiraIssueLinks + jiraLinkTemplate.format(jiraIssueNumber=p.group(3))

f_version_info.close()
f_jira_issues.close()
f_download_links.close()

version_info["jiraLinks"] = jiraIssueLinks
version_info["downloadLinks"] = downloadLinks
version_info["uploadedFiles"] = uploadedFiles

emailTemplate = """To: {sendTo}
Subject: Build completed for {jobName}
Content-Type: text/html

<!DOCTYPE html>
<html>
	<body>
		<p>
			The build "{jobName}" has completed successfully.
		</p>
		<br/>
		<p><b>Files Uploaded to S3:</b></p>
		<ul>{uploadedFiles}</ul>
	</body>
</html>"""

print emailTemplate.format(**version_info)
