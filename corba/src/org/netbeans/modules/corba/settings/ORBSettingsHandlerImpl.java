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

package org.netbeans.modules.corba.settings;

import java.util.StringTokenizer;
import java.util.Properties;
import java.util.HashMap;
import java.util.LinkedList;

import org.xml.sax.AttributeList;

import org.openide.execution.NbProcessDescriptor;


/**
 *
 * @author  Karel Gardas
 * @version Jan 11 2001
 */
public class ORBSettingsHandlerImpl implements ORBSettingsHandler {

    //final private static boolean DEBUG = true;
    final private static boolean DEBUG = false;
	
    private ORBSettings _M_settings;

    private String _M_code;

    private String _M_import;

    private ORBBindingDescriptor _M_binding;

    private POAPolicyDescriptor _M_policy;

    private POAPolicyValueDescriptor _M_policy_value;

    private Properties _M_template_table;

    private HashMap _M_java_patch_table = new HashMap ();

    private LinkedList _M_wizard_requirements = new LinkedList ();

    private Properties _M_binding_template_code = new Properties ();

    private boolean _M_is_supported = true;
    //private boolean _M_template_code_element;
	

    public ORBSettingsHandlerImpl() {
    }

    public void setSettings (ORBSettings __settings) {
	_M_settings = __settings;
    }

    public void handle_policies_header (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	if (_M_settings.getPOASettings () == null)
	    _M_settings.createPOASettings ();
	_M_settings.getPOASettings ().setPoliciesHeader
	    (ORBSettings.xml2java (__attr.getValue (0)));
    }
    
    public void handle_get_root_poa_pattern (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	if (_M_settings.getPOASettings () == null)
	    _M_settings.createPOASettings ();
	_M_settings.getPOASettings ().setGetRootPOAPattern (__attr.getValue (0));
    }
    
    public void handle_orb_settings (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	//System.out.println ("handle_orb_settings");
	_M_settings.setOrbName (__attr.getValue ("name"));
	_M_settings.setORBTag (__attr.getValue ("tag"));
	String __tmp = __attr.getValue ("supported");
	__tmp.toLowerCase ();
	if (__tmp.equals ("true"))
	    _M_settings.setSupported (true);
	else
	    _M_settings.setSupported (false);
	_M_settings.setJavaTemplateCodePatchTable (_M_java_patch_table);
	_M_java_patch_table = new HashMap ();
    }
    
    public void handle_poa_settings (String __data,AttributeList __attr)
	throws org.xml.sax.SAXException {
	//System.out.println ("handle_poa_settings");
    }
    
    public void handle_compiler (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	if (DEBUG)
	    System.out.println ("handle_compiler: " + __attr.getValue (0));
	StringTokenizer __st = new StringTokenizer (__attr.getValue (0));
	String __compiler = __st.nextToken ();
	StringBuffer __args = new StringBuffer (); // NOI18N
	while (__st.hasMoreTokens ()) {
	    if (__args.length () > 0) {
		__args.append (" "); // NOI18N
		__args.append (__st.nextToken ());
	    }
	    else
		__args.append (__st.nextToken ());
	}
	if (DEBUG) {
	    System.out.println ("process: " + __compiler); // NOI18N
	    System.out.println ("args: " + __args.toString ()); // NOI18N
	}
	NbProcessDescriptor __desc = new NbProcessDescriptor
	    (__compiler, __args.toString (), "");
	_M_settings.setIdl (__desc); // NOI18N
	if (DEBUG)
	    System.out.println (_M_settings.getIdl ());
    }
    
    public void handle_import (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	//System.out.print ("handle_import: " + __data);
	if (__data == null)
	    __data = "";
	_M_import = __data;
    }
    
    public void handle_section_init_poas (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	if (_M_settings.getPOASettings () == null)
	    _M_settings.createPOASettings ();
	_M_settings.getPOASettings ().setSectionInitPOAs (__attr.getValue (0));
    }
    
    public void handle_tie_impl_prefix (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	_M_settings.setTieImplPrefix (__attr.getValue (0));
    }
    
    public void handle_ext_class_prefix (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	_M_settings.setExtClassPrefix (__attr.getValue (0));
    }
    
    public void handle_code (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	//System.out.print ("handle_code: " + __data);
	if (__data == null)
	    __data = "";
	_M_code = __data;
    }
    
    public void handle_root_poa_init (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	if (_M_settings.getPOASettings () == null)
	    _M_settings.createPOASettings ();
	_M_settings.getPOASettings ().setRootPOAInit
	    (ORBSettings.xml2java (__attr.getValue (0)));
    }
    
    public void handle_create_servant_instance (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	if (_M_settings.getPOASettings () == null)
	    _M_settings.createPOASettings ();
	_M_settings.getPOASettings ().setCreateServantInstance
	    (ORBSettings.xml2java (__attr.getValue (0)));
    }
    
    public void handle_idl_template_code (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	_M_settings.addIDLTemplateCode (_M_template_table);
	_M_template_table = null;
    }
    
    public void handle_file_position (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	_M_settings.setFilePosition (__attr.getValue (0));
    }
    
    public void handle_message_position (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	_M_settings.setMessagePosition (__attr.getValue (0));
    }

    public void handle_policies_footer (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	if (_M_settings.getPOASettings () == null)
	    _M_settings.createPOASettings ();
	_M_settings.getPOASettings ().setPoliciesFooter
	    (ORBSettings.xml2java (__attr.getValue (0)));
    }

    public void handle_line_position (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	_M_settings.setLinePosition (__attr.getValue (0));
    }
    /*
      public void handle_system_id (String __data, AttributeList __attr)
      throws org.xml.sax.SAXException {
      Integer __int = new Integer (__attr.getValue (0));
      int __value = __int.intValue ();
      _M_settings.getPOASettings ().setSystemId (__value);
      }
    */
    public void handle_impl_int_postfix (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	_M_settings.setImplIntPostfix (__attr.getValue (0));
    }
    
    public void handle_tie_impl_postfix (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	_M_settings.setTieImplPostfix (__attr.getValue (0));
    }
    
    public void handle_package_param (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	_M_settings.setPackageParam (__attr.getValue (0));
    }
    
    public void handle_tie_param (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	_M_settings.setTieParam (__attr.getValue (0));
    }
    /*
      public void handle_user_id (String __data, AttributeList __attr)
      throws org.xml.sax.SAXException {
      Integer __int = new Integer (__attr.getValue (0));
      int __value = __int.intValue ();
      _M_settings.getPOASettings ().setUserId (__value);
      }
    */
    public void handle_activate_poa (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	if (_M_settings.getPOASettings () == null)
	    _M_settings.createPOASettings ();
	_M_settings.getPOASettings ().setActivatePOA
	    (ORBSettings.xml2java (__attr.getValue (0)));
    }
    
    public void handle_error_expression (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	_M_settings.setErrorExpression (__attr.getValue (0));
    }
    
    public void handle_poa_policy (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	if (DEBUG)
	    System.out.println ("handle_poa_policy: " + __attr.getValue ("name"));
	if (_M_policy == null)
	    _M_policy = new POAPolicyDescriptor ();
	_M_policy.setName (__attr.getValue ("name")); // NOI18N
	_M_policy.setMnemonicCharacter (__attr.getValue ("mnemonic_character"));
	if (_M_settings.getPOASettings () == null)
	    _M_settings.createPOASettings ();
	_M_settings.getPOASettings ().addPolicy (_M_policy);
	if (DEBUG)
	    System.out.println ("_M_policy: " + _M_policy);
	_M_policy = null;
    }
    
    public void handle_implbase_impl_prefix (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	_M_settings.setImplBaseImplPrefix (__attr.getValue (0));
    }
    
    public void handle_require_policy (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	if (_M_policy_value == null)
	    _M_policy_value = new POAPolicyValueDescriptor ();
	POAPolicySimpleDescriptor __policy = new POAPolicySimpleDescriptor ();
	__policy.setName (__attr.getValue ("name")); // NOI18N
	__policy.setValue (__attr.getValue ("value")); // NOI18N
	_M_policy_value.addRequiredPolicy (__policy);
    }
    
    public void handle_section_init_servants (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	if (_M_settings.getPOASettings () == null)
	    _M_settings.createPOASettings ();
	_M_settings.getPOASettings ().setSectionInitServants (__attr.getValue (0));
    }
    
    public void handle_policies_separator (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	if (_M_settings.getPOASettings () == null)
	    _M_settings.createPOASettings ();
	_M_settings.getPOASettings ().setPoliciesSeparator (__attr.getValue (0));
    }
    /*
      public void handle_activate_default_servant (String __data, AttributeList __attr)
      throws org.xml.sax.SAXException {
      _M_settings.getPOASettings ().setActivateDefaultServant (__attr.getValue (0));
      }
    */
    /*
      public void handle_none (String __data, AttributeList __attr)
      throws org.xml.sax.SAXException {
      Integer __int = new Integer (__attr.getValue (0));
      int __value = __int.intValue ();
      _M_settings.getPOASettings ().setNone (__value);
      }
    */
    public void handle_template_code (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	if (DEBUG)
	    System.out.println ("handle_template_code");
	//if (__attr.getValue ("name").equals ("ORB_PROPERTIES"))
	//System.out.println ("properties: " + __data);
	if (_M_template_table == null)
	    _M_template_table = new Properties ();
	if (__data == null)
	    __data = "";
	_M_template_table.setProperty (__attr.getValue ("name"), __data);
    }
    
    public void handle_ext_class_postfix (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	_M_settings.setExtClassPostfix (__attr.getValue (0));
    }
    
    public void handle_policies_declaration (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	if (_M_settings.getPOASettings () == null)
	    _M_settings.createPOASettings ();
	_M_settings.getPOASettings ().setPoliciesDeclaration
	    (ORBSettings.xml2java (__attr.getValue (0)));
    }
    
    public void handle_idl_compiler_settings (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
    }
    
    public void handle_dir_param (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	_M_settings.setDirParam (__attr.getValue (0));
    }
    
    public void handle_section_activate_poas (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	if (_M_settings.getPOASettings () == null)
	    _M_settings.createPOASettings ();
	_M_settings.getPOASettings ().setSectionActivatePOAs (__attr.getValue (0));
    }
    /*    
	  public void handle_activate_servant_manager (String __data, AttributeList __attr)
	  throws org.xml.sax.SAXException {
	  _M_settings.getPOASettings ().setActivateServantManager (__attr.getValue (0));
	  }
    */
    public void handle_impl_generator_settings (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
    }
    
    public void handle_column_position (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	_M_settings.setColumnPosition (__attr.getValue (0));
    }
    
    public void handle_prepare_code (String __data,AttributeList __attr)
	throws org.xml.sax.SAXException {
	if (_M_policy == null)
	    _M_policy = new POAPolicyDescriptor ();
	if (__data == null)
	    __data = "";
	_M_policy.setPrepareCode (__data);
    }
    
    public void handle_client_binding (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	WizardSettings __ws = new WizardSettings (_M_is_supported,
						  _M_wizard_requirements);
	_M_binding = new ORBBindingDescriptor ();
	_M_binding.setName (__attr.getValue ("name"));
	_M_binding.setTemplateTag (__attr.getValue ("template-tag"));
	_M_binding.setImport (_M_import);
	_M_binding.setCode (_M_code);
	_M_binding.setWizardSettings (__ws);
	_M_binding.setJavaTemplateCodeTable (_M_binding_template_code);
	_M_settings.addClientBinding (_M_binding);
	_M_import = null;
	_M_code = null;
	_M_is_supported = true;
	_M_wizard_requirements = new LinkedList ();
	_M_binding_template_code = new Properties ();
    }
    
    public void handle_impl_int_prefix (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	_M_settings.setImplIntPrefix (__attr.getValue (0));
    }
    
    public void handle_package_delimiter (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	_M_settings.setPackageDelimiter (__attr.getValue (0));
    }
    
    public void handle_create_code (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	if (_M_policy == null)
	    _M_policy = new POAPolicyDescriptor ();
	if (__data == null)
	    __data = "";
	_M_policy.setCreateCode (__data);
    }
    
    public void handle_policies_pattern (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	if (_M_settings.getPOASettings () == null)
	    _M_settings.createPOASettings ();
	_M_settings.getPOASettings ().setPoliciesPattern (__attr.getValue (0));
    }
    
    public void handle_servant_instance_pattern (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	if (_M_settings.getPOASettings () == null)
	    _M_settings.createPOASettings ();
	_M_settings.getPOASettings ().setServantInstancePattern (__attr.getValue (0));
    }
    
    public void handle_create_poa (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	if (_M_settings.getPOASettings () == null)
	    _M_settings.createPOASettings ();
	_M_settings.getPOASettings ().setCreatePOA
	    (ORBSettings.xml2java (__attr.getValue (0)));
    }
    
    public void handle_value (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	//System.out.println ("_M_policy: " + _M_policy);
	if (DEBUG)
	    System.out.println ("handle_value: " + __attr.getValue ("name"));
	if (_M_policy == null)
	    _M_policy = new POAPolicyDescriptor ();
	if (_M_policy_value == null)
	    _M_policy_value = new POAPolicyValueDescriptor ();
	_M_policy_value.setName (__attr.getValue ("name"));
	_M_policy.addValue (_M_policy_value);
	_M_policy_value = null;
    }
    
    public void handle_servant_pattern (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	if (_M_settings.getPOASettings () == null)
	    _M_settings.createPOASettings ();
	_M_settings.getPOASettings ().setServantPattern (__attr.getValue (0));
    }
    
    public void handle_java_template_code (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	_M_settings.addJavaTemplateCode (_M_template_table);
	_M_template_table = null;
	//_M_settings.setJavaTemplateCodePatchTable (_M_java_patch_table);
	//_M_java_patch_table = new HashMap ();
    }
    
    public void handle_conflicts_with_policy (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	if (_M_policy_value == null)
	    _M_policy_value = new POAPolicyValueDescriptor ();
	POAPolicySimpleDescriptor __policy = new POAPolicySimpleDescriptor ();
	__policy.setName (__attr.getValue ("name")); // NOI18N
	__policy.setValue (__attr.getValue ("value")); // NOI18N
	_M_policy_value.addConflictsPolicy (__policy);
    }
    
    public void handle_activate_servant_with_system_id (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	if (_M_settings.getPOASettings () == null)
	    _M_settings.createPOASettings ();
	_M_settings.getPOASettings ().setActivateServantWithSystemId
	    (ORBSettings.xml2java (__attr.getValue (0)));
    }
    
    public void handle_activate_servant_with_user_id (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	if (_M_settings.getPOASettings () == null)
	    _M_settings.createPOASettings ();
	_M_settings.getPOASettings ().setActivateServantWithUserId
	    (ORBSettings.xml2java (__attr.getValue (0)));
    }
    
    public void handle_servant_manager_pattern (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	if (_M_settings.getPOASettings () == null)
	    _M_settings.createPOASettings ();
	_M_settings.getPOASettings ().setServantManagerPattern (__attr.getValue (0));
    }
    
    public void handle_root_poa_pattern (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	if (_M_settings.getPOASettings () == null)
	    _M_settings.createPOASettings ();
	_M_settings.getPOASettings ().setRootPOAPattern (__attr.getValue (0));
    }
    
    public void handle_server_binding (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	WizardSettings __ws = new WizardSettings (_M_is_supported,
						  _M_wizard_requirements);
	_M_binding = new ORBBindingDescriptor ();
	_M_binding.setName (__attr.getValue ("name"));
	_M_binding.setTemplateTag (__attr.getValue ("template-tag"));
	_M_binding.setImport (_M_import);
	_M_binding.setCode (_M_code);
	_M_binding.setWizardSettings (__ws);
	_M_binding.setJavaTemplateCodeTable (_M_binding_template_code);
	_M_settings.addServerBinding (_M_binding);
	_M_import = null;
	_M_code = null;
	_M_is_supported = true;
	_M_wizard_requirements = new LinkedList ();
	_M_binding_template_code = new Properties ();
    }
    
    public void handle_poa_pattern (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	if (_M_settings.getPOASettings () == null)
	    _M_settings.createPOASettings ();
	_M_settings.getPOASettings ().setPOAPattern (__attr.getValue (0));
    }
    
    public void handle_default_servant_pattern (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	if (_M_settings.getPOASettings () == null)
	    _M_settings.createPOASettings ();
	_M_settings.getPOASettings ().setDefaultServantPattern (__attr.getValue (0));
    }
    
    public void handle_implbase_impl_postfix (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	_M_settings.setImplBaseImplPostfix (__attr.getValue (0));
    }
    
    public void handle_get_poa_manager (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	if (_M_settings.getPOASettings () == null)
	    _M_settings.createPOASettings ();
	_M_settings.getPOASettings ().setGetPOAManagerMethod (__attr.getValue (0));
    }

    public void handle_servant_class (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	if (_M_settings.getPOASettings () == null)
	    _M_settings.createPOASettings ();
	_M_settings.getPOASettings ().setServantClass (__attr.getValue (0));
    }
    
    public void handle_poa_activator_pattern (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	if (_M_settings.getPOASettings () == null)
	    _M_settings.createPOASettings ();
	_M_settings.getPOASettings ().setPOAActivatorPattern (__attr.getValue (0));
    }

    public void handle_disable_action (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	if (_M_policy_value == null)
	    _M_policy_value = new POAPolicyValueDescriptor ();
	_M_policy_value.addDisabledAction (__attr.getValue (0));
    }

    public void handle_prepare_code_pattern (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	if (_M_policy == null)
	    _M_policy = new POAPolicyDescriptor ();
	_M_policy.setPrepareCodePattern (__attr.getValue (0));
    }

    public void handle_create_code_pattern (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	if (_M_policy == null)
	    _M_policy = new POAPolicyDescriptor ();
	_M_policy.setCreateCodePattern (__attr.getValue (0));
    }

    public void handle_policies_header_pattern (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	if (_M_settings.getPOASettings () == null)
	    _M_settings.createPOASettings ();
	_M_settings.getPOASettings ().setPoliciesHeaderPattern (__attr.getValue (0));
    }

    public void handle_policies_declaration_pattern (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	if (_M_settings.getPOASettings () == null)
	    _M_settings.createPOASettings ();
	_M_settings.getPOASettings ().setPoliciesDeclarationPattern (__attr.getValue (0));
    }

    public void handle_policies_footer_pattern (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	if (_M_settings.getPOASettings () == null)
	    _M_settings.createPOASettings ();
	_M_settings.getPOASettings ().setPoliciesFooterPattern (__attr.getValue (0));
    }

    public void handle_set_default_servant (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	if (_M_settings.getPOASettings () == null)
	    _M_settings.createPOASettings ();
	_M_settings.getPOASettings ().setSetDefaultServant
	    (ORBSettings.xml2java (__attr.getValue (0)));
    }

    public void handle_set_servant_manager (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	if (_M_settings.getPOASettings () == null)
	    _M_settings.createPOASettings ();
	_M_settings.getPOASettings ().setSetServantManager
	    (ORBSettings.xml2java (__attr.getValue (0)));
    }

    public void handle_set_poa_activator (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	if (_M_settings.getPOASettings () == null)
	    _M_settings.createPOASettings ();
	_M_settings.getPOASettings ().setSetPOAActivator
	    (ORBSettings.xml2java (__attr.getValue (0)));
    }

    public void handle_unrecognized_policy (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	if (_M_settings.getPOASettings () == null)
	    _M_settings.createPOASettings ();
	_M_settings.getPOASettings ().setUnrecognizedPolicy (__attr.getValue (0));
    }

    public void handle_local_bundle (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	_M_settings.setLocalBundle (__attr.getValue (0));
    }

    public void handle_value_impl_prefix (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	_M_settings.setValueImplPrefix (__attr.getValue (0));
    }

    public void handle_value_impl_postfix (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	_M_settings.setValueImplPostfix (__attr.getValue (0));
    }

    public void handle_valuefactory_impl_prefix (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	_M_settings.setValueFactoryImplPrefix (__attr.getValue (0));
    }

    public void handle_valuefactory_impl_postfix (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	_M_settings.setValueFactoryImplPostfix (__attr.getValue (0));
    }

    public void handle_default_default_servant_var_name (String __data,
							 AttributeList __attr)
	throws org.xml.sax.SAXException {
	_M_settings.getPOASettings ().setDefaultDefaultServantVarName (__attr.getValue (0));
    }

    public void handle_default_poa_activator_var_name (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	_M_settings.getPOASettings ().setDefaultPOAActivatorVarName (__attr.getValue (0));
    }

    public void handle_default_poa_name (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	_M_settings.getPOASettings ().setDefaultPOAName (__attr.getValue (0));
    }

    public void handle_default_poa_var_name (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	_M_settings.getPOASettings ().setDefaultPOAVarName (__attr.getValue (0));
    }

    public void handle_default_servant_id (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	_M_settings.getPOASettings ().setDefaultServantId (__attr.getValue (0));
    }

    public void handle_default_servant_id_var_name (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	_M_settings.getPOASettings ().setDefaultServantIdVarName (__attr.getValue (0));
    }

    public void handle_default_servant_var_name (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	_M_settings.getPOASettings ().setDefaultServantVarName (__attr.getValue (0));
    }

    public void handle_default_servant_manager_var_name (String __data,
							 AttributeList __attr)
	throws org.xml.sax.SAXException {
	_M_settings.getPOASettings ().setDefaultServantManagerVarName (__attr.getValue (0));
    }

    public void handle_patch_code (String __data, AttributeList __meta)
	throws org.xml.sax.SAXException {
	//System.out.println ("addJavaTemplateCodePatchPair (2) <-" + __meta.getValue (0) + ", " + __data);
	//_M_settings.addJavaTemplateCodePatchPair (__meta.getValue (0), __data);
	if (_M_java_patch_table == null)
	    _M_java_patch_table = new HashMap ();
	if (__data == null)
	    __data = "";
	_M_java_patch_table.put (__meta.getValue (0), __data);
    }

    public void handle_wizard_requires (String __data, AttributeList __meta)
	throws org.xml.sax.SAXException {
	_M_wizard_requirements.add (new WizardRequirement (__meta.getValue ("value"),
							   __meta.getValue ("title"),
							   __meta.getValue ("type")));
	//System.out.println ("requirements: " + _M_wizard_requirements);
    }

    public void handle_wizard_does_not_support (String __data, AttributeList __meta)
	throws org.xml.sax.SAXException {
	_M_is_supported = false;
    }

    public void handle_binding_template_code (String __data, AttributeList __meta)
	throws org.xml.sax.SAXException {
	//System.out.println ("handle_binding_template_code: " + __meta.getValue ("name") + ": " + __data);
	if (__data == null)
	    __data = "";
	_M_binding_template_code.put (__meta.getValue ("name"), __data);
    }

    public void handle_tie_class_prefix (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	_M_settings.setTieClassPrefix (__attr.getValue (0));
    }

    public void handle_tie_class_postfix (String __data, AttributeList __attr)
	throws org.xml.sax.SAXException {
	_M_settings.setTieClassPostfix (__attr.getValue (0));
    }

    public void endDocument () throws org.xml.sax.SAXException {
	if (DEBUG)
	    System.out.println ("end document...");
    }
    
    public void startDocument () throws org.xml.sax.SAXException {
	if (DEBUG)
	    System.out.println ("start document...");
    }
    
    public void startElement (String __name, AttributeList __attr)
	throws org.xml.sax.SAXException {
	if (DEBUG)
	    System.out.println ("start element: " + __name);
    }
    
    public void characters (char[] __p1, int __p2, int __p3)
	throws org.xml.sax.SAXException {
    }
    
    public void setDocumentLocator (org.xml.sax.Locator __p1) {
    }
    
    public void endElement (String __name) throws org.xml.sax.SAXException {
	if (DEBUG)
	    System.out.println ("end element: " + __name);
    }
    
    public void ignorableWhitespace (char[] __p1, int __p2, int __p3)
	throws org.xml.sax.SAXException {
    }
    
    public void processingInstruction (String __p1, String __p2)
	throws org.xml.sax.SAXException {
    }
    
}


