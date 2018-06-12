#!/bin/bash

export WORKSPACE="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
WORKSPACE="$(dirname "$WORKSPACE")"
echo "The directory WORKSPACE is ${WORKSPACE}"
source "$WORKSPACE/bldConfig/build.sh" 9999
