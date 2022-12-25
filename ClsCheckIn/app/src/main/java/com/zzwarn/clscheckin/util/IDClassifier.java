package com.zzwarn.clscheckin.util;

import java.util.regex.Pattern;

public class IDClassifier {
    final static String stu_re = "^[sS]\\d{8}$";
    final static String tchr_re  = "^[tT]\\d{8}$";

    public static IdType judge(String id){
        Pattern stu_pattern = Pattern.compile(stu_re);
        Pattern tchr_pattern  = Pattern.compile(tchr_re);
        boolean stu_flag = stu_pattern.matcher(id).find();
        boolean tchr_flag = tchr_pattern.matcher(id).find();
        if (stu_flag || tchr_flag){
            if (stu_flag){
                return IdType.STU;
            }else if (tchr_flag){
                return IdType.TCHR;
            }
        }
        return IdType.NONE;
    }
}
