/*
*  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied.  See the License for the
*  specific language governing permissions and limitations
*  under the License.
*/
package org.wso2.ballerinalang.compiler.tree;

import org.ballerinalang.model.tree.IdentifierNode;
import org.ballerinalang.model.tree.XMLNSDeclarationNode;

/**
 * @since 0.94
 */
public class BLangXMLNS extends BLangNode implements XMLNSDeclarationNode {
    
    public BLangIdentifier namespaceURI;
    public BLangIdentifier prefix;

    @Override
    public BLangIdentifier getNamespaceURI() {
        return namespaceURI;
    }

    @Override
    public BLangIdentifier getPrefix() {
        return prefix;
    }

    @Override
    public void accept(BLangNodeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void setNamespaceURI(IdentifierNode namespaceURI) {
        this.namespaceURI = (BLangIdentifier) namespaceURI;
    }

    @Override
    public void setPrefix(IdentifierNode prefix) {
        this.prefix = (BLangIdentifier) prefix;
    }
    
}