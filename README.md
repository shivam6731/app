# logistics-app-android
Delivery application for our riders

## Environment setup

- Download and install [Android Studio](http://developer.android.com/sdk/index.html)
- Import the project in Android Studio
- Try "Build -> Clean project" (it will log the required dependencies)
- Launch the SDK Manager (Tools -> Android -> SDK Manager -> Launch Standalone SDK Manager) and install:
  - SDK Platform of target version (can be found in [gradle.build](https://github.com/foodpanda/logistics-app-android/blob/master/app/build.gradle))
  - Build dependencies (`SDK Build Tool`, `SDK Tools`, `Support Libs` and `Support Repo`)
- Open the "Build Variants" window and choose the flavour to build `devDebug`

## Tests setup

- Create a run/debug configuration (Add a new configuration -> JUnit)
  - Test kind: `All in package`
  - Package: `com.foodpanda.urbanninja`
  - Search for test: `In a single module`
  - Use classpath of module: `app`

Pro tip: Edit your Android run/debug configuration to execute the tests task before launch

## Release setup

- execute gradle command `gradlew build` to generate all apk file for all flavors (dev, staging, production)  both singed and unsigned versions
- open [staging](https://rink.hockeyapp.net/manage/apps/292921) or [production](https://rink.hockeyapp.net/manage/apps/292913) version of the app 
- click `Add Version` and after select generated `apk` file 
- add release notes to let riders know what new in this version:
  - link to the jira ticket
  - title of this ticket 

- after finish release 

 To force all users to update new version needs to
  - click to version  
  - `Manage Version` 
  - `Status` 
  - check `Mandatory Update` -> `Enabled` 

 To release app only for selected users need to 
  - click to version 
  - `Manage Version`
  - `Status` 
  - and check `Restrict Downloads` -> `Enabled` 
  - selected `group`, `users` or `tag` from the list to let them upload new version

