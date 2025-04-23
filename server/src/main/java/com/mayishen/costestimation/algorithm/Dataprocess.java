package com.mayishen.costestimation.algorithm;

import com.google.common.base.Preconditions;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Dataprocess {
    private ArrayList<String> encodeWords_values = new ArrayList<String>();
    public static Set<String> all_word = new HashSet<String>();
    private Synonym synonym;

    public Dataprocess() {
        synonym = new Synonym();
        File file = new File("src/main/resources/dicts/synonym.txt");
        InputStream input = null;
        List<String> contents = null;
        try {
            input = new FileInputStream(file);
            contents = IOUtils.readLines(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (String content : contents) {
            content = Preconditions.checkNotNull(content);
            String[] strsArr = content.split(" ");
            String[] strs = Preconditions.checkNotNull(strsArr);
            String encode = null;
            int length = strs.length;
            if (length > 1) {
                encode = strs[0];//获取编码
            }
            for (int i = 1; i < length; i++) {
                if (encode.matches("H.*")) {
                    encodeWords_values.add(strs[i]);
                }
            }
        }
    }

    public static int getnum(String origin, String target) {
        int res = 0;
        int i = origin.indexOf(target);
        while (i != -1) {
            i = origin.indexOf(target, i + 1);
            res++;
        }
        return res;
    }

    public static String set_gettext(Set<String> po_set, List<String> target, int num) {
        List<String> content_list = new ArrayList<String>(target);
        int j = 0;
        for (int i = 0; i < content_list.size(); i++) {
            if (po_set.contains(content_list.get(i))) {
                j++;
                if (j == num) {
                    String content = content_list.get(i);
                    content_list.set(i, "-" + content + "-");
                    break;
                }
            }
        }
        return StringUtils.join(content_list.toArray(), "");
    }

    public static String regex_gettext(String regex, List<String> target, int num) {
        List<String> content_list = new ArrayList<String>(target);
        int j = 0;
        for (int i = 0; i < content_list.size(); i++) {
            if (content_list.get(i).matches(regex)) {
                j++;
                if (j == num) {
                    String content = content_list.get(i);
                    content_list.set(i, "-" + content + "-");
                    break;
                }
            }
        }
        return StringUtils.join(content_list.toArray(), "");
    }

    public Set<String> get_Synonym(List<String> target) {
        Set<String> syn_word = new HashSet<String>();
        for (String e2 : encodeWords_values) {
            for (String e1 : target) {
                if (synonym.getSimilarity(e1, e2) > 0.9 && e2.length() == 2) {
//                    syn_word.add(e2);
                    if (!all_word.contains(e2)) {
                        syn_word.add(e2);
                        all_word.add(e2);
                    }
                }
            }
            if (syn_word.size() >= 3) {
                break;
            }
        }
        syn_word.addAll(target);
        return syn_word;
    }

    public static boolean is_ILF(List<String> target, int i, int k, Set<String> ILF_set) {
        String word = "";
        int num = 0;
        for (int j = 0; j < target.size() && num < k; j++) {
            word = target.get(j);
            if (num >= i) {
                if (ILF_set.contains(word)) {
                    return true;
                }
            }
            if (word.equals("。")) {
                num++;
            }
        }
        return false;
    }
}
