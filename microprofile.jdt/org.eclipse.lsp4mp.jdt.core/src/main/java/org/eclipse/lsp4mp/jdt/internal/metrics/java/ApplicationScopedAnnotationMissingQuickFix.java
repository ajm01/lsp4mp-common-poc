/*******************************************************************************
* Copyright (c) 2020 IBM Corporation and others.
*
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v. 2.0 which is available at
* http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
* which is available at https://www.apache.org/licenses/LICENSE-2.0.
*
* SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
*
* Contributors:
*     IBM Corporation - initial API and implementation
*******************************************************************************/
package org.eclipse.lsp4mp.jdt.internal.metrics.java;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.internal.corext.dom.Bindings;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.CodeActionKind;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.WorkspaceEdit;
import org.eclipse.lsp4jdt.commons.codeaction.CodeActionResolveData;
import org.eclipse.lsp4mp.commons.codeaction.MicroProfileCodeActionId;
import org.eclipse.lsp4jdt.core.java.codeaction.ExtendedCodeAction;
import org.eclipse.lsp4jdt.core.java.codeaction.IJavaCodeActionParticipant;
import org.eclipse.lsp4jdt.core.java.codeaction.JavaCodeActionContext;
import org.eclipse.lsp4jdt.core.java.codeaction.JavaCodeActionResolveContext;
import org.eclipse.lsp4jdt.core.java.corrections.proposal.ChangeCorrectionProposal;
import org.eclipse.lsp4jdt.core.java.corrections.proposal.ReplaceAnnotationProposal;
import org.eclipse.lsp4mp.jdt.core.utils.JDTTypeUtils;
import org.eclipse.lsp4mp.jdt.internal.metrics.MicroProfileMetricsConstants;

/**
 * QuickFix for fixing
 * {@link MicroProfileMetricsErrorCode#ApplicationScopedAnnotationMissing} error
 * by providing several code actions:
 *
 * <ul>
 * <li>Remove @RequestScoped | @SessionScoped | @Dependent annotation</li>
 * <li>Insert @ApplicationScoped annotation and the proper import.</li>
 * </ul>
 *
 * @author Kathryn Kodama
 *
 */
public class ApplicationScopedAnnotationMissingQuickFix implements IJavaCodeActionParticipant {

	private static final Logger LOGGER = Logger.getLogger(ApplicationScopedAnnotationMissingQuickFix.class.getName());

	private static final String[] REMOVE_ANNOTATION_NAMES = new String[] {
			MicroProfileMetricsConstants.REQUEST_SCOPED_ANNOTATION_NAME,
			MicroProfileMetricsConstants.SESSION_SCOPED_ANNOTATION_NAME,
			MicroProfileMetricsConstants.DEPENDENT_ANNOTATION_NAME };

	private static final String[] ADD_ANNOTATIONS = new String[] {
			MicroProfileMetricsConstants.APPLICATION_SCOPED_JAKARTA_ANNOTATION,
			MicroProfileMetricsConstants.APPLICATION_SCOPED_JAVAX_ANNOTATION };

	@Override
	public String getParticipantId() {
		return ApplicationScopedAnnotationMissingQuickFix.class.getName();
	}

	@Override
	public List<? extends CodeAction> getCodeActions(JavaCodeActionContext context, Diagnostic diagnostic,
			IProgressMonitor monitor) throws CoreException {
		String addAnnotation = getAddAnnotation(context);
		ExtendedCodeAction codeAction = new ExtendedCodeAction(getLabel(addAnnotation));
		codeAction.setRelevance(0);
		codeAction.setDiagnostics(Collections.singletonList(diagnostic));
		codeAction.setKind(CodeActionKind.QuickFix);
		codeAction.setData(
				new CodeActionResolveData(context.getUri(), getParticipantId(), context.getParams().getRange(), null,
						context.getParams().isResourceOperationSupported(),
						context.getParams().isCommandConfigurationUpdateSupported(),
						MicroProfileCodeActionId.InsertApplicationScopedAnnotation));

		return Collections.singletonList(codeAction);
	}

	@Override
	public CodeAction resolveCodeAction(JavaCodeActionResolveContext context) {
		String addAnnotation = getAddAnnotation(context);
		CodeAction toResolve = context.getUnresolved();
		String name = getLabel(addAnnotation);
		ASTNode node = context.getCoveringNode();
		IBinding parentType = getBinding(node);

		ChangeCorrectionProposal proposal = new ReplaceAnnotationProposal(name, context.getCompilationUnit(),
				context.getASTRoot(), parentType, 0, addAnnotation, REMOVE_ANNOTATION_NAMES);
		try {
			toResolve.setEdit(context.convertToWorkspaceEdit(proposal));
		} catch (CoreException e) {
			LOGGER.log(Level.SEVERE, "Failed to create workspace edit to replace bean scope annotation", e);
		}

		return toResolve;
	}

	private String getAddAnnotation(JavaCodeActionContext context) {
		for (String annotation : ADD_ANNOTATIONS) {
			if (JDTTypeUtils.findType(context.getJavaProject(), annotation) != null) {
				return annotation;
			}
		}
		return MicroProfileMetricsConstants.APPLICATION_SCOPED_JAKARTA_ANNOTATION;
	}

	private IBinding getBinding(ASTNode node) {
		if (node.getParent() instanceof VariableDeclarationFragment) {
			return ((VariableDeclarationFragment) node.getParent()).resolveBinding();
		}
		return Bindings.getBindingOfParentType(node);
	}

	private static String getLabel(String annotation) {
		StringBuilder name = new StringBuilder("Replace current scope with ");
		String annotationName = annotation.substring(annotation.lastIndexOf('.') + 1, annotation.length());
		name.append("@");
		name.append(annotationName);
		return name.toString();
	}

}
