package com.example.endline.utils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.Integer.parseInt;

public class Converter {
  /**
   * Convert byte array into hexadecimal string
   */
  public static String byteArrayToHexString(byte[] data) {
    if (data == null) return "";
    return String.format("%0" + (data.length * 2) + "X", new BigInteger(1, data));
  }

  /**
   * Convert a hexadecimal String into a byte array
   */
  public static byte[] hexStringToByteArray(String s) {
    int len = s.length();
    byte[] data = new byte[len / 2];
    for (int i = 0; i < len; i += 2) {
      data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
              + Character.digit(s.charAt(i + 1), 16));
    }
    return data;
  }

  public static String hex2Decimal(String s) {
  String jobid="" ;
      String itemID ="";
      String[] characters =new String[4] ;
    for(int i=0;i<s.length()-22;i++)
    {
        if(i<=1){
            jobid = String.format(jobid + s.charAt(i));

        }
        else{
            itemID = String.format(itemID +s.charAt(i));

        }
    }
      List<String> strings = new ArrayList<String>();
      int index = 0;
      int count=0;
      while (index < itemID.length()) {
          strings.add(itemID.substring(index, Math.min(index + 2,itemID.length())));
          index += 2;

      }
Collections.reverse(strings);

      String item1= String.format(strings.get(0)+strings.get(1)+strings.get(2)+strings.get(3));
      int item = parseInt(item1, 16);
      System.out.println("dec"+item);
      int job = parseInt(jobid,16);
      System.out.println("job "+job);
      String Id = Integer.toString(item) ;

 return Id;
  }
}
