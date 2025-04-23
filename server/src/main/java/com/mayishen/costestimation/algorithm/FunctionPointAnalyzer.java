package com.mayishen.costestimation.algorithm;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.mayishen.costestimation.service.FunctionService;
import com.spire.doc.Document;
import com.spire.doc.Section;
import com.spire.doc.documents.Paragraph;
import com.mayishen.costestimation.dto.FunctionDTO;
import com.mayishen.costestimation.service.CostProjectService;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FunctionPointAnalyzer {
    public static HashMap<String, HashMap<String, String>> analyze(String path) {
        if (path == null || path.length() == 0)
            return new HashMap<String, HashMap<String, String>>();
        Dataprocess dataprocess = new Dataprocess();
        Document doc = new Document(path);
        HashMap<String, HashMap<String, String>> point_dict = new HashMap<>();//存放{标题名：{功能点类型：该功能点对应的段落文字}}的字典
        String regexp = "(\\d、+)|(\\d\\.*)+";
        String title = "\\s*(((\\d\\.*)+|\\d、*|([一二三四五六七八九十])、*).*)";
        boolean is_possible_point = false;
        Pattern patten = Pattern.compile(regexp);
        int pre_level = 10;
        String pre_po = "";
        String pre_text = "";
        List<String> point_list = new ArrayList<>();
        for (int i = 0; i < doc.getSections().getCount(); i++) {
            Section section = doc.getSections().get(i);
            for (int j = 0; j < section.getParagraphs().getCount(); j++) {
                Paragraph paragraph = section.getParagraphs().get(j);
                if (paragraph.getText().matches("\\s*")) {
                    continue;
                }
                if (paragraph.getText().matches(title)) {
                    if (pre_text.matches(title)) {
                        point_list.remove(pre_po);
                    }
                    String str_ptext = paragraph.getText();
                    str_ptext = str_ptext.trim();
//                    if (Dataprocess.getnum(str_ptext, ".") > 1 || Dataprocess.getnum(str_ptext, "、") > 1) {
                    int num = Dataprocess.getnum(str_ptext, ".");
                    Matcher matcher1 = patten.matcher(str_ptext);
                    str_ptext = matcher1.replaceAll("");
                    str_ptext = str_ptext.trim();
                    if (num > pre_level && !pre_text.matches(title)) {
                        point_list.remove(pre_po);
                    }
                    point_list.add(str_ptext);
                    pre_level = num;
                    pre_po = str_ptext;
//                    }
                }
                pre_text = paragraph.getText();
            }
        }
//        for (String word:point_list) {
//            System.out.println(word);
//        }
        String current_point = "";
        String pre_content = "";
        HashMap<String, List<String>> title_content = new HashMap<>();
        for (int i = 0; i < doc.getSections().getCount(); i++) {
            Section section = doc.getSections().get(i);
            for (int j = 0; j < section.getParagraphs().getCount(); j++) {
                Paragraph paragraph = section.getParagraphs().get(j);
                if (is_possible_point) {
                    title_content.get(current_point).add(paragraph.getText());
                    pre_content = paragraph.getText();
                }
                if (paragraph.getText().matches(title)) {
                    if (is_possible_point) {
                        title_content.get(current_point).remove(pre_content);
                    }
                    is_possible_point = false;
                    Matcher matcher1 = patten.matcher(paragraph.getText());
                    String str_po = matcher1.replaceAll("");
                    str_po = str_po.trim();
                    if (point_list.contains(str_po)) {
                        current_point = str_po.trim();
                        List<String> content_list = new ArrayList<>();
                        title_content.put(current_point, content_list);
                        is_possible_point = true;
                    }
                }
            }
        }
        for (Map.Entry<String, List<String>> entry : title_content.entrySet()) {
            String content = StringUtils.join(title_content.get(entry.getKey()).toArray(), "");
            JiebaSegmenter segmenter = new JiebaSegmenter();
            List<String> result = segmenter.sentenceProcess(content);
            List<String> delstop = new ArrayList<>(result);
            title_content.put(entry.getKey(), delstop);
        }
        String file = "src/main/resources/dicts/stopword.txt";
        List<String> stopword = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                stopword.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<String> mark_list = new ArrayList<>();
        Collections.addAll(mark_list, "，", "。", "、", "？", "；", "“", "”", "{", "}", "【", "】", "《", "》", "/", "并", "和", "及", "以及", "或");
        List<String> mark_set = new ArrayList<>();
        Collections.addAll(mark_set, "‘", "’", "：", "（", "）", "！", "……");
        mark_set.addAll(mark_list);
        List<String> sep_list = new ArrayList<>();
        Collections.addAll(sep_list, "。");
        List<String> ILF_list = new ArrayList<>();
        Collections.addAll(ILF_list, "维护", "建立", "归集", "采集", "库", "新建");
        List<String> EI_list = new ArrayList<>();
        Collections.addAll(EI_list, "修改", "增加", "登记", "删除", "编辑", "刷新", "新增", "管理", "录入", "添加");
        List<String> EO_list = new ArrayList<>();
        Collections.addAll(EO_list, "分析", "统计", "深入分析", "数据分析", "处理");
        List<String> EQ_list = new ArrayList<>();
        Collections.addAll(EQ_list, "展示", "查阅", "查询", "查看", "显示", "筛选", "审核", "检查", "刷新");
        ILF_list.addAll(EI_list);
        ILF_list.addAll(EO_list);
        ILF_list.addAll(EQ_list);
        Set<String> ILF_set = dataprocess.get_Synonym(ILF_list);
        Set<String> EQ_set = dataprocess.get_Synonym(EQ_list);
        Set<String> EI_set = dataprocess.get_Synonym(EI_list);
        Set<String> EO_set = dataprocess.get_Synonym(EO_list);
        ILF_set.addAll(EQ_set);
        ILF_set.addAll(EI_set);
        ILF_set.addAll(EO_set);
        int EIF = 0, ILF = 0, EI = 0, EO = 0, EQ = 0;
        String EIFpa = ".*信息.*|.*库|.*表";
        List<String> repeat_list = new ArrayList<>();
        Collections.addAll(repeat_list, "信息", "库", "表");
        Set<String> exist_word = new HashSet<>();
//        System.out.println(ILF_set);
        int current_E_I = 0, current_EI = 0, current_EO = 0, current_EQ = 0;
        String current_E_I_word = "", current_EI_word = "", pre_E_I_word = "", next_EI_word = "", next_EO_word = "", next_EQ_word = "";
        boolean is_E_I = false;
//        System.out.println(title_content.keySet());
        for (Map.Entry<String, List<String>> entry : title_content.entrySet()) {
            boolean flag = true, EI_flag = true, EO_flag = true, EQ_flag = true, pre_flag = true, current_flag = true;
            boolean E_I_flag = true;
//            boolean is_change = false;
            int EIF_ILF = 0, EInum = 0, EOnum = 0, EQnum = 0, EIFnum = 0, ILFnum = 0, paragraph_E_I_num = 0, sep_num = 0;
            int pre_EInum = 0, pre_EOnum = 0, pre_EQnum = 0;
            List<String> words = title_content.get(entry.getKey());
            String key = entry.getKey();
//            Set<String> pre_exist_word = new HashSet<String>();

            HashMap<String, String> word_pos = new HashMap<>();
            Set<String> next_EI_exist_word = new HashSet<>();
            Set<String> EI_exist_word = new HashSet<>();
//            Set<String> next_EO_exist_word = new HashSet<String>();
//            Set<String> next_EQ_exist_word = new HashSet<String>();
            for (String word : words) {
                if (mark_list.contains(word.substring(0, 1))) {
                    if (!flag && next_EI_exist_word.contains(next_EI_word) && EI_exist_word.contains(current_EI_word)) {
                        flag = true;
                        EI_flag = true;
                        next_EI_word = "";
                        continue;
                    } else if (!flag && !EI_flag) {
                        if (!stopword.contains(next_EI_word) && !next_EI_word.equals("")) {
                            next_EI_exist_word.add(next_EI_word);
                        }

                        word_pos.put("EI" + pre_EInum, Dataprocess.set_gettext(EI_set, words, current_EI));
                        EI_exist_word.add(current_EI_word);
                        EI++;
//                        flag = true;
                    }
                    next_EI_word = "";
                    EI_flag = true;
                    EO_flag = true;
                    EQ_flag = true;
                    flag = true;
                    continue;
                }
                if (!flag && !EI_flag) {
                    if (next_EI_word.equals("")) {
                        next_EI_word = word;
                    }
                }
                if (EI_set.contains(word)) {
                    EInum++;
                    if (!flag) {
                        continue;
                    }
                    current_EI = EInum;
                    current_EI_word = word;
                    pre_EInum++;
//                    word_pos.put("EQ" + pre_EQnum, Dataprocess.set_gettext(EQ_set, words, current_EQ));
//                    EQ++;
                    flag = false;
                    EI_flag = false;
                } else if (EO_set.contains(word)) {
                    EOnum++;
                    current_EO = EOnum;
                    pre_EOnum++;
                    if (!flag) {
                        continue;
                    }
                    if (!EO_flag) {
                        if (next_EO_word.equals("")) {
                            next_EO_word = word;
                        }
                        continue;
                    }
                    word_pos.put("EO" + pre_EOnum, Dataprocess.set_gettext(EO_set, words, current_EO));
                    EO++;
                    flag = false;
                    EO_flag = false;
                } else if (EQ_set.contains(word)) {
                    EQnum++;
                    current_EQ = EQnum;
                    pre_EQnum++;
                    if (!flag) {
                        continue;
                    }
                    if (!EQ_flag) {
                        if (next_EQ_word.equals("")) {
                            next_EQ_word = word;
                        }
                        continue;
                    }
                    word_pos.put("EQ" + pre_EQnum, Dataprocess.set_gettext(EQ_set, words, current_EQ));
                    EQ++;
                    flag = false;
                    EQ_flag = false;
                }
            }
            flag = true;
            String paragraph = StringUtils.join(words, "");
            for (String word : repeat_list) {
                paragraph_E_I_num += Dataprocess.getnum(paragraph, word);
            }
            for (String word : words) {
                if (exist_word.contains(word)) {
                    paragraph_E_I_num--;
                }
            }
            for (String word : words) {
                if (paragraph_E_I_num == Dataprocess.getnum(paragraph, "。")) {
                    if (Dataprocess.getnum(paragraph, "。") == 0) {
                        E_I_flag = !Dataprocess.is_ILF(words, 0, 1000, ILF_set);
                    } else {
                        if (word.equals("。")) {
                            sep_num++;
                        }
                        E_I_flag = !Dataprocess.is_ILF(words, sep_num, sep_num + 1, ILF_set);
                        if (word.equals("。")) {
                            E_I_flag = !Dataprocess.is_ILF(words, sep_num - 1, sep_num, ILF_set);
                        }
                    }
                }
                if (mark_list.contains(word.substring(0, 1))) {
//                    if (!flag&&pre_exist_word.contains(pre_E_I_word)){
//                        pre_flag = false;
//                        is_E_I = false;
//                    }
//                    else if (!flag&&!stopword.contains(pre_E_I_word)){
//                        pre_exist_word.add(pre_E_I_word);
//                    }
                    flag = true;
                    if (exist_word.contains(current_E_I_word)) {
                        current_flag = false;
                    } else if (!repeat_list.contains(current_E_I_word)) {
                        exist_word.add(current_E_I_word);
                    }
                    if ((ILF_set.contains(pre_E_I_word) || mark_set.contains(pre_E_I_word)) && !current_flag) {
                        is_E_I = false;
                    }
                    if (current_E_I_word.equals("")) {
                        is_E_I = false;
                    }
                    if (current_E_I_word.matches(".表")) {
                        is_E_I = false;
                    }
                    if (is_E_I) {
                        if (E_I_flag && !current_E_I_word.equals("数据库")) {
                            EIFnum++;
                            word_pos.put("EIF" + EIFnum, Dataprocess.regex_gettext(EIFpa, words, current_E_I));
                        } else {
                            ILFnum++;
                            word_pos.put("ILF" + ILFnum, Dataprocess.regex_gettext(EIFpa, words, current_E_I));
                        }
//                        E_I_flag = true;
                    }
                    is_E_I = false;
                    current_flag = true;
//                    pre_flag = true;
                }
                if (sep_list.contains(word.substring(0, 1))) {
                    E_I_flag = true;
                }
                if (ILF_set.contains(word)) {
                    E_I_flag = false;
                }
                if (word.matches(EIFpa)) {
                    is_E_I = true;
                    EIF_ILF++;
                    if (!flag) {
                        continue;
                    }
                    if (word.matches(".表") || word.matches(".库")) {
                        is_E_I = false;
                        pre_E_I_word = word;
                        continue;
                    }
                    current_E_I = EIF_ILF;
                    current_E_I_word = word;

                    flag = false;
//                    continue;
                }
                if (flag) {
                    if (!mark_list.contains(word)) {
                        pre_E_I_word = word;
                    }
                }

            }
            current_E_I_word = "";
            pre_E_I_word = "";
            next_EI_word = "";
            next_EO_word = "";
            next_EQ_word = "";
            EIF += EIFnum;
            ILF += ILFnum;
            point_dict.put(key, word_pos);
        }
        return point_dict;
    }

    public static Integer toType(String type) {
        if (type.contains("ILF"))
            return 0;
        if (type.contains("EIF"))
            return 1;
        if (type.contains("EI"))
            return 2;
        if (type.contains("EO"))
            return 3;
        if (type.contains("EQ"))
            return 4;
        return -1;
    }

    ;

    public static void insertfunctionPoints(CostProjectService projectService, FunctionService service, Integer id, String path) {
        HashMap<String, HashMap<String, String>> point_dict = FunctionPointAnalyzer.analyze(path);
        int total = point_dict.keySet().size(), progress = 0;
        for (Map.Entry<String, HashMap<String, String>> entry : point_dict.entrySet()) {
            String title1 = entry.getKey(), name = title1.length() > 100 ? title1.substring(0, 100) : title1;
            for (Map.Entry<String, String> entry1 : point_dict.get(entry.getKey()).entrySet()) {
                Pattern pattern = Pattern.compile("-.*-");
                Matcher matcher = pattern.matcher(point_dict.get(title1).get(entry1.getKey()));
                if (matcher.find()) {
                    String content = point_dict.get(title1).get(entry1.getKey()).replace(matcher.group(), "<span class='k'>" + matcher.group().replace("-", "") + "</span>");
                    Integer type = FunctionPointAnalyzer.toType(entry1.getKey());
                    FunctionDTO f = new FunctionDTO();
                    f.setName(name);
                    f.setType(type);
                    f.setContent(content);
                    f.setProjectId(id);
                    service.insert(f);
                } else {
                    String content = point_dict.get(title1).get(entry1.getKey());
                    Integer type = FunctionPointAnalyzer.toType(entry1.getKey());
                    FunctionDTO f = new FunctionDTO();
                    f.setName(name);
                    f.setType(type);
                    f.setContent(content);
                    f.setProjectId(id);
                    service.insert(f);
                }
            }
            progress += 1;
            projectService.setProgress(id, (int) (100.0 * progress / total));
        }
    }
}
