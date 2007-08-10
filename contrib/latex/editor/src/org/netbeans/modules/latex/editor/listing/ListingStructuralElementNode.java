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
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2007.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.editor.listing;

import java.beans.IntrospectionException;
import org.netbeans.modules.latex.model.command.SourcePosition;
import org.netbeans.modules.latex.model.structural.StructuralNode;

/**
 *
 * @author Jan Lahoda
 */
public class ListingStructuralElementNode extends StructuralNode<ListingStructuralElement> {
    
    /** Creates a new instance of SectionStructuralElementNode */
    public ListingStructuralElementNode(ListingStructuralElement el) throws IntrospectionException {
        super(el);
        
        this.setIconBase("org/netbeans/modules/latex/model/structural/impl/resources/slide-icon");
    }
    
    public SourcePosition getOpeningPosition() {
        return getBean().getNode().getStartingPosition();
    }
}