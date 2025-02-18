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

import java.util.concurrent.CancellationException;
import java.util.function.BiFunction;
import java.util.logging.Logger;

import org.eclipse.lsp4j.TextDocumentItem;
import org.eclipse.lsp4j.jsonrpc.CancelChecker;

/**
 * A {@link TextDocument} which is associate to a model loaded in async.
 *
 * @author Angelo ZERR
 *
 * @param <T> the model type (ex : DOM Document)
 */
public class ModelTextDocument<T> extends TextDocument {

	private static final Logger LOGGER = Logger.getLogger(ModelTextDocument.class.getName());

	private final BiFunction<TextDocument, CancelChecker, T> parse;

	private T model;

	public ModelTextDocument(TextDocumentItem document, BiFunction<TextDocument, CancelChecker, T> parse) {
		super(document);
		this.parse = parse;
	}

	public ModelTextDocument(String text, String uri, BiFunction<TextDocument, CancelChecker, T> parse) {
		super(text, uri);
		this.parse = parse;
	}

	public T getExistingModel() {
		return model;
	}

	public T getModel() {
		if (model == null) {
			return getSynchronizedModel();
		}
		return model;
	}

	/**
	 * Return the existing parsed model synchronized with last version of the text
	 * document or parse the model.
	 * 
	 * @return the existing parsed model synchronized with last version of the text
	 *         document or parse the model.
	 */
	private synchronized T getSynchronizedModel() {
		if (model != null) {
			return model;
		}
		int version = super.getVersion();
		long start = System.currentTimeMillis();
		try {
			LOGGER.fine("Start parsing of model with version '" + version);
			// Stop of parse process can be done when completable future is canceled or when
			// version of document changes
			CancelChecker cancelChecker = new TextDocumentVersionChecker(this, version);
			// parse the model
			model = parse.apply(this, cancelChecker);
		} catch (CancellationException e) {
			LOGGER.fine("Stop parsing parsing of model with version '" + version + "' in "
					+ (System.currentTimeMillis() - start) + "ms");
			throw e;
		} finally {
			LOGGER.fine("End parse of model with version '" + version + "' in " + (System.currentTimeMillis() - start)
					+ "ms");
		}
		return model;
	}

	@Override
	public void setText(String text) {
		super.setText(text);
		// text changed, mark the model as dirty
		cancelModel();
	}

	@Override
	public void setVersion(int version) {
		super.setVersion(version);
		// version changed, mark the model as dirty
		cancelModel();
	}

	/**
	 * Mark the model as dirty
	 */
	private void cancelModel() {
		model = null;
	}

}