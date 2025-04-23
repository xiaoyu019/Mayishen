package com.mayishen.codeanalyzer.algorithm.clonedetection;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Winnowing {

    /**
     * 子串匹配至少与噪声阈值一样长，才能被检测到（用于过滤）
     */
    private final int minDetectedLength;
    /**
     * 滑动窗口的大小
     */
    private int windowSize;


    public Winnowing(int minDetectedLength, int noiseThreshold) {
        this.minDetectedLength = minDetectedLength;
        if (noiseThreshold > minDetectedLength) {
            throw new IllegalArgumentException("噪声阈值不能大于最小匹配保证阈值！");
        }
        this.windowSize = minDetectedLength - noiseThreshold + 1;
    }

    public Winnowing() {
        this(8, 4);
    }

    public Set<Integer> getHashFingerPrint(String text, String delimiter) throws NoSuchAlgorithmException {
        List<Integer> nh = getHashesForNGramsOfWords(text, delimiter);
        return buildFingerprintSet(nh);
    }

    private List<Integer> getHashesForNGramsOfWords(String text, String delimiter) throws NoSuchAlgorithmException {
        //基于分隔符delimiter对文本text进行划分并移除结果中的空格（trimResults方法）和空字符串（omitEmptyStrings方法）
        Iterator<String> tok = Arrays.asList(text.split(delimiter)).iterator();
        List<Integer> n_grams = new ArrayList<>();
        List<String> list = new ArrayList<>();
        while (tok.hasNext()) {
            list.add(tok.next());
            if (list.size() == this.minDetectedLength) {
                n_grams.add(getHash(String.join(" ", list)));
                list.remove(0);
            }
        }
        /* 当tokens比minDetectedLength短 */
        if (n_grams.isEmpty() && list.size() > 0) {
            n_grams.add(getHash(String.join(" ", list)));
        }
        return n_grams;
    }

    private Set<Integer> buildFingerprintSet(List<Integer> nHash) {
        Set<Integer> fp = new TreeSet<>();
        for (int i = 0; i < nHash.size() - this.windowSize + 1; i++) {
            List<Integer> s = new ArrayList<>(nHash.subList(i, i + this.windowSize));
            fp.add(Collections.min(s));
        }
        return fp;
    }

    public HashMap getParams() {
        HashMap<String, Integer> params = new HashMap<>();
        params.put("minDetectedLength", this.minDetectedLength);
        params.put("windowSize", this.windowSize);
        return params;
    }

    @SuppressWarnings("UnstableApiUsage")
    private int getHash(String token) throws NoSuchAlgorithmException {
        int h = hash(token);
        return Math.abs(h);
    }

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static int hash(String key) throws NoSuchAlgorithmException {
        byte[] md5hex = MessageDigest.getInstance("SHA1").digest(key.getBytes());
        return new BigInteger(bytesToHex(md5hex), 16).intValue();
    }
}
