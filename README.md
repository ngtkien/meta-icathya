# meta-icathya

**Progressive Embedded Linux Learning Layer for NXP i.MX8MP Boards**

## Overview

`meta-icathya` is a custom Yocto layer designed for learning embedded Linux development through progressive image building. This layer contains a series of image recipes that build upon each other, teaching Yocto concepts from minimal bootable systems to full multimedia/graphics platforms.

## Target Hardware

- **NXP i.MX8M Plus LPDDR4 EVK** (`MACHINE=imx8mpevk`)
- **Variscite VAR-SOM-MX8M-PLUS / VAR-DART** (`MACHINE=imx8mp-var-dart`)

## Learning Progression

This layer provides 6 progressive image recipes:

| Stage | Image Recipe | Size | Key Concepts |
|-------|--------------|------|--------------|
| 1 | `icathya-image-minimal` | ~50-100 MB | Boot, SSH, core packages |
| 2 | `icathya-image-base` | ~150-200 MB | Full cmdline tools, packagegroups |
| 3 | `icathya-image-hw` | ~250-350 MB | WiFi/BT, firmware, hardware support |
| 4 | `icathya-image-multimedia` | ~400-600 MB | GStreamer, VPU, hardware codecs |
| 5 | `icathya-image-graphics` | ~600-800 MB | Wayland/Weston, GPU, EGL/DRM |
| 6 | `icathya-image-qt` | ~900MB-1.2GB | Qt6 framework, SDK generation |

## Dependencies

This layer depends on:

- `poky` (Yocto core)
- `meta-freescale` / `meta-imx` (NXP BSP)
- `meta-variscite-bsp-imx` (Variscite BSP)
- `meta-variscite-sdk-imx` (Variscite SDK)
- `meta-openembedded` (additional packages)

## Quick Start

### 1. Add Layer to Build

```bash
cd /mnt/d/yocto-project
source var-setup-release.sh -b build-icathya
bitbake-layers add-layer ../sources/meta-icathya
```

### 2. Build Stage 1 (Minimal Image)

```bash
# For NXP EVK
MACHINE=imx8mpevk DISTRO=fsl-imx-xwayland source var-setup-release.sh -b build-icathya
bitbake icathya-image-minimal

# For Variscite VAR-DART
MACHINE=imx8mp-var-dart DISTRO=fsl-imx-xwayland source var-setup-release.sh -b build-icathya
bitbake icathya-image-minimal
```

### 3. Flash and Test

```bash
# Flash to SD card (replace /dev/sdX with your SD card device)
zstd -d tmp/deploy/images/imx8mpevk/icathya-image-minimal-imx8mpevk.wic.zst
sudo dd if=icathya-image-minimal-imx8mpevk.wic of=/dev/sdX bs=4M status=progress conv=fsync
```

## Image Recipes

### Stage 1: icathya-image-minimal

**Purpose:** Minimal bootable system with console and SSH access.

**Contents:**
- Linux kernel + minimal rootfs
- Busybox (basic shell utilities)
- SSH server (dropbear or openssh)
- Package manager (opkg)

**Learning Objectives:**
- Understand `IMAGE_INSTALL` vs `IMAGE_FEATURES`
- Learn about `packagegroup-core-boot`
- Boot to console, remote SSH access

**Build Command:**
```bash
bitbake icathya-image-minimal
```

### Stage 2: icathya-image-base

**Purpose:** Full command-line environment with complete GNU utilities.

**Additional Contents:**
- Full bash shell
- GNU coreutils (ls, cp, grep, etc.)
- Network tools (curl, wget, tcpdump)
- System monitoring (htop, iostat)

**Learning Objectives:**
- Understand packagegroups (`packagegroup-core-full-cmdline`)
- Difference between Busybox and full utilities

**Build Command:**
```bash
bitbake icathya-image-base
```

### Stage 3: icathya-image-hw

**Purpose:** Hardware support for peripherals and connectivity.

**Additional Contents:**
- WiFi/Bluetooth firmware and tools
- ALSA audio utilities
- I2C/SPI/GPIO debugging tools
- Hardware-specific drivers

**Learning Objectives:**
- BSP (Board Support Package) concepts
- Firmware loading and kernel modules
- Hardware bringup and debugging

**Build Command:**
```bash
bitbake icathya-image-hw
```

### Stage 4: icathya-image-multimedia

**Purpose:** Multimedia support with hardware-accelerated video.

**Additional Contents:**
- GStreamer 1.0 with NXP VPU plugins
- Hardware video codecs (H.264, H.265, VP9)
- Audio frameworks (PulseAudio/PipeWire)
- Video testing utilities

**Learning Objectives:**
- Hardware video acceleration (VPU)
- GStreamer pipeline architecture
- Codec licensing and restricted packages

**Build Command:**
```bash
bitbake icathya-image-multimedia
```

### Stage 5: icathya-image-graphics

**Purpose:** Graphics support with Wayland/Weston and GPU acceleration.

**Additional Contents:**
- Weston compositor (Wayland)
- Vivante GPU drivers
- EGL/OpenGL ES libraries
- GPU testing tools (glmark2, es2gears)

**Learning Objectives:**
- Wayland compositor architecture
- GPU driver stack
- DRM/KMS display management

**Build Command:**
```bash
bitbake icathya-image-graphics
```

### Stage 6: icathya-image-qt

**Purpose:** Application framework with Qt6 support.

**Additional Contents:**
- Qt6 base libraries
- Qt6 Wayland integration
- Qt6 multimedia
- Sample Qt applications

**Learning Objectives:**
- Application framework integration
- Qt licensing considerations
- SDK generation for development

**Build Command:**
```bash
bitbake icathya-image-qt
```

## Documentation

- `docs/LEARNING-NOTES.md` - Your personal learning journal
- `docs/BOARD-NOTES.md` - Board-specific configuration notes
- See [implementation_plan.md](file:///home/zeder/.gemini/antigravity/brain/2eb752b3-6de8-4000-86f3-fd25aa2d62b2/implementation_plan.md) for detailed guidance

## Directory Structure

```
meta-icathya/
├── conf/
│   └── layer.conf                    # Layer configuration
├── recipes-core/
│   ├── images/
│   │   ├── icathya-image-minimal.bb
│   │   ├── icathya-image-base.bb
│   │   ├── icathya-image-hw.bb
│   │   ├── icathya-image-multimedia.bb
│   │   ├── icathya-image-graphics.bb
│   │   └── icathya-image-qt.bb
│   └── packagegroups/
│       └── packagegroup-icathya.bb   # (Future: custom packagegroups)
├── recipes-apps/
│   └── (Future: custom application recipes)
├── docs/
│   ├── LEARNING-NOTES.md
│   └── BOARD-NOTES.md
└── README.md
```

## Tips

1. **Build Times:** First build will take 2-4 hours. Use `sstate-cache` to speed up subsequent builds.

2. **Shared State Cache:** Consider using a shared sstate directory:
   ```bash
   SSTATE_DIR = "/opt/yocto_sstates"
   ```

3. **Download Cache:** Reuse downloads across builds:
   ```bash
   DL_DIR = "/opt/yocto_downloads"
   ```

4. **Parallel Builds:** Adjust based on your system:
   ```bash
   BB_NUMBER_THREADS = "8"
   PARALLEL_MAKE = "-j 8"
   ```

## Troubleshooting

### Layer Not Found

If `bitbake-layers show-layers` doesn't show `meta-icathya`:

```bash
bitbake-layers add-layer ../sources/meta-icathya
```

### Build Failures

Check the log files:
```bash
less tmp/work/imx8mpevk-poky-linux/icathya-image-minimal/1.0-r0/temp/log.do_rootfs
```

### Board Won't Boot

1. Verify U-Boot version matches your board
2. Check device tree compatibility
3. Ensure correct `MACHINE` setting

## Support

For issues specific to:
- **NXP BSP:** Check `meta-imx` documentation
- **Variscite BSP:** Check `meta-variscite-bsp-imx` documentation
- **Yocto General:** https://docs.yoctoproject.org/

## License

MIT License (same as Yocto Project)
