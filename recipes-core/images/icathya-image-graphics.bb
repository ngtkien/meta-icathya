# Copyright (C) 2025 meta-icathya
# Released under the MIT license

SUMMARY = "Icathya Stage 5: Graphics and display with Wayland/Weston"
DESCRIPTION = "Adds Wayland compositor (Weston) and GPU acceleration. First stage with \
a graphical user interface and desktop environment."

LICENSE = "MIT"

# Inherit from Stage 4 - we get all multimedia support
require icathya-image-multimedia.bb

SUMMARY = "Icathya Stage 5: Graphics and display with Wayland/Weston"

#
# LEARNING SECTION 1: Display Server Architecture - X11 vs Wayland
# ----------------------------------------------------------------------------
# Two main Linux display technologies:
#
# X11 (X Window System):
#   - Legacy (1984!)
#   - Complex, many layers (X server, window manager, compositor)
#   - Network-transparent (can run app on machine A, display on machine B)
#   - High overhead, security issues
#   - NOT recommended for new i.MX8/9 projects (NXP deprecated it)
#
# Wayland:
#   - Modern (2008)
#   - Simpler architecture (compositor = display server + window manager)
#   - Better performance, lower latency
#   - Better security (apps can't spy on each other)
#   - Direct hardware access (GPU accelerated)
#   - Designed for embedded systems
#
# i.MX8MP: Use Wayland (via Weston compositor)
#

#
# LEARNING SECTION 2: What is Weston?
# ----------------------------------------------------------------------------
# Weston = Reference Wayland compositor (developed by Wayland project)
#
# Think of it like:
#   - Windows Desktop = explorer.exe
#   - macOS Desktop = Finder
#   - Linux Desktop = Weston (or GNOME Shell, KDE Plasma, etc.)
#
# Weston provides:
#   1. Display management (Wayland protocol implementation)
#   2. Window management (moving, resizing, switching)
#   3. Basic desktop shell (panel, background, launcher)
#   4. Input handling (keyboard, mouse, touch)
#
# For embedded: Weston is lightweight and well-tested on i.MX platforms.
#

#
# LEARNING SECTION 3: GPU Driver Stack for i.MX8MP
# ----------------------------------------------------------------------------
# i.MX8MP GPU: Vivante GC7000L (OpenGL ES 3.1, Vulkan 1.1)
#
# Driver stack (from bottom to top):
#   1. Kernel: etnaviv (open source) or Vivante (proprietary)
#   2. Userspace driver: libGAL, libVSC (Vivante proprietary)
#   3. API libraries: libGLES, libEGL (OpenGL ES, EGL)
#   4. Wayland integration: wayland-egl
#
# NXP BSP uses PROPRIETARY Vivante drivers (better performance than etnaviv).
# These are already in meta-imx/meta-variscite-bsp-imx.
#

#
# LEARNING SECTION 4: Adding Weston and GPU Support
# ----------------------------------------------------------------------------
# IMAGE_FEATURES:
#   - weston: Adds Weston compositor and its dependencies
#   - splash: Boot splash screen (useful for product demos)
#
# This automatically brings in:
#   - Weston compositor
#   - Wayland libraries
#   - Basic Weston clients (weston-terminal, weston-flower, etc.)
#
IMAGE_FEATURES += " \
    weston \
    splash \
"

#
# LEARNING SECTION 5: GPU Tools and Packagegroups
# ----------------------------------------------------------------------------
# packagegroup-fsl-tools-gpu: NXP's GPU testing tools
#   - Specific to Vivante GPU
#   - Hardware-accelerated OpenGL ES demos
#
# packagegroup-fsl-tools-gpu-external: Community GPU tools
#   - glmark2: OpenGL ES 2.0 benchmark (measures FPS)
#   - es2gears: Simple spinning gears demo (classic GL test)
#   - kmscube: Low-level DRM/KMS test (doesn't need Wayland)
#
IMAGE_INSTALL += " \
    packagegroup-fsl-tools-gpu \
    packagegroup-fsl-tools-gpu-external \
"

#
# LEARNING SECTION 6: Additional Wayland Clients (for testing)
# ----------------------------------------------------------------------------
# Basic Wayland test applications:
#   - weston-terminal: Terminal emulator (runs in Weston)
#   - weston-editor: Simple text editor
#   - weston-flower: Demo app (shows Wayland rendering)
#   - wayland-utils: wayland-info (like xdpyinfo for X11)
#
# These are automatically included with 'weston' IMAGE_FEATURE,
# but we list them here for clarity.
#

#
# LEARNING SECTION 7: DRM/KMS - Direct Rendering Manager / Kernel Mode Setting
# ----------------------------------------------------------------------------
# DRM/KMS: Low-level kernel subsystem for graphics
#
# DRM (Direct Rendering Manager):
#   - Kernel interface for GPU access
#   - Provides memory management, command submission
#
# KMS (Kernel Mode Setting):
#   - Kernel interface for display configuration
#   - Sets resolution, refresh rate, pixel format
#   - Replaces old fbdev (framebuffer device)
#
# Tools:
#   - modetest: Test display modes, connectors, CRTCs
#   - drm-tests: Additional DRM test utilities
#
IMAGE_INSTALL += " \
    libdrm \
    libdrm-tests \
"

#
# LEARNING SECTION 8: OpenGL ES vs Desktop OpenGL
# ----------------------------------------------------------------------------
# OpenGL ES (Embedded Systems):
#   - Subset of desktop OpenGL
#   - Designed for mobile/embedded (lower power)
#   - i.MX8MP supports: OpenGL ES 2.0, 3.0, 3.1
#
# Desktop OpenGL:
#   - NOT supported on i.MX8MP
#   - Don't try to run desktop OpenGL apps (they'll fail)
#
# For development: Use OpenGL ES APIs
# For porting desktop apps: Use ANGLE or Zink (GL-to-GLES translation)
#

# Increase rootfs size for graphics libraries
IMAGE_ROOTFS_SIZE = "49152"

#
# LEARNING NOTES:
# ----------------------------------------------------------------------------
# SIZE INCREASE: ~400-600 MB → ~600-800 MB
# BUILD TIME INCREASE: Graphics stack is complex (+45-90 min)
#
# IMPORTANT: You now have a GRAPHICAL interface!
# When you boot, you should see:
#   1. Boot splash (if configured)
#   2. Weston desktop with wallpaper
#   3. Mouse cursor, panel (if configured)
#
# TESTING EXERCISES:
#
# 1. Basic Weston test:
#    # Boot the board, connect HDMI monitor
#    # Weston should auto-start (configured by systemd)
#    # You should see a desktop background
#
# 2. Launch Weston terminal:
#    # Click on terminal icon, or from SSH:
#    $ weston-terminal &
#
# 3. GPU benchmark:
#    # From Weston terminal or SSH (with WAYLAND_DISPLAY set):
#    $ export WAYLAND_DISPLAY=wayland-0
#    $ glmark2-es2-wayland
#    # Should show ~60 FPS for simple scenes
#
# 4. OpenGL ES gears demo:
#    $ es2gears &
#    # Should show spinning gears
#
# 5. Check Wayland compositor:
#    $ wayland-info
#    # Shows Wayland compositor info, supported formats
#
# 6. Low-level DRM test (no Wayland needed):
#    $ modetest -M mxsfb
#    # Shows display connectors, modes, CRTCs
#
# 7. kmscube (raw DRM/KMS, no Wayland):
#    $ kmscube
#    # Should show spinning cube on HDMI
#
# 8. GStreamer video with Wayland sink:
#    # Now we can use waylandsink instead of fbdevsink!
#    $ gst-launch-1.0 filesrc location=test.mp4 ! qtdemux ! vpudec ! waylandsink
#
# TROUBLESHOOTING:
#
# If Weston doesn't start:
#   1. Check logs: $ journalctl -u weston
#   2. Check GPU: $ ls /dev/dri/card*
#   3. Try manual start: $ weston --backend=drm-backend.so
#
# If display is blank:
#   - Check HDMI cable connection
#   - Try different HDMI port/monitor
#   - Check kernel boot args for video= settings
#   - Run: $ modetest -M mxsfb  # See if display is detected
#
# If glmark2 shows low FPS:
#   - Check if using hardware acceleration: $ glxinfo (should show Vivante)
#   - Check GPU frequency: $ cat /sys/class/devfreq/38000000.gpu/cur_freq
#
# BOARD-SPECIFIC NOTES:
#
# NXP i.MX8MP EVK:
#   - HDMI output on connector J5
#   - Can also use MIPI-DSI (with optional display)
#
# Variscite VAR-DART:
#   - HDMI output (check carrier board schematic)
#   - May support LVDS (check device tree)
#   - Some carrier boards have touchscreen
#
