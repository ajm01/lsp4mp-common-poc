/*******************************************************************************
* Copyright (c) 2019-2020 Red Hat Inc. and others.
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

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.lsp4jdt.commons.JavaProjectLabelsParams;
import org.eclipse.lsp4mp.commons.ProjectLabelInfoEntry;
import org.eclipse.lsp4jdt.core.utils.IJDTUtils;
import org.eclipse.lsp4mp.jdt.core.utils.JDTMicroProfileUtils;
import org.eclipse.lsp4mp.jdt.core.utils.JDTTypeUtils;
import org.eclipse.lsp4mp.jdt.internal.core.ProjectLabelRegistry;

/**
 * Project label manager which provides <code>ProjectLabelInfo</code> containing
 * project labels for all projects in the workspace
 *
 */
public class ProjectLabelManager {
	private static final ProjectLabelManager INSTANCE = new ProjectLabelManager();

	public static ProjectLabelManager getInstance() {
		return INSTANCE;
	}

	private ProjectLabelManager() {

	}

	/**
	 * Returns project label results for all projects in the workspace
	 *
	 * @return project label results for all projects in the workspace
	 */
	public List<ProjectLabelInfoEntry> getProjectLabelInfo() {
		List<ProjectLabelInfoEntry> results = new ArrayList<>();
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();

		for (IProject project : projects) {
			ProjectLabelInfoEntry info = getProjectLabelInfo(project, null);
			if (info != null) {
				results.add(info);
			}
		}
		return results;
	}

	/**
	 * Returns project label results for the given Eclipse project.
	 *
	 * @param project Eclipse project.
	 * @param types   the Java type list to check.
	 * @return project label results for the given Eclipse project.
	 */
	private ProjectLabelInfoEntry getProjectLabelInfo(IProject project, List<String> types) {
		String uri = JDTMicroProfileUtils.getProjectURI(project);
		if (uri != null) {
			return new ProjectLabelInfoEntry(uri, project.getName(), getProjectLabels(project, types));
		}
		return null;
	}

	/**
	 * Returns project label results for the given Java file uri parameter.
	 *
	 * @param params  the Java file uri parameter.
	 * @param utils   the JDT utilities.
	 * @param monitor the progress monitor.
	 * @return project label results for the given Java file uri parameter.
	 */
	public ProjectLabelInfoEntry getProjectLabelInfo(JavaProjectLabelsParams params, IJDTUtils utils,
			IProgressMonitor monitor) {
		IProject project = findProject(params.getUri(), utils);
		if (project == null) {
			// The uri doesn't belong to an Eclipse project
			return ProjectLabelInfoEntry.EMPTY_PROJECT_INFO;
		}
		return getProjectLabelInfo(project, params.getTypes());
	}

	private static IProject findProject(String uri, IJDTUtils utils) {
		if (uri.startsWith("jdt://jarentry")) {
			URI jarEntryUri = URI.create(uri);
			String rootId = jarEntryUri.getQuery();
			IPackageFragmentRoot packageRoot = (IPackageFragmentRoot) JavaCore.create(rootId);
			if (packageRoot != null) {
				IJavaProject javaProject = packageRoot.getJavaProject();
				return javaProject != null ? javaProject.getProject() : null;
			}
			return null;
		}
		IFile file = utils.findFile(uri);
		return file != null ? file.getProject() : null;
	}

	/**
	 * Returns the project labels for the given project.
	 *
	 * @param project the Eclipse project.
	 * @param types   the Java type list to check.
	 * @return the project labels for the given project.
	 */
	private List<String> getProjectLabels(IProject project, List<String> types) {
		IJavaProject javaProject = JavaCore.create(project);

		if (javaProject == null) {
			return Collections.emptyList();
		}

		// Update labels by using the
		// "org.eclipse.lsp4mp.jdt.core.projectLabelProviders" extension point (ex
		// : "maven", "gradle", "quarkus", "microprofile").
		List<String> projectLabels = new ArrayList<>();
		List<ProjectLabelDefinition> definitions = ProjectLabelRegistry.getInstance().getProjectLabelDefinitions();
		for (ProjectLabelDefinition definition : definitions) {
			projectLabels.addAll(definition.getProjectLabels(javaProject));
		}
		// Update labels by checking if some Java types are in the classpath of the Java
		// project.
		if (types != null) {
			for (String type : types) {
				if (JDTTypeUtils.findType(javaProject, type) != null) {
					projectLabels.add(type);
				}
			}
		}

		return projectLabels;
	}

}
