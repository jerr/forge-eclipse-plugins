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

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.Properties;

import javax.enterprise.inject.spi.BeanManager;

import org.jboss.forge.project.ProjectModelException;
import org.jboss.forge.project.facets.BaseFacet;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.resources.events.ResourceModified;

/**
 * @author Jérmie Lagarde
 * 
 */
public abstract class AbstractEclipseFacetImpl extends BaseFacet
{
   
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

   public FileResource<?> getBuildPropertiesFile()
   {
      Resource<?> file = project.getProjectRoot().getChild(
               "build.properties");
      return (FileResource<?>) file;
   }

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
   
      getBeanManager().fireEvent(new ResourceModified(getBuildPropertiesFile()),
               new Annotation[] {});
   }

   protected abstract BeanManager getBeanManager();
}