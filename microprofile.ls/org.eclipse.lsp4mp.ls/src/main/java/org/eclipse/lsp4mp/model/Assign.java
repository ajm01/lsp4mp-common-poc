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
 * Assign node
 *
 * @author Angelo ZERR
 *
 */
public class Assign extends Node {

	@Override
	public NodeType getNodeType() {
		return NodeType.ASSIGN;
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
