package com.mayishen.codeanalyzer.algorithm.bugdetection;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class XmlReader {
    //    读取文件地址，返回名称和版本号。
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
                        System.out.println(name + " " + version);
                        map.put(name, version);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public static void main(String[] args) {
//        String path = "C:\\Users\\betciso\\Desktop\\example3\\src\\main\\testpom.xml";
        String path = "C:\\Users\\betciso\\Desktop\\example3\\src\\main\\pom.xml";

        Map<String, String> xmlReader = XmlReader(path);
        System.out.println(xmlReader);
    }
}