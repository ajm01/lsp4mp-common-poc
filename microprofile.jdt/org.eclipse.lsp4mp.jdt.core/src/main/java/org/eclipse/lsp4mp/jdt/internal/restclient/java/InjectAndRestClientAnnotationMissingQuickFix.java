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
package org.eclipse.lsp4mp.jdt.internal.restclient.java;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4mp.commons.codeaction.MicroProfileCodeActionId;
import org.eclipse.lsp4mp.jdt.core.MicroProfileConfigConstants;
import org.eclipse.lsp4mp.jdt.core.java.codeaction.InsertAnnotationMissingQuickFix;
import org.eclipse.lsp4jdt.core.java.codeaction.JavaCodeActionContext;
import org.eclipse.lsp4mp.jdt.core.utils.JDTTypeUtils;
import org.eclipse.lsp4mp.jdt.internal.restclient.MicroProfileRestClientConstants;
import org.eclipse.lsp4mp.jdt.internal.restclient.MicroProfileRestClientErrorCode;

/**
 * QuickFix for fixing
 * {@link MicroProfileRestClientErrorCode#InjectAndRestClientAnnotationMissing}
 * error by providing several code actions:
 *
 * <ul>
 * <li>Insert @Inject and @RestClient annotation and the proper import.</li>
 * </ul>
 *
 * @author Angelo ZERR
 *
 */
public class InjectAndRestClientAnnotationMissingQuickFix extends InsertAnnotationMissingQuickFix {

	public InjectAndRestClientAnnotationMissingQuickFix() {
		super(true, MicroProfileConfigConstants.INJECT_JAKARTA_ANNOTATION,
				MicroProfileConfigConstants.INJECT_JAVAX_ANNOTATION,
				MicroProfileRestClientConstants.REST_CLIENT_ANNOTATION);
	}

	@Override
	public String getParticipantId() {
		return InjectAndRestClientAnnotationMissingQuickFix.class.getName();
	}

	@Override
	protected void insertAnnotations(Diagnostic diagnostic, JavaCodeActionContext context, List<CodeAction> codeActions)
			throws CoreException {
		String[] annotations = getAnnotations();
		String injectAnnotation = null;
		for (String annotation : annotations) {
			if (JDTTypeUtils.findType(context.getJavaProject(), annotation) != null && injectAnnotation == null
					&& (annotation.equals(MicroProfileConfigConstants.INJECT_JAVAX_ANNOTATION)
							|| annotation.equals(MicroProfileConfigConstants.INJECT_JAKARTA_ANNOTATION))) {
				injectAnnotation = annotation;
			}
		}
		if (injectAnnotation != null) {
			insertAnnotation(diagnostic, context, codeActions, injectAnnotation,
					MicroProfileRestClientConstants.REST_CLIENT_ANNOTATION);
		} else {
			insertAnnotation(diagnostic, context, codeActions, MicroProfileRestClientConstants.REST_CLIENT_ANNOTATION);
		}
	}

	@Override
	protected MicroProfileCodeActionId getCodeActionId() {
		return MicroProfileCodeActionId.InsertInjectAndRestClientAnnotations;
	}
}
