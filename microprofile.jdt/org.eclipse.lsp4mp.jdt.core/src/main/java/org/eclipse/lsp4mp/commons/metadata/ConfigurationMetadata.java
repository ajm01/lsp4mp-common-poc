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

import java.util.List;

/**
 * Configuration metadata
 *
 * @author Angelo ZERR
 *
 * @see https://docs.spring.io/spring-boot/docs/current/reference/html/appendix-configuration-metadata.html
 */
public class ConfigurationMetadata {

	private List<ItemMetadata> properties;

	private List<ItemHint> hints;

	public List<ItemMetadata> getProperties() {
		return properties;
	}

	public void setProperties(List<ItemMetadata> properties) {
		this.properties = properties;
	}

	public List<ItemHint> getHints() {
		return hints;
	}

	public void setHints(List<ItemHint> hints) {
		this.hints = hints;
	}

	/**
	 * Returns the item hint from the given item metadata name or type and null
	 * otherwise.
	 *
	 * @param property the item metadata
	 * @return the item hint from the given item metadata name or type and null
	 *         otherwise.
	 */
	public ItemHint getHint(ItemMetadata property) {
		return getHint(property.getName(), property.getHintType());
	}

	/**
	 * Returns the item hint from the given possible hint and null otherwise.
	 *
	 * @param hint possibles hint
	 * @return the item hint from the given possible hint and null otherwise.
	 */
	public ItemHint getHint(String... hint) {
		if (hints == null || hint == null) {
			return null;
		}
		for (ItemHint itemHint : hints) {
			for (String name : hint) {
				if (itemHint.getName().equals(name)) {
					return itemHint;
				}
			}
		}
		return null;
	}

}
