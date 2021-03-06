/*
  * The contents of this file are subject to the terms of the Common Development
  * and Distribution License (the License). You may not use this file except in
  * compliance with the License.
  *
  * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
  * or http://www.netbeans.org/cddl.txt.
  *
  * When distributing Covered Code, include this CDDL Header Notice in each file
  * and include the License file at http://www.netbeans.org/cddl.txt.
  * If applicable, add the following below the CDDL Header, with the fields
  * enclosed by brackets [] replaced by your own identifying information:
  * "Portions Copyrighted [year] [name of copyright owner]"
  *
  * The Original Software is NetBeans. The Initial Developer of the Original
  * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
  * Microsystems, Inc. All Rights Reserved.
  */

package org.netbeans.modules.portalpack.servers.websynergy;

import javax.enterprise.deploy.shared.factories.DeploymentFactoryManager;
import javax.enterprise.deploy.spi.DeploymentManager;
import javax.enterprise.deploy.spi.factories.DeploymentFactory;
import org.netbeans.modules.portalpack.servers.core.api.PSDeploymentFactory;
import org.netbeans.modules.portalpack.servers.websynergy.common.LiferayConstants;

/**
 *
 * @author root
 */
public class LiferayDeploymentFactory extends PSDeploymentFactory{
    
     private static DeploymentFactory instance;  
     public static synchronized DeploymentFactory create() {
        if (instance == null) {
            instance = new LiferayDeploymentFactory();
            DeploymentFactoryManager.getInstance().registerDeploymentFactory(instance);
        }
        return instance;
    }
    
    public DeploymentManager getPSDeploymentManager(String uri, String psVersion) {
        return new LiferayDeploymentManager(uri,psVersion);
    }

    public String getDisplayName() {
        return org.openide.util.NbBundle.getMessage(LiferayDeploymentFactory.class, "LBL_LifeRay");
    }

    public String getURIPrefix() {
        return LiferayConstants.LR_1_0_URI_PREFIX;
    }

    public String getPSVersion() {
        return LiferayConstants.LR_1_0;
    }
    
}
