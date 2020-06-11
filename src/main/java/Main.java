import Entity.TTTRrecord;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Parser;
import me.tongfei.progressbar.ProgressBar;

import java.awt.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException {
        int NUMBEROFPHOTONS = 10000000;
        int NUMBEROFPHOTONStoEND = 100000;
        double bin = 0.000001;
        double duration = 0.01;

        File file = new File("D:\\Work\\Correlations\\3YYB_I0_Ch0_1k_Ch1_1k_1h.ptu");

        InputStream inputStream = new FileInputStream(file);

        byte[] buffer = new byte[8];
        inputStream.read(buffer);
        String magic = new String(buffer).trim();
        System.out.println(magic);
        buffer = new byte[8];
        inputStream.read(buffer);
        String version = new String(buffer).trim();
        System.out.println(version);

        Map<String, String> headers = new HashMap<>();
        //header
        while (true) {
            String tagString = "";

            buffer = new byte[32];
            inputStream.read(buffer);
            String tagIdent = new String(buffer, "UTF-8").trim();
            buffer = new byte[4];
            inputStream.read(buffer);
            int tagIdx = byteArrayToShortLE(buffer, 0);

            buffer = new byte[4];
            inputStream.read(buffer);
            //int tagTyp = byteArrayToShortLE(buffer, 0);
            String tagTyp = new String(buffer);
            tagTyp = "";
            for (byte b : buffer) {
                tagTyp += recursiveReverse(byteToHex(b));
            }
            tagTyp = recursiveReverse(tagTyp);

//            if (tagIdx > -1) {
//                evalName = tagIdent + '(' + tagIdx + ')';
//            } else {
//                evalName = tagIdent;
//            }
//            buffer = new byte[8];
//            inputStream.read(buffer);
//           // int tagInt =  byteArrayToIntLE(buffer, 0);
//            String tagInt = new String(buffer , "UTF-8");
//            System.out.println(tagInt);
//            System.out.println();


            if (tagTyp.equalsIgnoreCase("4001FFFF")) {
                buffer = new byte[8];
                inputStream.read(buffer);
                int tagInt = byteArrayToShortLE(buffer, 0);

                buffer = new byte[tagInt];
                inputStream.read(buffer);
                tagString = new String(buffer).trim();
            }
            if (tagTyp.equalsIgnoreCase("FFFF0008")) { //tyEmpty8
                buffer = new byte[8];
                inputStream.read(buffer);
            }
            if (tagTyp.equalsIgnoreCase("00000008")) { //tyBool8
                buffer = new byte[8];
                inputStream.read(buffer);
                int tagInt = byteArrayToShortLE(buffer, 0);
                if (tagInt == 0) {
                    tagString = "False";
                } else {
                    tagString = "True";
                }
            }
            if (tagTyp.equalsIgnoreCase("10000008")) { //tyInt8
                buffer = new byte[8];
                inputStream.read(buffer);
                int tagInt = byteArrayToIntLE(buffer, 0);
                tagString = "" + tagInt;
            }
            if (tagTyp.equalsIgnoreCase("11000008")) { //tyBitSet64
                buffer = new byte[8];
                inputStream.read(buffer);
                int tagInt = byteArrayToShortLE(buffer, 0);
                tagString = "" + tagInt;
            }
            if (tagTyp.equalsIgnoreCase("12000008")) { //tyColor8
                buffer = new byte[8];
                inputStream.read(buffer);
                int tagInt = byteArrayToShortLE(buffer, 0);
                tagString = "" + tagInt;
            }
            if (tagTyp.equalsIgnoreCase("20000008")) { //tyFloat8
                buffer = new byte[8];
                inputStream.read(buffer);
                double tagInt = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN).getDouble();
                tagString = "" + tagInt;
            }
            if (tagTyp.equalsIgnoreCase("21000008")) { //tyTDateTime
                buffer = new byte[8];
                inputStream.read(buffer);
                int tagInt = byteArrayToShortLE(buffer, 0);
                tagString = "" + tagInt;
            }
            if (tagTyp.equalsIgnoreCase("2001FFFF")) { //tyFloat8Array
                buffer = new byte[8];
                inputStream.read(buffer);
                int tagInt = byteArrayToShortLE(buffer, 0);
                tagString = "" + tagInt;
            }
            if (tagTyp.equalsIgnoreCase("4002FFFF")) { //tyWideString
                buffer = new byte[8];
                inputStream.read(buffer);
                int tagInt = byteArrayToShortLE(buffer, 0);
                System.out.println(tagInt);
                buffer = new byte[tagInt];
                inputStream.read(buffer);
                tagString = new String(buffer).trim();

            }
            if (tagTyp.equalsIgnoreCase("FFFFFFFF")) { //tyBinaryBlob
                buffer = new byte[8];
                inputStream.read(buffer);
                int tagInt = byteArrayToShortLE(buffer, 0);
                tagString = "" + tagInt;
            }

            headers.put(tagIdent, tagString);


            if (tagIdent.equals("Header_End")) {
                break;
            }
        }

        System.out.println(headers.get("TTResult_NumberOfRecords"));
        System.out.println(headers.get("MeasDesc_GlobalResolution"));

        double globclock = Double.valueOf(headers.get("MeasDesc_GlobalResolution"));
        int numRec = Integer.parseInt(headers.get("TTResult_NumberOfRecords"));

        long ofltime = 0;
        int WRAPAROUND = 210698240;
        int recordData = 0;
        long nsync = 0;
        int chan = 0;
        int dtime = 0;
        int markers = 0;
        int frameNb = 1;
        long syncCountPerLine = 0;
        long sync_start_count = 0;
        long nLines = 0;
        int timeTag = 0;
        int channel = 0;
        int valid = 0;
        int overflows = 0;
        int route = 0;
        double truetime = 0;
        int reserved = 0;
        Boolean insideLine = false;
        try (ProgressBar pb = new ProgressBar("Test", numRec)) {
            ArrayList<TTTRrecord> tttRrecords = new ArrayList<>();
            for (int n = 0; n < numRec; n++) {
                pb.step();
                TTTRrecord tttRrecord = new TTTRrecord();
                byte[] record = new byte[4];
                inputStream.read(record);

                recordData = ((record[3] & 0xFF) << 24) | ((record[2] & 0xFF) << 16) | ((record[1] & 0xFF) << 8) | (record[0] & 0xFF); //Convert from little endian uint32

                timeTag = (recordData >> 4) & 268435455;
                channel = (recordData >> 28) & 0xF;

                //System.out.println(Integer.toBinaryString(recordData));
                if (channel == 15) { // 15 == 0xF
                    markers = (recordData << 28) & 0xF;
                    if (markers == 0) {
                        // System.out.println(n + " :  OVF");
                        ofltime += WRAPAROUND;
                        //System.out.println(ofltime);
                        overflows++;
                    }
                } else {
                    truetime = (ofltime + timeTag) * globclock;
                    tttRrecord.setChannel(channel);
                    tttRrecord.setTimeTag(timeTag);
                    tttRrecord.setTrueTime(truetime);
                    tttRrecords.add(tttRrecord);
                    // System.out.println(n + " : CHN " + channel + " " + timeTag + " " + truetime);
                }

                boolean finish = false;
                if (numRec - n < NUMBEROFPHOTONStoEND) {
                    finish = true;
                }


                if (tttRrecords.size() > NUMBEROFPHOTONS) {

                    System.out.println("start calculate");
                    ArrayList<Double> differences = calculate(tttRrecords, finish);
                    System.out.println("finish");
                }


            }
        }

    }

    public static short byteArrayToShortLE(final byte[] b, final int offset) {
        short value = 0;
        for (int i = 0; i < 2; i++) {
            value |= (b[i + offset] & 0x000000FF) << (i * 8);
        }

        return value;
    }

    public static int byteArrayToIntLE(final byte[] b, final int offset) {
        int value = 0;

        for (int i = 0; i < 4; i++) {
            value |= ((int) b[i + offset] & 0x000000FF) << (i * 8);
        }

        return value;
    }

    public static String byteToHex(byte num) {
        char[] hexDigits = new char[2];
        hexDigits[0] = Character.forDigit((num >> 4) & 0xF, 16);
        hexDigits[1] = Character.forDigit((num & 0xF), 16);
        return new String(hexDigits);
    }

    public static byte hexToByte(String hexString) {
        int firstDigit = toDigit(hexString.charAt(0));
        int secondDigit = toDigit(hexString.charAt(1));
        return (byte) ((firstDigit << 4) + secondDigit);
    }

    private static int toDigit(char hexChar) {
        int digit = Character.digit(hexChar, 16);
        if (digit == -1) {
            throw new IllegalArgumentException(
                    "Invalid Hexadecimal Character: " + hexChar);
        }
        return digit;
    }

    static String recursiveReverse(String s) {
        if ((null == s) || (s.length() <= 1)) {
            return s;
        }
        return recursiveReverse(s.substring(1)) + s.charAt(0);
    }

    static ArrayList<Double> calculate(ArrayList<TTTRrecord> tttRecords, Boolean finish) {
        ArrayList<Double> difference = new ArrayList<>();
        for (int i = 0; i < tttRecords.size(); i++) {
            TTTRrecord tttRrecord = tttRecords.get(i);
            if (tttRrecord.getChannel() == 0) {
                for (int n = i + 1; n < tttRecords.size(); n++) {
                    TTTRrecord tttRrecordCompare = tttRecords.get(n);
                    if (tttRrecordCompare.getChannel() == 1) {
                        double differense = tttRrecordCompare.getTrueTime() - tttRrecord.getTrueTime();
                        difference.add(differense);
                        // System.out.println((tttRrecordCompare.getTrueTime()));
                    }

                    if ((tttRrecordCompare.getTrueTime() - tttRrecord.getTrueTime()) > 0.001) {
                        break;
                    }
                }
            }
            if (!finish & (tttRecords.size() - i) < 10000) {

                for (int del = 0; (tttRecords.size() - del) < 10000; del++) {
                    tttRecords.remove(tttRecords.get(i));
                }
                break;
            }

        }
        return difference;
    }


}

