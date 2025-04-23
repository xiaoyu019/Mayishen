package com.mayishen.codeanalyzer.algorithm.bugdetection;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonToForm {
    /**
     * 以post或get方式调用对方接口方法，
     *
     * @param pathUrl
     */
    public static StringBuffer doPostOrGet(String pathUrl, String data) {
        OutputStreamWriter out = null;
        BufferedReader br = null;
        StringBuffer json = new StringBuffer();
        try {
            URL url = new URL(pathUrl);
            //打开和url之间的连接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //请求方式
            conn.setRequestMethod("POST");
            //conn.setRequestMethod("GET");

            //设置通用的请求属性
//            conn.setRequestProperty("accept", "*/*");
//            conn.setRequestProperty("connection", "Keep-Alive");
//            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
            conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");

            //DoOutput设置是否向httpUrlConnection输出，DoInput设置是否从httpUrlConnection读入，此外发送post请求必须设置这两个
            conn.setDoOutput(true);
            conn.setDoInput(true);

            /**
             * 下面的三句代码，就是调用第三方http接口
             */
            //获取URLConnection对象对应的输出流
            out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            //发送请求参数即数据
            out.write(data);
            //flush输出流的缓冲
            out.flush();

            /**
             * 下面的代码相当于，获取调用第三方http接口后返回的结果
             */
            //获取URLConnection对象对应的输入流
            InputStream is = conn.getInputStream();
            //构造一个字符流缓存
            br = new BufferedReader(new InputStreamReader(is));
            String str = null;
            while ((str = br.readLine()) != null) {
                json.append(str);
            }
            //关闭流
            is.close();
            //断开连接，disconnect是在底层tcp socket链接空闲时才切断，如果正在被其他线程使用就不切断。
            conn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return json;
    }

    public static Map<String, String> XmlReader2(String path) {
//        有的pom文件的版本号单独写在<properties>中，需要读取变成map。后期遇到从这里面读取版本号。
        DocumentBuilderFactory domfac = DocumentBuilderFactory.newInstance();
        Map<String, String> map = new HashMap<String, String>();
        try {
            DocumentBuilder domBuilder = domfac.newDocumentBuilder();
            InputStream is = new FileInputStream(new File(path));
            Document doc = domBuilder.parse(is);
            Element root = doc.getDocumentElement();
            NodeList properties = root.getElementsByTagName("properties");

            if (properties != null) {
                for (int i = 0; i < properties.getLength(); i++) {
                    Node property = properties.item(i);
                    if (property.getNodeType() == Node.ELEMENT_NODE) {
                        for (Node node = property.getFirstChild(); node != null; node = node.getNextSibling()) {
                            if (node.getNodeType() == Node.ELEMENT_NODE) {
                                String name = node.getNodeName();
                                String version = node.getFirstChild().getNodeValue();
                                map.put(name, version);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public static Map<String, String> XmlReader(String path) {
//        读取pom文件，获取<dependency>,获取<groupID>:<artifactID>-><version>
        DocumentBuilderFactory domfac = DocumentBuilderFactory.newInstance();
        Map<String, String> map = new HashMap<String, String>();
        try {
            DocumentBuilder domBuilder = domfac.newDocumentBuilder();
            InputStream is = new FileInputStream(new File(path));
            Document doc = domBuilder.parse(is);
            Element root = doc.getDocumentElement();
            NodeList dependencies = root.getElementsByTagName("dependency");

            if (dependencies != null) {
                for (int i = 0; i < dependencies.getLength(); i++) {
                    Node dependency = dependencies.item(i);
                    String groupId = null;
                    String artifactId = null;
                    String version = null;
                    if (dependency.getNodeType() == Node.ELEMENT_NODE) {
                        for (Node node = dependency.getFirstChild(); node != null; node = node.getNextSibling()) {
                            if (node.getNodeType() == Node.ELEMENT_NODE) {
                                if (node.getNodeName().equals("groupId")) {
                                    groupId = node.getFirstChild().getNodeValue();
//                                    System.out.println(groupId);
                                }
                                if (node.getNodeName().equals("artifactId")) {
                                    artifactId = node.getFirstChild().getNodeValue();
//                                    System.out.println(artifactId);
                                }
                                if (node.getNodeName().equals("version")) {
                                    version = node.getFirstChild().getNodeValue();
//                                    System.out.println(version);
                                }
//                                System.out.println(groupId+artifactId+version);
                            }
                        }
                        String name = groupId + ":" + artifactId;
//                        System.out.println(name+" "+version);
                        map.put(name, version);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public static OpenFrame[] getResult(String path) {
        Map<String, String> xmlReader2 = XmlReader2(path);
        Map<String, String> xmlReader = XmlReader(path);

        //将参数取出。替换为版本号。
        for (Map.Entry<String, String> vo : xmlReader.entrySet()) {
            String key = vo.getKey();
            String value = vo.getValue();
            if (value != null) {
                Pattern r = Pattern.compile("^\\$(\\S)*");
                Matcher m = r.matcher(value);
                if (m.matches()) {
                    value = value.replaceAll("\\$\\{", "");
                    value = value.replaceAll("\\}", "");
                    String value2 = xmlReader2.get(value);
                    xmlReader.put(key, value2);
                }
            }
        }
//        删除不加版本号的条目
        Iterator<String> iter = xmlReader.keySet().iterator();
        while (iter.hasNext()) {
            String key = iter.next();
            if (xmlReader.get(key) == null) {
                iter.remove();
            }
        }
        OpenFrame[] openFrames = new OpenFrame[xmlReader.size()];
        Iterator<String> iter2 = xmlReader.keySet().iterator();
        int j = 0;
        while (iter2.hasNext()) {
            String key = iter2.next();
            String value = xmlReader.get(key);
            openFrames[j] = new OpenFrame();
            openFrames[j].setName(key);
            openFrames[j].setVersion(value);
            String query = "{\"version\": \"" + value + "\",\"package\": {\"name\": \"" + key + "\"}}";
            StringBuffer result = doPostOrGet("https://api.osv.dev/v1/query", query);
            if (result.length() > 2) {
                String str = result.toString();
//                JSONArray array = JSONArray.parseArray(str);
                JSONObject json = JSONObject.parseObject(str);
                String vulns = json.getString("vulns");
                JSONArray jsonArray = JSONArray.parseArray(vulns);
                openFrames[j].setSize(jsonArray.size());
                openFrames[j].setOpenFrameBug(new OpenFrameBug[jsonArray.size()]);
                for (int i = 0; i < jsonArray.size(); i++) {
                    openFrames[j].getOpenFrameBug()[i] = new OpenFrameBug();
                    JSONObject job = jsonArray.getJSONObject(i);
                    openFrames[j].getOpenFrameBug()[i].setId(job.getString("id"));
                    openFrames[j].getOpenFrameBug()[i].setFrameName(key);
                    openFrames[j].getOpenFrameBug()[i].setModified(job.getString("modified"));
                    openFrames[j].getOpenFrameBug()[i].setPublished(job.getString("published"));
                    openFrames[j].getOpenFrameBug()[i].setSummary(job.getString("summary") + "\n" + job.getString("details"));
                    String aliases = job.getString("aliases");
                    JSONArray aliasesArray = JSONArray.parseArray(aliases);
                    openFrames[j].getOpenFrameBug()[i].setBugName(aliasesArray.getString(0));
                    String database_specific = job.getString("database_specific");
                    JSONObject dsJson = JSONObject.parseObject(database_specific);
                    openFrames[j].getOpenFrameBug()[i].setLevel(dsJson.getString("severity"));
                    String affected = job.getString("affected");
                    JSONArray afJson = JSONArray.parseArray(affected);
                    String aJ = afJson.getString(0);
                    JSONObject ajj = JSONObject.parseObject(aJ);
                    String a = ajj.getString("versions");
                    openFrames[j].getOpenFrameBug()[i].setVersions(a);
                }
            }
            j++;
        }
        return openFrames;
    }

    public static void main(String[] args) {
        String path = "E:\\Library\\Document\\WeChat Files\\wxid_eozgsi1ed3g322\\FileStorage\\File\\2022-12\\function_point\\testpom.xml";
        OpenFrame[] openFrames = getResult(path);
        System.out.println(Arrays.asList(openFrames).toString());
    }
}
