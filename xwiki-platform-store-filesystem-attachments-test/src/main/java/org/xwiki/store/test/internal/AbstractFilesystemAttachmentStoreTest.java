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
package org.xwiki.store.test.internal;

import java.io.File;

import com.xpn.xwiki.web.Utils;
import javax.servlet.ServletContext;
import org.jmock.Expectations;
import org.junit.Before;
import org.xwiki.environment.Environment;
import org.xwiki.environment.internal.ServletEnvironment;
import org.xwiki.test.AbstractMockingComponentTestCase;
import org.xwiki.test.annotation.MockingRequirement;
import org.xwiki.test.annotation.AllComponents;

/**
 * Boilerplate for filesystem attachment tests.
 *
 * @version $Id$
 * @since TODO
 */
@AllComponents
@MockingRequirement(ServletEnvironment.class)
public abstract class AbstractFilesystemAttachmentStoreTest extends AbstractMockingComponentTestCase
{
    /** The environment for getting permanent files to store the attachments. */
    private ServletEnvironment environment;

    @Before
    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        Utils.setComponentManager(this.getComponentManager());

        final ServletEnvironment env = (ServletEnvironment) this.getComponentManager().getInstance(Environment.class);
        final ServletContext mockServletContext = this.getMockery().mock(ServletContext.class);
        env.setServletContext(mockServletContext);

        this.getMockery().checking(new Expectations() {
            {
                allowing(mockServletContext).getAttribute("javax.servlet.context.tempdir");
                will(returnValue(new File(System.getProperty("java.io.tmpdir"))));

                allowing(mockServletContext).getResource("/WEB-INF/xwiki.properties");
                will(returnValue(null));
            }
        });
    }
}
