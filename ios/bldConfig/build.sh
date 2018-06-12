#!/bin/bash


function failed() {
    echo "Failed: $@" >&2
    exit 1
}

function message() {
  msg="** $@ **"

  length=${#msg}
  echo `perl -E 'say "*" x $length'`
  echo "$msg"
  echo `perl -E 'say "*" x $length'`
}

function htmlHeader() {
cat <<HEADERDOC > $HTML_FILE
<!DOCTYPE html>
<html>
<head>
  <style>
    body {
      font-family: arial, helvetica;
      background-color:red;
    }

    html, body, div.list{
      height:100%;
      margin:0;
      padding:0;
    }

    div.item {
      padding:20px;0px;20px;2px;
      vertical-align: middle;
      width: 100%;
    }

    div.Debug {
      background-color:#FFFF99;
    }

    div.Release {
      background-color:#99FFCC;
    }
  </style>
  <meta name="viewport" content="user-scalable=yes,width=device-width" />
</head>
<body>
<div class="list">
HEADERDOC
}

function htmlEntry() {
  # the plist file name
  file=$1; shift
  # the IPA file name
  name=$1; shift
  # the type of build (debug/release)
  config=$1; shift
  
cat <<ENTRYDOC >> $HTML_FILE
<div class="item $config">
<a href="itms-services://?action=download-manifest&url=`$SIGN_URL $URL_HOST $URL_ROOT/$file $EXPIRES y`">
$name
</a>
</div>
ENTRYDOC
}

function htmlFooter() {
cat <<FOOTERDOC >> $HTML_FILE
</div>
</body>
</html>
FOOTERDOC
}

function plist() {
    # the directory that contains the IPA file
    loc=$1; shift
    # the name of the IPA file
    name=$1; shift
    # the type of build (debug/release)
    config=$1; shift
    # the app's bundle ID 
    bundle_id=$1; shift

    url="`$SIGN_URL https://s3.amazonaws.com /ehi-enterprise-mobile-client-app/iOS/${name}.ipa $EXPIRES | sed 's/\&/\&amp;/g;s/>/\&gt;/g;s/</\&lt;/g'`"
    filename=${name}.plist

    htmlEntry $filename $name $config

cat <<HEREDOC > $loc/${filename}
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
  <key>items</key>
  <array>
    <dict>
      <key>assets</key>
      <array>
        <dict>
          <key>kind</key>
          <string>software-package</string>
          <key>url</key>
          <string>$url</string>
        </dict>
      </array>
      <key>metadata</key>
      <dict>
        <key>bundle-identifier</key>
        <string>${bundle_id}</string>
        <key>bundle-version</key>
        <string>${VERSION}</string>
        <key>kind</key>
        <string>software</string>
        <key>title</key>
        <string>Enterprise</string>
      </dict>
    </dict>
  </array>
</dict>
</plist>

HEREDOC
}

set -ex

# Import config
. "$WORKSPACE/bldConfig/build.config"

# Get Master Version from project file
MASTER_VERSION=`xcodebuild -configuration Debug -showBuildSettings | grep EHI_BUILD_VERSION |  cut -c25-`

if [ "$MASTER_VERSION" = "" ]; then
  MASTER_VERSION=UNKNOWN
fi

# Retrieve build number from either command-line parameter or environment
BUILD_NUMBER_DISCOVERED=$1
if [ "$BUILD_NUMBER_DISCOVERED" = "" ]; then
BUILD_NUMBER_DISCOVERED=$BUILD_NUMBER
fi

export VERSION=${MASTER_VERSION}.${BUILD_NUMBER_DISCOVERED}

launchctl remove com.apple.CoreSimulator.CoreSimulatorService || true

if [ -z $DEVELOPER_DIR ]; then
    export DEVELOPER_DIR='/Applications/Xcode-9.2.0.app/Contents/Developer'
fi

SIGN_URL=/var/jenkinsslave/signUrl

export LC_CTYPE=en_US.UTF-8
export OUTPUT=$WORKSPACE/output
export DERIVED_DATA=$WORKSPACE/DerivedData
export BUILD_DIR=$WORKSPACE/build
rm -rf $OUTPUT
rm -rf $DERIVED_DATA
rm -rf $BUILD_DIR
mkdir -p $OUTPUT
PROFILE_HOME=~/Library/MobileDevice/Provisioning\ Profiles/
KEYCHAIN=~/Library/Keychains/login.keychain
HTML_FILENAME=Enterprise-${VERSION}.html
HTML_FILE=$OUTPUT/$HTML_FILENAME
URL_HOST="https://s3.amazonaws.com"
URL_ROOT="/ehi-enterprise-mobile-client-app/iOS"

EXPIRES=`date "+%s"`
EXPIRES=$(($EXPIRES + (365 * 24 * 60 * 60 ) ))

BUILD_DIR=$WORKSPACE/build

cd "$WORKSPACE/"

# Set build Number in Application plist 
agvtool new-version -all ${BUILD_NUMBER_DISCOVERED}
export EHI_BUILD_NUMBER=${BUILD_NUMBER_DISCOVERED}

OVERRIDE=$WORKSPACE/build/configOverrides

htmlHeader

  # loop through all the sdks listed in the scheme specific config
for scheme in ${SCHEMES}; do   

  rm -rf $DERIVED_DATA
  rm -rf $BUILD_DIR
  
  # Import the build specific config
  . "$WORKSPACE/bldConfig/build-${scheme}.config"

  # loop through all the sdks listed in the scheme specific config
  for sdk in ${SDKS}; do
      for config in ${CONFIGURATIONS}; do
      
          cd "$WORKSPACE"

          signer=$(eval echo \$`echo Signer${config}`)          
          bundle_id=${BundleId}
          team_id=${TeamId}
          export_options_plist="$WORKSPACE/bldConfig/exportOptions-${scheme}-${config}.plist"
          

          echo "team_id ${team_id}"
          echo "signer ${signer}"
          echo "bundle_id ${bundle_id}"

          archive="Enterprise-${VERSION}-${scheme}-${config}"

          [ -f "${cert}" ] && cp "${cert}" "$PROFILE_HOME"

          echo "Cleaning"
          xcodebuild -derivedDataPath ${DERIVED_DATA} -scheme Enterprise clean || failed clean

          echo "Building ${scheme}-${config}"
          xcodebuild archive \
                -allowProvisioningUpdates \
                -workspace "Enterprise.xcworkspace" \
                -scheme "Enterprise" \
                -configuration ${config} \
                -sdk ${sdk} \
                -derivedDataPath ${DERIVED_DATA} \
                -archivePath "${BUILD_DIR}/${config}-iphoneos/Enterprise.xcarchive" \
                CODE_SIGN_STYLE=Automatic \
                DEVELOPMENT_TEAM=${team_id} \
                PROVISIONING_PROFILE_SPECIFIER="" \
                APP_BUNDLE_IDENTIFIER="${bundle_id}" \
                ARCHS="arm64 armv7 armv7s" ONLY_ACTIVE_ARCH=NO \
                OBJROOT=${WORKSPACE}/build | "${WORKSPACE}/bldConfig/xcpretty" || failed build


          

          echo "Packaging ${scheme}-${config}"
          xcodebuild -exportArchive \
                -allowProvisioningUpdates \
                -archivePath "${BUILD_DIR}/${config}-iphoneos/Enterprise.xcarchive" \
                -exportPath "${OUTPUT}" \
                -exportOptionsPlist "${export_options_plist}" \
                CODE_SIGN_STYLE=Automatic \
                CODE_SIGN_IDENTITY="${signer}" | "${WORKSPACE}/bldConfig/xcpretty" || failed build

          mv "${OUTPUT}/Enterprise.ipa" "${OUTPUT}/${archive}.ipa"

          plist ${OUTPUT} ${archive} ${config} ${bundle_id}
      done
  done
done

htmlFooter

echo "Access uploaded builds at `$SIGN_URL $URL_HOST $URL_ROOT/$HTML_FILENAME $EXPIRES`"

