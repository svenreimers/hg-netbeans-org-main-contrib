/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.pmd;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import net.sourceforge.pmd.ExternalRuleID;
import net.sourceforge.pmd.Rule;

import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;

/** Special RuleSetFactory that use different classloader.
 *
 * @author  Radim Kubacki
 */
public class NbRuleSetFactory extends RuleSetFactory {

    private static final String BUNDLE_RULESET = "org/netbeans/modules/pmd/resources/badbundlecode.xml"; // NOI18N
    private static final String CLASSES_RULESET = "org/netbeans/modules/pmd/resources/innerclasses.xml"; // NOI18N
    
    private static NbRuleSetFactory instance;
    
    public static NbRuleSetFactory getDefault () {
        if (instance == null) {
            instance = new NbRuleSetFactory ();
        }
        return instance;
    }
    
    /** Creates a new instance of NbRuleSetFactory */
    public NbRuleSetFactory() {
    }
    
    /**
     * Returns an Iterator of RuleSet objects
     */
    public Iterator getRegisteredRuleSets() throws RuleSetNotFoundException {
        List ruleSets = new ArrayList();
        ruleSets.add (createRuleSet (BUNDLE_RULESET));
        ruleSets.add (createRuleSet (CLASSES_RULESET));

        return ruleSets.iterator();
    }

    /**
     * Creates a ruleset.  If passed a comma-delimited string (rulesets/basic.xml,rulesets/unusedcode.xml)
     * it will parse that string and create a new ruleset for each item in the list.
     */
    public RuleSet createRuleSet(String name) throws RuleSetNotFoundException {
        if (name.indexOf(',') == -1) {
           return createRuleSet(tryToGetStreamTo(name));
        }

        RuleSet ruleSet = new RuleSet();
        for (StringTokenizer st = new StringTokenizer(name, ","); st.hasMoreTokens();) {
            String ruleSetName = st.nextToken().trim();
            RuleSet tmpRuleSet = createRuleSet(ruleSetName);
            ruleSet.addRuleSet(tmpRuleSet);
        }
        return ruleSet;
    }

    public RuleSet createRuleSet(InputStream inputStream) {
        // The only difference is changed class loader
        
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(inputStream);
            Element root = doc.getDocumentElement();

            RuleSet ruleSet = new RuleSet();
            ruleSet.setName(root.getAttribute("name"));
            ruleSet.setDescription(root.getChildNodes().item(1).getFirstChild().getNodeValue());

            NodeList rules = root.getElementsByTagName("rule");
            for (int i =0; i<rules.getLength(); i++) {
                Node ruleNode = rules.item(i);
                Rule rule = null;
                if (ruleNode.getAttributes().getNamedItem("ref") != null) {
                    ExternalRuleID externalRuleID = new ExternalRuleID(ruleNode.getAttributes().getNamedItem("ref").getNodeValue());
                    // Changed: class loader & factory
                    RuleSetFactory rsf = new NbRuleSetFactory();
                    RuleSet externalRuleSet = rsf.createRuleSet(classLoader ().getResourceAsStream(externalRuleID.getFilename()));
                    rule = externalRuleSet.getRuleByName(externalRuleID.getRuleName());
                } else {
                    // carefully here
                    rule = (Rule)Class.forName(ruleNode.getAttributes().getNamedItem("class").getNodeValue()).newInstance();
                    rule.setName(ruleNode.getAttributes().getNamedItem("name").getNodeValue());
                    rule.setMessage(ruleNode.getAttributes().getNamedItem("message").getNodeValue());
                }

                // get the description, priority, example and properties (if any)
                Node node = ruleNode.getFirstChild();
                while (node != null) {
                    if (node.getNodeName() != null && node.getNodeName().equals("description")) {
                        rule.setDescription(node.getFirstChild().getNodeValue());
                    }

                    if (node.getNodeName() != null && node.getNodeName().equals("priority")) {
                        rule.setPriority(Integer.parseInt(node.getFirstChild().getNodeValue()));
                    }

                    if (node.getNodeName() != null && node.getNodeName().equals("example")) {
                        rule.setExample(node.getFirstChild().getNextSibling().getNodeValue());
                    }

                    if (node.getNodeName().equals("properties")) {
                        Node propNode = node.getFirstChild().getNextSibling();
                        while (propNode != null && propNode.getAttributes() != null) {
                            String propName = propNode.getAttributes().getNamedItem("name").getNodeValue();
                            String propValue = propNode.getAttributes().getNamedItem("value").getNodeValue();
                            rule.addProperty(propName, propValue);
                            propNode = propNode.getNextSibling().getNextSibling();
                        }
                    }

                    node = node.getNextSibling();
                }
                ruleSet.addRule(rule);
            }
            return ruleSet;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Couldn't read from that source: " + e.getMessage());
        }
    }

    private ClassLoader classLoader () {
        return (ClassLoader)org.openide.util.Lookup.getDefault().lookup(ClassLoader.class);
    }
    
    /** Uses system ClassLoader to get the stream */
    private InputStream tryToGetStreamTo(String name) throws RuleSetNotFoundException {
        InputStream in = classLoader ().getResourceAsStream(name);
        if (in == null) {
            throw new RuleSetNotFoundException("Can't find ruleset " + name + "; make sure that path is on the CLASSPATH");
        }
        return in;
    }
}
