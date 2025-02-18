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
package org.eclipse.lsp4mp.model;

/**
 * The property key node
 *
 * @author Angelo ZERR
 *
 */
public class PropertyKey extends Node {

	@Override
	public NodeType getNodeType() {
		return NodeType.PROPERTY_KEY;
	}

	/**
	 * Returns the profile of the property key and null otherwise.
	 *
	 * <ul>
	 * <li>'%dev.key' will return 'dev'.</li>
	 * <li>'key' will return null.</li>
	 * </ul>
	 *
	 * @return the profile of the property key and null otherwise.
	 */
	public String getProfile() {
		int profileEndOffset = getEndProfileOffset();
		if (profileEndOffset != -1) {
			String fulltext = getOwnerModel().getText();
			return fulltext.substring(getStart() + 1, profileEndOffset);
		}
		return null;
	}

	/**
	 * Returns the property name without the profile of the property key and null
	 * otherwise.
	 *
	 * For multiline property names, this method returns the property name with the
	 * backslashes and newlines removed.
	 *
	 * <ul>
	 * <li>'%dev.' will return null.</li>
	 * <li>'%dev.key' will return 'key'.</li>
	 * <li>'key' will return 'key'.</li>
	 * <li>'key1.\ key2.\ key3' will return 'key1.key2.key3'</li>
	 * </ul>
	 *
	 * @return the property name without the profile of the property key and null
	 *         otherwise.
	 */
	public String getPropertyName() {
		int profileEndOffset = getEndProfileOffset();
		if (profileEndOffset != -1) {
			int end = getEnd();
			if (profileEndOffset < end) {
				return getOwnerModel().getText(profileEndOffset + 1, end, true);
			}
			return null;
		}
		return getText(true);
	}

	/**
	 * Returns the property name with the profile of the property key and null
	 * otherwise.
	 *
	 * For multiline property names, this method returns the property name with the
	 * profile, with backslashes and newlines removed.
	 *
	 * <ul>
	 * <li>'%dev.' will return '%dev.'.</li>
	 * <li>'%dev.key' will return '%dev.key'.</li>
	 * <li>'key' will return 'key'.</li>
	 * <li>'%dev.\' 'key1.\ key2' will return '%dev.key1.key2'</li>
	 * </ul>
	 *
	 * @return the property name with the profile of the property key and null
	 *         otherwise.
	 */
	public String getPropertyNameWithProfile() {
		return getText(true);
	}

	/**
	 * Returns true if the given offset is before the profile and false otherwise.
	 *
	 * @param offset the offset
	 * @return true if the given offset is before the profile and false otherwise.
	 */
	public boolean isBeforeProfile(int offset) {
		int profileEndOffset = getEndProfileOffset();
		if (profileEndOffset == -1) {
			return false;
		}
		return profileEndOffset >= offset;
	}

	/**
	 *
	 * @return
	 */
	private int getEndProfileOffset() {
		int start = getStart();
		int end = getEnd();
		if (start == -1 || end == -1) {
			return -1;
		}
		String fulltext = getOwnerModel().getText();
		if (start >= fulltext.length()) {
			return -1;
		}
		int i = start;
		if (fulltext.charAt(i) == '%') {
			while (i < end) {
				char c = fulltext.charAt(i);
				if (c == '.') {
					break;
				}
				i++;
			}
			return i;
		}
		return -1;
	}

	@Override
	public Property getParent() {
		return (Property) super.getParent();
	}

	/**
	 * Returns the owner property.
	 * 
	 * @return the owner property.
	 */
	public Property getProperty() {
		return getParent();
	}

}
