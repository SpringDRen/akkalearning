package com.rlc.test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by renlc on 2016/6/15.
 */
public class Main {

    public static int count = 0;

    public static final String patternRule =
            "([0-9\\.]*)"//([0-9\.]*)  匹配第一个字段IP 1
                    + "(?: - )"  //(?: - ) 不捕获
                    + "([^ ]*)" //任意非空格字符  捕获第二个字段 2
                    + "(?: )"  //空格分割符
                    + "(\\[[^\\[\\]]*\\])"  //(\[.*\])   捕获第三个字段 [中间任意非[]字符]  日志时间输出 3
                    + "(?: )"  //空格分割符
                    + "(\"[^\"]*\")" //("[^"]*")  引号开始，中间为非"的任意字符，"结束   HTTP请求头 4
                    + "(?: )"  //空格分割符
                    + "(\\d*)" //捕获数字  请求状态 5
                    + "(?: )"  //空格分割符
                    + "(\\d*)"//捕获数字  流量 6
                    + "(?: )"  //空格分割符
                    + "(\\d*)"//捕获数字  流量 7
                    + "(?: )"  //空格分割符
                    + "(\\d*)" //捕获数字  。。。 8
                    + "(?: )"  //空格分割符
                    + "(\"[^\"]*\")"//捕获"-"或者 "bytes=0-"  。。。 9
                    + "(?: )"  //空格分割符
                    + "(\"[^\"]*\")"//捕获"-" 或者  URL？ 10
                    + "(?: )"  //空格分割符
                    + "(\"[^\"]*\")"//也可能为"-" ("[^"]*")  引号开始，中间为非"的任意字符，"结束  UserAgent 11
                    + "(?: )"  //空格分割符
                    + "(\"[^\"]*\")" //("[^"]*")  引号开始，中间为非"的任意字符，"结束  IP段 12
                    + "(?: )"  //空格分割符
                    + "(\"[^\"]*\")" //("[^"]*")  引号开始，中间为非"的任意字符，"结束  。。。 13
                    + "(?: )"  //空格分割符
                    + "([\\d-]*)" //捕获数字  。。。 14
                    + "(?: )"  //空格分割符
                    + "(\"[^\"]*\")" //("[^"]*")  引号开始，中间为非"的任意字符，"结束   HIT 15
                    + "(?: )" //空格分割符
                    + "([0-9\\.]*)"//([0-9\.]*)  最后一个字段  IP 16
            ;

    private static final String patterRulesMp4 = "_\\d{8}_\\d{1}_\\d{1}_(\\d+).mp4";

    /**
     * 读取文件
     *
     * @param path
     * @return
     */
    public static String readFile(String path) {
        StringBuilder strBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            while (br.ready()) {
                String line = br.readLine();
                if (line == null) {
                    break;
                }
                strBuilder.append(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return strBuilder.toString();
    }

    /**
     * 按行解析日志
     *
     * @param path
     */
    public static void parseLog(String path) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            while (br.ready()) {
                String line = br.readLine();
                if (line == null) {
                    break;
                }
                if (!parseStr(line)) {
                    System.out.println("匹配失败的行：");
                    System.out.println(line);
                    return;
                }
            }
            System.out.println("匹配成功的总条数：" + count);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析字符串
     *
     * @param str
     * @return
     */
    public static boolean parseStr(String str) {
        Pattern pattern = Pattern.compile(patternRule);
        Matcher matcher = pattern.matcher(str);
        boolean flag = false;
        while (matcher.find()) {
            flag = true;
            count++;
            for (int i = 0; i <= matcher.groupCount(); i++) {
                //System.out.println(i + ":" + matcher.group(i));
            }
        }
        return flag;
    }

    public static void pattern(String str) {
        Pattern pattern = Pattern.compile(patternRule);
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            for (int i = 0; i <= matcher.groupCount(); i++) {
                System.out.println(i + ":" + matcher.group(i));
            }
        }
    }

    public static void main(String[] args) {
//                String s = "122.139.204.191 - abd [02/Feb/2016:00:59:59 +0800] " +
//                "\"GET pcvideogs.titan.mgtv.com/mp4/2014/dianying/bmzy_21704/34A225989FC5BAE7B999E61D6EB5267B_20141209_1_1_682.mp4/3840000_3850000_v01_mp4.t/pcvideogs.titan.mgtv.com/mp4/2014/dianying/bmzy_21704/34A225989FC5BAE7B999E61D6EB5267B_20141209_1_1_682.mp4/3840000_3850000_v01_mp4.ts?t=56affe48&ver=0x03&pno=1120&sign=23d9035b3a413b66122270fafc070049&nid=25012&urgid=1354&payload=usertoken%3Duuid%3D71f6a8130c5f4c27a6d3321cb40835f8%5Eruip%3D2055982271%5Ehit%3D0&win=300&limitrate=0&rdur=21600&arange=0&srgids=25012&fid=34A225989FC5BAE7B999E61D6EB5267B&uuid=71f6a8130c5f4c27a6d3321cb40835f8&srgid=25012 HTTP/1.1\"" +
//                " 200 255642 255304 0 \"-\" \"-\"" +
//                " \"AppleCoreMedia/1.0.0.13D15 (iPhone; U; CPU OS 9_2_1 like Mac OS X; zh_cn)\" " +
//                "\"122.143.15.200,122.139.204.191\" \"143845815\" 1120 \"HIT\" 122.143.15.198 "
//                + "122.139.204.191 - abd [02/Feb/2016:00:59:59 +0800] " +
//                "\"GET pcvideogs.titan.mgtv.com/mp4/2014/dianying/bmzy_21704/34A225989FC5BAE7B999E61D6EB5267B_20141209_1_1_682.mp4/3840000_3850000_v01_mp4.t/pcvideogs.titan.mgtv.com/mp4/2014/dianying/bmzy_21704/34A225989FC5BAE7B999E61D6EB5267B_20141209_1_1_682.mp4/3840000_3850000_v01_mp4.ts?t=56affe48&ver=0x03&pno=1120&sign=23d9035b3a413b66122270fafc070049&nid=25012&urgid=1354&payload=usertoken%3Duuid%3D71f6a8130c5f4c27a6d3321cb40835f8%5Eruip%3D2055982271%5Ehit%3D0&win=300&limitrate=0&rdur=21600&arange=0&srgids=25012&fid=34A225989FC5BAE7B999E61D6EB5267B&uuid=71f6a8130c5f4c27a6d3321cb40835f8&srgid=25012 HTTP/1.1\"" +
//                " 200 255642 255304 0 \"-\" \"-\"" +
//                " \"AppleCoreMedia/1.0.0.13D15 (iPhone; U; CPU OS 9_2_1 like Mac OS X; zh_cn)\" " +
//                "\"122.143.15.200,122.139.204.191\" \"143845815\" 1120 \"HIT\" 122.143.15.198";

//        readFile("C:\\Users\\renlc\\Desktop\\licun.log");

//        parseLog("C:\\Users\\renlc\\Desktop\\licun.log");
//
//
//        String s = "183.204.23.222 - - [01/Feb/2016:21:57:46 +0800] \"GET pcvideogs.titan.mgtv.com/mp4/2016/dongman/wpys_18621/6E80A1C252F7C9CC766BADEF61AF7867_20160106_1_1_386.mp4/10000_20000_v01_mp4.t/pcvideogs.titan.mgtv.com/mp4/2016/dongman/wpys_18621/6E80A1C252F7C9CC766BADEF61AF7867_20160106_1_1_386.mp4/10000_20000_v01_mp4.ts?t=56afd25e&ver5e&ver=0x03&pno=102130207c6b83ea45e3c953e8be33082bac&nid=25012&urgid=2819&payload=usertoken%3Duuid%3D0d01d162fa5a4d968c8a4fb2b0e240e2%5Eruip%3D3083605982%5Ehit%3D0&win=300&limitrate=0&rdur=21600&arange=0&srgids=25012&fid=6E80A1C252F7C9CC766BADEF61AF7867&uuid=0d01d162fa5a4d968c8a4fb2b0e240e2&srgid=25012 HTTP/1.1\" 403 357 162 0 \"bytes=0-\" \"-\" \"Mozilla/5.0 (Linux; U; Android 4.3; zh-CN; Coolpad 8720L Build/JSS15Q) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 UCBrowser/10.9.2.712 U3/0.8.0 Mobile Safari/534.30\" \"183.204.23.222\" \"66106071\" 102130207c6b83ea45e3c953e8be33082bac \"DISPATCH_INIT\" 111.7.165.142";
//        pattern(s);

        String s = "51F874A570BB045F07F27A6BE82B493A_20160601_1_1_386.mp4";
        Pattern pattern = Pattern.compile(patterRulesMp4);
        Matcher matcher = pattern.matcher(s);
        while (matcher.find()) {
            for (int i = 0; i <= matcher.groupCount(); i++) {
                System.out.println(i + ":" + matcher.group(i));
            }
        }

    }
}
