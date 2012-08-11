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
import java.util.List;
import java.util.Properties;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.apache.maven.model.Model;
import org.jboss.forge.eclipse.EclipseFeatureFacet;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.maven.MavenPluginFacet;
import org.jboss.forge.parser.xml.Node;
import org.jboss.forge.parser.xml.XMLParser;
import org.jboss.forge.project.Facet;
import org.jboss.forge.project.ProjectModelException;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.resources.events.ResourceModified;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.RequiresFacet;

/**
 * @author Jérmie Lagarde
 * 
 */
@Dependent
@Alias("forge.eclipse.EclipseFeatureFacet")
@RequiresFacet({ MavenPluginFacet.class })
public class EclipseFeatureFacetImpl extends AbstractEclipseFacetImpl implements EclipseFeatureFacet, Facet
{

   @Inject BeanManager manager;
   
   @Override
   public boolean isInstalled()
   {
      return getFeatureXmlFile().exists();
   }

   @Override
   public boolean install()
   {
      createFeatureXmlFile();
      installBuildProperties();
      return true;
   }

   private FileResource<?> getFeatureXmlFile()
   {
      Resource<?> file = project.getProjectRoot().getChild(
               "feature.xml");
      return (FileResource<?>) file;
   }

   private FileResource<?> createFeatureXmlFile()
   {
      FileResource<?> featureXmlFile = getFeatureXmlFile();
      if (!featureXmlFile.exists())
      {
         getFeatureXmlRootNode();
      }
      return featureXmlFile;
   }

   private void installBuildProperties()
   {
      Properties properties = getBuildProperties();
      properties.setProperty("bin.includes", "feature.xml");
      setBuildProperties(properties);
   }

   private Node getFeatureXmlRootNode()
   {
      FileResource<?> featureXmlFile = getFeatureXmlFile();
      if (!featureXmlFile.exists())
      {
         MavenCoreFacet mavenFacet = project.getFacet(MavenCoreFacet.class);
         Model pom = mavenFacet.getPOM();

         featureXmlFile.createNewFile();
         Node feature = XMLParser.parse("<feature/>");
         feature.attribute("id", pom.getArtifactId());
         feature.attribute("label", pom.getName());
         feature.attribute("version", pom.getVersion().replace("-SNAPSHOT", ".qualifier"));
         saveFeatureXml(feature);
         return feature;
      }
      return XMLParser.parse(featureXmlFile.getResourceInputStream());
   }

   private void saveFeatureXml(Node feature)
   {
      getFeatureXmlFile().setContents(XMLParser.toXMLInputStream(feature));
      manager.fireEvent(new ResourceModified(getFeatureXmlFile()),
               new Annotation[] {});
   }

   public Properties getFeatureProperties()
   {
      Properties properties = new Properties();

      if (getFeaturePropertiesFile().exists())
      {
         InputStream inputStream = getFeaturePropertiesFile().getResourceInputStream();
         try
         {
            properties.load(inputStream);
         }
         catch (IOException e)
         {
            throw new ProjectModelException("Could not read feature.properties file: "
                     + getFeaturePropertiesFile(), e);
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
                  throw new ProjectModelException("Could not read feature.properties file: "
                           + getFeaturePropertiesFile(), e);
               }
         }
      }
      return properties;
   }

   public FileResource<?> getFeaturePropertiesFile()
   {
      Resource<?> file = project.getProjectRoot().getChild(
               "feature.properties");
      return (FileResource<?>) file;
   }

   public void setFeatureProperties(Properties properties)
   {
      FileWriter fw = null;
      try
      {
         fw = new FileWriter(getFeaturePropertiesFile().getUnderlyingResourceObject());
         properties.store(fw, null);
      }
      catch (IOException e)
      {
         throw new ProjectModelException("Could not write feature.properties file: "
                  + getFeaturePropertiesFile(), e);
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
               throw new ProjectModelException("Could not write feature.properties file: "
                        + getFeaturePropertiesFile(), e);
            }
      }

      manager.fireEvent(new ResourceModified(getFeaturePropertiesFile()),
               new Annotation[] {});
   }

   @Override
   public String getFeatureId()
   {
      return getFeatureXmlRootNode().getAttribute("id");
   }

   @Override
   public void setFeatureId(String id)
   {
      Node feature = getFeatureXmlRootNode();
      feature.attribute("id", id);
      saveFeatureXml(feature);
   }

   @Override
   public String getFeatureLabel()
   {
      return getFeatureXmlRootNode().getAttribute("label");
   }

   @Override
   public void setFeatureLabel(String label)
   {
      Node feature = getFeatureXmlRootNode();
      feature.attribute("label", label);
      saveFeatureXml(feature);
   }

   @Override
   public String getFeatureVersion()
   {
      return getFeatureXmlRootNode().getAttribute("version");
   }

   @Override
   public void setFeatureVersion(String version)
   {
      Node feature = getFeatureXmlRootNode();
      feature.attribute("version", version);
      saveFeatureXml(feature);
   }

   @Override
   public String getFeatureProviderName()
   {
      return getFeatureXmlRootNode().getAttribute("provider-name");
   }

   @Override
   public void setFeatureProviderName(String providerName)
   {
      Node feature = getFeatureXmlRootNode();
      feature.attribute("provider-name", providerName);
      saveFeatureXml(feature);
   }

   @Override
   public String getDescription()
   {
      Node feature = getFeatureXmlRootNode();
      Node description = feature.getSingle("description");
      if (description != null)
         return description.getText();
      return null;
   }

   @Override
   public void setDescription(String description)
   {
      Node feature = getFeatureXmlRootNode();
      feature.getOrCreate("description").text(description);
      saveFeatureXml(feature);
   }

   @Override
   public String getDescriptionUrl()
   {
      Node feature = getFeatureXmlRootNode();
      Node description = feature.getSingle("description");
      if (description != null)
         return description.getAttribute("url");
      return null;
   }

   @Override
   public void setDescriptionUrl(String descriptionUrl)
   {
      Node feature = getFeatureXmlRootNode();
      feature.getOrCreate("description").attribute("url", descriptionUrl);
      saveFeatureXml(feature);
   }

   @Override
   public String getCopyright()
   {
      Node feature = getFeatureXmlRootNode();
      Node copyright = feature.getSingle("copyright");
      if (copyright != null)
         return copyright.getText();
      return null;
   }

   @Override
   public void setCopyright(String copyright)
   {
      Node feature = getFeatureXmlRootNode();
      feature.getOrCreate("copyright").text(copyright);
      saveFeatureXml(feature);
   }

   @Override
   public String getCopyrightUrl()
   {
      Node feature = getFeatureXmlRootNode();
      Node copyright = feature.getSingle("copyright");
      if (copyright != null)
         return copyright.getAttribute("url");
      return null;
   }

   @Override
   public void setCopyrightUrl(String copyrightUrl)
   {
      Node feature = getFeatureXmlRootNode();
      feature.getOrCreate("copyright").attribute("url", copyrightUrl);
      saveFeatureXml(feature);
   }

   @Override
   public String getLicense()
   {
      Node feature = getFeatureXmlRootNode();
      Node license = feature.getSingle("license");
      if (license != null)
         return license.getText();
      return null;
   }

   @Override
   public void setLicense(String license)
   {
      Node feature = getFeatureXmlRootNode();
      feature.getOrCreate("license").text(license);
      saveFeatureXml(feature);
   }

   @Override
   public String getLicenseUrl()
   {
      Node feature = getFeatureXmlRootNode();
      Node license = feature.getSingle("license");
      if (license != null)
         return license.getAttribute("url");
      return null;
   }

   @Override
   public void setLicenseUrl(String licenseUrl)
   {
      Node feature = getFeatureXmlRootNode();
      feature.getOrCreate("license").attribute("url", licenseUrl);
      saveFeatureXml(feature);
   }

   @Override
   public void addPlugin(String id, String version)
   {
      Node feature = getFeatureXmlRootNode();
      List<Node> plugins = feature.get("plugin");
      Node plugin = null;
      for (Node node : plugins)
      {
         if (id.equals(node.getAttribute(id)))
         {
            plugin = node;
            break;
         }
      }
      if (plugin == null)
      {
         plugin = feature.createChild("plugin");
         plugin.attribute("id", id);
      }

      if (plugin.getAttribute("download-size") == null)
         plugin.attribute("download-size", 0);

      if (plugin.getAttribute("install-size") == null)
         plugin.attribute("install-size", 0);

      if (version == null || version.isEmpty())
         version = "0.0.0.0";
      plugin.attribute("version", version);

      if (plugin.getAttribute("unpack") == null)
         plugin.attribute("unpack", "false");

      saveFeatureXml(feature);
   }

   @Override
   protected BeanManager getBeanManager()
   {
      return manager;
   }

}