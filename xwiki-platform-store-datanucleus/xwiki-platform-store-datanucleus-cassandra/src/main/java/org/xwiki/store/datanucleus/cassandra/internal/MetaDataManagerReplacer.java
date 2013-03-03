/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
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
package org.xwiki.store.datanucleus.cassandra.internal;

import java.util.Map;

import javax.jdo.PersistenceManagerFactory;
import org.apache.commons.lang.reflect.FieldUtils;
import org.datanucleus.jdo.JDOPersistenceManagerFactory;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.OMFContext;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.jdo.metadata.JDOMetaDataManager;
import org.xwiki.store.objects.PersistableClassLoader;
import org.xwiki.component.util.ReflectionUtils;
import org.xwiki.store.objects.Callback;

/**
 * TODO: offer a patch to make this an extension point so this can go away.
 */
public class MetaDataManagerReplacer implements Callback
{
    private Map classMetaDataByClass;
    private Map classMetaDataByEntityName;
    private Map classMetaDataByDiscriminatorName;
    private Map ormClassMetaDataByClass;
    private Map classMetaDataByInterface;

    private MetaDataManagerReplacer(PersistenceManagerFactory pmf, PersistableClassLoader pcl) throws Exception
    {
        final JDOPersistenceManagerFactory jpmf = (JDOPersistenceManagerFactory) pmf;
        final JDOMetaDataManager mdm = (JDOMetaDataManager) jpmf.getOMFContext().getMetaDataManager();
        this.classMetaDataByClass = (Map) FieldUtils.readField(mdm, "classMetaDataByClass", true);
        this.classMetaDataByEntityName = (Map) FieldUtils.readField(mdm, "classMetaDataByEntityName", true);
        this.classMetaDataByDiscriminatorName = (Map) FieldUtils.readField(mdm, "classMetaDataByDiscriminatorName", true);
        this.ormClassMetaDataByClass = (Map) FieldUtils.readField(mdm, "ormClassMetaDataByClass", true);
        this.classMetaDataByInterface = (Map) FieldUtils.readField(mdm, "classMetaDataByInterface", true);
        pcl.onClassRedefinition(this);
    }

    public static void ugly(PersistenceManagerFactory pmf, PersistableClassLoader pcl)
    {
        try {
            new MetaDataManagerReplacer(pmf, pcl);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize MetaDataManagerReplacer", e);
        }
    }

    public void callback(final Object[] args)
    {
        final String className = (String) args[0];
        final AbstractClassMetaData acmd = (AbstractClassMetaData) this.classMetaDataByClass.get(className);
        if (acmd != null) {
            // Remove the acmd from all maps which might contain it, handling duplicate values.
            while (this.classMetaDataByClass.values().remove(acmd));
            while (this.classMetaDataByEntityName.values().remove(acmd));
            while (this.classMetaDataByDiscriminatorName.values().remove(acmd));
            while (this.ormClassMetaDataByClass.values().remove(acmd));
            while (this.classMetaDataByInterface.values().remove(acmd));
        }
    }
}
