/*
 * JBoss, by Red Hat.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.forge.eclipse.facets;

import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.Properties;
import java.util.jar.Attributes.Name;
import java.util.jar.Manifest;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.apache.maven.model.Model;
import org.jboss.forge.eclipse.EclipsePluginFacet;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.maven.MavenPluginFacet;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.project.Facet;
import org.jboss.forge.project.ProjectModelException;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.resources.events.ResourceModified;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.osgi.framework.Constants;

/**
 * @author Jérmie Lagarde
 * 
 */
@Dependent
@Alias("forge.eclipse.EclipsePluginFacet")
@RequiresFacet({ MavenPluginFacet.class })
public class EclipsePluginFacetImpl extends AbstractEclipseFacetImpl implements EclipsePluginFacet, Facet
{
   @Inject BeanManager manager;
   
   @Override
   public boolean isInstalled()
   {
      return getManiestFile().exists() && getBuildPropertiesFile().exists();
   }

   @Override
   public boolean install()
   {
      createManifest();
      installBuildProperties();
      return true;
   }

   @Override
   public Manifest getManifest()
   {
      if (!getManiestFile().exists())
         return new Manifest();
      InputStream inputStream = getManiestFile()
               .getResourceInputStream();
      try
      {
         Manifest manifest = new Manifest(inputStream);
         return manifest;
      }
      catch (IOException e)
      {
         throw new ProjectModelException("Could not read MANIFEST file: "
                  + getManiestFile(), e);
      }
      finally
      {
         if (inputStream != null)
            try
            {
               inputStream.close();
            }
            catch (IOException e)
            {
               throw new ProjectModelException("Could not read MANIFEST file: "
                        + getManiestFile(), e);
            }
      }
   }

   @Override
   public FileResource<?> getManiestFile()
   {
      Resource<?> file = project.getProjectRoot().getChild(
               "META-INF/MANIFEST.MF");
      return (FileResource<?>) file;
   }

   @Override
   public void setManifest(Manifest manifest)
   {
      FileWriter fw = null;
      try
      {
         fw = new FileWriter(getManiestFile().getUnderlyingResourceObject());
         ByteArrayOutputStream content = new ByteArrayOutputStream();
         manifest.write(content);
         fw.write(content.toString("UTF-8"));

      }
      catch (IOException e)
      {
         throw new ProjectModelException("Could not write MANIFEST file: "
                  + getManiestFile(), e);
      }
      finally
      {
         if (fw != null)
            try
            {
               fw.close();
            }
            catch (IOException e)
            {
               throw new ProjectModelException(
                        "Could not write MANIFEST file: "
                                 + getManiestFile(), e);
            }
      }
      manager.fireEvent(new ResourceModified(getManiestFile()), new Annotation[] {});
   }

   private Manifest createManifest()
   {
      FileResource<?> manifestFile = getManiestFile();
      if (!manifestFile.exists())
      {
         manifestFile.createNewFile();
      }

      MavenCoreFacet mavenFacet = project.getFacet(MavenCoreFacet.class);
      Model pom = mavenFacet.getPOM();

      Manifest manifest = new Manifest();
      manifest.getMainAttributes().put(Name.MANIFEST_VERSION, "1.0");
      manifest.getMainAttributes().putValue(Constants.BUNDLE_MANIFESTVERSION,
               "2");
      manifest.getMainAttributes().put(new Name(Constants.BUNDLE_NAME),
               pom.getArtifactId());
      manifest.getMainAttributes().put(
               new Name(Constants.BUNDLE_SYMBOLICNAME),
               pom.getArtifactId() + "; singleton:=true");
      manifest.getMainAttributes().put(new Name(Constants.BUNDLE_VERSION),
               pom.getVersion().replace("-SNAPSHOT", ".qualifier"));
      manifest.getMainAttributes().put(new Name(Constants.REQUIRE_BUNDLE),"org.eclipse.core.runtime");
      manifest.getMainAttributes().put(new Name(Constants.BUNDLE_ACTIVATIONPOLICY),Constants.ACTIVATION_LAZY);      
      setManifest(manifest);
      return manifest;
   }

   private void installBuildProperties()
   {
      MavenCoreFacet mavenFacet = project.getFacet(MavenCoreFacet.class);
      Model pom = mavenFacet.getPOM();
      String sourceDirectory = pom.getBuild().getSourceDirectory();
      {
         Properties properties =  getBuildProperties();
         properties.setProperty("source..", sourceDirectory);
         properties.setProperty("output..", "bin");
         setBuildProperties(properties);
      }
   }

   @Override
   public void setActivator(JavaClass activatorClass)
   {
      Manifest manifest = getManifest();
      manifest.getMainAttributes().put(new Name(Constants.BUNDLE_ACTIVATOR),activatorClass.getQualifiedName());
      setManifest(manifest);
   }

   @Override
   protected BeanManager getBeanManager()
   {
      return manager;
   }

}