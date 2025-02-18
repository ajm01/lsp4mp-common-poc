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
package org.eclipse.lsp4mp.ls.api;

import java.util.concurrent.CompletableFuture;

import org.eclipse.lsp4j.jsonrpc.services.JsonNotification;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;
import org.eclipse.lsp4j.services.LanguageServer;
import org.eclipse.lsp4mp.commons.MicroProfileProjectInfoParams;
import org.eclipse.lsp4mp.commons.MicroProfilePropertiesChangeEvent;

/**
 * MicroProfile language server API.
 *
 * @author Angelo ZERR
 *
 */
public interface MicroProfileLanguageServerAPI extends LanguageServer {

	public static class JsonSchemaForProjectInfo {

		private String projectURI;

		private String jsonSchema;

		public JsonSchemaForProjectInfo() {

		}

		public JsonSchemaForProjectInfo(String projectURI, String jsonSchema) {
			this.projectURI = projectURI;
			this.jsonSchema = jsonSchema;
		}

		public String getProjectURI() {
			return projectURI;
		}

		public void setProjectURI(String projectURI) {
			this.projectURI = projectURI;
		}

		public String getJsonSchema() {
			return jsonSchema;
		}

		public void setJsonSchema(String jsonSchema) {
			this.jsonSchema = jsonSchema;
		}
	}

	/**
	 * Notification for MicroProfile properties changed which occurs when:
	 *
	 * <ul>
	 * <li>classpath (java sources and dependencies) changed</li>
	 * <li>only java sources changed</li>
	 * </ul>
	 *
	 * @param event the MicroProfile properties change event which gives the
	 *              information if changed comes from classpath or java sources.
	 */
	@JsonNotification("microprofile/propertiesChanged")
	void propertiesChanged(MicroProfilePropertiesChangeEvent event);

	/**
	 * Returns the Json Schema for the MicroProfile properties of the given
	 * application.yaml URI.
	 *
	 * @param params the application.yaml URI
	 * @return the Json Schema for the MicroProfile properties of the given
	 *         application.yaml URI.
	 */
	@JsonRequest("microprofile/jsonSchemaForProjectInfo")
	CompletableFuture<JsonSchemaForProjectInfo> getJsonSchemaForProjectInfo(MicroProfileProjectInfoParams params);
}
