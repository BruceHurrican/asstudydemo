/*
 * BruceHurrican
 * Copyright (c) 2016.
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 *    This document is Bruce's individual learning the android demo, wherein the use of the code from the Internet, only to use as a learning exchanges.
 *    And where any person can download and use, but not for commercial purposes.
 *    Author does not assume the resulting corresponding disputes.
 *    If you have good suggestions for the code, you can contact BurrceHurrican@foxmail.com
 *    本文件为Bruce's个人学习android的作品, 其中所用到的代码来源于互联网，仅作为学习交流使用。
 *    任和何人可以下载并使用, 但是不能用于商业用途。
 *    作者不承担由此带来的相应纠纷。
 *    如果对本代码有好的建议，可以联系BurrceHurrican@foxmail.com
 */

package com.bruce.demo;

import java.util.regex.Pattern;

/**
 * Created by BruceHurrican on 16/9/14.
 */
public class AA {
    public static void main(String[] args) {
        test02();
//        test01();
//        test03();
    }

    private static void test02() {
        String ss = "33.25698";
        ss = "  ";
        System.out.println(ss.trim().length());
        System.out.println(ss.contains("."));
        System.out.println(ss.indexOf("."));
        System.out.println(ss.length() + "--" + ss.substring(0, ss.indexOf(".") + 3));
    }

    private static void test01() {
        String regex = "(\\(\\d{3,4}\\)|\\d{3,4}-|\\s)?\\d{7,8}";
//        String regex = "(\\(\\d{3,4}\\)|\\d{3,4}-|\\s|\\d{3,4})?\\d{7,8}";
        boolean isTel = Pattern.matches(regex, "07551234567");
        System.out.println("istel: " + isTel);
    }

    /**
     * 验证手机号码(支持国际格式, +86135xxxxxxxx(中国内地), +00852137xxxx...(香港))
     *
     * @return true 是手机号, false 不是手机号
     */
    private static void test03() {
        String regex = "(\\+\\d+)?1[3458]\\d{9}$";
        boolean isPhone = Pattern.matches(regex, "12345678901");
        System.out.println("isPhone: " + isPhone);
    }
}
