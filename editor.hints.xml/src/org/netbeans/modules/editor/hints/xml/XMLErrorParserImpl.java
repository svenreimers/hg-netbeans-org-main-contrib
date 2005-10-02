/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.editor.hints.xml;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.netbeans.api.xml.services.UserCatalog;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.editor.hints.ErrorDescriptionFactory;
import org.netbeans.spi.editor.hints.ProvidersList;
import org.netbeans.spi.editor.hints.support.ErrorParserSupport;
import org.openide.ErrorManager;
import org.openide.xml.EntityCatalog;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;

/**
 *
 * @author Jan Lahoda
 */
public final class XMLErrorParserImpl extends ErrorParserSupport {
    
    private static final ErrorManager ERR = ErrorManager.getDefault().getInstance(XMLErrorParserImpl.class.getName());
    
    /** Creates a new instance of XMLErrorParserImpl */
    public XMLErrorParserImpl() {
    }

    public List parseForErrors(final Document doc) {
        if (!ProvidersList.isProviderEnabled(XMLProviderDescription.XML_ERROR_PROVIDER))
            return Collections.EMPTY_LIST;
        
        final List result = new ArrayList();

        try {
            String text = doc.getText(0, doc.getLength());
            XMLReader readerForDTD = createParser(false);
            final boolean[] hasDTD = new boolean[1];
            
            try {
                readerForDTD.setProperty("http://xml.org/sax/properties/lexical-handler", new LexicalHandler() {
                    public void comment(char[] ch, int start, int length) throws SAXException {
                    }
                    public void endCDATA() throws SAXException {
                    }
                    public void endDTD() throws SAXException {
                    }
                    public void endEntity(String name) throws SAXException {
                    }
                    public void startCDATA() throws SAXException {
                    }
                    public void startDTD(String name, String publicId, String systemId) throws SAXException {
                        hasDTD[0] = true;
                    }
                    public void startEntity(String name) throws SAXException {
                    }
                });
                
                readerForDTD.setErrorHandler(new ErrorHandler() {
                    public void warning(SAXParseException exception) throws SAXException {
                        //ignore
                    }

                    public void error(SAXParseException exception) throws SAXException {
                        //ignore
                    }

                    public void fatalError(SAXParseException exception) throws SAXException {
                        //ignore
                    }
                });
                
                readerForDTD.parse(new InputSource(new StringReader(text)));
            } catch (SAXException e) {
                //simply ignore:
                if (ERR.isLoggable(ErrorManager.INFORMATIONAL)) {
                    ERR.notify(ErrorManager.INFORMATIONAL, e);
                }
            }
            
            XMLReader reader = createParser(hasDTD[0]);
            
            reader.setErrorHandler(new ErrorHandler() {
                public void error(SAXParseException exception) throws SAXException {
                    int severity = ProvidersList.getErrorSeverity(XMLProviderDescription.XML_ERROR_PROVIDER, XMLProviderDescription.KEY_XML_ERROR);
                    
                    result.add(ErrorDescriptionFactory.createErrorDescription(severity, exception.getMessage(), doc, exception.getLineNumber()));
                }
                public void fatalError(SAXParseException exception) throws SAXException {
                    int severity = ProvidersList.getErrorSeverity(XMLProviderDescription.XML_ERROR_PROVIDER, XMLProviderDescription.KEY_XML_FATAL_ERROR);
                    
                    result.add(ErrorDescriptionFactory.createErrorDescription(severity, exception.getMessage(), doc, exception.getLineNumber()));
                }
                public void warning(SAXParseException exception) throws SAXException {
                    int severity = ProvidersList.getErrorSeverity(XMLProviderDescription.XML_ERROR_PROVIDER, XMLProviderDescription.KEY_XML_WARNING);
                    
                    result.add(ErrorDescriptionFactory.createErrorDescription(severity, exception.getMessage(), doc, exception.getLineNumber()));
                }
            });
            
            reader.parse(new InputSource(new StringReader(text)));
        } catch (SAXException e) {
            //ignore
        } catch (IOException e) {
            //ignore
        } catch (BadLocationException e) {
            ErrorManager.getDefault().notify(e);
        }
        
        return result;
    }

    private static final class CompoundEntityResolver implements EntityResolver {
        
        public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
            InputSource source = UserCatalog.getDefault().getEntityResolver().resolveEntity(publicId, systemId);
            
            if (source != null) {
                return source;
            }
            
            return EntityCatalog.getDefault().resolveEntity(publicId, systemId);
        }
        
    }
    
    /** 
     * Create and preconfigure new parser. Default implementation uses JAXP.
     * @param validate true if validation module is required
     * @return SAX reader that is used for command performing or <code>null</code>
     */
    protected XMLReader createParser(boolean validate) {
       
        XMLReader ret = null;
        final String XERCES_FEATURE_PREFIX = "http://apache.org/xml/features/";         // NOI18N
        final String XERCES_PROPERTY_PREFIX = "http://apache.org/xml/properties/";      // NOI18N
        
       // JAXP plugin parser (bastarded by core factories!)
        
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(validate);

        //??? It is Xerces specifics, but no general API for XML Schema based validation exists
        if (validate) {
            try {
                factory.setFeature(XERCES_FEATURE_PREFIX + "validation/schema", validate); // NOI18N
            } catch (Exception ex) {
                //ignore?
            }                
        }
	
        try {
            SAXParser parser = factory.newSAXParser();
            ret = parser.getXMLReader();
        } catch (Exception ex) {
            //ignore
            return null;
        }


        if (ret != null) {
            ret.setEntityResolver(new CompoundEntityResolver());
        }
        
        return ret;
        
    }

}
