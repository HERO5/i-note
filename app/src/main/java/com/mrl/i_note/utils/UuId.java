package com.mrl.i_note.utils;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Random;

/**
 * Created by apple on 2017/12/12.
 */

public class UuId {

    private static String codes = "AB01CD23EF45GHI6JKL789LMNOPQRSTUVWXYZ";
    private static Random radom = new Random();

    public static UuId newIntance(){
        return new UuId();
    }

    public static String getId(){
        StringBuffer id = new StringBuffer("");
        String baseId = Long.toString(System.currentTimeMillis());
        //3位置插入两位，6个位置插入三位，末尾插入五
        id.append(baseId.substring(0, 3)).append(getRandomCode()).append(getRandomCode()).append(baseId.substring(3, 6));
        id.append(getRandomCode()).append(getRandomCode()).append(getRandomCode()).append(baseId.substring(6));
        for(int i=0;i<5;i++){
            id.append(getRandomCode());
        }
        return id.toString();
    }

    private static char getRandomCode(){
        return codes.charAt(radom.nextInt(codes.length()));
    }

    public String dealPassword(String password){
        password = password.toUpperCase();
        String newPass = "";
        for(int i=0;i<password.length();i++){
            String flag = Integer.toHexString(codes.indexOf(password.charAt(i))*7);
            if(flag.length()!=2){
                newPass += "F"+flag;
            }else{
                newPass += flag;
            }
        }
        return newPass;
    }

    /**
     * 生成UUID
     *
     * @return UUID
     */
    public static String generateGUID() {
        return new BigInteger(165, new Random()).toString(36).toUpperCase();
    }

    public static String getIdByDate(){
        Calendar now = new GregorianCalendar();
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        String fileName = simpleDate.format(now.getTime());
        return fileName;
    }
}
