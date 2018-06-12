#!/bin/bash

env

BUILD_NUMBER="${TRAVIS_BUILD_NUMBER}" \
	./gradlew clean ${BUILD_COMMAND} \
		-Penterprise_mobile_app_keystore_file="../keystores/internal_release.jks" \
		-Penterprise_mobile_app_key_alias="enterprise" -Penterprise_mobile_app_store_password=${KEY_PASSWORD} \
		-Penterprise_mobile_app_key_password="${KEY_PASSWORD}"

