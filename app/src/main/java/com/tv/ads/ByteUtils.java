package com.tv.ads;


import java.nio.ByteBuffer;


/**
 * 各基础类型与byte之间的转换
 **/
public class ByteUtils {


    /**
     * 将boolean转成byte[]
     *
     * @param val
     * @return byte[]
     */
    public static byte[] Boolean2ByteArray(boolean val) {
        int tmp = (!val) ? 0 : 1;
        return ByteBuffer.allocate(4).putInt(tmp).array();
    }


    /**
     * 将byte[]转成boolean
     *
     * @param data
     * @return boolean
     */
    public static boolean ByteArray2Boolean(byte[] data) {
        if (data == null || data.length < 4) {
            return false;
        }
        int tmp = ByteBuffer.wrap(data, 0, 4).getInt();
        return tmp != 0;
    }


    /**
     * 将int转成byte[]
     *
     * @param val
     * @return byte[]
     */
    public static byte[] Int2ByteArray(int val) {
        return ByteBuffer.allocate(4).putInt(val).array();
    }


    /**
     * 将byte[]转成int
     *
     * @param data
     * @return int
     */
    public static int ByteArray2Int(byte[] data) {
        if (data == null || data.length < 4) {
            return 0xDEADBEEF;
        }
        return ByteBuffer.wrap(data, 0, 4).getInt();
    }


    /**
     * 将float转成byte[]
     *
     * @param val
     * @return byte[]
     */
    public static byte[] Float2ByteArray(float val) {
        return ByteBuffer.allocate(4).putFloat(val).array();
    }

    public static byte[] Long2ByteArray(long val) {
        return ByteBuffer.allocate(8).putLong(val).array();
    }

    public static long ByteArray2Long(byte[] data) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.put(data, 0, data.length);
        buffer.flip();
        return buffer.getLong();
    }


    /**
     * 将byte[]转成float
     *
     * @param data
     * @return float
     */
    public static float ByteArray2Float(byte[] data) {
        if (data == null || data.length < 4) {
            return -1234.0f;
        }
        return ByteBuffer.wrap(data).getFloat();
    }


    /**
     * 将byte[]数组转成short[]数组
     *
     * @param b
     * @return short[]
     */
    public static short[] byteArray2ShortArray(byte[] b) {
        int len = b.length / 2;
        int index = 0;
        short[] re = new short[len];
        byte[] buf = new byte[2];
        for (int i = 0; i < b.length; ) {
            buf[0] = b[i];
            buf[1] = b[i + 1];
            short st = byteToShort(buf);
            re[index] = st;
            index++;
            i += 2;
        }
        return re;
    }


    /**
     * 将一个short[]数组反转为byte[]字节数组
     *
     * @param b
     */
    public static byte[] shortArray2ByteArray(short[] b) {
        byte[] rebt = new byte[b.length * 2];
        int index = 0;
        for (int i = 0; i < b.length; i++) {
            short st = b[i];
            byte[] bt = shortToByte(st);
            rebt[index] = bt[0];
            rebt[index + 1] = bt[1];
            index += 2;
        }
        return rebt;
    }


    /**
     * short转换为byte[]
     *
     * @param number
     * @return byte[]
     */
    public static byte[] shortToByte(short number) {
        int temp = number;
        byte[] b = new byte[2]; // 将最低位保存在最低位
        b[0] = (byte) (temp & 0xff);
        temp = temp >> 8; // 向右移8位
        b[1] = (byte) (temp & 0xff);
        return b;
    }

    /**
     * byte[]转换为short
     *
     * @param b
     * @return short
     */
    public static short byteToShort(byte[] b) {
        short s = 0;
        short s0 = (short) (b[0] & 0xff);// 最低位
        short s1 = (short) (b[1] & 0xff);
        s1 <<= 8;
        s = (short) (s0 | s1);
        return s;
    }

    public static byte[] double2Bytes(double d) {
        long value = Double.doubleToRawLongBits(d);
        byte[] byteRet = new byte[8];
        for (int i = 0; i < 8; i++) {
            byteRet[i] = (byte) ((value >> 8 * i) & 0xff);
        }
        return byteRet;
    }

    public static double bytes2Double(byte[] arr) {
        long value = 0;
        for (int i = 0; i < 8; i++) {
            value |= ((long) (arr[i] & 0xff)) << (8 * i);
        }
        return Double.longBitsToDouble(value);
    }

}