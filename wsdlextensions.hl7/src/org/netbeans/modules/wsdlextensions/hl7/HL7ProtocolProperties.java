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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.wsdlextensions.hl7;

/**
* @author raghunadh.teegavarapu@sun.com
*/
public interface HL7ProtocolProperties extends HL7Component {
    public static final String HL7_LLP_TYPE = "llpType";
    public static final String HL7_ACK_MODE = "acknowledgmentMode";
    public static final String HL7_START_BLOCK_CHARACTER = "startBlockCharacter";
    public static final String HL7_END_DATA_CHARACTER = "endDataCharacter";
    public static final String HL7_END_BLOCK_CHARACTER = "endBlockCharacter";
    public static final String HL7_HLLP_CHECKSUM_ENABLED = "hllpChecksumEnabled";
	public static final String HL7_SEQNUM_ENABLED = "seqNumEnabled";
	public static final String HL7_VALIDATE_MSH = "validateMSH";
    public static final String HL7_PROCESSING_ID = "processingID";
    public static final String HL7_VERSION_ID = "versionID";
    public static final String HL7_ENABLED_SFT = "enabledSFT";
    public static final String HL7_SOFTWARE_VENDOR_ORGANIZATION = "softwareVendorOrganization";
    public static final String HL7_SOFTWARE_CERTIFIED_VERSION = "softwareCertifiedVersionOrReleaseNumber";
    public static final String HL7_SOFTWARE_PRODUCT_NAME = "softwareProductName";
    public static final String HL7_SOFTWARE_BINARY_ID = "softwareBinaryID";
    public static final String HL7_SOFTWARE_PRODUCT_INFORMATION = "softwareProductInformation";
    public static final String HL7_SOFTWARE_INSTALL_DATE = "softwareInstallDate";
   
    
    //<hl7:proptocolproperties acknowledgmentMode="original"
    public void setAckMode(String val);
    public String getAckMode();
	//<hl7:proptocolproperties llpType="MLLP"
    public void setLLPType(String val);
    public String getLLPType();
	//<hl7:proptocolproperties endDataCharacter=28
	public void setEndDataChar(Byte val);
    public Byte getEndDataChar();
	//<hl7:proptocolproperties endBlockCharacter=13
    public void setEndBlockChar(Byte val);
	public Byte getEndBlockChar();
	//<hl7:proptocolproperties startBlockCharacter=11
    public void setStartBlockChar(Byte val);
    public Byte getStartBlockChar();
	//<hl7:proptocolproperties hllpChecksumEnabled=false
    public void setHLLPChkSumEnabled(Boolean val);
    public Boolean getHLLPChkSumEnabled();
	//<hl7:proptocolproperties seqNumEnabled=false
    public void setSeqNumEnabled(Boolean val);
    public Boolean getSeqNumEnabled();
	//<hl7:proptocolproperties validateMSH=false
	public void setValidateMSHEnabled(Boolean val);
    public Boolean getValidateMSHEnabled();
	//<hl7:proptocolproperties processingID="P"
	public void setProcessingID(String val);
    public String getProcessingID();
    //<hl7:proptocolproperties versionID="2.4"
	public void setVersionID(String val);
    public String getVersionID();
    //<hl7:proptocolproperties enabledSFT=false
    public void setSFTEnabled(Boolean val);
    public Boolean getSFTEnabled();
	//<hl7:proptocolproperties softwareVendorOrganization=""
    public void setSoftwareVendorOrganization(String val);
    public String getSoftwareVendorOrganization();
	//<hl7:proptocolproperties softwareCertifiedVersion=""
    public void setSoftwareCertifiedVersionOrReleaseNumber(String val);
    public String getSoftwareCertifiedVersionOrReleaseNumber();
	//<hl7:proptocolproperties softwareProductName=""
    public void setSoftwareProductName(String val);
    public String getSoftwareProductName();
	//<hl7:proptocolproperties softwareBinaryID=""
    public void setSoftwareBinaryID(String val);
    public String getSoftwareBinaryID();
	//<hl7:proptocolproperties softwareProductInformation=""
    public void setSoftwareProductInformation(String val);
    public String getSoftwareProductInformation();
	//<hl7:proptocolproperties softwareInstalledDate=""
    public void setSoftwareInstallDate(String val);
    public String getSoftwareInstallDate();
  
}


