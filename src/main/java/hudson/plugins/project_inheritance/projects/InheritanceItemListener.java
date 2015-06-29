package hudson.plugins.project_inheritance.projects;

import java.io.IOException;
import java.util.Collection;

import hudson.Extension;

import hudson.model.Item;
import hudson.model.listeners.ItemListener ;

import hudson.plugins.project_inheritance.projects.references.AbstractProjectReference;

import jenkins.model.Jenkins;

import hudson.plugins.project_inheritance.projects.creation.ProjectCreationEngine;

@Extension
public class InheritanceItemListener extends ItemListener {


	@Override
	public void onLocationChanged(Item item, String oldFullName, String newFullName) {
		System.out.println("[MOVEDTO] [FROM]" + oldFullName + " [TO] " + newFullName + "=" + item.getFullName());
		super.onLocationChanged(item, oldFullName, newFullName);
		if ( !(item instanceof InheritanceProject) ) {
			return;
		}
		System.out.println("It is an InheritanceProject");
		ProjectCreationEngine.instance.notifyProjectChange((InheritanceProject) item);

		Collection<InheritanceProject> iProjects = InheritanceProject.getProjectsMap().values();
		for (InheritanceProject pReadOnly : iProjects) {
			InheritanceProject p = (InheritanceProject) Jenkins.getInstance().getItemByFullName(pReadOnly.getFullName());
			InheritanceProject.clearBuffers((InheritanceProject) item);
			InheritanceProject.clearBuffers(p);
			boolean changed = p.switchProject(oldFullName, newFullName);
			if (changed) {
				try {
					String xmlBefore = p.getConfigFile().asString();
					p.save();
ProjectCreationEngine.instance.notifyProjectChange(p);
					String xmlAfter = p.getConfigFile().asString();
					System.out.println(xmlBefore.equals(xmlAfter));
					System.out.println(xmlAfter);
					InheritanceProject.clearBuffers(p);
				} catch (IOException cause) {
					cause.printStackTrace();
				}
			}
		}
		InheritanceProject.clearBuffers(null);
	}
}
