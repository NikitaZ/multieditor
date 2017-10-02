package org.multieditor.web;

import javax.faces.annotation.FacesConfig;


/**
 * This class provides proper configuration for using JSF 2.3. Without it CDI doesn't work with JSF 2.3 in faces-config.xml
 */
/*  Excerpt from JavaDoc:
    version = FacesConfig.Version.JSF_2_3 :
    This value indicates CDI should be used for EL resolution as well as enabling JSF CDI injection, as specified
    in Section 5.6.3 "CDI for EL Resolution" and Section 5.9 "CDI Integration".

     Cite from JSF 2,3 spec:

    5.6.3 CDI for EL Resolution
    If the any of the managed beans in the application have the @javax.faces.annotation.FacesConfig
    annotation, the ImplicitObjectELResolver from Section 5.6.2.1 “Implicit Object ELResolver for Facelets and
    Programmatic Access” is not present in the chain. Instead, CDI is used to perform EL resolution...
*/
@FacesConfig(version = FacesConfig.Version.JSF_2_3)
public class JSFConfigurationBean {

}
