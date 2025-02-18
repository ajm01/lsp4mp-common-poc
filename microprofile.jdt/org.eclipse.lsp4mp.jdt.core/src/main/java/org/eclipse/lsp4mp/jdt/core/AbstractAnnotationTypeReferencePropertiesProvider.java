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

import static org.eclipse.lsp4jdt.core.utils.AnnotationUtils.isMatchAnnotation;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IAnnotatable;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.TypeReferenceMatch;

/**
 * Abstract class for properties provider based on annotation search.
 *
 * @author Angelo ZERR
 *
 */
public abstract class AbstractAnnotationTypeReferencePropertiesProvider extends AbstractPropertiesProvider {

	private static final Logger LOGGER = Logger
			.getLogger(AbstractAnnotationTypeReferencePropertiesProvider.class.getName());

	private static class ElementAndAnnotationKey {
	
		private final IJavaElement javaElement;
		private final String annotationName;
		
		public ElementAndAnnotationKey(IJavaElement javaElement, String annotationName) {
			super();
			this.javaElement = javaElement;
			this.annotationName = annotationName;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((annotationName == null) ? 0 : annotationName.hashCode());
			result = prime * result + ((javaElement == null) ? 0 : javaElement.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ElementAndAnnotationKey other = (ElementAndAnnotationKey) obj;
			if (annotationName == null) {
				if (other.annotationName != null)
					return false;
			} else if (!annotationName.equals(other.annotationName))
				return false;
			if (javaElement == null) {
				if (other.javaElement != null)
					return false;
			} else if (!javaElement.equals(other.javaElement))
				return false;
			return true;
		}
		
		
	}
	
	@Override
	protected String[] getPatterns() {
		return getAnnotationNames();
	}

	/**
	 * Returns the annotation names to search.
	 *
	 * @return the annotation names to search.
	 */
	protected abstract String[] getAnnotationNames();

	@Override
	protected SearchPattern createSearchPattern(String annotationName) {
		return createAnnotationTypeReferenceSearchPattern(annotationName);
	}

	@Override
	public void collectProperties(SearchMatch match, SearchContext context, IProgressMonitor monitor) {
		IJavaElement javaElement = null;
		try {
			Object element = getMatchedElement(match);
			if (element instanceof IAnnotation) {
				// ex : for Local variable
				IAnnotation annotation = ((IAnnotation) element);
				javaElement = annotation.getParent();
				processAnnotation(javaElement, context, monitor, annotation);
			} else if (element instanceof IAnnotatable && element instanceof IJavaElement) {
					javaElement = (IJavaElement) element;
					processAnnotation(javaElement, context, monitor);
			}
		} catch (Exception e) {
			if (LOGGER.isLoggable(Level.SEVERE)) {
				LOGGER.log(Level.SEVERE,
						"Cannot compute MicroProfile properties for the Java element '" + javaElement != null
								? javaElement.getElementName()
								: match.getElement() + "'.",
						e);
			}
		}
	}


	/**
	 * Return the element associated with the
	 * given <code>match</code> and null otherwise
	 *
	 * @param match the match
	 * @return
	 */
	private static Object getMatchedElement(SearchMatch match) {
		if (match instanceof TypeReferenceMatch) {
			// localElement exists if matched element is a
			// local variable (constructor/method parameter)
			Object localElement = ((TypeReferenceMatch) match).getLocalElement();
			return localElement != null ? localElement : match.getElement();
		}
		return match.getElement();
	}

	/**
	 * Processes the annotations bound to the current
	 * <code>javaElement</code> and adds item metadata if needed
	 *
	 * @param javaElement the Java element
	 * @param context     the context
	 * @param monitor     the monitor
	 * @throws JavaModelException
	 */
	protected void processAnnotation(IJavaElement javaElement, SearchContext context, IProgressMonitor monitor)
			throws JavaModelException {
		IAnnotation[] annotations = ((IAnnotatable) javaElement).getAnnotations();
		for (IAnnotation annotation : annotations) {
			processAnnotation(javaElement, context, monitor, annotation);
		}
	}

	/**
	 * Processes the current <code>annotation</code> bound to current
	 * <code>javaElement</code> and adds item metadata if needed
	 *
	 * @param javaElement the Java element
	 * @param context     the context
	 * @param monitor     the monitor
	 * @param annotation  the annotation
	 * @throws JavaModelException
	 */
	private void processAnnotation(IJavaElement javaElement, SearchContext context, IProgressMonitor monitor,
			IAnnotation annotation) throws JavaModelException {
		String[] names = getAnnotationNames();
		for (String annotationName : names) {
			if (isMatchAnnotation(annotation, annotationName)) {
				// The provider matches the annotation based
				if (isAlreadyProcessed(new ElementAndAnnotationKey(javaElement, annotationName), context)) {
					// The processAnnotation has already been done for the Java element and the annotation
					return;
				}				
				processAnnotation(javaElement, annotation, annotationName, context, monitor);
				break;
			}
		}
	}

	protected abstract void processAnnotation(IJavaElement javaElement, IAnnotation annotation, String annotationName,
			SearchContext context, IProgressMonitor monitor) throws JavaModelException;

}
