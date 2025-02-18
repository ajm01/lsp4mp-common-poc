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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.eclipse.lsp4mp.commons.MicroProfileProjectInfo;
import org.eclipse.lsp4mp.commons.metadata.ItemHint;
import org.eclipse.lsp4mp.commons.metadata.ItemMetadata;
import org.eclipse.lsp4mp.commons.metadata.ValueHint;
import org.junit.Assert;

/**
 * MicroProfile assert for JUnit tests.
 *
 * @author Angelo ZERR
 *
 */
public class MicroProfileAssert {

	// ------------------------- Assert properties

	/**
	 * Assert MicroProfile properties.
	 *
	 * @param info     the MicroProfile project information
	 * @param expected the expected MicroProfile properties.
	 */
	public static void assertProperties(MicroProfileProjectInfo info, ItemMetadata... expected) {
		assertProperties(info, null, expected);
	}

	/**
	 * Assert MicroProfile properties.
	 *
	 * @param info          the MicroProfile project information
	 * @param expectedCount MicroProfile properties expected count.
	 * @param expected      the expected MicroProfile properties.
	 */
	public static void assertProperties(MicroProfileProjectInfo info, Integer expectedCount, ItemMetadata... expected) {
		if (expectedCount != null) {
			Assert.assertEquals(expectedCount.intValue(), info.getProperties().size());
		}
		for (ItemMetadata item : expected) {
			assertProperty(info, item);
		}
	}

	/**
	 * Assert MicroProfile metadata property
	 *
	 * @param info     the MicroProfile project information
	 * @param expected the MicroProfile property.
	 */
	private static void assertProperty(MicroProfileProjectInfo info, ItemMetadata expected) {
		List<ItemMetadata> matches = info.getProperties().stream().filter(completion -> {
			return expected.getName().equals(completion.getName());
		}).collect(Collectors.toList());

		Assert.assertEquals(
				expected.getName() + " should only exist once: Actual: "
						+ info.getProperties().stream().map(c -> c.getName()).collect(Collectors.joining(",")),
				1, matches.size());

		ItemMetadata actual = matches.get(0);
		Assert.assertEquals("Test 'extension name' for '" + expected.getName() + "'", expected.getExtensionName(),
				actual.getExtensionName());
		Assert.assertEquals("Test 'type' for '" + expected.getName() + "'", expected.getType(), actual.getType());
		Assert.assertEquals("Test 'description' for '" + expected.getName() + "'", expected.getDescription(),
				actual.getDescription());
		Assert.assertEquals("Test 'binary' for '" + expected.getName() + "'", expected.isBinary(), actual.isBinary());
		Assert.assertEquals("Test 'source type' for '" + expected.getName() + "'", expected.getSourceType(),
				actual.getSourceType());
		Assert.assertEquals("Test 'source field' for '" + expected.getName() + "'", expected.getSourceField(),
				actual.getSourceField());
		Assert.assertEquals("Test 'source method' for '" + expected.getName() + "'", expected.getSourceMethod(),
				actual.getSourceMethod());
		Assert.assertEquals("Test 'phase' for '" + expected.getName() + "'", expected.getPhase(), actual.getPhase());
		Assert.assertEquals("Test 'default value' for '" + expected.getName() + "'", expected.getDefaultValue(),
				actual.getDefaultValue());
	}

	/**
	 * Returns an instance of MicroProfile property.
	 *
	 * @param extensionName Quarkus extension name
	 * @param name          the property name
	 * @param type          the property class type
	 * @param description   the Javadoc
	 * @param binary        true if it comes from a binary field/method and false
	 *                      otherwise.
	 * @param sourceType    the source type (class or interface)
	 * @param sourceField   the source field name and null otherwise
	 * @param sourceMethod  the source method signature and null otherwise
	 * @param phase         the ConfigPhase.
	 * @param defaultValue  the default value
	 * @return
	 */
	public static ItemMetadata p(String extensionName, String name, String type, String description, boolean binary,
			String sourceType, String sourceField, String sourceMethod, int phase, String defaultValue) {
		ItemMetadata item = new ItemMetadata();
		item.setExtensionName(extensionName);
		item.setName(name);
		item.setType(type);
		item.setDescription(description);
		item.setSource(!binary);
		item.setSourceType(sourceType);
		item.setSourceMethod(sourceMethod);
		item.setSourceField(sourceField);
		item.setPhase(phase);
		item.setDefaultValue(defaultValue);
		return item;
	}

	/**
	 * Assert duplicate properties from the given the MicroProfile project
	 * information
	 *
	 * @param info the MicroProfile project information
	 */
	public static void assertPropertiesDuplicate(MicroProfileProjectInfo info) {
		Map<String, Long> propertiesCount = info.getProperties().stream()
				.collect(Collectors.groupingBy(ItemMetadata::getName, Collectors.counting()));
		List<Entry<String, Long>> result = propertiesCount.entrySet().stream().filter(entry -> entry.getValue() > 1)
				.collect(Collectors.toList());
		Assert.assertEquals(
				result.stream().map(entry -> entry.getKey() + "=" + entry.getValue()).collect(Collectors.joining(",")),
				0, result.size());
	}

	// ------------------------- Assert hints

	/**
	 * Assert MicroProfile hints.
	 *
	 * @param info     the MicroProfile project information
	 * @param expected the expected MicroProfile hints.
	 */
	public static void assertHints(MicroProfileProjectInfo info, ItemHint... expected) {
		assertHints(info, null, expected);
	}

	/**
	 * Assert MicroProfile hints.
	 *
	 * @param info          the MicroProfile project information
	 * @param expectedCount MicroProfile hints expected count.
	 * @param expected      the expected MicroProfile hints.
	 */
	public static void assertHints(MicroProfileProjectInfo info, Integer expectedCount, ItemHint... expected) {
		if (expectedCount != null) {
			Assert.assertEquals(expectedCount.intValue(), info.getHints().size());
		}
		for (ItemHint item : expected) {
			assertHint(info, item);
		}
	}

	/**
	 * Assert MicroProfile metadata hint
	 *
	 * @param info     the MicroProfile project information
	 * @param expected the MicroProfile hint.
	 */
	private static void assertHint(MicroProfileProjectInfo info, ItemHint expected) {
		List<ItemHint> matches = info.getHints().stream().filter(completion -> {
			return expected.getName().equals(completion.getName());
		}).collect(Collectors.toList());

		Assert.assertEquals(
				expected.getName() + " should only exist once: Actual: "
						+ info.getHints().stream().map(c -> c.getName()).collect(Collectors.joining(",")),
				1, matches.size());

		ItemHint actual = matches.get(0);
		Assert.assertEquals("Test 'description' for '" + expected.getName() + "'", expected.getDescription(),
				actual.getDescription());
	}

	/**
	 * Returns an instance of MicroProfile {@link ItemHint}.
	 *
	 * @param name        the property name
	 * @param description the Javadoc
	 * @param binary      true if it comes from a binary field/method and false
	 *                    otherwise.
	 * @param sourceType  the source type (class or interface)
	 * @param values      the hint values
	 * @return an instance of MicroProfile {@link ItemHint}.
	 */
	public static ItemHint h(String name, String description, boolean binary, String sourceType, ValueHint... values) {
		ItemHint item = new ItemHint();
		item.setName(name);
		if (!binary) {
			item.setSource(Boolean.TRUE);
		}
		item.setDescription(description);
		item.setSourceType(sourceType);
		if (values != null) {
			item.setValues(Arrays.asList(values));
		}
		return item;
	}

	/**
	 * Returns an instance of MicroProfile {@link ValueHint}.
	 *
	 * @param value
	 * @param description
	 * @param sourceType
	 * @return an instance of MicroProfile {@link ValueHint}.
	 */
	public static ValueHint vh(String value, String description, String sourceType) {
		ValueHint vh = new ValueHint();
		vh.setValue(value);
		vh.setDescription(description);
		vh.setSourceType(sourceType);
		return vh;
	}

	/**
	 * Assert duplicate hints from the given the MicroProfile project information
	 *
	 * @param info the MicroProfile project information
	 */
	public static void assertHintsDuplicate(MicroProfileProjectInfo info) {
		Map<String, Long> hintsCount = info.getHints().stream()
				.collect(Collectors.groupingBy(ItemHint::getName, Collectors.counting()));
		List<Entry<String, Long>> result = hintsCount.entrySet().stream().filter(entry -> entry.getValue() > 1)
				.collect(Collectors.toList());
		Assert.assertEquals(
				result.stream().map(entry -> entry.getKey() + "=" + entry.getValue()).collect(Collectors.joining(",")),
				0, result.size());
	}

}
