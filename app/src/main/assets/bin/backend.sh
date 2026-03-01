#!/system/bin/sh

FASTBOOT="/system/bin/fastboot"
echo "=== BACKEND V2 AKTIF ==="
echo "[MengTools] Mengecek device fastboot..."

DEVICE=$($FASTBOOT devices 2>/dev/null | awk '{print $1}')

if [ -z "$DEVICE" ]; then
    echo "[MengTools] Device tidak terdeteksi di mode fastboot."
    exit 1
fi

echo "[MengTools] Device terdeteksi: $DEVICE"
echo "[MengTools] Rebooting ke Recovery..."

su -c "$FASTBOOT reboot recovery"