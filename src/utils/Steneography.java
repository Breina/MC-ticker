package utils;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class Steneography {

    private static final byte[] SIGNATURE = "Tick".getBytes();

    public static boolean hide(BufferedImage bi, byte[] data) {
        if (bi.getWidth() * bi.getHeight() * 3 < data.length * 8 + SIGNATURE.length + 4)
            return false;

        hide(bi, SIGNATURE, 0);
        hide(bi, ByteBuffer.allocate(4).putInt(data.length).array(), SIGNATURE.length * 8);
        hide(bi, data, SIGNATURE.length * 8 + 32);

        return true;
    }

    public static void hide(BufferedImage bi, byte[] data, int imageIndex) {
        DataBuffer db = bi.getRaster().getDataBuffer();

        for (int dataIndex = 0; dataIndex < data.length; dataIndex++)
            for (byte bit = 0; bit < 8; bit++) {
                byte pixel = (byte) db.getElem(imageIndex);
                pixel = (byte) (pixel & 0xFE | getBit(data[dataIndex], bit) & 0x01);
                db.setElem(imageIndex++, pixel);
            }
    }

    public static byte[] getDataFromImage(BufferedImage bi) {

        byte[] supposedSignature = read(bi, 0, SIGNATURE.length);
        if (!Arrays.equals(supposedSignature, SIGNATURE))
            return null;

        int length = ByteBuffer.wrap(read(bi, SIGNATURE.length * 8, 4)).getInt();
        return read(bi, SIGNATURE.length * 8 + 32, length);
    }

    public static byte[] read(BufferedImage bi, int imageIndex, int length) {
        DataBuffer db = bi.getRaster().getDataBuffer();
        byte[] bytes = new byte[length];

        for (int dataIndex = 0; dataIndex < length; dataIndex++) {
            byte dataByte = 0x0;
            for (int bit = 0; bit < 8; bit++)
                dataByte = (byte) (dataByte & ~(0x01 << bit) | (db.getElem(imageIndex++) & 0x01) << bit);
            bytes[dataIndex] = dataByte;
        }

        return bytes;
    }

    private static byte getBit(byte data, byte bitNum) {
        return (byte) ((data & 0x01 << bitNum) >>> bitNum);
    }
}
