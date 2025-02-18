/*******************************************************************************
* Copyright (c) 2023 Red Hat Inc. and others.
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
package org.eclipse.lsp4mp.jdt.core.graphql.java;

import static org.eclipse.lsp4mp.jdt.core.MicroProfileForJavaAssert.assertJavaDiagnostics;
import static org.eclipse.lsp4mp.jdt.core.MicroProfileForJavaAssert.d;

import java.util.Arrays;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4jdt.commons.DocumentFormat;
import org.eclipse.lsp4jdt.commons.JavaDiagnosticsParams;
import org.eclipse.lsp4mp.jdt.core.BasePropertiesManagerTest;
import org.eclipse.lsp4jdt.core.utils.IJDTUtils;
import org.eclipse.lsp4mp.jdt.internal.graphql.MicroProfileGraphQLConstants;
import org.eclipse.lsp4mp.jdt.internal.graphql.java.MicroProfileGraphQLErrorCode;
import org.junit.Test;

/**
 * Tests for {@link org.eclipse.lsp4mp.jdt.internal.graphql.java.MicroProfileGraphQLASTValidator}.
 */
public class MicroProfileGraphQLValidationTest extends BasePropertiesManagerTest {

	@Test
	public void testVoidQueryMethod() throws Exception {
		IJavaProject javaProject = loadMavenProject(
				MicroProfileMavenProjectName.microprofile_graphql);
		IJDTUtils utils = JDT_UTILS;

		JavaDiagnosticsParams diagnosticsParams = new JavaDiagnosticsParams();
		IFile javaFile = javaProject.getProject()
				.getFile(new Path("src/main/java/io/openliberty/graphql/sample/WeatherService.java"));
		diagnosticsParams.setUris(Arrays.asList(javaFile.getLocation().toFile().toURI().toString()));
		diagnosticsParams.setDocumentFormat(DocumentFormat.Markdown);

		Diagnostic d1 = d(31, 13, 27,
				"Annotate 'WeatherService' with '@GraphQLApi' in order for microprofile-graphql to recognize 'currentConditions' as a part of the GraphQL API.",
				DiagnosticSeverity.Warning, MicroProfileGraphQLConstants.DIAGNOSTIC_SOURCE,
				MicroProfileGraphQLErrorCode.MISSING_GRAPHQL_API_ANNOTATION);

		Diagnostic d2 = d(88, 11, 15,
				"Methods annotated with microprofile-graphql's `@Query` cannot have 'void' as a return type.",
				DiagnosticSeverity.Error, MicroProfileGraphQLConstants.DIAGNOSTIC_SOURCE,
				MicroProfileGraphQLErrorCode.NO_VOID_QUERIES);

		Diagnostic d3 = d(92, 11, 15,
				"Methods annotated with microprofile-graphql's `@Mutation` cannot have 'void' as a return type.",
				DiagnosticSeverity.Error, MicroProfileGraphQLConstants.DIAGNOSTIC_SOURCE,
				MicroProfileGraphQLErrorCode.NO_VOID_MUTATIONS);

		assertJavaDiagnostics(diagnosticsParams, utils, //
				d1, d2, d3);
	}
}
