# !/usr/bin/env bash
set -eo pipefail

scriptPath="$( cd "$(dirname "$0")" ; pwd -P )"
hooks=$(git rev-parse --show-toplevel)/.git/hooks
hookName=commit-msg

if [ $1 == "install" ]; then
	rm -rf "$hooks"/"$hookName"
	ln -s "$scriptPath"/commit-msg "$hooks"
fi

if [ $1 == "uninstall" ]; then
	echo rm -rf "$hooks"/"$hookName"
fi