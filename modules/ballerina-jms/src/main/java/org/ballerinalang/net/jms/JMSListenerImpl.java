/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * <p>
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.ballerinalang.net.jms;

import org.ballerinalang.connector.api.ConnectorFuture;
import org.ballerinalang.connector.api.ConnectorFutureListener;
import org.ballerinalang.connector.api.Executor;
import org.ballerinalang.connector.api.Resource;
import org.ballerinalang.natives.connectors.BallerinaConnectorManager;
import org.ballerinalang.net.jms.actions.utils.Constants;
import org.ballerinalang.services.DefaultServerConnectorErrorHandler;
import org.ballerinalang.util.exceptions.BallerinaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.messaging.CarbonCallback;
import org.wso2.carbon.messaging.CarbonMessage;
import org.wso2.carbon.messaging.ServerConnectorErrorHandler;
import org.wso2.carbon.transport.jms.callback.JMSCallback;
import org.wso2.carbon.transport.jms.contract.JMSListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * JMS Connector listener for Ballerina
 */
public class JMSListenerImpl implements JMSListener {

    private static final Logger breLog = LoggerFactory.getLogger(JMSListenerImpl.class);

    public static void handleError(CarbonMessage cMsg, CarbonCallback callback, Throwable throwable) {
        String errorMsg = throwable.getMessage();

        // bre log should contain bre stack trace, not the ballerina stack trace
        breLog.error("error: " + errorMsg, throwable);
        Object protocol = cMsg.getProperty("PROTOCOL");
        //TODO remove ballerina connector manager
        Optional<ServerConnectorErrorHandler> optionalErrorHandler = BallerinaConnectorManager.getInstance()
                .getServerConnectorErrorHandler((String) protocol);

        try {
            optionalErrorHandler.orElseGet(DefaultServerConnectorErrorHandler::getInstance)
                    .handleError(new BallerinaException(errorMsg, throwable.getCause()), cMsg, callback);
        } catch (Exception e) {
            breLog.error("Cannot handle error using the error handler for: " + protocol, e);
        }

    }

    @Override
    public void onMessage(CarbonMessage carbonMessage, JMSCallback jmsCallback) {
        Resource resource = JMSDispatcher.findResource(carbonMessage);

        Map<String, Object> properties = null;
        if (jmsCallback != null) {
            properties = new HashMap<>();
            properties.put(Constants.JMS_SESSION_ACKNOWLEDGEMENT_MODE, jmsCallback.getAcknowledgementMode());
        }

        ConnectorFuture future = Executor
                .submit(resource, properties, JMSDispatcher.getSignatureParameters(resource, carbonMessage));

        if (jmsCallback != null) {
            ConnectorFutureListener futureListener = new JMSConnectorFutureListener(jmsCallback);
            future.setConnectorFutureListener(futureListener);
        }
    }

    @Override
    public void onError(Throwable throwable) {

    }

}
