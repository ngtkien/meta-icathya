# Copyright (C) 2025 meta-icathya
# Released under the MIT license

SUMMARY = "Icathya Stage 4: Multimedia support with hardware acceleration"
DESCRIPTION = "Adds GStreamer framework with NXP VPU (Video Processing Unit) hardware \
acceleration for video encoding/decoding. Still console-only (no GUI yet)."

LICENSE = "MIT"

# Inherit from Stage 3 - we get all hardware support
require icathya-image-hw.bb

SUMMARY = "Icathya Stage 4: Multimedia support with hardware acceleration"

#
# LEARNING SECTION 1: What is the VPU?
# ----------------------------------------------------------------------------
# VPU = Video Processing Unit (hardware video encoder/decoder)
#
# The i.MX8MP has a powerful VPU that can:
#   - Decode: H.264, H.265/HEVC, VP9, VP8 (in hardware)
#   - Encode: H.264, H.265/HEVC (in hardware)
#
# Benefits of hardware acceleration:
#   1. MUCH faster than software (30x-100x speedup)
#   2. Lower CPU usage (CPU stays idle while VPU works)
#   3. Lower power consumption
#
# Without VPU, a 1080p H.264 video would max out the CPU.
# With VPU, CPU usage is <5% for the same video.
#

#
# LEARNING SECTION 2: GStreamer Architecture
# ----------------------------------------------------------------------------
# GStreamer = Multimedia framework (like FFmpeg, but plugin-based)
#
# Architecture:
#   - Pipeline: Chain of elements (source → decoder → sink)
#   - Elements: Plugins that do one thing (demux, decode, scale, encode, etc.)
#   - Pads: Connection points between elements
#
# Example pipeline:
#   filesrc → qtdemux → vpudec → videoconvert → fbdevsink
#     ^         ^         ^          ^             ^
#     |         |         |          |             |
#   Read file  Parse MP4  VPU decode  Color conv   Display
#
# NXP provides custom plugins (vpudec, vpuenc) that use the VPU hardware.
#

#
# LEARNING SECTION 3: GStreamer Package Groups
# ----------------------------------------------------------------------------
# packagegroup-fsl-gstreamer1.0: Base GStreamer for NXP
#   - Core GStreamer libraries
#   - NXP-specific plugins (VPU, GPU, IPU)
#
# packagegroup-fsl-gstreamer1.0-full: Extended plugin set
#   - All the plugins from base group
#   - Additional codecs and formats
#   - Network streaming (RTSP, RTP)
#   - More demuxers/muxers
#
IMAGE_INSTALL += " \
    packagegroup-fsl-gstreamer1.0 \
    packagegroup-fsl-gstreamer1.0-full \
"

#
# LEARNING SECTION 4: Audio Framework
# ----------------------------------------------------------------------------
# For multimedia, we need more than just ALSA utilities.
#
# PulseAudio: Sound server that sits on top of ALSA
#   - Allows multiple applications to play audio simultaneously
#   - Per-application volume control
#   - Network audio streaming
#
# Alternative: PipeWire (newer, but PulseAudio is more mature)
#
IMAGE_INSTALL += " \
    pulseaudio \
    pulseaudio-server \
    pulseaudio-module-loopback \
"

#
# LEARNING SECTION 5: Useful Multimedia Utilities
# ----------------------------------------------------------------------------
# Tools for testing and debugging multimedia:
#
# v4l-utils: Video4Linux utilities
#   - v4l2-ctl: Query/control cameras and capture devices
#   - Test cameras, HDMI input, etc.
#
# gstreamer1.0-plugins-bad/ugly: Additional codec plugins
#   - These have licensing issues or code quality concerns
#   - But they're needed for many common formats (MP3, AAC, etc.)
#
# gst-examples: Sample GStreamer applications
#   - Useful for testing pipelines
#
IMAGE_INSTALL += " \
    v4l-utils \
    gstreamer1.0-plugins-bad \
    gstreamer1.0-plugins-ugly \
"

#
# LEARNING SECTION 6: Testing and Benchmarking
# ----------------------------------------------------------------------------
# packagegroup-fsl-tools-benchmark: NXP's benchmark tools
#   - Useful for comparing software vs hardware performance
#
# Note: We can still test video decode at this stage even without a GUI!
# Methods:
#   1. Decode to framebuffer (/dev/fb0)
#   2. Decode and save as raw YUV
#   3. Decode and benchmark (measure FPS)
#
IMAGE_INSTALL += " \
    packagegroup-fsl-tools-benchmark \
"

#
# LEARNING SECTION 7: Understanding Codecs and Licensing
# ----------------------------------------------------------------------------
# Video codec licensing is COMPLEX:
#
# FREE CODECS (no patent issues):
#   - VP8, VP9: Google (patent-free)
#   - AV1: Next-gen (patent-free)
#
# LICENSED CODECS (may require patent licensing):
#   - H.264/AVC: Widely used, requires MPEG-LA license for commercial use
#   - H.265/HEVC: Better compression, more expensive licensing
#
# For personal/development: Usually OK
# For commercial products: Consult legal team
#
# The VPU can decode these formats, but DISTRIBUTING products that use
# them may require paying licensing fees to MPEG-LA.
#

# Increase rootfs size for multimedia libraries
IMAGE_ROOTFS_SIZE = "32768"

#
# LEARNING NOTES:
# ----------------------------------------------------------------------------
# SIZE INCREASE: ~250-350 MB → ~400-600 MB
# BUILD TIME INCREASE: GStreamer has many dependencies (+30-60 min)
#
# WHAT YOU CAN DO NOW (even without GUI):
#
# 1. Hardware-accelerated video decode:
#    # Decode H.264 to framebuffer (shows on HDMI if console is on HDMI)
#    $ gst-launch-1.0 filesrc location=test.mp4 ! qtdemux ! vpudec ! fbdevsink
#
#    # Decode and examine performance
#    $ gst-launch-1.0 filesrc location=test.mp4 ! qtdemux ! vpudec ! fakesink
#
#    # Decode and save as raw YUV
#    $ gst-launch-1.0 filesrc location=test.mp4 ! qtdemux ! vpudec ! \
#          videoconvert ! filesink location=output.yuv
#
# 2. Check VPU plugin availability:
#    $ gst-inspect-1.0 vpudec
#    $ gst-inspect-1.0 vpuenc
#
# 3. List all available plugins:
#    $ gst-inspect-1.0
#
# 4. Test audio playback:
#    $ gst-launch-1.0 filesrc location=test.mp3 ! mpegaudioparse ! mpg123audiodec ! \
#          audioconvert ! pulsesink
#
# 5. Benchmark software vs hardware decode:
#    # Software decode (uses CPU):
#    $ time gst-launch-1.0 filesrc location=test.mp4 ! qtdemux ! avdec_h264 ! fakesink
#
#    # Hardware decode (uses VPU):
#    $ time gst-launch-1.0 filesrc location=test.mp4 ! qtdemux ! vpudec ! fakesink
#
#    Compare CPU usage with 'htop' in another terminal!
#
# 6. Camera testing (if camera is connected):
#    $ v4l2-ctl --list-devices
#    $ gst-launch-1.0 v4l2src device=/dev/video0 ! fbdevsink
#
# TROUBLESHOOTING:
#
# If VPU doesn't work:
#   1. Check kernel driver: $ ls /dev/mxc_hantro*
#   2. Check firmware: $ ls /lib/firmware/ | grep vpu
#   3. Check dmesg: $ dmesg | grep -i vpu
#
# If no video output on framebuffer:
#   - Console might be using framebuffer
#   - Try switching to a different VT (Ctrl+Alt+F2)
#   - Or disable console on framebuffer in kernel args
#
