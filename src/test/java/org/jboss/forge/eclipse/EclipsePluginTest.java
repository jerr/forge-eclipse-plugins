package org.jboss.forge.eclipse;

import javax.inject.Inject;

import junit.framework.Assert;

import org.apache.maven.model.Model;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.DependencyResolver;
import org.jboss.forge.project.packaging.PackagingType;
import org.jboss.forge.test.AbstractShellTest;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;

/**
 * @author Jérmie Lagarde
 * 
 */
public class EclipsePluginTest extends AbstractShellTest
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
   public void testDefaultSetup() throws Exception
   {
      Project project = initializeProject(PackagingType.BASIC);
      queueInputLines("");
      getShell().execute("eclipse-plugins setup");
      Assert.assertTrue("has EclipsePluginFacet", getProject().hasFacet(EclipsePluginFacet.class));
      Model pom = project.getFacet(MavenCoreFacet.class).getPOM();
      Assert.assertEquals(EclipsePackagingType.PLUGIN.getType(), pom.getPackaging());
   }

   @Test
   public void testSetupAsEclipseParentProject() throws Exception
   {
      Project project = initializeProject(PackagingType.BASIC);
      queueInputLines("");
      getShell().execute("eclipse-plugins setup --type " + EclipsePackagingType.PARENT.getType());
      Model pom = project.getFacet(MavenCoreFacet.class).getPOM();
      Assert.assertEquals(EclipsePackagingType.PARENT.getType(), pom.getPackaging());
      Assert.assertNotNull("has tycho-version property", pom.getProperties().get("tycho-version"));
   }

   @Test
   public void testSetupAsEclipseParentProjectWithTychoVersion() throws Exception
   {
      Project project = initializeProject(PackagingType.BASIC);
      queueInputLines("");
      getShell().execute("eclipse-plugins setup --tychoVersion 0.14.0 --type " + EclipsePackagingType.PARENT.getType());
      Model pom = project.getFacet(MavenCoreFacet.class).getPOM();
      Assert.assertEquals("0.14.0", pom.getProperties().get("tycho-version"));
   }

   @Test
   public void testSetupAsEclipseParentProjectWithBadTychoVersion() throws Exception
   {
      Project project = initializeProject(PackagingType.BASIC);
      queueInputLines("");
      getShell().execute("eclipse-plugins setup --tychoVersion 0.0.0 --type " + EclipsePackagingType.PARENT.getType());
      Model pom = project.getFacet(MavenCoreFacet.class).getPOM();
      Assert.assertEquals("0.15.0", pom.getProperties().get("tycho-version"));
   }

   @Test
   public void testSetupAsEclipseParentProjectWithEclipsePluginModule() throws Exception
   {
      Project project = initializeProject(PackagingType.BASIC);
      queueInputLines("");
      getShell().execute("eclipse-plugins setup  --type " + EclipsePackagingType.PARENT.getType());
      queueInputLines("");
      getShell()
               .execute("new-project --named com.test.plugin --projectFolder com.test.plugin --topLevelPackage com.test --type JAR");
      queueInputLines("");
      getShell().execute("eclipse-plugins setup --type " + EclipsePackagingType.PLUGIN.getType());
      Model pom = project.getFacet(MavenCoreFacet.class).getPOM();
      Assert.assertEquals(EclipsePackagingType.PLUGIN.getType(), pom.getPackaging());
   }
}
