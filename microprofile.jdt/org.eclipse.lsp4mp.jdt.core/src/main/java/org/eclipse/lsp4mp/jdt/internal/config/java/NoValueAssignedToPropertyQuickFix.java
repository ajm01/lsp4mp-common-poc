/*******************************************************************************
 * Copyright (c) 2021 Red Hat Inc. and others.
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

import static org.eclipse.lsp4mp.commons.codeaction.MicroProfileCodeActionFactory.createAddToUnassignedExcludedCodeAction;
import static org.eclipse.lsp4mp.jdt.core.MicroProfileConfigConstants.CONFIG_PROPERTY_ANNOTATION;
import static org.eclipse.lsp4mp.jdt.core.MicroProfileConfigConstants.CONFIG_PROPERTY_ANNOTATION_NAME;
import static org.eclipse.lsp4mp.jdt.core.MicroProfileConfigConstants.DIAGNOSTIC_DATA_NAME;
import static org.eclipse.lsp4jdt.core.utils.AnnotationUtils.getFirstAnnotation;
import static org.eclipse.lsp4jdt.core.utils.AnnotationUtils.getAnnotationMemberValue;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IAnnotatable;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextDocumentEdit;
import org.eclipse.lsp4j.TextDocumentItem;
import org.eclipse.lsp4j.TextEdit;
import org.eclipse.lsp4j.VersionedTextDocumentIdentifier;
import org.eclipse.lsp4j.WorkspaceEdit;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4jdt.commons.codeaction.CodeActionResolveData;
import org.eclipse.lsp4mp.commons.codeaction.MicroProfileCodeActionId;
import org.eclipse.lsp4jdt.core.java.codeaction.IJavaCodeActionParticipant;
import org.eclipse.lsp4jdt.core.java.codeaction.JavaCodeActionContext;
import org.eclipse.lsp4jdt.core.java.codeaction.JavaCodeActionResolveContext;
import org.eclipse.lsp4mp.jdt.core.project.IConfigSource;
import org.eclipse.lsp4mp.jdt.core.project.JDTMicroProfileProject;
import org.eclipse.lsp4mp.jdt.core.project.JDTMicroProfileProjectManager;
import org.eclipse.lsp4jdt.core.utils.IJDTUtils;
import org.eclipse.lsp4mp.ls.commons.CodeActionFactory;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * QuickFix for fixing
 * {@link MicroProfileConfigErrorCode#NO_VALUE_ASSIGNED_TO_PROPERTY} error by
 * providing several code actions:
 *
 * <ul>
 * <li>Insert the proper property inside *.properties files.</li>
 * </ul>
 *
 * @author Angelo ZERR
 *
 */
public class NoValueAssignedToPropertyQuickFix implements IJavaCodeActionParticipant {

	private static final String CODE_ACTION_LABEL = "Insert ''{0}'' property in ''{1}''";

	private static final String PROPERTY_NAME_KEY = "propertyName";
	private static final String CONFIG_TEXT_DOCUMENT_URI_KEY = "uri";

	@Override
	public String getParticipantId() {
		return NoValueAssignedToPropertyQuickFix.class.getName();
	}

	@Override
	public List<? extends CodeAction> getCodeActions(JavaCodeActionContext context, Diagnostic diagnostic,
			IProgressMonitor monitor) throws CoreException {
		List<CodeAction> codeActions = new ArrayList<>();
		IJavaProject javaProject = context.getJavaProject();

		String lineSeparator = context.getCompilationUnit().findRecommendedLineSeparator();
		if (lineSeparator == null) {
			lineSeparator = System.lineSeparator();
		}
		String propertyName = getPropertyName(diagnostic, context);
		String insertText = propertyName + "=" + lineSeparator;

		JDTMicroProfileProject mpProject = JDTMicroProfileProjectManager.getInstance()
				.getJDTMicroProfileProject(javaProject);
		List<IConfigSource> configSources = mpProject.getConfigSources();
		for (IConfigSource configSource : configSources) {
			String uri = configSource.getSourceConfigFileURI();
			if (uri != null) {
				// the properties file exists
				TextDocumentItem document = new TextDocumentItem(uri, "properties", 0, insertText);
				CodeAction codeAction = CodeActionFactory.insert(
						getTitle(propertyName, configSource.getConfigFileName()), MicroProfileCodeActionId.AssignValueToProperty, new Position(0, 0), insertText,
						document, diagnostic);
				codeActions.add(codeAction);
			}
		}
		if (context.getParams().isCommandConfigurationUpdateSupported()) {
			// Exclude validation for the given property
			codeActions.add(createAddToUnassignedExcludedCodeAction(propertyName, diagnostic));
		}
		return codeActions;
	}

	@Override
	public CodeAction resolveCodeAction(JavaCodeActionResolveContext context) {
		CodeAction unresolved = context.getUnresolved();
		CodeActionResolveData data = (CodeActionResolveData) context.getUnresolved().getData();
		String uri = (String) data.getExtendedDataEntry(CONFIG_TEXT_DOCUMENT_URI_KEY);
		String propertyName = (String) data.getExtendedDataEntry(PROPERTY_NAME_KEY);

		String lineSeparator = null;
		try {
			lineSeparator = context.getCompilationUnit().findRecommendedLineSeparator();
		} catch (JavaModelException e) {
			// do nothing
		}
		if (lineSeparator == null) {
			lineSeparator = System.lineSeparator();
		}
		String insertText = propertyName + "=" + lineSeparator;
		TextDocumentEdit tde = insertTextEdit(new TextDocumentItem(uri, "properties", 0, insertText), insertText,
				new Position(0, 0));
		unresolved.setEdit(new WorkspaceEdit(Collections.singletonList(Either.forLeft(tde))));
		return unresolved;
	}

	private static TextDocumentEdit insertTextEdit(TextDocumentItem document, String insertText, Position position) {
		VersionedTextDocumentIdentifier documentId = new VersionedTextDocumentIdentifier(document.getUri(),
				document.getVersion());
		TextEdit te = new TextEdit(new Range(position, position), insertText);
		return new TextDocumentEdit(documentId, Collections.singletonList(te));
	}

	private static String getPropertyName(Diagnostic diagnostic, JavaCodeActionContext context)
			throws JavaModelException {
		if (diagnostic.getData() != null) {
			// retrieve the property name from the diagnostic data
			JsonObject data = (JsonObject) diagnostic.getData();
			JsonElement name = data.get(DIAGNOSTIC_DATA_NAME);
			if (name != null) {
				return name.getAsString();
			}
		}

		// retrieve the property name from the data
		Position hoverPosition = diagnostic.getRange().getStart();
		IJDTUtils utils = context.getUtils();
		ITypeRoot typeRoot = context.getTypeRoot();
		int offset = utils.toOffset(typeRoot.getBuffer(), hoverPosition.getLine(), hoverPosition.getCharacter());
		IJavaElement hoverElement = typeRoot.getElementAt(offset);

		IAnnotation configPropertyAnnotation = getFirstAnnotation((IAnnotatable) hoverElement, CONFIG_PROPERTY_ANNOTATION);
		return getAnnotationMemberValue(configPropertyAnnotation, CONFIG_PROPERTY_ANNOTATION_NAME);
	}

	private static String getTitle(String propertyName, String configFileName) {
		return MessageFormat.format(CODE_ACTION_LABEL, propertyName, configFileName);
	}

}
