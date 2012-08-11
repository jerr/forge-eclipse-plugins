package org.jboss.forge.eclipse;

import static org.junit.Assert.assertFalse;

import javax.inject.Inject;

import junit.framework.Assert;

import org.apache.maven.model.Model;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.DependencyResolver;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.packaging.PackagingType;
import org.jboss.forge.shell.util.Packages;
import org.jboss.forge.test.AbstractShellTest;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;

/**
 * @author Jérmie Lagarde
 * 
 */
public class EclipsePluginFeatureTest extends AbstractShellTest
{

   @Inject
   private DependencyResolver resolver;

   @Deployment
   public static JavaArchive getDeployment()
   {
      return AbstractShellTest.getDeployment().addPackages(true,
               EclipsePlugin.class.getPackage());
   }


   @Test
   public void testSetupAsEclipseFeatureProject() throws Exception
   {
      Project project = initializeProject(PackagingType.BASIC);
      queueInputLines("");
      getShell().execute("eclipse-plugins setup --type " + EclipsePackagingType.FEATURE.getType());
      Model pom = project.getFacet(MavenCoreFacet.class).getPOM();
      Assert.assertEquals(EclipsePackagingType.FEATURE.getType(), pom.getPackaging());      
   }
}
