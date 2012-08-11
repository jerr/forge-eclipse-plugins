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

/**
 * @author Jérmie Lagarde
 * 
 */
public interface EclipseFeatureFacet extends EclipseFacet
{

   /**
    * Get the feature id.
    */
   public String getFeatureId();

   /**
    * Set the feature id.
    */
   public void setFeatureId(String id);

   /**
    * Get the feature label.
    */
   public String getFeatureLabel();

   /**
    * Set the feature label.
    */
   public void setFeatureLabel(String label);

   /**
    * Get the feature version.
    */
   public String getFeatureVersion();

   /**
    * Set the feature version.
    */
   public void setFeatureVersion(String version);

   /**
    * Get the feature provider name.
    */
   public String getFeatureProviderName();

   /**
    * Set the feature provider name.
    */
   public void setFeatureProviderName(String providerName);

   /**
    * Get the description.
    */
   public String getDescription();

   /**
    * Set the description.
    */
   public void setDescription(String description);

   /**
    * Get the description url.
    */
   public String getDescriptionUrl();

   /**
    * Set the description url.
    */
   public void setDescriptionUrl(String descriptionUrl);

   /**
    * Get the copyright.
    */
   public String getCopyright();

   /**
    * Set the copyright.
    */
   public void setCopyright(String copyright);

   /**
    * Get the copyright url.
    */
   public String getCopyrightUrl();

   /**
    * Set the copyright url.
    */
   public void setCopyrightUrl(String copyrightUrl);

   /**
    * Get the license.
    */
   public String getLicense();

   /**
    * Set the license.
    */
   public void setLicense(String license);

   /**
    * Get the license url.
    */
   public String getLicenseUrl();

   /**
    * Set the license url.
    */
   public void setLicenseUrl(String licenseUrl);

   /**
    * Add plugin.
    */
   void addPlugin(String id, String version);

}
