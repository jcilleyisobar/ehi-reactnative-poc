# !/usr/bin/env bash
set -eo pipefail

scriptPath="$( cd "$(dirname "$0")" ; pwd -P )"
xcodeTemplateDirectory=~/Library/Developer/Xcode/Templates/File\ Templates/
linkName="$xcodeTemplateDirectory"/Enterprise

if [ $1 == "install" ]; then
	# Create the install directory if it does not exist.
	if [ ! -d "$xcodeTemplateDirectory" ]; then
		mkdir -p "$xcodeTemplateDirectory"
	fi

	rm -rf "$linkName"
	ln -s "$scriptPath"/Enterprise "$xcodeTemplateDirectory"
	# Copy all of the xctemplate folders into the install directory.
	# cp -r *.xctemplate "$xcodeTemplateDirectory"
fi

if [ $1 == "uninstall" ]; then
	rm -rf "$linkName"
fi