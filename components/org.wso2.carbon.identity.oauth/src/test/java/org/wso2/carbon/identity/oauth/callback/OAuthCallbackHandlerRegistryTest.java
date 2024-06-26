/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.identity.oauth.callback;

import org.apache.commons.lang.StringUtils;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.testng.MockitoTestNGListener;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.wso2.carbon.identity.application.authentication.framework.model.AuthenticatedUser;
import org.wso2.carbon.identity.oauth.config.OAuthCallbackHandlerMetaData;
import org.wso2.carbon.identity.oauth.config.OAuthServerConfiguration;
import org.wso2.carbon.identity.oauth2.IdentityOAuth2Exception;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

/**
 * Unit tests for OAuthCallbackHandlerRegistryTest.
 */
@Listeners(MockitoTestNGListener.class)
public class OAuthCallbackHandlerRegistryTest {

    private static final int CALLBACK_HANDLER_PRIORITY = 0;
    private static final int METADATA_SET_SIZE = 2;

    @Mock
    private OAuthServerConfiguration mockOAuthServerConfiguration;

    @DataProvider(name = "testGetOAuthAuthzHandler")
    public Object[][] oAuthHandlerClassName() {
        return new Object[][] {
                {"org.wso2.carbon.identity.oauth.callback.DefaultCallbackHandler"},
                {null}
        };
    }

    @Test(expectedExceptions = IdentityOAuth2Exception.class)
    public void testGetInstance() throws Exception {

        try (MockedStatic<OAuthServerConfiguration> oAuthServerConfiguration = mockStatic(
                OAuthServerConfiguration.class)) {
            String className = "org.wso2.carbon.identity.oauth.callback.NonExistingCallbackHandler";
            getOAuthCallbackHandlerRegistry(className, METADATA_SET_SIZE, oAuthServerConfiguration);
        }
    }

    @Test(dataProvider = "testGetOAuthAuthzHandler")
    public void testGetOAuthAuthzHandler(String className) throws Exception {

        try (MockedStatic<OAuthServerConfiguration> oAuthServerConfiguration = mockStatic(
                OAuthServerConfiguration.class)) {
            // Create OAuthCallbackHandlerRegistry.
            OAuthCallbackHandlerRegistry oAuthCallbackHandlerRegistry =
                    getOAuthCallbackHandlerRegistry(className, METADATA_SET_SIZE, oAuthServerConfiguration);
            // Create OAuthCallback to be handled.
            AuthenticatedUser authenticatedUser = new AuthenticatedUser();
            OAuthCallback oAuthCallback = new OAuthCallback(authenticatedUser, "client", OAuthCallback.OAuthCallbackType
                    .ACCESS_DELEGATION_AUTHZ);
            oAuthCallback.setAuthorized(false);
            oAuthCallback.setValidScope(false);
            // Get the OAuthCallBackHandler that can handle the above OAuthCallback.
            OAuthCallbackHandler oAuthCallbackHandler =
                    oAuthCallbackHandlerRegistry.getOAuthAuthzHandler(oAuthCallback);

            if (StringUtils.isNotEmpty(className)) {
                assertEquals(oAuthCallbackHandler.getPriority(), CALLBACK_HANDLER_PRIORITY,
                        "OAuthHandlers priority should be equal to the given priority in the " +
                                "OAuthCallBackHandlerMetaData.");
            } else {
                assertNull(oAuthCallbackHandler,
                        "Should return null when there is no OAuthCallbackHandler can handle the" +
                                " given OAuthCallback.");
            }
        }
    }

    private OAuthCallbackHandlerRegistry getOAuthCallbackHandlerRegistry(String className, int metaDataSetSize,
                                                                         MockedStatic<OAuthServerConfiguration>
                                                                                 oAuthServerConfiguration)
            throws IdentityOAuth2Exception, NoSuchFieldException, IllegalAccessException {

        // Clear the OAuthCallbackHandlerRegistry.
        Field instance = OAuthCallbackHandlerRegistry.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);

        // Mock oAuthServerConfiguration to have the MetaData of the given OAuthCallbackHandler.
        Set<OAuthCallbackHandlerMetaData> oAuthCallbackHandlerMetaDataSet = new HashSet<>();
        for (int i = 0; i < metaDataSetSize; i++) {
            if (StringUtils.isNotEmpty(className)) {
                OAuthCallbackHandlerMetaData oAuthCallbackHandlerMetaData = new OAuthCallbackHandlerMetaData(className,
                        new Properties(), i);
                oAuthCallbackHandlerMetaDataSet.add(oAuthCallbackHandlerMetaData);
            }
        }
        when(mockOAuthServerConfiguration.getCallbackHandlerMetaData()).thenReturn(oAuthCallbackHandlerMetaDataSet);
        oAuthServerConfiguration.when(OAuthServerConfiguration::getInstance).thenReturn(mockOAuthServerConfiguration);

        return OAuthCallbackHandlerRegistry.getInstance();
    }
}
