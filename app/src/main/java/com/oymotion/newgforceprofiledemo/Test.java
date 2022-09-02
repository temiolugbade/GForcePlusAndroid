package com.oymotion.newgforceprofiledemo;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Test {
    public static void main(String[] args) {
//        for (int i = 1; i <= 1; i++) {
//            for (int j = 1; j <= 1; j++) {
////                String path = "D:\\participant_data\\participant0".concat(String.valueOf(i)).concat("\\experiment").concat(String.valueOf(j));
////                String readPath = path.concat("\\emg.csv");
////                String writePath = path.concat("\\emg_processed.csv");
//                String readPath1 = "D:\\participant_data\\participant01\\experiment1\\emg.csv";
//                String writePath1 = "D:\\participant_data\\participant01\\experiment1\\emg_processed.csv";
////                System.out.println(readPath);
//                System.out.println(readPath1);
////                System.out.println(writePath);
//                System.out.println(writePath1);
//                processEMG(readPath1, writePath1);
//            }
//
//        }
        String readPath1 = "D:\\participant_data\\participant01\\experiment1\\emg.csv";
        String writePath1 = "D:\\participant_data\\participant01\\experiment1\\emg_processed.csv";
        System.out.println(readPath1);
        System.out.println(writePath1);
        processEMG(readPath1, writePath1);
    }

    public static void processEMG(String readPath, String writePath){
//        byte[] b = new byte[4];
//        new Random(0).nextBytes(b);
//
//
////        System.out.println("1");
//        System.out.println(byteToInt(b));
//        System.out.println(getLong(b));

//        File csv = new File("G:\\FileSave\\dataFile\\read.csv");
        File csv = new File("D:\\participant_data\\participant01\\experiment1\\emg.csv");
//        File csv = new File(readPath);

//        File writeFile = new File("C:\\Users\\lilil\\GforceData\\rawData\\emg_processed.csv");
        File writeFile = new File("D:\\participant_data\\participant01\\experiment1\\emg_processed.csv");
//        File writeFile = new File(writePath);


        try{
            BufferedReader textFile = new BufferedReader(new FileReader(csv));
            BufferedWriter writeText = new BufferedWriter(new FileWriter(writeFile));
            writeText.write("id,p_id,e_id,itr_id,itr_type,hand,clt_id," +
                    "ch_01," +
                    "ch_02," +
                    "ch_03," +
                    "ch_04," +
                    "ch_05," +
                    "ch_06," +
                    "ch_07," +
                    "ch_08," +
                    "timestamp");
            String lineDta = "";

            while ((lineDta = textFile.readLine()) != null){
                System.out.println(lineDta);
                parse2csv(writeText, lineDta);
            }

            textFile.close();
            writeText.flush();
            writeText.close();
        }catch (FileNotFoundException e){
            System.out.println("cannot find the file");
        }catch (IOException e){
            System.out.println("errors happened in ");
        }

    }
    public static int byteToInt(byte[] arr) {
        int i0 = (int) ((arr[0] & 0xff) << 0 * 8);
        int i1 = (int) ((arr[1] & 0xff) << 1 * 8);
        int i2 = (int) ((arr[2] & 0xff) << 2 * 8);
        int i3 = (int) ((arr[3] & 0xff) << 3 * 8);
        return i0 + i1 + i2 + i3;
    }
    //if correct
    public static long getLong(byte[] b) {

        long accum = 0;
        accum = accum | (b[0] & 0xffL) << 0;
        accum = accum | (b[1] & 0xffL) << 8;
        accum = accum | (b[2] & 0xffL) << 16;
        accum = accum | (b[3] & 0xffL) << 24;

        System.out.println(accum);
        return accum;

    }



    public static void parseCSV(BufferedWriter writeText, String inputLine) {

        String[] splitFields;
        splitFields = inputLine.split(";");

        try {
            int id = Integer.parseInt(splitFields[0]);
            int p_id = Integer.parseInt(splitFields[1]);
            int e_id = Integer.parseInt(splitFields[2]);
            int itr_id = Integer.parseInt(splitFields[3]);
            int itr_type = Integer.parseInt(splitFields[4]);
            int hand = Integer.parseInt(splitFields[5]);
            int clt_id = Integer.parseInt(splitFields[6]);
            int state = Integer.parseInt(splitFields[15]);
            String timestamp = splitFields[16];
            List<String> ch01 = getEMGArray(writeText, id, itr_type, hand, 1, splitFields[7], timestamp);
            List<String> ch02 = getEMGArray(writeText, id, itr_type, hand, 2, splitFields[8], timestamp);
            List<String> ch03 = getEMGArray(writeText, id, itr_type, hand, 3, splitFields[9], timestamp);
            List<String> ch04 = getEMGArray(writeText, id, itr_type, hand, 4, splitFields[10], timestamp);
            List<String> ch05 = getEMGArray(writeText, id, itr_type, hand, 5, splitFields[11], timestamp);
            List<String> ch06 = getEMGArray(writeText, id, itr_type, hand, 6, splitFields[12], timestamp);
            List<String> ch07 = getEMGArray(writeText, id, itr_type, hand, 7, splitFields[13], timestamp);
            List<String> ch08 = getEMGArray(writeText, id, itr_type, hand, 8, splitFields[14], timestamp);
            ArrayList<String> emgCsv = new ArrayList<String>(16);

        } catch (Exception e) {

        }
    }


    public static void parse2csv(BufferedWriter writeText, String inputLine) {

        String[] splitFields;
        splitFields = inputLine.split(";");

        try {
            int id = Integer.parseInt(splitFields[0]);
            int p_id = Integer.parseInt(splitFields[1]);
            int e_id = Integer.parseInt(splitFields[2]);
            int itr_id = Integer.parseInt(splitFields[3]);
            int itr_type = Integer.parseInt(splitFields[4]);
            int hand = Integer.parseInt(splitFields[5]);
            int clt_id = Integer.parseInt(splitFields[6]);
            int state = Integer.parseInt(splitFields[15]);
            String timestamp = splitFields[16];
            String[] ch01= getEMGArr(splitFields[7]);
            String[] ch02= getEMGArr(splitFields[8]);
            String[] ch03= getEMGArr(splitFields[9]);
            String[] ch04= getEMGArr(splitFields[10]);
            String[] ch05= getEMGArr(splitFields[11]);
            String[] ch06= getEMGArr(splitFields[12]);
            String[] ch07= getEMGArr(splitFields[13]);
            String[] ch08= getEMGArr(splitFields[14]);
            ArrayList<String> emgCsv = new ArrayList<String>(16);
            for (int i =0; i <16;i++) {
                String strCom = (id*100+i)+","+p_id+","+e_id +","+itr_id +","+itr_type+","+hand+","+clt_id+","+
                        ch01[i]+","+
                        ch02[i]+","+
                        ch03[i]+","+
                        ch04[i]+","+
                        ch05[i]+","+
                        ch06[i]+","+
                        ch07[i]+","+
                        ch08[i]+","+
                        timestamp;
                emgCsv.add(strCom);
                writeText.newLine();
                writeText.write(strCom);
                System.out.println(strCom);
            }

        }catch(Exception e){

        }
    }

    @NotNull
    public static List<String> getEMGArray(BufferedWriter writeText,int id, int itr_type, int hand,int channel, @NotNull String emg,String timestamp) throws IOException {
        String emgSub = emg.substring(1,emg.length()-1);
        String emgArray[] = emgSub.split(", ");
        List<String> emgList = Arrays.asList(emgArray);
        System.out.println(emg);
        System.out.println(emgList.size());
//        ArrayList<Integer> emgList = new ArrayList<Integer>(16);
        ArrayList<String> emgCsv = new ArrayList<String>(16);
        int ini = 1;
        for (String str:emgList) {
//            int a = Integer.valueOf(str);
            String strCom = (id*1000+channel*100+ini)+","+itr_type+","+hand+","+channel+","+str+","+timestamp;//if add a “,” at the end.
//            emgList.add(a);
            writeText.newLine();
            writeText.write(strCom);
            emgCsv.add(strCom);
            System.out.println(strCom);
            ini++;

        }
        return emgCsv;
    }

    @NotNull
    public static String[] getEMGArr(String emg)  {
        String emgSub = emg.substring(1,emg.length()-1);
        String emgArray[] = emgSub.split(", ");
        return emgArray;
    }
}
