# Copyright (C) 2025 meta-icathya
# Released under the MIT license

SUMMARY = "Icathya Stage 1: Minimal bootable Linux image"
DESCRIPTION = "A minimal image just capable of booting with SSH access and package management. \
This is the starting point for progressive embedded Linux learning."

LICENSE = "MIT"

# Inherit the core-image class which provides the base image functionality
inherit core-image

#
# LEARNING SECTION 1: IMAGE_FEATURES
# ----------------------------------------------------------------------------
# IMAGE_FEATURES are high-level features that bring in groups of packages.
# Think of them as "presets" or "feature bundles".
#
# Common IMAGE_FEATURES:
#   - ssh-server-openssh: Installs OpenSSH server for remote access
#   - package-management: Installs package manager (opkg/dnf/apt)
#   - debug-tweaks: Development conveniences (empty root password, etc.)
#   - tools-debug: Debugging tools like gdb, strace
#   - hwcodecs: Hardware codec support
#
# For a minimal image, we only need SSH and package management.
#
IMAGE_FEATURES += " \
    ssh-server-openssh \
    package-management \
"

#
# LEARNING SECTION 2: IMAGE_INSTALL
# ----------------------------------------------------------------------------
# IMAGE_INSTALL explicitly lists packages to include in the rootfs.
# This is the "manual" way to add specific packages.
#
# packagegroup-core-boot: The absolute minimum packages needed to boot Linux
#   - This includes: kernel, init system (systemd/sysvinit), basic shell (busybox)
#
# CORE_IMAGE_EXTRA_INSTALL: A variable you can set in local.conf to add
#   extra packages to ALL images without modifying recipes.
#
IMAGE_INSTALL = " \
    packagegroup-core-boot \
    ${CORE_IMAGE_EXTRA_INSTALL} \
"

#
# LEARNING SECTION 3: IMAGE_ROOTFS_SIZE
# ----------------------------------------------------------------------------
# Controls the root filesystem size. For minimal images, we keep it small.
# Units are in KB (kilobytes).
#
# 8192 KB = 8 MB base size (very small, will auto-expand based on content)
#
IMAGE_ROOTFS_SIZE ?= "8192"

#
# Add extra space for systemd if enabled (systemd is larger than sysvinit)
#
IMAGE_ROOTFS_EXTRA_SPACE:append = "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', ' + 4096', '', d)}"

#
# LEARNING SECTION 4: Why This Works
# ----------------------------------------------------------------------------
# When you build this image:
#   1. Bitbake inherits core-image class → gets basic image infrastructure
#   2. packagegroup-core-boot pulls in kernel, init, shell
#   3. ssh-server-openssh feature adds OpenSSH server + dependencies
#   4. package-management adds opkg (or rpm/deb depending on distro config)
#   5. Result: A ~50-100 MB image that boots to console with SSH access
#
# What's MISSING compared to a "full" system:
#   - No bash (only busybox sh)
#   - No WiFi/Bluetooth firmware
#   - No X11/Wayland (no graphics)
#   - No GStreamer (no multimedia)
#   - Limited command-line tools (busybox provides minimal versions)
#
# This is intentional! Each subsequent stage will add these features.
#

#
# LEARNING NOTES:
# ----------------------------------------------------------------------------
# After building this image, try these experiments:
#
# 1. Check what packages are installed:
#    $ cat tmp/deploy/images/<machine>/icathya-image-minimal-<machine>.manifest
#
# 2. Check image size:
#    $ ls -lh tmp/deploy/images/<machine>/icathya-image-minimal-<machine>.wic.zst
#
# 3. Boot the image and explore:
#    $ ls /bin /sbin /usr/bin  # See what commands are available
#    $ opkg list               # See what packages are installed
#    $ ps aux                  # See what processes are running
#
# 4. Compare to core-image-minimal from Poky:
#    $ bitbake core-image-minimal
#    $ diff manifest files to see the difference
#
