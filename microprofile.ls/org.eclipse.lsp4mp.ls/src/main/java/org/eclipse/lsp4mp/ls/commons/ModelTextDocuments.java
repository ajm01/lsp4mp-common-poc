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
package org.eclipse.lsp4mp.ls.commons;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.eclipse.lsp4j.TextDocumentItem;
import org.eclipse.lsp4j.jsonrpc.CancelChecker;
import org.eclipse.lsp4j.jsonrpc.CompletableFutures;
import org.eclipse.lsp4j.jsonrpc.CompletableFutures.FutureCancelChecker;

/**
 * The cache of {@link TextDocument} linked to a model.
 *
 * @author Angelo ZERR
 *
 * @param <T> the model type (ex : DOM Document)
 */
public class ModelTextDocuments<T> extends TextDocuments<ModelTextDocument<T>> {

	private final BiFunction<TextDocument, CancelChecker, T> parse;

	public ModelTextDocuments(BiFunction<TextDocument, CancelChecker, T> parse) {
		this.parse = parse;
	}

	@Override
	public ModelTextDocument<T> createDocument(TextDocumentItem document) {
		ModelTextDocument<T> doc = new ModelTextDocument<T>(document, parse);
		doc.setIncremental(isIncremental());
		return doc;
	}

	/**
	 * Returns the model of the given text document Uri and null otherwise.
	 *
	 * @param uri the text document uri.
	 *
	 * @return the model of the given text document Uri and null otherwise.
	 */
	public T getExistingModel(TextDocumentIdentifier documentIdentifier) {
		return getExistingModel(documentIdentifier.getUri());
	}

	/**
	 * Returns the model of the given text document Uri and null otherwise.
	 *
	 * @param uri the text document uri.
	 *
	 * @return the model of the given text document Uri and null otherwise.
	 */
	public T getExistingModel(String uri) {
		ModelTextDocument<T> document = get(uri);
		if (document != null) {
			return document.getExistingModel();
		}
		return null;
	}

	/**
	 * Returns the model of the given text document Uri and null otherwise.
	 *
	 * @param uri the text document uri.
	 *
	 * @return the model of the given text document Uri and null otherwise.
	 */
	public T getModel(TextDocumentIdentifier documentIdentifier) {
		return getModel(documentIdentifier.getUri());
	}

	/**
	 * Returns the model of the given text document Uri and null otherwise.
	 *
	 * @param uri the text document uri.
	 *
	 * @return the model of the given text document Uri and null otherwise.
	 */
	public T getModel(String uri) {
		ModelTextDocument<T> document = get(uri);
		if (document != null) {
			return document.getModel();
		}
		return null;
	}
	
	/**
	 * Get or parse the model and apply the code function which expects the model.
	 *
	 * @param <R>
	 * @param documentIdentifier the document identifier.
	 * @param code               a bi function that accepts the parsed model and
	 *                           {@link CancelChecker} and returns the to be
	 *                           computed value
	 * @return the model for a given uri in a future and then apply the given
	 *         function.
	 */
	public <R> CompletableFuture<R> computeModelAsync(TextDocumentIdentifier documentIdentifier,
			BiFunction<T, CancelChecker, R> code) {
		return CompletableFutures.computeAsync(cancelChecker -> {
			// Get or parse the model.
			T model = getModel(documentIdentifier);
			if (model == null) {
				return null;
			}
			cancelChecker.checkCanceled();
			// Apply the function code by using the parsed model.
			return code.apply(model, cancelChecker);
		});
	}

	/**
	 * Get or parse the model and apply the code function which expects the model.
	 *
	 * @param <R>
	 * @param documentIdentifier the document identifier.
	 * @param code               a bi function that accepts the parsed model and
	 *                           {@link CancelChecker} and returns as future the to
	 *                           be computed value
	 * @return the model for a given uri in a future and then apply the given
	 *         function.
	 */
	public <R> CompletableFuture<R> computeModelAsyncCompose(TextDocumentIdentifier documentIdentifier,
			BiFunction<T, CancelChecker, CompletableFuture<R>> code) {
		return computeAsyncCompose(cancelChecker -> {
			// Get or parse the model.
			T model = getModel(documentIdentifier);
			if (model == null) {
				return null;
			}
			cancelChecker.checkCanceled();
			// Apply the function code by using the parsed model.{
			return code.apply(model, cancelChecker);
		});
	}

	private static <R> CompletableFuture<R> computeAsyncCompose(Function<CancelChecker, CompletableFuture<R>> code) {
		CompletableFuture<CancelChecker> start = new CompletableFuture<>();
		CompletableFuture<R> result = start.thenComposeAsync(code);
		start.complete(new FutureCancelChecker(result));
		return result;
	}
}