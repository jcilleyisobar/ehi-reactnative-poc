import os
import re
def getAndroidDebugCertPath():
	windowsHome = os.environ.get('HOMEPATH');
	if windowsHome != None:
		return os.path.join(windowsHome, ".android", "debug.keystore");
	else:
		return os.path.join(os.environ.get('HOME'), ".android", "debug.keystore");
def getGradleSettingsPath():
	windowsHome = os.environ.get('HOMEPATH');
	if windowsHome != None:
		return os.path.join(windowsHome, ".gradle", "gradle.properties");
	else:
		return os.path.join(os.environ.get('HOME'), ".gradle", "gradle.properties");
#note: this does not follow all of the rules on http://docs.oracle.com/cd/E23095_01/Platform.93/ATGProgGuide/html/s0204propertiesfileformat01.html so as it will only be used to check the existence of certian properties (and append values to the file in the case)
def parsePropFile(fileName):
	props = {}
	if os.path.isfile(fileName):
		file = open(fileName, "r");
		for v in file:
			vv = re.split("=", v)
			if(len(vv) >= 2): 
				props[vv[0].strip("\n\r ")] = vv[1].strip("\n\r ")
		file.close();
	return props;

class PropFile:
	def __init__(self, filePath):
		self.filePath= filePath;
		self.props = parsePropFile(filePath);
		self.appendMe = "";
	def defaultValue(self, key, value):
		if not (key in self.props):
			#ToDo: talor line endings, account for special chars in "value"
			self.appendMe += "\n" + key + "=" + value
			self.props[key] = value;
	def writeChanges(self):
		if len(self.appendMe) > 0:
			f = open(self.filePath, "a");
			f.write(self.appendMe);
			f.close();
			self.appendMe = "";


gradleProps = PropFile(getGradleSettingsPath());
gradleProps.defaultValue("enterprise_mobile_app_keystore_file", getAndroidDebugCertPath())
gradleProps.defaultValue("enterprise_mobile_app_key_alias","androiddebugkey");
gradleProps.defaultValue("enterprise_mobile_app_store_password","android");
gradleProps.defaultValue("enterprise_mobile_app_key_password","android");
gradleProps.writeChanges();