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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.jsonrpc.CancelChecker;
import org.eclipse.lsp4mp.commons.utils.IConfigSourcePropertiesProvider;
import org.eclipse.lsp4mp.commons.utils.StringUtils;
import org.eclipse.lsp4mp.ls.commons.BadLocationException;
import org.eclipse.lsp4mp.ls.commons.TextDocument;
import org.eclipse.lsp4mp.model.parser.ErrorEvent;
import org.eclipse.lsp4mp.model.parser.ErrorHandler;
import org.eclipse.lsp4mp.model.parser.ParseContext;
import org.eclipse.lsp4mp.model.parser.ParseException;
import org.eclipse.lsp4mp.model.parser.PropertiesHandler;
import org.eclipse.lsp4mp.model.parser.PropertiesParser;

/**
 * The properties model (application.properties) which stores each start/end
 * offset of each property keys/values.
 *
 * @author Angelo ZERR
 *
 */
public class PropertiesModel extends Node implements IConfigSourcePropertiesProvider {

	/**
	 * This handler catch each properties events (start/end property, etc) to build
	 * a DOM properties model which maintains offset locations.
	 *
	 */
	private static class PropertiesModelHandler implements PropertiesHandler {

		private final PropertiesModel model;
		private Property property;
		private Comments comment;

		public PropertiesModelHandler(PropertiesModel model) {
			this.model = model;
		}

		@Override
		public void startDocument(ParseContext context) {
			model.setStart(0);
		}

		@Override
		public void endDocument(ParseContext context) {
			model.setEnd(context.getLocationOffset());
		}

		@Override
		public void startProperty(ParseContext context) {
			this.property = new Property();
			property.setStart(context.getLocationOffset());
			model.addNode(property);
		}

		@Override
		public void startPropertyName(ParseContext context) {
			PropertyKey key = new PropertyKey();
			key.setStart(context.getLocationOffset());
			property.setKey(key);
		}

		@Override
		public void endPropertyName(ParseContext context) {
			Node key = property.getKey();
			key.setEnd(context.getLocationOffset());
		}

		@Override
		public void startPropertyValue(ParseContext context) {
			PropertyValue value = new PropertyValue();
			value.setStart(context.getLocationOffset());
			property.setValue(value);
		}

		@Override
		public void endPropertyValue(ParseContext context) {
			Node value = property.getValue();
			value.setEnd(context.getLocationOffset());
		}

		@Override
		public void endProperty(ParseContext context) {
			property.setEnd(context.getLocationOffset());
			this.property = null;
		}

		@Override
		public void startComment(ParseContext context) {
			this.comment = new Comments();
			comment.setStart(context.getLocationOffset());
			model.addNode(comment);
		}

		@Override
		public void endComment(ParseContext context) {
			comment.setEnd(context.getLocationOffset());
			this.comment = null;
		}

		@Override
		public void delimiterAssign(ParseContext context) {
			Node assign = new Assign();
			assign.setStart(context.getLocationOffset());

			// assumption: delimiters are only one character long
			assign.setEnd(context.getLocationOffset() + 1);

			property.setDelimiterAssign(assign);
		}

		@Override
		public void blankLine(ParseContext context) {

		}

		@Override
		public void startPropertyValueLiteral(ParseContext context) {
			Node valLiteral = new PropertyValueLiteral();
			valLiteral.setStart(context.getLocationOffset());
			property.getValue().addNode(valLiteral);
		}

		@Override
		public void endPropertyValueLiteral(ParseContext context) {
			List<Node> propFragments = property.getValue().getChildren();
			propFragments.get(propFragments.size() - 1).setEnd(context.getLocationOffset());
		}

		@Override
		public void startPropertyValueExpression(ParseContext context) {
			Node expression = new PropertyValueExpression();
			expression.setStart(context.getLocationOffset());
			property.getValue().addNode(expression);
		}

		@Override
		public void endPropertyValueExpression(ParseContext context) {
			List<Node> propFragments = property.getValue().getChildren();
			propFragments.get(propFragments.size() - 1).setEnd(context.getLocationOffset());
		}
	}

	private final TextDocument document;
	private CancelChecker cancelChecker;
	private transient Set<String> keys;

	PropertiesModel(TextDocument document, CancelChecker cancelChecker) {
		this.document = document;
		this.cancelChecker = cancelChecker;
		this.keys = null;
	}

	@Override
	public NodeType getNodeType() {
		return NodeType.DOCUMENT;
	}

	/**
	 * Returns the properties model from the given text.
	 *
	 * @param text
	 * @param uri
	 * @return the properties model from the given text.
	 */
	public static PropertiesModel parse(String text, String uri, CancelChecker cancelChecker) {
		return parse(new TextDocument(text, uri), cancelChecker);
	}

	/**
	 * Returns the properties model from the given text.
	 *
	 * This parse request cannot be cancelled
	 *
	 * @param text
	 * @param uri
	 * @return the properties model from the given text
	 */
	public static PropertiesModel parse(String text, String uri) {
		return parse(text, uri, () -> {
		});
	}

	/**
	 * Returns the properties model from the text of the given document.
	 *
	 * @param document the text document
	 * @return the properties model from the text of the given document.
	 */
	public static PropertiesModel parse(TextDocument document, CancelChecker cancelChecker) {
		PropertiesModel model = new PropertiesModel(document, cancelChecker);
		PropertiesParser parser = new PropertiesParser();
		parser.parse(document.getText(), new PropertiesModelHandler(model), new ErrorHandler() {

			@Override
			public void error(ParseContext context, ErrorEvent errorEvent) throws ParseException {

			}
		}, cancelChecker);
		return model;
	}

	/**
	 * Returns the text from the <code>start</code> offset (inclusive) to the
	 * <code>end</code> offset (exclusive).
	 *
	 * @param start         the start offset
	 * @param end           the end offset
	 * @param skipMultiLine determines whether or not new lines characters and
	 *                      backslashes should be preserved for multi line text
	 *                      values
	 * @return the text from the <code>start</code> offset (inclusive) to the
	 *         <code>end</code> offset (exclusive).
	 */
	public String getText(int start, int end, boolean skipMultiLine) {
		String text = document.getText();
		if (!skipMultiLine) {
			cancelChecker.checkCanceled();
			return text.substring(start, end);
		}
		
		StringBuilder sb = new StringBuilder();
		int i = start;
		boolean trimLeading = false;
		while (i < end) {
			cancelChecker.checkCanceled();
			char curr = text.charAt(i);
			if (curr == '\\') {
				if (i < end - 1 && text.charAt(i + 1) == '\n') {
					i += 2;
					trimLeading = true;
					continue;
				} else if (i < end - 2 && text.charAt(i + 1) == '\r' && text.charAt(i + 2) == '\n') {
					i += 3;
					trimLeading = true;
					continue;
				}
			}

			if (!trimLeading || !Character.isWhitespace(curr)) {
				trimLeading = false;
				sb.append(curr);
			}

			i++;
		}
		return sb.toString();
	}

	public int offsetAt(Position position) throws BadLocationException {
		return document.offsetAt(position);
	}

	public Position positionAt(int position) throws BadLocationException {
		return document.positionAt(position);
	}

	@Override
	public PropertiesModel getOwnerModel() {
		return this;
	}

	@Override
	public TextDocument getDocument() {
		return document;
	}

	@Override
	public String getText() {
		return document.getText();
	}

	public String getDocumentURI() {
		return getDocument().getUri();
	}

	public CancelChecker getCancelChecker() {
		return cancelChecker;
	}

	@Override
	public Set<String> keys() {
		if (keys != null) {
			return keys;
		}

		keys = new HashSet<>();

		for (Node child : getChildren()) {
			if (child.getNodeType() == NodeType.PROPERTY) {
				Property property = (Property) child;
				PropertyValue valueNode = property.getValue();
				if (valueNode != null && StringUtils.hasText(valueNode.getText(true))) {
					String key = property.getPropertyNameWithProfile();
					if (StringUtils.hasText(key)) {
						keys.add(key);
					}
				}
			}
		}

		return keys;
	}

	@Override
	public boolean hasKey(String key) {
		return keys().contains(key);
	}

	@Override
	public String getValue(String key) {
		if (key == null) {
			return null;
		}
		for (Node child : getChildren()) {
			if (child.getNodeType() == NodeType.PROPERTY) {
				Property property = (Property) child;
				PropertyKey keyNode = property.getKey();
				if (key.equals(keyNode.getPropertyNameWithProfile())) {
					PropertyValue valueNode = property.getValue();
					if (valueNode != null && StringUtils.hasText(valueNode.getText(true))) {
						return valueNode.getText(true);
					}
				}

			}
		}
		return null;
	}

}
