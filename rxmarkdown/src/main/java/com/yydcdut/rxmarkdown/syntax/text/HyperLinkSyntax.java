/*
 * Copyright (C) 2016 yydcdut (yuyidong2015@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.yydcdut.rxmarkdown.syntax.text;

import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableStringBuilder;

import com.yydcdut.rxmarkdown.RxMDConfiguration;
import com.yydcdut.rxmarkdown.callback.OnLinkClickCallback;
import com.yydcdut.rxmarkdown.span.MDURLSpan;
import com.yydcdut.rxmarkdown.syntax.SyntaxKey;

import java.util.regex.Pattern;

/**
 * The implementation of syntax for hyper link.
 * syntax:
 * "[content](http://link.html)"
 * <p>
 * Created by yuyidong on 16/5/14.
 */
class HyperLinkSyntax extends TextSyntaxAdapter {


    private int mColor;
    private boolean isUnderLine;
    private OnLinkClickCallback mOnLinkClickCallback;

    public HyperLinkSyntax(@NonNull RxMDConfiguration rxMDConfiguration) {
        super(rxMDConfiguration);
        mColor = rxMDConfiguration.getLinkColor();
        isUnderLine = rxMDConfiguration.isLinkUnderline();
        mOnLinkClickCallback = rxMDConfiguration.getOnLinkClickCallback();
    }

    @Override
    boolean isMatch(@NonNull String text) {
        if (!(text.contains(SyntaxKey.KEY_HYPER_LINK_LEFT) && text.contains(SyntaxKey.KEY_HYPER_LINK_MIDDLE) && text.contains(SyntaxKey.KEY_HYPER_LINK_RIGHT))) {
            return false;
        }
        Pattern pattern = Pattern.compile(".*[\\[]{1}.*[\\](]{1}.*[)]{1}.*");
        return pattern.matcher(text).matches();
    }

    @NonNull
    @Override
    SpannableStringBuilder encode(@NonNull SpannableStringBuilder ssb) {
        int index0;
        while (true) {
            String text = ssb.toString();
            index0 = text.indexOf(SyntaxKey.KEY_HYPER_LINK_BACKSLASH_VALUE_LEFT);
            if (index0 == -1) {
                break;
            }
            ssb.replace(index0, index0 + SyntaxKey.KEY_HYPER_LINK_BACKSLASH_VALUE_LEFT.length(), SyntaxKey.KEY_ENCODE);
        }
        int index1;
        while (true) {
            String text = ssb.toString();
            index1 = text.indexOf(SyntaxKey.KEY_HYPER_LINK_BACKSLASH_VALUE_MIDDLE);
            if (index1 == -1) {
                break;
            }
            ssb.replace(index1, index1 + SyntaxKey.KEY_HYPER_LINK_BACKSLASH_VALUE_MIDDLE.length(), SyntaxKey.KEY_ENCODE_1);
        }
        int index3;
        while (true) {
            String text = ssb.toString();
            index3 = text.indexOf(SyntaxKey.KEY_HYPER_LINK_BACKSLASH_VALUE_RIGHT);
            if (index3 == -1) {
                break;
            }
            ssb.replace(index3, index3 + SyntaxKey.KEY_HYPER_LINK_BACKSLASH_VALUE_RIGHT.length(), SyntaxKey.KEY_ENCODE_3);
        }
        return ssb;
    }

    @Override
    SpannableStringBuilder format(@NonNull SpannableStringBuilder ssb) {
        String text = ssb.toString();
        return parse(text, ssb);
    }

    @NonNull
    @Override
    SpannableStringBuilder decode(@NonNull SpannableStringBuilder ssb) {
        int index0;
        while (true) {
            String text = ssb.toString();
            index0 = text.indexOf(SyntaxKey.KEY_ENCODE);
            if (index0 == -1) {
                break;
            }
            ssb.replace(index0, index0 + SyntaxKey.KEY_ENCODE.length(), SyntaxKey.KEY_HYPER_LINK_BACKSLASH_VALUE_LEFT);
        }
        int index1;
        while (true) {
            String text = ssb.toString();
            index1 = text.indexOf(SyntaxKey.KEY_ENCODE_1);
            if (index1 == -1) {
                break;
            }
            ssb.replace(index1, index1 + SyntaxKey.KEY_ENCODE_1.length(), SyntaxKey.KEY_HYPER_LINK_BACKSLASH_VALUE_MIDDLE);
        }
        int index3;
        while (true) {
            String text = ssb.toString();
            index3 = text.indexOf(SyntaxKey.KEY_ENCODE_3);
            if (index3 == -1) {
                break;
            }
            ssb.replace(index3, index3 + SyntaxKey.KEY_ENCODE_3.length(), SyntaxKey.KEY_HYPER_LINK_BACKSLASH_VALUE_RIGHT);
        }
        return ssb;
    }

    /**
     * parse
     *
     * @param text the original content,the class type is {@link String}
     * @param ssb  the original content,the class type is {@link SpannableStringBuilder}
     * @return the content after parsing
     */
    @NonNull
    private SpannableStringBuilder parse(@NonNull String text, @NonNull SpannableStringBuilder ssb) {
        SpannableStringBuilder tmp = new SpannableStringBuilder();
        String tmpTotal = text;
        while (true) {
            int position4Key0 = tmpTotal.indexOf(SyntaxKey.KEY_HYPER_LINK_LEFT);
            int position4Key1 = tmpTotal.indexOf(SyntaxKey.KEY_HYPER_LINK_MIDDLE);
            int position4Key2 = tmpTotal.indexOf(SyntaxKey.KEY_HYPER_LINK_RIGHT);
            if (position4Key0 == -1 || position4Key1 == -1 || position4Key2 == -1) {
                break;
            }
            if (position4Key0 < position4Key1 && position4Key1 < position4Key2) {
                //处理aa[bb[b](cccc)dddd
                int tmpCenter = tmpTotal.indexOf(SyntaxKey.KEY_HYPER_LINK_MIDDLE);
                String tmpLeft = tmpTotal.substring(0, tmpCenter);
                //正常流程
                int positionHeader = tmpLeft.lastIndexOf(SyntaxKey.KEY_HYPER_LINK_LEFT);
                tmp.append(tmpTotal.substring(0, positionHeader));
                int index = tmp.length();
                tmpTotal = tmpTotal.substring(positionHeader + SyntaxKey.KEY_HYPER_LINK_LEFT.length(), tmpTotal.length());
                int positionCenter = tmpTotal.indexOf(SyntaxKey.KEY_HYPER_LINK_MIDDLE);
                ssb.delete(tmp.length(), tmp.length() + SyntaxKey.KEY_HYPER_LINK_LEFT.length());
                tmp.append(tmpTotal.substring(0, positionCenter));
                tmpTotal = tmpTotal.substring(positionCenter + SyntaxKey.KEY_HYPER_LINK_MIDDLE.length(), tmpTotal.length());
                int positionFooter = tmpTotal.indexOf(SyntaxKey.KEY_HYPER_LINK_RIGHT);
                String link = tmpTotal.substring(0, positionFooter);
                ssb.setSpan(new MDURLSpan(link, mColor, isUnderLine, mOnLinkClickCallback), index, tmp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                ssb.delete(tmp.length(), tmp.length() + SyntaxKey.KEY_HYPER_LINK_MIDDLE.length() + link.length() + SyntaxKey.KEY_HYPER_LINK_RIGHT.length());
                tmpTotal = tmpTotal.substring(positionFooter + SyntaxKey.KEY_HYPER_LINK_RIGHT.length(), tmpTotal.length());
            } else if (position4Key0 < position4Key1 && position4Key0 < position4Key2 && position4Key2 < position4Key1) {
                //111[22)22](33333)
                tmpTotal = replaceFirstOne(tmpTotal, SyntaxKey.KEY_HYPER_LINK_RIGHT, SyntaxKey.PLACE_HOLDER);
            } else if (position4Key1 < position4Key0 && position4Key1 < position4Key2) {
                //](在最前面的情况 111](2222[333)4444  1111](2222)3333[4444
                tmp.append(tmpTotal.substring(0, position4Key1 + SyntaxKey.KEY_HYPER_LINK_MIDDLE.length()));
                tmpTotal = tmpTotal.substring(position4Key1 + SyntaxKey.KEY_HYPER_LINK_MIDDLE.length(), tmpTotal.length());
            } else if (position4Key2 < position4Key0 && position4Key2 < position4Key1) {
                //)在最前面的情况 111)2222](333[4444  1111)2222[3333](4444
                tmp.append(tmpTotal.substring(0, position4Key2 + SyntaxKey.KEY_HYPER_LINK_RIGHT.length()));
                tmpTotal = tmpTotal.substring(position4Key2 + SyntaxKey.KEY_HYPER_LINK_RIGHT.length(), tmpTotal.length());
            }
        }
        return ssb;
    }

    /**
     * replace the key words
     *
     * @param content     the original content
     * @param target      the key words
     * @param replacement the replacement string
     * @return
     */
    @NonNull
    private String replaceFirstOne(@NonNull String content, @NonNull String target, @NonNull String replacement) {
        if (target == null) {
            throw new NullPointerException("target == null");
        }
        if (replacement == null) {
            throw new NullPointerException("replacement == null");
        }
        int matchStart = content.indexOf(target, 0);
        if (matchStart == -1) {
            return content;
        }
        int targetLength = target.length();
        if (targetLength == 0) {
            int resultLength = content.length() + (content.length() + 1) * replacement.length();
            StringBuilder result = new StringBuilder(resultLength);
            result.append(replacement);
            for (int i = 0; i != content.length(); ++i) {
                result.append(content.charAt(i));
                result.append(replacement);
            }
            return result.toString();
        }
        StringBuilder result = new StringBuilder(content.length());
        for (int i = 0; i < matchStart; ++i) {
            result.append(content.charAt(i));
        }
        result.append(replacement);
        int over = matchStart + targetLength;
        for (int i = over; i < content.length(); ++i) {
            result.append(content.charAt(i));
        }
        return result.toString();
    }
}
