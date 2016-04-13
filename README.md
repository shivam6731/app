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
