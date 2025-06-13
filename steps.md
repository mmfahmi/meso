# Android Development Setup on WSL

This guide shows how to set up Android development tools for the Meso project using WSL (Windows Subsystem for Linux).

## Prerequisites

- Windows with WSL2 installed
- Ubuntu or similar Linux distribution in WSL

## 1. Install Java Development Kit

```bash
# Update package list
sudo apt update

# Install OpenJDK (Java 17 or 21 both work)
sudo apt install openjdk-21-jdk

# Verify installation
java -version
```

## 2. Set Up Android SDK in WSL

### Download Android Command Line Tools

```bash
# Create SDK directory
cd ~
mkdir android-sdk
cd android-sdk

# Download command line tools
wget https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip

# Extract and organize
unzip commandlinetools-linux-*.zip
mkdir -p cmdline-tools/latest
mv cmdline-tools/* cmdline-tools/latest/
```

### Set Environment Variables

```bash
# Add to ~/.bashrc
echo 'export ANDROID_HOME="$HOME/android-sdk"' >> ~/.bashrc
echo 'export PATH="$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools"' >> ~/.bashrc

# Reload environment
source ~/.bashrc
```

### Install SDK Components

```bash
# Accept all licenses
yes | sdkmanager --licenses

# Install required components
sdkmanager "platform-tools"
sdkmanager "platforms;android-34"
sdkmanager "build-tools;34.0.0"
```

## 3. Configure Project

### Create local.properties

In your project root (`/path/to/meso/`), create `local.properties`:

```properties
sdk.dir=/home/yato/android-sdk
```

### Update gradle.properties

Create or update `gradle.properties` in project root:

```properties
# Use WSL Java
org.gradle.java.home=/usr/lib/jvm/java-21-openjdk-amd64

# Optional: Enable build cache for faster builds
org.gradle.caching=true
```

## 4. Add Missing Resources

The project requires these string resources in `app/src/main/res/values/strings.xml`:

```xml
<resources>
    <string name="app_name">Meso</string>
    <string name="notification_channel_name">Monitoring Service</string>
    <string name="notification_channel_description">Channel for monitoring service notifications</string>
    <string name="monitoring_notification_title">App Monitoring Active</string>
    <string name="monitoring_notification_text">Monitoring social media usage</string>
</resources>
```

## 5. Build the Project

```bash
# Navigate to project directory
cd /path/to/meso

# Build debug APK
./gradlew assembleDebug

# For release build
./gradlew assembleRelease
```

## 6. Running on Emulator

### Using Android Studio
1. Open Android Studio
2. Configure terminal to use WSL: **File** → **Settings** → **Tools** → **Terminal** → Shell path: `wsl.exe`
3. Start an emulator from **Tools** → **AVD Manager**
4. Click **Run** button in Android Studio

### Using Command Line

```bash
# List connected devices/emulators
adb devices

# Install APK manually
adb install app/build/outputs/apk/debug/app-debug.apk

# Launch app
adb shell am start -n com.socialmediasafety.rating/.MainActivity
```

## Troubleshooting

### License Issues
```bash
# If you get license errors
yes | sdkmanager --licenses
```

### Java Version Issues
```bash
# Check Java version
java -version

# If using wrong Java, update gradle.properties
echo 'org.gradle.java.home=/usr/lib/jvm/java-21-openjdk-amd64' >> gradle.properties
```

### SDK Path Issues
```bash
# Verify SDK structure
ls -la $ANDROID_HOME/cmdline-tools/latest/bin/

# Verify project configuration
cat local.properties
```

### Permission Issues
```bash
# Make gradlew executable
chmod +x gradlew

# Don't use sudo with gradle
./gradlew assembleDebug
```

## Project Structure

```
meso/
├── app/
│   ├── src/main/
│   │   ├── java/com/socialmediasafety/rating/
│   │   └── res/values/strings.xml
│   └── build.gradle
├── gradle.properties
├── local.properties
└── gradlew
```

## Useful Commands

```bash
# Check Gradle version
./gradlew --version

# Clean build
./gradlew clean

# Build with debug info
./gradlew assembleDebug --info

# List all tasks
./gradlew tasks

# Check connected devices
adb devices

# View app logs
adb logcat
```