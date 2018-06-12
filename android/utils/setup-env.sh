#!/bin/sh

#Usage add:
# . "$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/setup-env.sh"
# at the Very beginning of your script to set the Env variables (note: script must be in the same Directory as setup-env.sh)

#SCRIPT_DIR -> where all of the utility scripts are Ex: "~/git/national-mob-android/build"
#REPO_ROOT -> the top level of the projects git repo Ex: "~/git/national-mob-android"
echo "--> Running setup-env.sh"
#export SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
export SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
export REPO_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

. "$SCRIPT_DIR/default-env.sh"

if [ -e "$SCRIPT_DIR/env.sh" ]; then
	. "$SCRIPT_DIR/env.sh"
else 
	echo "Custom env.sh not provided, assuming it running on jenkins server"
fi

if [ -n "$ANDROID_HOME" ]; then 
	export PATH=$ANDROID_HOME/platform-tools:$ANDROID_HOME/tools:$PATH 
fi
if [ -n "$ANT_HOME" ]; then 
	export PATH=$ANT_HOME/bin:$PATH
fi
if [ -n "$JAVA_HOME" ]; then
	export PATH=$JAVA_HOME/bin:$PATH
fi

echo "----> SCRIPT_DIR: $SCRIPT_DIR"
echo "----> REPO_ROOT: $REPO_ROOT"