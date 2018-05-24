# !/bin/bash
set -e

# just test PRs
if test "${TRAVIS_PULL_REQUEST}" == "false"
then
    xcodebuild \
        -workspace Enterprise.xcworkspace \
        -scheme Enterprise \
        -configuration ${XCODE_CONFIGURATION} \
        -derivedDataPath . \
        PRODUCT_BUNDLE_IDENTIFIER="com.ehi.Enterprise" \
        EHI_UAT_BUNDLE_ID="com.ehi.Enterprise" \
        EHI_BUILD_NUMBER="${TRAVIS_BUILD_NUMBER}" \
        EXECUTE_DSYM_UPLOAD=1 \
        INCLUDE_GITHASH=YES \
        ONLY_ACTIVE_ARCH=NO \
        ENABLE_BITCODE=NO \
        DEVELOPMENT_TEAM="TL76Q6ELX9" \
        CODE_SIGN_IDENTITY="iPhone Distribution" \
        CODE_SIGN_ENTITLEMENTS="./Enterprise/Entitlements/Production.entitlements" \
        PROVISIONING_PROFILE="d64f6662-47c7-4224-b66c-fa85e0d8e829" \
        clean build \
        | xcpretty -c && exit ${PIPESTATUS[0]}
else
    echo "Just build when not in a PR"
fi
