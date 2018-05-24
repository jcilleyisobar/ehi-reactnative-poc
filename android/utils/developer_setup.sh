#!/bin/sh
. "$(cd "$(dirname "$0")" && pwd)/setup-env.sh"
echo 'setting up '
echo '  developer Only gradle-wrapper.properties (uses Web to download wrapper stuff)'
cp $SCRIPT_DIR/developer/gradle-wrapper.properties $SCRIPT_DIR/../gradle/wrapper/gradle-wrapper.properties
echo '  global gradle.properties'
python $SCRIPT_DIR/setup_gradle_props.py
