#!/bin/bash

for locale in en_US en_CA en_GB es_US es_ES fr_CA fr_FR de_DE
do
	mkdir -p app/src/main/res/values-${locale/_/-r}
	curl https://api-xqa.nonprod.gcs.ehi.com/bin/mobile-app.enterprise.${locale}.xml?dd=cachebust=aaa > app/src/main/res/values-${locale/_/-r}/strings.xml
done

#No region / default languages
#for locale in en_GB es_ES fr_FR de_DE
#do
#        mkdir -p app/src/main/res/values-${locale/_/-r}
#        curl --basic -u admin:admin http://ec2-54-173-125-23.compute-1.amazonaws.com:4502/content/eapp/${locale}/jcr:content.xml > app/src/main/res/values-${locale/_../}/strings.xml
#done

# Default strings file and default language files
cp app/src/main/res/values-en-rGB/strings.xml app/src/main/res/values/strings.xml
cp app/src/main/res/values-en-rGB/strings.xml app/src/main/res/values-en/strings.xml
cp app/src/main/res/values-de-rDE/strings.xml app/src/main/res/values-de/strings.xml
cp app/src/main/res/values-es-rES/strings.xml app/src/main/res/values-es/strings.xml
cp app/src/main/res/values-fr-rFR/strings.xml app/src/main/res/values-fr/strings.xml

