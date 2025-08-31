# Bluetooth Scanner Application

## Project Overview

This is a simple Android application that allows you to scan for nearby Bluetooth Low Energy (BLE) devices, display them in a list, and connect, pair, and disconnect from them.

## Features

- Scan for BLE devices.
- Display found devices in a list with their name and MAC address.
- Connect to a selected device.
- Pair with a selected device.
- Disconnect from a connected device.
- Improved UI with a `RecyclerView` for efficient display of devices.
- A background `Service` to handle all Bluetooth operations.

## How to Build and Run

1.  **Open in Android Studio:**
    *   Open Android Studio.
    *   Click on "Open" or "Open an Existing Project".
    *   Navigate to the root directory of this project and select it.
    *   Android Studio will automatically import the project and download the necessary dependencies.

2.  **Enable Developer Mode and USB Debugging on your Android device:**
    *   Go to `Settings > About phone`.
    *   Tap on `Build number` 7 times to enable Developer options.
    *   Go to `Settings > System > Developer options`.
    *   Enable `USB debugging`.

3.  **Run the application:**
    *   Connect your Android device to your computer via a USB cable.
    *   In Android Studio, select your device from the dropdown menu in the toolbar.
    *   Click the "Run" button (the green play icon) or press `Shift + F10`.
    *   The application will be built, installed, and launched on your device.

## Permissions

The application requires the following permissions:

-   **Bluetooth:** `BLUETOOTH`, `BLUETOOTH_ADMIN` (for older devices), `BLUETOOTH_SCAN`, `BLUETOOTH_CONNECT` (for API 31+).
-   **Location:** `ACCESS_FINE_LOCATION` (required for Bluetooth scanning on some Android versions).

The application will request these permissions at runtime when you click the "Scan" button for the first time. Please grant the permissions for the application to function correctly.

## Debugging

The application includes logging to help with debugging. You can view the logs in Android Studio's **Logcat** window.

-   Open the Logcat window by clicking on `View > Tool Windows > Logcat`.
-   You can filter the logs by the application's package name (`com.example.bluetoothscanner`) or by the log tag `BluetoothService` to see the logs from the Bluetooth service.