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
package org.eclipse.lsp4mp.jdt.core;

import static org.eclipse.lsp4mp.jdt.core.MicroProfileAssert.assertProperties;
import static org.eclipse.lsp4mp.jdt.core.MicroProfileAssert.assertPropertiesDuplicate;
import static org.eclipse.lsp4mp.jdt.core.MicroProfileAssert.p;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.lsp4jdt.commons.ClasspathKind;
import org.eclipse.lsp4jdt.commons.DocumentFormat;
import org.eclipse.lsp4mp.commons.MicroProfileProjectInfo;
import org.eclipse.lsp4mp.commons.MicroProfilePropertiesScope;
import org.eclipse.lsp4mp.jdt.internal.core.utils.DependencyUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test collection of Quarkus properties from classpath kind
 *
 * <ul>
 * <li>not in classpath -> 0 quarkus properties</li>
 * <li>in /java/main/src classpath -> N quarkus properties</li>
 * <li>in /java/main/test classpath-> N + M quarkus properties</li>
 * </ul>
 *
 * @author Angelo ZERR
 *
 */
public class PropertiesManagerClassPathKindTest extends BasePropertiesManagerTest {

	@Test
	public void configQuickstartTest() throws Exception {

		IJavaProject javaProject = loadMavenProject(MicroProfileMavenProjectName.config_quickstart_test);

		// not in classpath -> 0 quarkus properties
		IFile fileFromNone = javaProject.getProject().getFile(new Path("application.properties"));
		MicroProfileProjectInfo infoFromNone = PropertiesManager.getInstance().getMicroProfileProjectInfo(fileFromNone,
				MicroProfilePropertiesScope.SOURCES_AND_DEPENDENCIES, JDT_UTILS,
				DocumentFormat.Markdown, new NullProgressMonitor());
		Assert.assertEquals(ClasspathKind.NONE, infoFromNone.getClasspathKind());
		Assert.assertEquals(0, infoFromNone.getProperties().size());

		File resteasyJARFile = DependencyUtil.getArtifact("io.quarkus", "quarkus-resteasy-common-deployment",
				"1.0.0.CR1", null, new NullProgressMonitor());
		Assert.assertNotNull("quarkus-resteasy-common-deployment*.jar is missing", resteasyJARFile);

		// in /java/main/src classpath -> N quarkus properties
		IFile fileFromSrc = javaProject.getProject().getFile(new Path("src/main/resources/application.properties"));
		MicroProfileProjectInfo infoFromSrc = PropertiesManager.getInstance().getMicroProfileProjectInfo(fileFromSrc,
				MicroProfilePropertiesScope.SOURCES_AND_DEPENDENCIES, JDT_UTILS,
				DocumentFormat.Markdown, new NullProgressMonitor());
		Assert.assertEquals(ClasspathKind.SRC, infoFromSrc.getClasspathKind());
		assertProperties(infoFromSrc, 3 /* properties from Java sources */ + //
				7 /* static properties from microprofile-context-propagation-api */ + //
				1 /* static property from microprofile config_ordinal */,

				// GreetingResource
				// @ConfigProperty(name = "greeting.message")
				// String message;
				p(null, "greeting.message", "java.lang.String", null, false, "org.acme.config.GreetingResource",
						"message", null, 0, null),

				// @ConfigProperty(name = "greeting.suffix" , defaultValue="!")
				// String suffix;
				p(null, "greeting.suffix", "java.lang.String", null, false, "org.acme.config.GreetingResource",
						"suffix", null, 0, "!"),

				// @ConfigProperty(name = "greeting.name")
				// Optional<String> name;
				p(null, "greeting.name", "java.util.Optional", null, false, "org.acme.config.GreetingResource", "name",
						null, 0, null));

		assertPropertiesDuplicate(infoFromSrc);

		// in /java/main/test classpath-> N + M quarkus properties
		File undertowJARFile = DependencyUtil.getArtifact("io.quarkus", "quarkus-undertow-deployment", "1.0.0.CR1",
				null, new NullProgressMonitor());
		Assert.assertNotNull("quarkus-undertow-deployment*.jar is missing", undertowJARFile);

		IFile filefromTest = javaProject.getProject().getFile(new Path("src/test/resources/application.properties"));
		MicroProfileProjectInfo infoFromTest = PropertiesManager.getInstance().getMicroProfileProjectInfo(filefromTest,
				MicroProfilePropertiesScope.SOURCES_AND_DEPENDENCIES, JDT_UTILS,
				DocumentFormat.Markdown, new NullProgressMonitor());
		Assert.assertEquals(ClasspathKind.TEST, infoFromTest.getClasspathKind());
		assertProperties(infoFromTest, 3 /* properties from (src) Java sources */ + //
		 3 /* properties from (test) Java sources */ + //
		 7 /* static properties from microprofile-context-propagation-api */ + //
		 1 /* static property from microprofile config_ordinal */,

				// GreetingResource
				// @ConfigProperty(name = "greeting.message")
				// String message;
				p(null, "greeting.message", "java.lang.String", null, false, "org.acme.config.GreetingResource",
						"message", null, 0, null),

				// @ConfigProperty(name = "greeting.suffix" , defaultValue="!")
				// String suffix;
				p(null, "greeting.suffix", "java.lang.String", null, false, "org.acme.config.GreetingResource",
						"suffix", null, 0, "!"),

				// @ConfigProperty(name = "greeting.name")
				// Optional<String> name;
				p(null, "greeting.name", "java.util.Optional", null, false, "org.acme.config.GreetingResource", "name",
						null, 0, null),

				// TestResource
				// @ConfigProperty(name = "greeting.message.test")
				// String message;
				p(null, "greeting.message.test", "java.lang.String", null, false, "org.acme.config.TestResource",
						"message", null, 0, null),

				// @ConfigProperty(name = "greeting.suffix.test" , defaultValue="!")
				// String suffix;
				p(null, "greeting.suffix.test", "java.lang.String", null, false, "org.acme.config.TestResource",
						"suffix", null, 0, "!"),

				// @ConfigProperty(name = "greeting.name.test")
				// Optional<String> name;
				p(null, "greeting.name.test", "java.util.Optional", null, false, "org.acme.config.TestResource", "name",
						null, 0, null)

		);

		assertPropertiesDuplicate(infoFromTest);
	}

}
