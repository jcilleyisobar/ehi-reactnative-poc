const execSync = require('child_process').execSync;
const fs = require('fs');
const path = require('path');

function environmentIsSane() {
    let [ major, minor, revision ] = process.versions.node.split('.');
    return (parseInt(major) > 8) ||
           (parseInt(major) === 8 && parseInt(minor) > 5) ||
           (parseInt(major) === 8 && parseInt(minor) === 5 && parseInt(revision) >= 0);
}

function installDevDependencies() {
  console.log('Adding dev dependencies for the project...');

  const devDependenciesJsonPath = path.resolve('devDependencies.json');
  const devDependencies = JSON.parse(fs.readFileSync(devDependenciesJsonPath));

  for (let depName in devDependencies) {
    const depVersion = devDependencies[depName];
    const depToInstall = depName + '@' + depVersion;
    console.log('Adding ' + depToInstall + '...');
    execSync(`yarn add ${depToInstall} -D`, {stdio: 'inherit'});
  }
}

function replaceAppKey() {
  execSync(`yarn add replace-in-file -D`, {stdio: 'inherit'});

  const appJsonPath = path.resolve('app.json');
  const appName = JSON.parse(fs.readFileSync(appJsonPath)).name;

  const replace = require('replace-in-file');

  replace.sync({
    files: [
      path.resolve('index.js')
    ],
    from: 'RNTemplate',
    to: appName
  });
}

function addFontAssetRefToPackageJSON() {
    const pkgJsonPath = path.resolve('package.json');
    let pkg = JSON.parse(fs.readFileSync(pkgJsonPath));

    pkg.rnpm = {
        assets: [
            "./assets/fonts"
        ]
    };

    pkg.jest = {
      preset: "react-native",
      clearMocks: true,
      setupTestFrameworkScriptFile: "<rootDir>/tests/setupFramework.js"
    }
    
    fs.writeFileSync(pkgJsonPath, JSON.stringify(pkg, null, '    '));
}

function fixFontCorruptionWorkaround() {
  const fontFolderPath = path.resolve('assets/fonts');

  let list = fs.readdirSync(fontFolderPath);
  for(let i = 0; i < list.length; i++) {
      let filename = path.join(fontFolderPath, list[i]);
      let stat = fs.statSync(filename);

      if(path.extname(filename) === ".png") {
          let newFilename = filename.replace(".png", "");
          fs.renameSync(filename, newFilename);
      }
  }
  
  execSync(`react-native link`);
}

function cleanup() {
  const devDependenciesJsonPath = path.resolve('devDependencies.json');
  fs.unlinkSync(devDependenciesJsonPath);
}

function postTemplateInit() {
  if(environmentIsSane() === true) {
      installDevDependencies();
      replaceAppKey();
      addFontAssetRefToPackageJSON();
      fixFontCorruptionWorkaround();
      cleanup();
  } else {
      console.log("Node >= 8.5.0 is required to run this script.");
  }
}

postTemplateInit();
