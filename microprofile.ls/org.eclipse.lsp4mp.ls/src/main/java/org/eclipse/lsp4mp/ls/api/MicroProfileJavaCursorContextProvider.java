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
package org.eclipse.lsp4mp.ls.api;

import java.util.concurrent.CompletableFuture;

import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;
import org.eclipse.lsp4jdt.commons.JavaCompletionParams;
import org.eclipse.lsp4jdt.commons.JavaCursorContextResult;

/**
 * Returns context related to the cursor location in the given document, or null
 * if the client doesn't yet support this.
 *
 * @see JavaCursorContextResult
 */
public interface MicroProfileJavaCursorContextProvider {

	@JsonRequest("microprofile/java/javaCursorContext")
	CompletableFuture<org.eclipse.lsp4jdt.commons.JavaCursorContextResult> getJavaCursorContext(JavaCompletionParams context);

}
