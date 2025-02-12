/*******************************************************************************
* Copyright (c) 2020 Red Hat Inc. and others.
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
package org.eclipse.lsp4mp.jdt.internal.config.java;

import static org.eclipse.lsp4mp.jdt.core.MicroProfileConfigConstants.CONFIG_PROPERTY_ANNOTATION;
import static org.eclipse.lsp4mp.jdt.core.MicroProfileConfigConstants.CONFIG_PROPERTY_ANNOTATION_DEFAULT_VALUE;
import static org.eclipse.lsp4mp.jdt.core.MicroProfileConfigConstants.CONFIG_PROPERTY_ANNOTATION_NAME;

import org.eclipse.lsp4mp.jdt.core.java.hover.PropertiesHoverParticipant;

/**
 *
 * MicroProfile Config Hover
 *
 * @author Angelo ZERR
 *
 * @See https://github.com/eclipse/microprofile-config
 *
 */
public class MicroProfileConfigHoverParticipant extends PropertiesHoverParticipant {

	public MicroProfileConfigHoverParticipant() {
		super(CONFIG_PROPERTY_ANNOTATION, CONFIG_PROPERTY_ANNOTATION_NAME, CONFIG_PROPERTY_ANNOTATION_DEFAULT_VALUE);
	}
}
