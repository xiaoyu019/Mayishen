package com.mayishen.costestimation.algorithm;

import com.google.common.base.Preconditions;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static java.lang.Math.PI;
import static java.lang.Math.cos;

public class Synonym {
    private Map<String, ArrayList<String>> word_code = new HashMap<String, ArrayList<String>>();
    private Map<String, ArrayList<String>> code_word = new HashMap<String, ArrayList<String>>();

    public Synonym() {
        File file = new File("src/main/resources/dicts/synonym.txt");
        InputStream input = null;
        List<String> contents = null;
        try {
            input = new FileInputStream(file);
            contents = IOUtils.readLines(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (contents == null) {
            word_code = null;
            code_word = null;
        } else {
            for (String content : contents) {
                content = Preconditions.checkNotNull(content);
                String[] strsArr = content.split(" ");
                String[] strs = Preconditions.checkNotNull(strsArr);
                String encode = null;
                int length = strs.length;
                if (length > 1) {
                    encode = strs[0];//获取编码
                }
                ArrayList<String> code_word_values = new ArrayList<String>(Arrays.asList(strs).subList(1, length));
                code_word.put(encode, code_word_values);
                for (int i = 1; i < length; i++) {
                    String key = strs[i];
                    if (word_code.containsKey(strs[i])) {
                        ArrayList<String> values = word_code.get(key);
                        values.add(encode);
                        word_code.put(key, values);
                    } else {
                        ArrayList<String> temp = new ArrayList<String>();
                        temp.add(encode);
                        word_code.put(key, temp);
                    }
                }
            }
        }
    }

    public double getSimilarity(String word1, String word2) {
        double a = 0.65;
        double b = 0.8;
        double c = 0.9;
        double d = 0.96;
        double e = 0.5;
        double f = 0.1;
        double degrees = 180;
        if (!word_code.containsKey(word1) || !word_code.containsKey(word2)) {
            return 0;
        }
        ArrayList<String> encode1 = getEncode(word1);
        ArrayList<String> encode2 = getEncode(word2);

        double maxValue = 0;
        for (String e1 : encode1) {
            for (String e2 : encode2) {
                String commonStr = getCommonStr(e1, e2);
                int length = StringUtils.length(commonStr);
                double k = getK(e1, e2);
                double n = getN(commonStr);
                double res = 0;
                if (e1.endsWith("@") || e2.endsWith("@") || 0 == length) {
                    if (f > maxValue) {
                        maxValue = f;
                    }
                    continue;
                }
                switch (length) {
                    case 1:
                        res = a * cos(n * PI / degrees) * ((n - k + 1) / n);
                        break;
                    case 2:
                        res = b * cos(n * PI / degrees) * ((n - k + 1) / n);
                        break;
                    case 4:
                        res = c * cos(n * PI / degrees) * ((n - k + 1) / n);
                        break;
                    case 5:
                        res = d * cos(n * PI / degrees) * ((n - k + 1) / n);
                        break;
                    default:
                        if (e1.endsWith("=") && e2.endsWith("=")) {
                            res = 1;
                        } else if (e1.endsWith("#") && e2.endsWith("#")) {
                            res = e;
                        }
                }
                if (res > maxValue) {
                    maxValue = res;
                }
            }
        }
        return maxValue;
    }

    public ArrayList<String> getEncode(String word) {
        return word_code.get(word);
    }

    public int getN(String encodeHead) {
        int length = StringUtils.length(encodeHead);
        switch (length) {
            case 1:
                return getCount(encodeHead, 2);
            case 2:
                return getCount(encodeHead, 4);
            case 4:
                return getCount(encodeHead, 5);
            case 5:
                return getCount(encodeHead, 7);
            default:
                return 0;
        }
    }

    public int getCount(String encodeHead, int end) {
        Set<String> res = new HashSet<String>();
        for (String curr : code_word.keySet()) {
            if (curr.startsWith(encodeHead)) {
                String temp = curr.substring(0, end);
                res.add(temp);
            }
        }
        return res.size();
    }

    public int getK(String encode1, String encode2) {
        String temp1 = encode1.substring(0, 1);
        String temp2 = encode2.substring(0, 1);
        if (StringUtils.equalsIgnoreCase(temp1, temp2)) {
            temp1 = encode1.substring(1, 2);
            temp2 = encode2.substring(1, 2);
        } else {
            return Math.abs(temp1.charAt(0) - temp2.charAt(0));
        }
        if (StringUtils.equalsIgnoreCase(temp1, temp2)) {
            temp1 = encode1.substring(2, 4);
            temp2 = encode2.substring(2, 4);
        } else {
            return Math.abs(temp1.charAt(0) - temp2.charAt(0));
        }
        if (StringUtils.equalsIgnoreCase(temp1, temp2)) {
            temp1 = encode1.substring(4, 5);
            temp2 = encode2.substring(4, 5);
        } else {
            return Math.abs(Integer.parseInt(temp1) - Integer.parseInt(temp2));
        }
        if (StringUtils.equalsIgnoreCase(temp1, temp2)) {
            temp1 = encode1.substring(5, 7);
            temp2 = encode2.substring(5, 7);
        } else {
            return Math.abs(temp1.charAt(0) - temp2.charAt(0));
        }
        return Math.abs(Integer.parseInt(temp1) - Integer.parseInt(temp2));
    }

    public String getCommonStr(String encode1, String encode2) {
        int length = StringUtils.length(encode1);
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            if (encode1.charAt(i) == encode2.charAt(i)) {
                sb.append(encode1.charAt(i));
            } else {
                break;
            }
        }
        int sbLen = StringUtils.length(sb);
        if (sbLen == 3 || sbLen == 6) {
            sb.deleteCharAt(sbLen - 1);
        }

        return String.valueOf(sb);
    }
}
