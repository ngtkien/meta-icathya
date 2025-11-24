# Board-Specific Notes

This document captures hardware-specific configurations, quirks, and differences between the NXP i.MX8MP EVK and Variscite VAR-DART boards.

---

## NXP i.MX8M Plus LPDDR4 EVK

### Hardware Overview

**Official Name:** MCIMX8M-PLUS-EVK  
**SoC:** i.MX8M Plus (quad-core Cortex-A53 @ 1.8 GHz)  
**RAM:** 4GB LPDDR4  
**Storage:** microSD, eMMC socket (optional)  
**Display:** HDMI, MIPI-DSI (optional)  

### Yocto Configuration

**MACHINE:** `imx8mpevk`

**BSP Layers:**
- `meta-freescale` (community)
- `meta-imx` (NXP official)

### Boot Configuration

**Bootloader:** U-Boot (NXP fork)  
**Boot Media:** microSD card (default)

**U-Boot Environment:**
```bash
# Check U-Boot version:
# (in U-Boot console)
=> version

# Default boot command:
=> printenv bootcmd
```

**Kernel Boot Args:**
```bash
# Check from booted system:
$ cat /proc/cmdline
```

### Display Configuration

**HDMI:**
- Connector: J5
- Supported Resolutions: 1080p, 720p, 4K (check device tree)
- Default: Usually 1080p60

**MIPI-DSI:**
- Connector: J17 (requires optional display panel)
- Check `meta-imx` for device tree overlays

**Framebuffer Device:**
```bash
$ ls /dev/fb*
# Should show: /dev/fb0
```

### WiFi/Bluetooth

**Configuration:** Depends on EVK revision

**Option 1:** On-board WiFi/BT module
- Check board revision and schematic

**Option 2:** M.2 module slot
- Supports standard M.2 E-key WiFi/BT modules
- Common: Intel AX200, Broadcom modules

**Firmware Location:**
```bash
$ ls /lib/firmware/ | grep -i wifi
$ dmesg | grep -i firmware
```

### Audio

**Codec:** WM8960 (Wolfson/Cirrus Logic)

**ALSA Device:**
```bash
$ aplay -l
# Look for: wm8960-audio
```

**Testing:**
```bash
# Playback test:
$ speaker-test -c 2 -t wav

# Capture test:
$ arecord -d 5 test.wav
$ aplay test.wav
```

### I2C Buses

```bash
$ i2cdetect -l
# Common buses:
# i2c-0: Main I2C bus
# i2c-1: PMIC (power management)
# i2c-2: HDMI
# i2c-3: Camera/expansion
```

**Common I2C Devices:**
```bash
$ i2cdetect -y 0
# 0x1a: Audio codec (WM8960)
# 0x25: PMIC
# (depends on board configuration)
```

### GPIO

**Accessing GPIOs:**
```bash
$ gpiodetect
# Should show: gpiochip0, gpiochip1, etc.

# Example: Control LED (check schematic for GPIO number)
$ gpioset gpiochip0 <pin>=1
```

### Known Issues

1. **HDMI Display Not Detected:**
   - Some monitors don't work on first boot
   - Workaround: Power cycle the monitor or try different cable

2. **WiFi/BT Firmware Missing:**
   - Depends on module installed
   - Check `dmesg | grep firmware` for missing files

3. **Audio Pops/Clicks:**
   - Power management may cause artifacts
   - Disable power saving: `amixer set 'Master' unmute`

### Device Tree

**Location:** `/boot/<dtb-file>`

**DTB Files:**
```bash
$ ls /boot/*.dtb
# imx8mp-evk.dtb (base)
# imx8mp-evk-usdhc1-m2.dtb (with M.2)
# (and variants)
```

**Changing DTB:**
```
# Edit /boot/extlinux/extlinux.conf or U-Boot bootargs
```

### Reference Documents

- [NXP i.MX8MP Product Page](https://www.nxp.com/products/processors-and-microcontrollers/arm-processors/i-mx-applications-processors/i-mx-8-processors/i-mx-8m-plus-arm-cortex-a53-machine-learning-vision-multimedia-and-industrial-iot:IMX8MPLUS)
- [EVK Hardware User Guide](https://www.nxp.com/docs/en/user-guide/IMX8MPEVKHUG.pdf)
- [meta-imx Documentation](https://github.com/nxp-imx/meta-imx)

---

## Variscite VAR-SOM-MX8M-PLUS (VAR-DART)

### Hardware Overview

**SoM:** VAR-SOM-MX8M-PLUS  
**Carrier Board:** VAR-DT8MCustomBoard (or other Variscite carrier)  
**SoC:** i.MX8M Plus (quad-core Cortex-A53 @ 1.8 GHz)  
**RAM:** 2GB/4GB/8GB LPDDR4 (depends on variant)  
**Storage:** eMMC (on-board), microSD

### Yocto Configuration

**MACHINE:** `imx8mp-var-dart`

**BSP Layers:**
- `meta-variscite-bsp-imx` (board support)
- `meta-variscite-sdk-imx` (SDK recipes)
- `meta-imx` (NXP base)

### Boot Configuration

**Bootloader:** U-Boot (Variscite customized)  
**Boot Media:** eMMC (primary), microSD (secondary)

**Boot Sequence:**
1. BootROM → SPL (Secondary Program Loader) → U-Boot
2. U-Boot loads kernel from eMMC or SD

**Flashing to eMMC:**
```bash
# From SD card boot:
# Use Variscite's flash script
$ /usr/bin/install_yocto.sh
```

### Display Configuration

**HDMI:**
- Available on carrier board (check schematic)
- Default resolution: 1080p60

**LVDS:**
- Some Variscite carrier boards support dual-channel LVDS
- Requires device tree configuration
- Check `meta-variscite-bsp-imx/recipes-kernel/linux/` for dts files

**Device Tree Selection:**
```bash
# U-Boot selects DTB automatically based on board detection
# Or manually set in U-Boot:
=> setenv fdt_file imx8mp-var-dart-dt8mcustomboard.dtb
=> saveenv
```

### WiFi/Bluetooth

**Module:** Sterling-LWB (Laird Connectivity / Ezurio)  
**Chipset:** Qualcomm QCA9377 or similar (check module marking)

**Firmware:**
```bash
# Variscite provides proprietary firmware in meta-variscite-bsp-imx
$ ls /lib/firmware/qca*
```

**WiFi Interface:** Usually `wlan0`

**Bluetooth Interface:** Usually `hci0`

**Testing:**
```bash
# WiFi:
$ ip link show wlan0
$ iw dev wlan0 scan

# Bluetooth:
$ hciconfig hci0 up
$ bluetoothctl
```

### Audio

**Codec:** Varies by carrier board  
**Common:** WM8904, TLV320AIC3x (check carrier board schematic)

**ALSA Device:**
```bash
$ aplay -l
# Output depends on carrier board
```

### I2C Buses

```bash
$ i2cdetect -l
# i2c-0: SoM I2C (EEPROM, sensors)
# i2c-1: Carrier board expansion
# i2c-2: HDMI
# (varies by carrier board)
```

**SoM EEPROM:**
```bash
# Variscite stores SoM info in EEPROM at address 0x50-0x57
$ i2cdump -y 0 0x52
```

### GPIO

**Variscite Naming:**
- Check carrier board schematic for GPIO assignments
- Many GPIOs are named (e.g., "GPIO_LED", "GPIO_BUTTON")

```bash
$ gpiofind "GPIO_LED"
# Returns: gpiochipX lineY
```

### Known Issues

1. **Boot from eMMC vs SD Card:**
   - Boot priority: eMMC > SD card
   - To force SD boot: Remove eMMC or hold boot select button

2. **Display Not Working:**
   - Check device tree selection (`fdt_file` in U-Boot)
   - Different carrier boards use different DTBs

3. **Sterling-LWB Not Detected:**
   - Check firmware installation
   - Verify WiFi/BT enable pins (controlled by GPIO)
   - `dmesg | grep -i qca` should show module detection

4. **Different RAM Sizes:**
   - Variscite SoMs come in 2GB/4GB/8GB variants
   - U-Boot auto-detects, but verify: `free -m`

### Device Tree Files

**Location in BSP:**
```
meta-variscite-bsp-imx/recipes-kernel/linux/linux-variscite/
```

**Common DTBs:**
- `imx8mp-var-dart-dt8mcustomboard.dtb` (standard carrier)
- `imx8mp-var-som-symphony.dtb` (Symphony carrier)
- (Check your specific carrier board)

**Modifying Device Tree:**
1. Edit `.dts` file in `meta-variscite-bsp-imx`
2. Create `.bbappend` for `linux-variscite_%.bbappend`
3. Rebuild kernel: `bitbake -c compile -f linux-variscite`

### Variscite-Specific Tools

**Recovery Mode:**
- Used for low-level eMMC flashing
- Requires UUU (NXP Universal Update Utility)

**EEPROM Programming:**
- Variscite provides scripts for SoM EEPROM

### Reference Documents

- [Variscite i.MX8MP Product Page](https://www.variscite.com/product/system-on-module-som/cortex-a53-krait/var-som-mx8m-plus-nxp-i-mx-8m-plus/)
- [VAR-SOM-MX8M-PLUS Datasheet](https://www.variscite.com/wp-content/uploads/2021/03/VAR-SOM-MX8M-PLUS_Datasheet.pdf)
- [meta-variscite-bsp-imx GitHub](https://github.com/varigit/meta-variscite-bsp-imx)
- [Variscite Wiki](https://variwiki.com/index.php?title=DART-MX8M-PLUS)

---

## Comparison: NXP EVK vs Variscite VAR-DART

| Feature | NXP EVK | Variscite VAR-DART |
|---------|---------|---------------------|
| **MACHINE** | `imx8mpevk` | `imx8mp-var-dart` |
| **Storage** | microSD (primary) | eMMC (primary), SD (secondary) |
| **WiFi/BT** | Depends on SKU/module | Sterling-LWB (on-board) |
| **Display** | HDMI, MIPI-DSI | HDMI, LVDS (carrier-dependent) |
| **Audio Codec** | WM8960 | Carrier-dependent (WM8904, TLV320) |
| **BSP Layer** | `meta-imx` | `meta-variscite-bsp-imx` |
| **U-Boot** | NXP stock | Variscite customized |
| **Device Tree** | Standard NXP DTs | Variscite custom DTs |
| **EEPROM** | No SoM EEPROM | SoM EEPROM (board info) |
| **Form Factor** | Dev board | SoM + Carrier |

### When to Use Which Board?

**NXP EVK:**
- Quick prototyping
- Learning NXP BSP
- Testing NXP reference software
- No production intent

**Variscite VAR-DART:**
- Production-ready designs
- Custom carrier boards
- Industrial temperature range
- Long-term availability (Variscite guarantees 15+ years)

---

## Common Debugging Steps (Both Boards)

### Boot Issues

1. **No Boot:**
   ```bash
   # Check U-Boot console (UART)
   # Baud: 115200, 8N1
   ```

2. **Kernel Panic:**
   ```bash
   # Check kernel command line: console=ttymxc1,115200
   # Check rootfs device: root=/dev/mmcblk1p2 (or mmcblk2p2 for eMMC)
   ```

### Display Issues

1. **No HDMI Output:**
   ```bash
   # From SSH, check framebuffer:
   $ cat /sys/class/graphics/fb0/modes
   
   # Try forcing mode in U-Boot:
   => setenv bootargs 'video=HDMI-A-1:1920x1080@60'
   ```

2. **Weston Won't Start:**
   ```bash
   $ journalctl -u weston
   $ weston --backend=drm-backend.so --log=/tmp/weston.log
   ```

### WiFi Issues

1. **Interface Not Found:**
   ```bash
   $ dmesg | grep -i wifi
   $ dmesg | grep -i firmware
   # Check if firmware is loaded
   ```

2. **Can't Connect:**
   ```bash
   # Manual WPA2 connection:
   $ wpa_passphrase "MySSID" "password" > /etc/wpa.conf
   $ wpa_supplicant -i wlan0 -c /etc/wpa.conf -B
   $ dhclient wlan0
   ```

### Performance Issues

1. **CPU Frequency:**
   ```bash
   $ cat /sys/devices/system/cpu/cpu*/cpufreq/scaling_cur_freq
   # Should show 1800000 (1.8 GHz) under load
   ```

2. **GPU Frequency:**
   ```bash
   $ cat /sys/class/devfreq/38000000.gpu/cur_freq
   ```

3. **Temperature:**
   ```bash
   $ cat /sys/class/thermal/thermal_zone*/temp
   # Values in millidegrees Celsius
   ```

---

## Your Notes

### Custom Device Tree Modifications

```
Document any device tree changes you made:


```

### Custom U-Boot Configuration

```
Document any U-Boot environment changes:


```

### Production Optimizations

```
Document any optimizations for your product:


```
