/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.carbon.identity.oauth2.listener;

import org.mockito.Mock;
import org.mockito.testng.MockitoTestNGListener;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.wso2.carbon.identity.common.testng.WithCarbonHome;
import org.wso2.carbon.identity.oauth2.TestConstants;
import org.wso2.carbon.stratos.common.beans.TenantInfoBean;
import org.wso2.carbon.stratos.common.exception.StratosException;

@WithCarbonHome
@Listeners(MockitoTestNGListener.class)
public class TenantCreationEventListenerTest {

    @Mock
    private TenantCreationEventListener tenantCreationEventListener;

    @Test
    public void testOnTenantCreate() throws Exception {

        TenantInfoBean tenantInfoBean = new TenantInfoBean();
        tenantInfoBean.setTenantId(TestConstants.TENANT_ID);
        tenantCreationEventListener.onTenantCreate(tenantInfoBean);
    }

    @Test
    public void testGetListenerOrder() {

        int listenerOrder = tenantCreationEventListener.getListenerOrder();
        Assert.assertEquals(listenerOrder, 0, "Listener order is different from expected.");
    }

    /**
     * Unit tests for empty methods
     *
     * @throws StratosException
     */
    @Test
    public void testDummyMethod() throws StratosException {

        TenantInfoBean tenantInfoBean = new TenantInfoBean();
        tenantInfoBean.setTenantId(TestConstants.TENANT_ID);
        boolean triggeredAllMethods = false;
        try {
            tenantCreationEventListener.onTenantUpdate(tenantInfoBean);
            tenantCreationEventListener.onTenantDelete(TestConstants.TENANT_ID);
            tenantCreationEventListener
                    .onTenantRename(TestConstants.TENANT_ID, TestConstants.TENANT_DOMAIN, TestConstants.TENANT_DOMAIN);
            tenantCreationEventListener.onTenantInitialActivation(TestConstants.TENANT_ID);
            tenantCreationEventListener.onTenantActivation(TestConstants.TENANT_ID);
            tenantCreationEventListener.onTenantDeactivation(TestConstants.TENANT_ID);
            tenantCreationEventListener.onSubscriptionPlanChange(TestConstants.TENANT_ID, TestConstants.TENANT_DOMAIN,
                    TestConstants.TENANT_DOMAIN);
            tenantCreationEventListener.onPreDelete(TestConstants.TENANT_ID);
            triggeredAllMethods = true;
        } finally {
            Assert.assertTrue(triggeredAllMethods);
        }
    }
}
