/*******************************************************************************
* Copyright (c) 2019 Red Hat Inc. and others.
*
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v. 2.0 which is available at
* http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
* which is available at https://www.apache.org/licenses/LICENSE-2.0.
*
* SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
*
* Contributors:
*     Red Hat Inc. - initial API and implementation
*******************************************************************************/
package org.eclipse.lsp4mp.commons.metadata;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.quarkus.runtime.util.StringUtil;

/**
 * Converter kind.
 *
 * @author Angelo ZERR
 *
 */
public enum ConverterKind {

	KEBAB_CASE(1), VERBATIM(2);

	private final int value;

	ConverterKind(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static ConverterKind forValue(int value) {
		ConverterKind[] allValues = ConverterKind.values();
		if (value < 1 || value > allValues.length)
			throw new IllegalArgumentException("Illegal enum value: " + value);
		return allValues[value - 1];
	}

	private static final String HYPHEN = "-";
	private static final Pattern PATTERN = Pattern.compile("([-_]+)");

	public static String convert(String value, ConverterKind converterKind) {
		if (converterKind == null || value == null) {
			return value;
		}

		switch (converterKind) {
		case KEBAB_CASE:
			return hyphenate(value);
		default:
			return value;
		}

	}

	/**
	 * Convert the given value to kebab case.
	 *
	 * @param value
	 * @return
	 * @see https://github.com/quarkusio/quarkus/blob/1457e12766d46507674f8e8d8391fc5a5e8f0103/core/runtime/src/main/java/io/quarkus/runtime/configuration/HyphenateEnumConverter.java#L54
	 */
	private static String hyphenate(String value) {
		StringBuffer target = new StringBuffer();
		String hyphenate = StringUtil.hyphenate(value);
		Matcher matcher = PATTERN.matcher(hyphenate);
		while (matcher.find()) {
			matcher.appendReplacement(target, HYPHEN);
		}
		matcher.appendTail(target);
		return target.toString();
	}
}
