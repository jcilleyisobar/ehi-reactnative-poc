#!/bin/sh
. "$(cd "$(dirname "$0")" && pwd)/setup-env.sh"

echo 'Running jenkins setup'
echo '  Copying Jenkins local.properties'
PROP_FILE=local.properties
if [ -f ${PROP_FILE} ]; then 
  cat ${PROP_FILE}
else
  cp -f ~/.android/.local.properties ${PROP_FILE}
fi

echo '  Copying Jenkins specific gradle-wrapper.properties (uses EHI network URL)'
cp $SCRIPT_DIR/jenkins/gradle-wrapper.properties $SCRIPT_DIR/../gradle/wrapper/gradle-wrapper.properties


