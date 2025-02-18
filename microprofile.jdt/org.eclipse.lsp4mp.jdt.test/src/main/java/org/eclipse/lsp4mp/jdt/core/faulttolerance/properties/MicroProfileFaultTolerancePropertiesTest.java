/*******************************************************************************
* Copyright (c) 2020 Red Hat Inc. and others.
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
package org.eclipse.lsp4mp.jdt.core.faulttolerance.properties;

import static org.eclipse.lsp4mp.jdt.core.MicroProfileAssert.assertHints;
import static org.eclipse.lsp4mp.jdt.core.MicroProfileAssert.assertHintsDuplicate;
import static org.eclipse.lsp4mp.jdt.core.MicroProfileAssert.assertProperties;
import static org.eclipse.lsp4mp.jdt.core.MicroProfileAssert.assertPropertiesDuplicate;
import static org.eclipse.lsp4mp.jdt.core.MicroProfileAssert.h;
import static org.eclipse.lsp4mp.jdt.core.MicroProfileAssert.p;
import static org.eclipse.lsp4mp.jdt.core.MicroProfileAssert.vh;

import org.eclipse.lsp4mp.commons.MicroProfileProjectInfo;
import org.eclipse.lsp4mp.commons.MicroProfilePropertiesScope;
import org.eclipse.lsp4mp.jdt.core.BasePropertiesManagerTest;
import org.eclipse.lsp4mp.jdt.internal.faulttolerance.MicroProfileFaultToleranceConstants;
import org.junit.Test;

/**
 * Test collection of MicroProfile properties for MicroProfile Fault Tolerance
 * annotations
 *
 * @author Angelo ZERR
 *
 */
public class MicroProfileFaultTolerancePropertiesTest extends BasePropertiesManagerTest {

	@Test
	public void microprofileFaultTolerancePropertiesTest() throws Exception {

		MicroProfileProjectInfo infoFromClasspath = getMicroProfileProjectInfoFromMavenProject(
				MicroProfileMavenProjectName.microprofile_fault_tolerance, MicroProfilePropertiesScope.SOURCES_AND_DEPENDENCIES);

		assertProperties(infoFromClasspath,

				// <classname>/<annotation>/<parameter>
				p(null, "org.acme.MyClient/Retry/maxRetries", "int", " *  **Returns:**" + System.lineSeparator() + //
						"    " + System.lineSeparator() + //
						"     *  The max number of retries. -1 means retry forever. The value must be greater than or equal to -1.",
						false, "org.acme.MyClient", null, null, 0, "3"),

				// <classname>/<methodname>/<annotation>/<parameter>
				p(null, "org.acme.MyClient/serviceA/Retry/maxRetries", "int",
						" *  **Returns:**" + System.lineSeparator() + //
								"    " + System.lineSeparator() + //
								"     *  The max number of retries. -1 means retry forever. The value must be greater than or equal to -1.",
						false, "org.acme.MyClient", null, "serviceA()V", 0, "90"),

				p(null, "org.acme.MyClient/serviceA/Retry/delay", "long",
						"The delay between retries. Defaults to 0. The value must be greater than or equal to 0."
								+ System.lineSeparator() + //
								"" + System.lineSeparator() + //
								" *  **Returns:**" + System.lineSeparator() + //
								"    " + System.lineSeparator() + //
								"     *  the delay time",
						false, "org.acme.MyClient", null, "serviceA()V", 0, "0"),

				// <annotation>
				// -> <annotation>/enabled
				p(null, "Asynchronous/enabled", "boolean", "Enabling the policy", false,
						"org.eclipse.microprofile.faulttolerance.Asynchronous", null, null, 0, "true"),

				// <annotation>/<parameter>
				p(null, "Bulkhead/value", "int",
						"Specify the maximum number of concurrent calls to an instance. The value must be greater than 0. Otherwise, [org.eclipse.microprofile.faulttolerance.exceptions.FaultToleranceDefinitionException](jdt://contents/microprofile-fault-tolerance-api-2.0.3.jar/org.eclipse.microprofile.faulttolerance.exceptions/FaultToleranceDefinitionException.class?=microprofile-fault-tolerance/%5C/home%5C/jenkins%5C/.m2%5C/repository%5C/org%5C/eclipse%5C/microprofile%5C/fault-tolerance%5C/microprofile-fault-tolerance-api%5C/2.0.3%5C/microprofile-fault-tolerance-api-2.0.3.jar=/maven.pomderived=/true=/=/maven.groupId=/org.eclipse.microprofile.fault-tolerance=/=/maven.artifactId=/microprofile-fault-tolerance-api=/=/maven.version=/2.0.3=/=/maven.scope=/compile=/=/maven.pomderived=/true=/%3Corg.eclipse.microprofile.faulttolerance.exceptions%28FaultToleranceDefinitionException.class#28) occurs."
								+ System.lineSeparator() + //
								"" + System.lineSeparator() + //
								" *  **Returns:**" + System.lineSeparator() + //
								"    " + System.lineSeparator() + //
								"     *  the limit of the concurrent calls",
						false, "org.eclipse.microprofile.faulttolerance.Bulkhead", null, "value()I", 0, "10"),

				p(null, "MP_Fault_Tolerance_NonFallback_Enabled", "boolean",
						MicroProfileFaultToleranceConstants.MP_FAULT_TOLERANCE_NONFALLBACK_ENABLED_DESCRIPTION, false,
						null, null, null, 0, "false")

		);

		assertPropertiesDuplicate(infoFromClasspath);

		assertHints(infoFromClasspath, h("java.time.temporal.ChronoUnit", null, true, "java.time.temporal.ChronoUnit", //
				vh("NANOS", null, null), //
				vh("MICROS", null, null), //
				vh("MILLIS", null, null), //
				vh("SECONDS", null, null), //
				vh("MINUTES", null, null), //
				vh("HALF_DAYS", null, null), //
				vh("DAYS", null, null), //
				vh("WEEKS", null, null), //
				vh("MONTHS", null, null), //
				vh("YEARS", null, null), //
				vh("DECADES", null, null), //
				vh("CENTURIES", null, null), //
				vh("MILLENNIA", null, null), //
				vh("ERAS", null, null), //
				vh("FOREVER", null, null)) //
		);

		assertHintsDuplicate(infoFromClasspath);
	}

}
