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
package org.eclipse.lsp4mp.jdt.internal.faulttolerance;

/**
 * MicroProfile Fault Tolerance constants
 *
 * @author Angelo ZERR
 *
 */
public class MicroProfileFaultToleranceConstants {

	public MicroProfileFaultToleranceConstants() {
	}

	// MicroProfile Fault Tolerance annotations

	public static final String ASYNCHRONOUS_ANNOTATION = "org.eclipse.microprofile.faulttolerance.Asynchronous";

	public static final String BULKHEAD_ANNOTATION = "org.eclipse.microprofile.faulttolerance.Bulkhead";

	public static final String CIRCUITBREAKER_ANNOTATION = "org.eclipse.microprofile.faulttolerance.CircuitBreaker";

	public static final String FALLBACK_ANNOTATION = "org.eclipse.microprofile.faulttolerance.Fallback";

	public static final String RETRY_ANNOTATION = "org.eclipse.microprofile.faulttolerance.Retry";

	public static final String TIMEOUT_ANNOTATION = "org.eclipse.microprofile.faulttolerance.Timeout";

	// MicroProfile Fault Tolerance annotation member keys

	public static final String FALLBACK_METHOD_FALLBACK_ANNOTATION_MEMBER = "fallbackMethod";

	public static final String DELAY_RETRY_ANNOTATION_MEMBER = "delay";

	public static final String DELAY_UNIT_RETRY_ANNOTATION_MEMBER = "delayUnit";

	public static final String MAX_DURATION_RETRY_ANNOTATION_MEMBER = "maxDuration";

	public static final String DURATION_UNIT_RETRY_ANNOTATION_MEMBER = "durationUnit";

	public static final String JITTER_RETRY_ANNOTATION_MEMBER = "jitter";

	public static final String JITTER_DELAY_UNIT_RETRY_ANNOTATION_MEMBER = "jitterDelayUnit";

	// MP_Fault_Tolerance_NonFallback_Enabled

	public static final String MP_FAULT_TOLERANCE_NON_FALLBACK_ENABLED = "MP_Fault_Tolerance_NonFallback_Enabled";

	public static final String MP_FAULT_TOLERANCE_NONFALLBACK_ENABLED_DESCRIPTION = "Some service mesh platforms, e.g. Istio, have their own Fault Tolerance policy.\r\n"
			+ //
			"The operation team might want to use the platform Fault Tolerance.\r\n" + //
			"In order to fulfil the requirement, MicroProfile Fault Tolerance provides a capability to have its resilient functionalities disabled except `fallback`.\r\n"
			+ //
			"The reason `fallback` is special is that the `fallback` business logic can only be defined by microservices and not by any other platforms.\r\n"
			+ //
			"\r\n" + //
			"Setting the config property of `MP_Fault_Tolerance_NonFallback_Enabled` with the value of `false` means the Fault Tolerance is disabled, except `@Fallback`.\r\n"
			+ //
			"If the property is absent or with the value of `true`, it means that MicroProfile Fault Tolerance is enabled if any annotations are specified.  For more information about how to set config properties, refer to MicroProfile Config specification.\r\n"
			+ //
			"\r\n" + //
			"In order to prevent from any unexpected behaviours, the property `MP_Fault_Tolerance_NonFallback_Enabled` will only be read on application starting.\r\n"
			+ //
			"Any dynamic changes afterwards will be ignored until  the application restarting.";

	public static final String DIAGNOSTIC_SOURCE = "microprofile-faulttolerance";

}
