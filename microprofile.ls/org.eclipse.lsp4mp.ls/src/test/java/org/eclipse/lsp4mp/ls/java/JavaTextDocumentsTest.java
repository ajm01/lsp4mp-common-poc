/*******************************************************************************
* Copyright (c) 2020 Red Hat Inc. and others.
* All rights reserved. This program and the accompanying materials
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v20.html
*
* SPDX-License-Identifier: EPL-2.0
*
* Contributors:
*     Red Hat Inc. - initial API and implementation
*******************************************************************************/
package org.eclipse.lsp4mp.ls.java;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.eclipse.lsp4j.TextDocumentItem;
import org.eclipse.lsp4jdt.commons.JavaProjectLabelsParams;
import org.eclipse.lsp4jdt.commons.ProjectLabelInfoEntry;
import org.eclipse.lsp4mp.ls.api.MicroProfileJavaProjectLabelProvider;
import org.eclipse.lsp4mp.ls.java.JavaTextDocuments.JavaTextDocument;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test for Java test documents.
 *
 * @author Angelo ZERR
 *
 */
public class JavaTextDocumentsTest {

	private static final boolean DEFAULT_VALUE = false;
	private static final String MP_PROJECT = "mp-project";

	private static final String NOMP_PROJECT = "nomp-project";

	private static MicroProfileJavaProjectLabelProvider PROVIDER = new MicroProfileJavaProjectLabelProvider() {

		@Override
		public CompletableFuture<ProjectLabelInfoEntry> getJavaProjectLabels(
				JavaProjectLabelsParams javaParams) {
			String uri = javaParams.getUri();
			List<String> labels = null;
			if (uri.startsWith(MP_PROJECT)) {
				labels = Arrays.asList("microprofile");
			}
			ProjectLabelInfoEntry projectInfo = new ProjectLabelInfoEntry(uri, "", labels);
			return CompletableFuture.completedFuture(projectInfo);
		}
		
		@Override
		public CompletableFuture<List<ProjectLabelInfoEntry>> getAllJavaProjectLabels() {
			return CompletableFuture.completedFuture(null);
		}
	};

	@Test
	public void inMicroProfileProject() throws InterruptedException, ExecutionException {
		JavaTextDocuments documents = new JavaTextDocuments(PROVIDER, null);
		JavaTextDocument document1 = documents
				.createDocument(new TextDocumentItem(MP_PROJECT + "/file1.java", "", 0, ""));
		CompletableFuture<Boolean> result = document1.executeIfInMicroProfileProject((projectInfo, cancelChecker) -> {
			// return the result of the execute
			return CompletableFuture.completedFuture(true);
		}, DEFAULT_VALUE);

		// the project is not a MP Project, the result of the execute
		Assert.assertTrue("Test executed in a MicroProfile project", result.get());
	}

	@Test
	public void inNonMicroProfileProject() throws InterruptedException, ExecutionException {
		JavaTextDocuments documents = new JavaTextDocuments(PROVIDER, null);
		JavaTextDocument document1 = documents
				.createDocument(new TextDocumentItem(NOMP_PROJECT + "/file1.java", "", 0, ""));
		CompletableFuture<Boolean> result = document1.executeIfInMicroProfileProject((projectInfo, cancelChecker) -> {
			// return the result of the execute
			return CompletableFuture.completedFuture(true);
		}, DEFAULT_VALUE);

		// the project is not a MP Project, the result is the default value.
		Assert.assertFalse("Test executed in a non-MicroProfile project", result.get());
	}
}
