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
package org.jboss.forge.eclipse;

import java.util.Properties;
import java.util.jar.Manifest;

import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.resources.FileResource;

/**
 * @author Jérmie Lagarde
 * 
 */
public interface EclipsePluginFacet extends EclipseFacet
{
   /**
    * Get the current Eclipse Plugin Manifest.
    */
   public Manifest getManifest();

   /**
    * Get the current Eclipse Plugin META-INF/MANIFEST.MF file.
    */
   public FileResource<?> getManiestFile();

   /**
    * Set the current Eclipse Plugin Manifest (overwriting any existing Manifest)
    */
   public void setManifest(Manifest manifest);

   /**
    * Get the current Eclipse Plugin Build Properties.
    */
   public Properties getBuildProperties();

   /**
    * Get the current Eclipse Plugin build.properties file.
    */
   public FileResource<?> getBuildPropertiesFile();

   /**
    * Set the current Eclipse Plugin Build Properties (overwriting any existing properties)
    */
   public void setBuildProperties(Properties properties);
   
   /**
    * Set Eclipse Plugin Activator class
    */
   public void setActivator(JavaClass activatorClass);

}
