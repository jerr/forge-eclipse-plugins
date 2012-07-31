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
public enum EclipsePackagingType
{
   NONE("", "None"),
   PARENT("pom", "Parent Project"),
   PLUGIN("eclipse-plugin", "Eclipse plugin project"),
   TESTPLUGIN("eclipse-test-plugin", "Eclipse test plugin project"),
   FEATURE("eclipse-feature", "Eclipse feature project"),
   REPOSITORY("eclipse-repository", "Eclipse repository project"),
   OTHER("", "Other packaging type");

   private String type;
   private String description;

   private EclipsePackagingType(final String type, final String description)
   {
      setType(type);
      setDescription(description);
   }

   public String getType()
   {
      return type;
   }

   private void setType(String type)
   {
      if (type != null)
      {
         type = type.trim().toLowerCase();
      }
      this.type = type;
   }

   @Override
   public String toString()
   {
      return type;
   }

   public String getDescription()
   {
      return description;
   }

   private void setDescription(final String description)
   {
      this.description = description;
   }

   public static EclipsePackagingType from(String type)
   {
      EclipsePackagingType result = OTHER;
      if ((type != null) && !type.trim().isEmpty())
      {
         type = type.trim();
         for (EclipsePackagingType p : values())
         {
            if (p.getType().equals(type) || p.name().equalsIgnoreCase(type))
            {
               result = p;
               break;
            }
         }
      }
      return result;
   }
}
