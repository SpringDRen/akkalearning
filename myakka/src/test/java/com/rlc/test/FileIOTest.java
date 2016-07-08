package com.rlc.test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by renlc on 2016/6/24.
 */
public class FileIOTest {

    public static List<byte[]> readFile(String path) {
        List<byte[]> list = new ArrayList<>();

        return list;
    }

    public static void testNoRoom(String path) {
        try (FileOutputStream fo = new FileOutputStream(path, true)) {
            while (true) {
                fo.write("11111111111111111111111111111111\n".getBytes());
                fo.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        testNoRoom("J:\\1.txt");
    }
}
