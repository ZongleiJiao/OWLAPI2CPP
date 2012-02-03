/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.monash.infotech;

/**
 *
 * @author Zonglei Jiao
 */
public class OWLProperty {
    
    String propertyType;
    String propertyName;
    String propertyDomain;
    String propertyRange;

    public String getPropertyDomain() {
        return propertyDomain;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getPropertyRange() {
        return propertyRange;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyDomain(String propertyDomain) {
        this.propertyDomain = propertyDomain;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public void setPropertyRange(String propertyRange) {
        this.propertyRange = propertyRange;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }
    
    
}
