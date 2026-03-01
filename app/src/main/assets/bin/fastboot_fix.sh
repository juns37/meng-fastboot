#!/system/bin/sh

cd "$(dirname "$0")"

IMG_DIR="/sdcard/fastboot"

BOOT_IMG="$IMG_DIR/boot.img"
VENDOR_BOOT_IMG="$IMG_DIR/vendor_boot.img"

echo "Checking fastboot device..."

DEVICE=$(./fastboot devices | awk '{print $1}')

if [ -z "$DEVICE" ]; then
    echo "No fastboot device detected."
    exit 1
fi

echo "Device detected: $DEVICE"
echo "Checking image files..."

if [ ! -f "$BOOT_IMG" ]; then
    echo "boot.img not found in /sdcard/fastboot/"
    exit 1
fi

if [ ! -f "$VENDOR_BOOT_IMG" ]; then
    echo "vendor_boot.img not found in /sdcard/fastboot/"
    exit 1
fi

echo "Flashing boot_ab..."
./fastboot flash boot_ab "$BOOT_IMG" || exit 1

echo "Flashing vendor_boot_ab..."
./fastboot flash vendor_boot_ab "$VENDOR_BOOT_IMG" || exit 1

echo "Flash completed successfully."