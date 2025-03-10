/**
 * Copyright (c) 2015-2017 Angelo ZERR.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Initial code from https://github.com/atom/node-oniguruma
 * Initial copyright Copyright (c) 2013 GitHub Inc.
 * Initial license: MIT
 *
 * Contributors:
 * - GitHub Inc.: Initial code, written in JavaScript, licensed under MIT license
 * - Angelo Zerr <angelo.zerr@gmail.com> - translation and adaptation to Java
 */
package org.eclipse.tm4e.core.internal.oniguruma;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.tm4e.core.TMException;
import org.joni.exception.JOniException;

/**
 * @see <a href="https://github.com/atom/node-oniguruma/blob/master/src/onig-searcher.cc">
 *      github.com/atom/node-oniguruma/blob/master/src/onig-searcher.cc</a>
 */
final class OnigSearcher {

	private final List<OnigRegExp> regExps;

	OnigSearcher(final List<String> regExps) {
		this.regExps = regExps.stream().map(OnigSearcher::createRegExp).collect(Collectors.toList());
	}

	private static OnigRegExp createRegExp(String exp) {
		// workaround for regular expressions that are unsupported by joni
		// from https://github.com/JetBrains/intellij-community/blob/881c9bc397b850bad1d393a67bcbc82861d55d79/plugins/textmate/core/src/org/jetbrains/plugins/textmate/regex/joni/JoniRegexFactory.kt#L32
		try {
			return new OnigRegExp(exp);
		} catch (TMException e) {
			if (e.getCause() instanceof JOniException) {
				e.printStackTrace();
				return new OnigRegExp("^$");
			} else {
				throw e;
			}
		}
	}

	@Nullable
	OnigResult search(final OnigString source, final int charOffset) {
		final int byteOffset = source.getByteIndexOfChar(charOffset);

		int bestLocation = 0;
		OnigResult bestResult = null;
		int index = 0;

		for (final OnigRegExp regExp : regExps) {
			final OnigResult result = regExp.search(source, byteOffset);
			if (result != null && result.count() > 0) {
				final int location = result.locationAt(0);

				if (bestResult == null || location < bestLocation) {
					bestLocation = location;
					bestResult = result;
					bestResult.setIndex(index);
				}

				if (location == byteOffset) {
					break;
				}
			}
			index++;
		}
		return bestResult;
	}
}
