# Copyright (C) 2025 meta-icathya
# Released under the MIT license

SUMMARY = "Icathya Stage 2: Full command-line system"
DESCRIPTION = "A complete command-line Linux environment with full GNU utilities, \
network tools, and system monitoring. Builds upon icathya-image-minimal."

LICENSE = "MIT"

# Inherit from our minimal image - this gives us everything from Stage 1
require icathya-image-minimal.bb

#
# LEARNING SECTION 1: Recipe Inheritance with 'require'
# ----------------------------------------------------------------------------
# By using 'require icathya-image-minimal.bb', we inherit ALL variables from
# Stage 1. This means we don't have to repeat:
#   - inherit core-image
#   - ssh-server-openssh feature
#   - package-management feature
#   - packagegroup-core-boot
#
# We EXTEND these variables using the += operator.
#
# Key difference between 'require' and 'inherit':
#   - require: Include another .bb or .bbclass file (relative path)
#   - inherit: Include a .bbclass from classes/ directory (no extension)
#

# Update the description to reflect this is Stage 2
SUMMARY = "Icathya Stage 2: Full command-line system"

#
# LEARNING SECTION 2: Understanding packagegroups
# ----------------------------------------------------------------------------
# packagegroup-core-full-cmdline: A "meta-package" that brings in a complete
# command-line environment. It includes:
#   - Full bash shell (instead of just busybox sh)
#   - GNU coreutils (ls, cp, mv, rm, cat, etc. - full versions)
#   - Base system tools (tar, gzip, findutils, etc.)
#   - Process management (psmisc, procps)
#
# This is MUCH larger than packagegroup-core-boot, but gives you a "real" Linux
# command-line experience similar to a Ubuntu/Debian server (but embedded).
#
IMAGE_INSTALL += " \
    packagegroup-core-full-cmdline \
"

#
# LEARNING SECTION 3: Adding Network Utilities
# ----------------------------------------------------------------------------
# Now we add specific packages for networking and diagnostics.
#
# These are NOT in packagegroups - we're adding them explicitly:
#   - curl: HTTP client (useful for API testing, downloading files)
#   - wget: Alternative HTTP client
#   - tcpdump: Network packet analyzer (debugging network issues)
#   - bind-utils: DNS tools (nslookup, dig, host)
#   - iproute2: Modern network configuration (ip command)
#   - iputils: ping, traceroute, etc.
#   - openssh-sftp-server: SFTP support for file transfers
#
IMAGE_INSTALL += " \
    curl \
    wget \
    tcpdump \
    bind-utils \
    iproute2 \
    iputils \
    openssh-sftp-server \
"

#
# LEARNING SECTION 4: System Monitoring and Debugging
# ----------------------------------------------------------------------------
# Development and debugging tools:
#   - htop: Interactive process viewer (better than 'top')
#   - iotop: I/O monitoring (see what's reading/writing to disk)
#   - iftop: Network bandwidth monitoring
#   - lsof: List open files (debugging file/socket issues)
#   - strace: System call tracer (advanced debugging)
#   - sysstat: System performance tools (iostat, mpstat, sar)
#
IMAGE_INSTALL += " \
    htop \
    iotop \
    iftop \
    lsof \
    strace \
    sysstat \
"

#
# LEARNING SECTION 5: File System Utilities
# ----------------------------------------------------------------------------
# Additional filesystem and storage tools:
#   - e2fsprogs: ext2/3/4 filesystem utilities (resize2fs, tune2fs, etc.)
#   - dosfstools: FAT filesystem tools (mkfs.vfat, fsck.vfat for SD cards)
#   - parted: Partition editor
#   - rsync: File synchronization (useful for updates/backups)
#
IMAGE_INSTALL += " \
    e2fsprogs \
    dosfstools \
    parted \
    rsync \
"

#
# LEARNING SECTION 6: Text Editors
# ----------------------------------------------------------------------------
# Even embedded systems need editors for config file editing:
#   - nano: Simple, user-friendly terminal editor
#   - vim: Powerful but complex editor (optional - comment out if not needed)
#
IMAGE_INSTALL += " \
    nano \
    vim \
"

#
# Increase rootfs size to accommodate the additional packages
#
IMAGE_ROOTFS_SIZE = "16384"

#
# LEARNING NOTES:
# ----------------------------------------------------------------------------
# What you gained from Stage 1 → Stage 2:
#
# SIZE INCREASE: ~50-100 MB → ~150-200 MB
# TIME INCREASE: +15-30 minutes build time
#
# NEW CAPABILITIES:
#   1. Full bash shell with command history, tab completion
#   2. Complete GNU tools (proper 'ls -l' with colors, full 'grep', etc.)
#   3. Network diagnostics (curl APIs, tcpdump packets, ping hosts)
#   4. System monitoring (htop to see CPU/RAM, tcpdump for network)
#   5. File editing (nano/vim instead of echo >> file)
#
# TESTING EXERCISES:
#   1. Compare bash vs busybox sh:
#      - Busybox: /bin/sh (STAGE 1)
#      - Bash: /bin/bash (STAGE 2)
#      Try: history, tab completion, arrow keys
#
#   2. Network testing:
#      $ curl -I https://www.google.com  # Should work now
#      $ tcpdump -i eth0                 # Capture network traffic
#
#   3. System monitoring:
#      $ htop                            # Interactive process viewer
#      $ iotop                           # See disk I/O
#
#   4. Package comparison:
#      Compare manifest files:
#      $ diff icathya-image-minimal.manifest icathya-image-base.manifest
#      Count packages:
#      $ wc -l *.manifest
#
