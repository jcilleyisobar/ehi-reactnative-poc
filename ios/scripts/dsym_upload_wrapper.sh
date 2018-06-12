#!/bin/sh

echo "executing dsym_upload_wrapper.sh ..."

if [ $EXECUTE_DSYM_UPLOAD -eq 1 ]; then
  REQUIRE_UPLOAD_SUCCESS=0
  
  INFOPLIST_FILE="${TARGET_BUILD_DIR}/${INFOPLIST_PATH}"
  
  # EMA iOS Prod
  APP_ID="52fb9395558d6a6d1b000006"
  API_KEY="ogkrk83tcowesan1jby7hepzg0swf4mm"
  source "${SRCROOT}"/scripts/dsym_upload.sh -v "${DWARF_DSYM_FOLDER_PATH}/${DWARF_DSYM_FILE_NAME}"
  
  # EMA iOS QA
  APP_ID="52fb93bc97c8f26539000004"
  API_KEY="f44b7917d02adccef68dee886b91686f"
  source "${SRCROOT}"/scripts/dsym_upload.sh -v "${DWARF_DSYM_FOLDER_PATH}/${DWARF_DSYM_FILE_NAME}"

  # EMA iOS Dev
  APP_ID="52fb92f58b2e332f65000001"
  API_KEY="34d741fe1914f6ac4dfccbb75b48d708"
  source "${SRCROOT}"/scripts/dsym_upload.sh -v "${DWARF_DSYM_FOLDER_PATH}/${DWARF_DSYM_FILE_NAME}"
else
  echo "EXECUTE_DSYM_UPLOAD variable not set"
fi
