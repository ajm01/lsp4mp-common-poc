/*******************************************************************************
* Copyright (c) 2018 Red Hat Inc. and others.
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
package org.eclipse.lsp4mp.ls.commons;

import java.util.Collection;

/**
 * Snippet syntax utilities.
 *
 * @see https://github.com/Microsoft/language-server-protocol/blob/master/snippetSyntax.md
 */
public class SnippetsBuilder {

	private SnippetsBuilder() {

	}

	public static void tabstops(int index, StringBuilder snippets) {
		snippets.append("$");
		snippets.append(index);
	}

	public static String tabstops(int index) {
		StringBuilder snippets = new StringBuilder();
		snippets.append("$");
		snippets.append(index);
		return snippets.toString();
	}

	public static void placeholders(int index, String text, StringBuilder snippets) {
		snippets.append("${");
		snippets.append(index);
		snippets.append(":");
		snippets.append(escape(text));
		snippets.append("}");
	}

	/**
	 *
	 * @param index
	 * @param values
	 * @return
	 *
	 * @see https://github.com/Microsoft/language-server-protocol/blob/master/snippetSyntax.md#choice
	 */
	public static void choice(int index, Collection<String> values, StringBuilder snippets) {
		snippets.append("${");
		snippets.append(index);
		snippets.append("|");
		int i = 0;
		for (String value : values) {
			if (i > 0) {
				snippets.append(",");
			}
			snippets.append(value);
			i++;
		}
		snippets.append("|");
		snippets.append("}");
	}

	private static String escape(String text) {
		return text.replace("$", "\\$").replace("}", "\\}");
	}

}
