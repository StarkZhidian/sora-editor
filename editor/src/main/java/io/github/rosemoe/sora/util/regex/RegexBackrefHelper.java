/*
 *    sora-editor - the awesome code editor for Android
 *    https://github.com/Rosemoe/sora-editor
 *    Copyright (C) 2020-2025  Rosemoe
 *
 *     This library is free software; you can redistribute it and/or
 *     modify it under the terms of the GNU Lesser General Public
 *     License as published by the Free Software Foundation; either
 *     version 2.1 of the License, or (at your option) any later version.
 *
 *     This library is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public
 *     License along with this library; if not, write to the Free Software
 *     Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 *     USA
 *
 *     Please contact Rosemoe by email 2073412493@qq.com if you need
 *     additional information or have any questions
 */
package io.github.rosemoe.sora.util.regex;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.regex.Matcher;

public class RegexBackrefHelper {

    public static String computeReplacement(@NonNull Matcher matcher, @NonNull RegexBackrefGrammar grammar, @NonNull String replacementPattern) {
        var parser = new RegexBackrefParser(grammar);
        var tokens = parser.parse(replacementPattern, matcher.groupCount());
        return computeReplacement(matcher, tokens);
    }

    public static String computeReplacement(@NonNull Matcher matcher, @NonNull List<RegexBackrefToken> tokens) {
        var sb = new StringBuilder();
        for (var token : tokens) {
            if (token.isReference()) {
                String text = matcher.group(token.getGroup());
                sb.append(text == null ? "" : text);
            } else {
                sb.append(token.getText());
            }
        }
        return sb.toString();
    }

}
