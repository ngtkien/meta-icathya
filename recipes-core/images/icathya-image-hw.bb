# Copyright (C) 2025 meta-icathya
# Released under the MIT license

SUMMARY = "Icathya Stage 3: Hardware support for peripherals"
DESCRIPTION = "Adds WiFi, Bluetooth, audio, and hardware debugging tools to the base image. \
Enables full peripheral support for i.MX8MP boards."

LICENSE = "MIT"

# Inherit from Stage 2 - we get all command-line tools
require icathya-image-base.bb

SUMMARY = "Icathya Stage 3: Hardware support for peripherals"

#
# LEARNING SECTION 1: BSP Concepts - What is a BSP?
# ----------------------------------------------------------------------------
# BSP = Board Support Package
#
# A BSP provides:
#   1. Kernel device drivers (compiled into kernel or as modules)
#   2. Firmware blobs (WiFi, Bluetooth, GPU, VPU)
#   3. Device tree files (.dtb) describing hardware layout
#   4. U-Boot bootloader configuration
#
# The meta-imx and meta-variscite-bsp-imx layers are BSPs.
# They already provide kernel drivers, but we need to add USERSPACE tools
# and FIRMWARE to actually use the hardware.
#

#
# LEARNING SECTION 2: WiFi and Bluetooth Support
# ----------------------------------------------------------------------------
# For WiFi/Bluetooth to work, you need:
#   1. Kernel driver (already in meta-imx/meta-variscite)
#   2. Firmware file (loaded by kernel at boot)
#   3. Userspace tools (wpa_supplicant for WiFi, bluez for Bluetooth)
#
# packagegroup-tools-bluetooth: Brings in BlueZ stack
#   - bluetoothctl: Command-line BT management
#   - hciconfig: Low-level HCI (Host Controller Interface) tool
#   - btmon: Bluetooth monitor/debugging
#
# linux-firmware: Collection of binary firmware files
#   - Contains firmware for many WiFi/BT chipsets
#   - i.MX8MP boards typically use QCA or Broadcom chips
#
# WiFi tools:
#   - wpa-supplicant: WPA/WPA2 authentication
#   - iw: Modern WiFi configuration (replaces iwconfig)
#   - wireless-tools: Legacy WiFi tools (iwconfig, iwlist)
#
IMAGE_INSTALL += " \
    packagegroup-tools-bluetooth \
    linux-firmware \
    wpa-supplicant \
    iw \
    wireless-tools \
"

#
# LEARNING SECTION 3: Audio Support (ALSA)
# ----------------------------------------------------------------------------
# ALSA = Advanced Linux Sound Architecture
#
# The kernel driver is already present, but we need userspace tools:
#   - alsa-utils: Command-line tools (aplay, arecord, amixer, alsactl)
#   - alsa-state: Saves/restores audio mixer settings across reboots
#
# NXP-specific:
#   - packagegroup-fsl-tools-audio: NXP's audio testing tools
#
IMAGE_INSTALL += " \
    alsa-utils \
    alsa-state \
    packagegroup-fsl-tools-audio \
"

#
# LEARNING SECTION 4: Hardware Debugging Tools
# ----------------------------------------------------------------------------
# When bringing up hardware, you need tools to inspect and test:
#
# I2C Tools (I2C = Inter-Integrated Circuit bus):
#   - i2c-tools: i2cdetect (scan bus), i2cget/i2cset (read/write registers)
#   - Used for: Sensors, RTCs, EEPROMs, touchscreens, etc.
#
# SPI Tools (SPI = Serial Peripheral Interface):
#   - spidev-test: Test SPI communication
#   - spi-tools: Additional SPI utilities
#
# GPIO Tools (GPIO = General Purpose Input/Output):
#   - libgpiod: Modern GPIO interface (v2 API)
#   - libgpiod-tools: gpiomon, gpioget, gpioset, gpiofind
#   - Replaces deprecated sysfs GPIO interface (/sys/class/gpio)
#
# CAN Bus (Controller Area Network - automotive/industrial):
#   - can-utils: cansend, candump, cangen (if you use CAN)
#
IMAGE_INSTALL += " \
    i2c-tools \
    spidev-test \
    libgpiod \
    libgpiod-tools \
"

# Optionally add CAN tools if you use CAN bus (comment out if not needed)
# IMAGE_INSTALL += "can-utils"

#
# LEARNING SECTION 5: Firmware Loading in Linux
# ----------------------------------------------------------------------------
# How firmware loading works:
#
# 1. Kernel driver requests firmware (e.g., "brcm/brcmfmac43455-sdio.bin")
# 2. Kernel looks in /lib/firmware/
# 3. If found, loads it into the hardware
# 4. If not found, device initialization fails
#
# The linux-firmware package installs thousands of firmware files to
# /lib/firmware/. This is why this package is LARGE (~100+ MB).
#
# For production, you should create a custom firmware package that ONLY
# includes your specific chip's firmware (covered in advanced topics).
#

#
# LEARNING SECTION 6: Board-Specific Considerations
# ----------------------------------------------------------------------------
# NXP i.MX8MP EVK:
#   - Uses on-board WiFi/BT or M.2 module (varies by SKU)
#   - Check documentation for specific chipset
#
# Variscite VAR-DART:
#   - Typically uses Sterling-LWB WiFi/BT module (QCA)
#   - Firmware is usually in meta-variscite-bsp-imx
#   - Check meta-variscite-bsp-imx/recipes-connectivity/
#
# IMPORTANT: Run 'dmesg | grep -i firmware' after boot to see what
# firmware files the kernel is trying to load.
#

# Increase rootfs size for firmware files
IMAGE_ROOTFS_SIZE = "24576"

#
# LEARNING NOTES:
# ----------------------------------------------------------------------------
# SIZE INCREASE: ~150-200 MB → ~250-350 MB
# WHY SO BIG: linux-firmware package is huge (~100+ MB)
#
# TESTING EXERCISES:
#
# 1. Bluetooth testing:
#    $ hciconfig hci0 up         # Bring up Bluetooth adapter
#    $ bluetoothctl              # Interactive BT management
#    > scan on                   # Scan for devices
#    > devices                   # List found devices
#
# 2. WiFi testing:
#    $ ip link show              # Find WiFi interface (wlan0, mlan0, etc.)
#    $ iw dev wlan0 scan         # Scan for access points
#    $ wpa_passphrase "SSID" "password" > /etc/wpa.conf
#    $ wpa_supplicant -i wlan0 -c /etc/wpa.conf -B
#    $ dhclient wlan0            # Get IP address
#
# 3. Audio testing:
#    $ aplay -l                  # List playback devices
#    $ arecord -l                # List capture devices
#    $ speaker-test -c 2         # Test stereo output
#    $ arecord -d 5 test.wav     # Record 5 seconds
#    $ aplay test.wav            # Play it back
#
# 4. I2C testing:
#    $ i2cdetect -l              # List I2C buses
#    $ i2cdetect -y 0            # Scan bus 0 for devices
#    $ i2cdump -y 0 0x50         # Dump EEPROM at address 0x50
#
# 5. GPIO testing:
#    $ gpiodetect                # List GPIO chips
#    $ gpiofind "GPIO_NAME"      # Find GPIO by name
#    $ gpioget gpiochip0 10      # Read GPIO pin 10
#    $ gpioset gpiochip0 10=1    # Set GPIO pin 10 high
#
# 6. Check loaded firmware:
#    $ dmesg | grep -i firmware  # See what firmware was loaded
#    $ ls /lib/firmware/         # See available firmware files
#
