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
import org.jboss.forge.project.Facet;
import org.jboss.forge.project.ProjectModelException;
import org.jboss.forge.project.facets.BaseFacet;
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
public class EclipsePluginFacetImpl extends BaseFacet implements EclipsePluginFacet, Facet
{

   @Inject
   private BeanManager manager;

   public EclipsePluginFacetImpl()
   {
   }

   @Override
   public boolean isInstalled()
   {
      return getManiestFile().exists();
   }

   @Override
   public boolean install()
   {
      createManifest();
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
               pom.getVersion().replace("SNAPSHOT", "qualifier"));
      setManifest(manifest);
      return manifest;
   }

   @Override
   public Properties getBuildProperties()
   {
      Properties properties = new Properties();

      if (getBuildPropertiesFile().exists())
      {
         InputStream inputStream = getBuildPropertiesFile().getResourceInputStream();
         try
         {
            properties.load(inputStream);
         }
         catch (IOException e)
         {
            throw new ProjectModelException("Could not read build.properties file: "
                     + getBuildPropertiesFile(), e);
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
                  throw new ProjectModelException("Could not read build.properties file: "
                           + getBuildPropertiesFile(), e);
               }
         }
      }
      return properties;
   }

   @Override
   public FileResource<?> getBuildPropertiesFile()
   {
      Resource<?> file = project.getProjectRoot().getChild(
               "build.properties");
      return (FileResource<?>) file;
   }

   @Override
   public void setBuildProperties(Properties properties)
   {
      FileWriter fw = null;
      try
      {
         fw = new FileWriter(getBuildPropertiesFile().getUnderlyingResourceObject());
         properties.store(fw, null);
      }
      catch (IOException e)
      {
         throw new ProjectModelException("Could not write build.properties file: "
                  + getBuildPropertiesFile(), e);
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
               throw new ProjectModelException("Could not write build.properties file: "
                        + getBuildPropertiesFile(), e);
            }
      }

      manager.fireEvent(new ResourceModified(getBuildPropertiesFile()),
               new Annotation[] {});
   }

}