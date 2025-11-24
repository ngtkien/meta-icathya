# Copyright (C) 2025 meta-icathya
# Released under the MIT license

SUMMARY = "Icathya Stage 6: Application framework with Qt6"
DESCRIPTION = "Adds Qt6 framework for building modern GUI applications. This is the \
full-featured image with desktop environment and application development support."

LICENSE = "MIT"

# Inherit from Stage 5 - we get all graphics support
require icathya-image-graphics.bb

SUMMARY = "Icathya Stage 6: Application framework with Qt6"

#
# LEARNING SECTION 1: What is Qt?
# ----------------------------------------------------------------------------
# Qt = Cross-platform application framework (C++)
#
# Think of it like:
#   - Windows: .NET Framework / WPF
#   - macOS: Cocoa / AppKit
#   - Web: React / Angular
#   - Linux Embedded: Qt
#
# Qt provides:
#   1. GUI widgets (buttons, lists, menus, dialogs)
#   2. Graphics (2D painting, OpenGL integration)
#   3. Multimedia (audio, video, camera)
#   4. Networking (HTTP, WebSockets, etc.)
#   5. Database access (SQLite, PostgreSQL, MySQL)
#   6. Many more modules...
#
# Used in: Automotive HMI, medical devices, industrial panels, consumer electronics
#

#
# LEARNING SECTION 2: Qt Licensing - CRITICAL!
# ----------------------------------------------------------------------------
# Qt has DUAL licensing:
#
# 1. COMMERCIAL LICENSE:
#    - Pay The Qt Company for license
#    - Can create PROPRIETARY (closed-source) apps
#    - Get official support
#    - Costs: $5,000-$10,000+ per developer per year
#
# 2. OPEN SOURCE LICENSE (LGPLv3 / GPLv3):
#    - FREE to use
#    - Must comply with LGPL (dynamic linking OK) or GPL (must open source)
#    - No official support (community only)
#
# IMPORTANT: Decide licensing BEFORE you write Qt code!
#   - If you start with open source Qt and later want commercial, you may have to rewrite
#   - If your legal team hasn't approved, DON'T start coding yet!
#
# For learning: Open source is fine.
# For products: Consult legal team.
#

#
# LEARNING SECTION 3: Qt6 vs Qt5
# ----------------------------------------------------------------------------
# Qt6 (current):
#   - Released 2020
#   - Better performance
#   - Modern C++ (C++17)
#   - Better Wayland support
#   - Recommended for NEW projects
#
# Qt5 (legacy):
#   - Released 2012
#   - Still widely used (mature, stable)
#   - More examples/tutorials available
#   - NXP BSP includes both Qt5 and Qt6
#
# i.MX8MP BSP: Supports both Qt5 and Qt6
# This recipe uses Qt6 (future-proof).
#

#
# LEARNING SECTION 4: Qt6 Packagegroup
# ----------------------------------------------------------------------------
# packagegroup-qt6-imx: NXP's Qt6 package collection for i.MX
#
# This brings in:
#   - qtbase: Core Qt libraries (QtCore, QtGui, QtWidgets, QtNetwork)
#   - qtdeclarative: QML/Quick (declarative UI, like React)
#   - qtmultimedia: Audio/video playback, camera
#   - qtwayland: Wayland integration
#   - qtsvg: SVG rendering
#   - qtvirtualkeyboard: On-screen keyboard (for touchscreens)
#   - Many more modules...
#
# Total size: ~300-400 MB of libraries
#
IMAGE_INSTALL += " \
    packagegroup-qt6-imx \
"

#
# LEARNING SECTION 5: Additional Qt6 Modules (Optional)
# ----------------------------------------------------------------------------
# The packagegroup-qt6-imx includes the essentials, but you may want more:
#
# qtwebengine: Chromium-based web browser (QtWebEngine)
#   - Adds ~200 MB!
#   - Use if you need to display web content in your app
#   - Example: In-car infotainment browser, settings UI with HTML
#
# Uncomment if needed (warning: very large!):
# IMAGE_INSTALL += "packagegroup-qt6-webengine"
#

#
# LEARNING SECTION 6: Qt Platform Abstraction (QPA)
# ----------------------------------------------------------------------------
# QPA = Qt Platform Abstraction (how Qt talks to the OS)
#
# On embedded Linux, Qt can run with different "backends":
#
# 1. eglfs: Direct-to-framebuffer (no window manager)
#    - Single fullscreen Qt app
#    - Used for kiosks, appliances
#    - Fastest performance
#    - Command: ./myapp -platform eglfs
#
# 2. wayland: Runs as Wayland client (under Weston)
#    - Multiple windows, standard desktop
#    - Use for multi-app systems
#    - Command: ./myapp -platform wayland
#
# 3. linuxfb: Legacy framebuffer (software rendering, SLOW)
#    - Don't use this
#
# i.MX8MP: Use wayland (you have Weston from Stage 5)
#

#
# LEARNING SECTION 7: Qt Development Workflow
# ----------------------------------------------------------------------------
# Two ways to develop Qt apps for embedded:
#
# METHOD 1: Cross-compile on PC
#   1. Build SDK: $ bitbake icathya-image-qt -c populate_sdk
#   2. Install SDK on PC
#   3. Write Qt app in Qt Creator (on PC)
#   4. Compile with SDK cross-compiler
#   5. Deploy to board
#
# METHOD 2: Develop on-board (slow, not recommended)
#   1. Add development tools to image (gcc, make, qt6-dev packages)
#   2. Write code directly on board
#   3. Compile on board (slow)
#
# Recommended: METHOD 1 (cross-compile)
#

#
# LEARNING SECTION 8: Qt Quick vs Qt Widgets
# ----------------------------------------------------------------------------
# Two ways to create Qt UIs:
#
# Qt Widgets (traditional):
#   - C++ classes (QPushButton, QLineEdit, QLabel, etc.)
#   - Imperative programming
#   - Good for: Desktop-style apps, complex layouts
#   - Example industries: Industrial HMI, medical devices
#
# Qt Quick / QML (modern):
#   - Declarative language (like HTML/CSS/JavaScript)
#   - Hardware-accelerated rendering
#   - Good for: Touch interfaces, animations, modern UIs
#   - Example industries: Automotive, consumer electronics
#
# i.MX8MP: Both work, but Qt Quick leverages the GPU better
#

# Inherit the populate_sdk_qt6 class for SDK generation
inherit populate_sdk_qt6

# Increase rootfs size significantly for Qt6 libraries
IMAGE_ROOTFS_SIZE = "65536"

#
# LEARNING NOTES:
# ----------------------------------------------------------------------------
# SIZE INCREASE: ~600-800 MB → ~900 MB - 1.2 GB
# BUILD TIME INCREASE: Qt6 is HUGE (+1-2 hours first build)
#
# WHY SO LARGE:
#   - Qt6 base libraries: ~150 MB
#   - Qt6 QML modules: ~100 MB
#   - Qt6 multimedia: ~50 MB
#   - Fonts, resources, examples: ~50 MB
#   - Total: 300-400 MB just for Qt!
#
# TESTING EXERCISES:
#
# 1. Check Qt installation:
#    $ ls /usr/bin/qt*
#    $ qmake -query
#
# 2. Run Qt6 demo applications:
#    # From SSH (set WAYLAND_DISPLAY):
#    $ export WAYLAND_DISPLAY=wayland-0
#    $ export QT_QPA_PLATFORM=wayland
#
#    # Find Qt examples:
#    $ ls /usr/share/examples/
#
#    # Run a demo (example):
#    $ /usr/share/examples/quick/demos/samegame/samegame -platform wayland
#
# 3. Test Qt multimedia:
#    # Qt6 video player example (if available):
#    $ ls /usr/share/examples/multimedia/
#
# 4. Test virtual keyboard (for touchscreen):
#    # Qt apps should automatically show keyboard when text input focused
#
# 5. Generate SDK for development:
#    # On your build machine:
#    $ bitbake icathya-image-qt -c populate_sdk
#    # SDK will be at: tmp/deploy/sdk/
#    # Install on PC: $ ./fsl-imx-xwayland-glibc-x86_64-icathya-image-qt-armv8a-imx8mpevk-toolchain-*.sh
#
# 6. Create a simple Qt6 app (on PC with SDK):
#    // main.cpp
#    #include <QApplication>
#    #include <QPushButton>
#    int main(int argc, char **argv) {
#        QApplication app(argc, argv);
#        QPushButton btn("Hello i.MX8MP!");
#        btn.show();
#        return app.exec();
#    }
#
#    # Compile with SDK:
#    $ source /opt/fsl-imx-xwayland/*/environment-setup-*
#    $ $CXX main.cpp -o hello-qt $(pkg-config --cflags --libs Qt6Widgets)
#
#    # Copy to board and run:
#    $ scp hello-qt root@<board-ip>:~/
#    $ ssh root@<board-ip>
#    # export QT_QPA_PLATFORM=wayland
#    # ./hello-qt -platform wayland
#
# TROUBLESHOOTING:
#
# Qt apps don't show:
#   - Check WAYLAND_DISPLAY: $ echo $WAYLAND_DISPLAY
#   - Set platform: $ export QT_QPA_PLATFORM=wayland
#   - Check Weston is running: $ ps aux | grep weston
#
# "Could not find the Qt platform plugin":
#   - Qt can't find platform plugins
#   - Check: $ ls /usr/lib/qt6/plugins/platforms/
#   - Should have: libqwayland.so
#
# Poor Qt performance:
#   - Make sure using Wayland, not software rendering
#   - Check GPU: $ es2gears  # Should be smooth
#
# PRODUCTION OPTIMIZATION:
#
# This image is LARGE. For production:
#   1. Remove Qt modules you don't use
#   2. Remove examples and demos
#   3. Remove development headers/tools
#   4. Use UPX to compress executables
#
# Example optimization (in production image):
#   IMAGE_INSTALL:remove = "qtquickcontrols2-examples qtmultimedia-examples"
#   TOOLCHAIN_TARGET_TASK:remove = "packagegroup-qt6-sdk"
#
