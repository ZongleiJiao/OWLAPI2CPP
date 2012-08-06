/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.monash.infotech.db;

import edu.monash.infotech.OWLAPIWrapper;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.coode.xml.OWLOntologyXMLNamespaceManager;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

/**
 *
 * @author ZongleiJiao
 */
public class OntologyLoader {
    
    
    private OWLDataFactory myFactory;
    private OWLOntologyManager manager;
    private DefaultPrefixManager prefixManager;
    public OWLOntology myOntology;
//    private String defaultNameSpace;
//    private String ontologyURI;
    
    private Set<OWLClass> ocset;
    private Set<OWLDeclarationAxiom> allDeclarationAxiom;

    public OntologyLoader(String filename) {
        try {
            //initializing 
            File file = new File(filename);
            manager = OWLManager.createOWLOntologyManager();
            myFactory = manager.getOWLDataFactory();
            myOntology = manager.loadOntologyFromOntologyDocument(file);
            
                              
        } catch (OWLOntologyCreationException ex) {
            Logger.getLogger(OWLAPIWrapper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 
    public String getOntologyName(OWLOntology thisOntology){
        String URI = getOntologyURI(thisOntology);
        try{
            String name = URI.substring(URI.lastIndexOf("/")+1,URI.lastIndexOf("."));
            return name;
        }catch(StringIndexOutOfBoundsException ex){
            //cannot parse the URI, it's not normal eg."http://xmlns.com/foaf/0.1/"
            return URI;
        }
    }
    
    public String getOntologyNameSpace(OWLOntology thisOntology) {
        //get default namespace  
        OWLOntologyXMLNamespaceManager ooxnm = new OWLOntologyXMLNamespaceManager(manager, thisOntology);
        String nameSpace = ooxnm.getDefaultNamespace();
        prefixManager = new DefaultPrefixManager(nameSpace);
        return nameSpace;
    }
    
    public String getOntologyURI(OWLOntology thisOntology){
        String ontologyURI = thisOntology.getOntologyID().getOntologyIRI().toURI().toString();
        return ontologyURI;
    }
    
    public Set<OWLClass> getAllClasses(OWLOntology thisOntology){
        Set<OWLClass> classes = thisOntology.getClassesInSignature(false);
        
        return classes;
    }
    
    public String getClassURI(OWLClass thisClass){
        return thisClass.getIRI().toURI().toString();
    }
    
    public String getIndividualURI(OWLNamedIndividual individual){
        return individual.getIRI().toURI().toString();
    }
    
    public String getClassName(OWLClass thisClass){
        String URI = this.getClassURI(thisClass);
        String name = URI.substring(URI.indexOf("#")+1);
        return name;
    }
    
    public String getClassNameSpace(OWLClass thisClass){
        String URI = this.getClassURI(thisClass);
        int idx = URI.indexOf("#");
        if(idx > 0){
            String name = URI.substring(0,idx+1);
            return name;
        }else{
            idx = URI.lastIndexOf("/");
            String name = URI.substring(0, idx+1);
            return name;
        }
    }
    
    public Set<OWLNamedIndividual> getAllIndividuals(OWLOntology thisOntology){
        Set<OWLNamedIndividual> individuals = thisOntology.getIndividualsInSignature(false);
        return individuals;
    }
    
    public String getIndividualName(OWLNamedIndividual individual){
        String URI = this.getIndividualURI(individual);
        String name = URI.substring(URI.indexOf("#")+1);
        return name;
        
    }
    
    public Set<OWLAnnotationProperty> getAllAnnoationProperties(OWLOntology thisOntology){
        Set<OWLAnnotationProperty> annotationProperties = thisOntology.getAnnotationPropertiesInSignature();
        return annotationProperties;
    }
    
    public Set<OWLDataProperty> getAllDataProperties(OWLOntology thisOntology){
        Set<OWLDataProperty> dataProperties = thisOntology.getDataPropertiesInSignature(false);
        return dataProperties;
    }
    
    public Set<OWLObjectProperty> getAllObjectProperties(OWLOntology thisOntology){
        Set<OWLObjectProperty> objectProperties = thisOntology.getObjectPropertiesInSignature(false);
        return objectProperties;
    }
    
    public int getNumOfClass(OWLOntology thisOntology){
        Set<OWLClass> classes = this.getAllClasses(thisOntology);
        if(classes == null){
            return 0;
        }else{
            return classes.size();
        }
    }
    
    public int getNumOfIndividuals(OWLOntology thisOntology){
        Set<OWLNamedIndividual> individuals = this.getAllIndividuals(thisOntology);
        if(individuals == null){
            return 0;
        }else{
            return individuals.size();
        }
    }
    
    public int getNumOfProperties(OWLOntology thisOntology){
        Set<OWLAnnotationProperty> annotationProperties = this.getAllAnnoationProperties(thisOntology);
        Set<OWLDataProperty> dataProperties = this.getAllDataProperties(thisOntology);
        Set<OWLObjectProperty> objectProperties = this.getAllObjectProperties(thisOntology);
        
        int anno = 0;
        int data = 0;
        int obj = 0;
        
        if(annotationProperties != null){
            anno = annotationProperties.size();
        }
        
        if(dataProperties != null){
            data = dataProperties.size();
        }
        
        if(objectProperties != null){
            obj = objectProperties.size();
        }
        
        return anno + data + obj;
    }
    
    public Set<OWLOntology> getImportedOntologies(OWLOntology thisOntology){
        Set<OWLOntology> imports = thisOntology.getImports();
        return imports;
    }
    
    public Set<OWLAnnotation> getAllAnnotations(OWLOntology thisOntology){
        Set<OWLAnnotation> annotations = thisOntology.getAnnotations();
        return annotations;
    }
    
    public Set<OWLIndividual> getIndividuals(OWLClass cls) {
        
        Set<OWLIndividual> set = cls.getIndividuals(myOntology);
        if (set.size() < 1) {
            return null;
        }

        return set;
    }
    
    public Set<OWLClassExpression> getSubClasses(OWLClass cls) {
        Set<OWLClassExpression> oceset = cls.getSubClasses(myOntology);
        
        if (oceset.size() < 1) {
            return null;
        }
        
        return oceset;
    }
    
    public Set<OWLClassExpression> getSuperClasses(OWLClass cls) {
        Set<OWLClassExpression> oceset = cls.getSuperClasses(myOntology);
        if (oceset.size() < 1) {
            return null;
        }
        return oceset;
    }
    
    public Set<OWLClassExpression> getDisjointClasses(OWLClass cls) {
        
        Set<OWLClassExpression> oceset = cls.getDisjointClasses(myOntology);
        if (oceset.size() < 1) {
            return null;
        }
        return oceset;
    }
    
    public Set<OWLClassExpression> getEquivalentClasses(OWLClass cls) {
        
        Set<OWLClassExpression> oceset = cls.getEquivalentClasses(myOntology);
        if (oceset.size() < 1) {
            return null;
        }
        return oceset;
    }
    
    //////////////equivalent class////////////
    public String getEquivalentClassesFormula(OWLClassExpression expression) {
        String str = expression.toString();
        str = removeNameSpace(str);
        str = this.replaceExpression2Symbol(str);
        String type = str.substring(0, str.indexOf("("));
        String equivalentClassExpression = replaceLogic(type, replaceLogic2Symbol(type, str));
        return equivalentClassExpression;
    }
    
    public String removeNameSpace(String str) {
        String sub =  str.replaceAll(this.getOntologyNameSpace(myOntology), "");
        sub = sub.replaceAll("owl:Thing", "Thing");
        return sub;
    }
    
    private String replaceExpression2Symbol(String str) {
        String objectAllValuesFrom = "ObjectAllValuesFrom";
        String objectHasValue = "ObjectHasValue";
        String objectExactCardinality = "ObjectExactCardinality";

        int indexOfObjectAllValuesFrom = -1;
        int indexOfEndObjectAllValuesFrom = -1;
        int indexOfObjectHasValue = -1;
        int indexOfEndObjectHasValue = -1;
        int indexOfObjectExactCardinality = -1;
        int indexOfEndObjectExactCardinality = -1;

        indexOfObjectAllValuesFrom = str.indexOf(objectAllValuesFrom);

        while (indexOfObjectAllValuesFrom != -1) {
            indexOfEndObjectAllValuesFrom = str.indexOf(")", indexOfObjectAllValuesFrom);
            String origin = str.substring(indexOfObjectAllValuesFrom, indexOfEndObjectAllValuesFrom + 1);
            String value = str.substring(indexOfObjectAllValuesFrom + objectAllValuesFrom.length() + 1, indexOfEndObjectAllValuesFrom);
            String[] values = value.split(" ");
            String modified = "∀" + values[0] + "(" + values[1] + ")";
            str = str.replace(origin, modified);
            indexOfObjectAllValuesFrom = str.indexOf(objectAllValuesFrom);
        }

        indexOfObjectHasValue = str.indexOf(objectHasValue);
        while (indexOfObjectHasValue != -1) {
            indexOfEndObjectHasValue = str.indexOf(")", indexOfObjectHasValue);
            String origin = str.substring(indexOfObjectHasValue, indexOfEndObjectHasValue + 1);
            String value = str.substring(indexOfObjectHasValue + objectHasValue.length() + 1, indexOfEndObjectHasValue);
            String[] values = value.split(" ");
            String modified = "∃" + values[0] + "(" + values[1] + ")";
            str = str.replace(origin, modified);
            indexOfObjectHasValue = str.indexOf(objectHasValue);
        }

        indexOfObjectExactCardinality = str.indexOf(objectExactCardinality);
        while (indexOfObjectExactCardinality != -1) {
            indexOfEndObjectExactCardinality = str.indexOf(")", indexOfObjectExactCardinality);
            String origin = str.substring(indexOfObjectExactCardinality, indexOfEndObjectExactCardinality + 1);
            String value = str.substring(indexOfObjectExactCardinality + objectExactCardinality.length() + 1, indexOfEndObjectExactCardinality);
            String[] values = value.split(" ");
            String modified = "≡" + values[1] + values[0] + "(" + values[2] + ")";
            str = str.replace(origin, modified);
            indexOfObjectExactCardinality = str.indexOf(objectExactCardinality);
        }
        return str;
    }
    
    private String replaceLogic(String type, String[] items) {
        String objectIntersectionOf = "ObjectIntersectionOf";
        String objectUnionOf = "ObjectUnionOf";

        for (int i = 0; i < items.length; i++) {
            if (items[i].startsWith(objectIntersectionOf)) {
                items[i] = "(" + replaceLogic(objectIntersectionOf, replaceLogic2Symbol(objectIntersectionOf, items[i])) + ")";
            } else if (items[i].startsWith(objectUnionOf)) {
                items[i] = "(" + replaceLogic(objectUnionOf, replaceLogic2Symbol(objectUnionOf, items[i])) + ")";
            }
        }

        String res = items[0];
        for (int j = 1; j < items.length; j++) {
            if (type.equals(objectIntersectionOf)) {
                res += " ∩ " + items[j];
            } else if (type.equals(objectUnionOf)) {
                res += " ∪ " + items[j];
            }
        }
        return res;
    }

    private String[] replaceLogic2Symbol(String type, String str) {
        String objectIntersectionOf = "ObjectIntersectionOf";
        String objectUnionOf = "ObjectUnionOf";
//        String complementOf= "complementOf";

        int indexOfObjectIntersectionOf = -1;
        int indexOfObjectUnionOf = -1;

        indexOfObjectIntersectionOf = str.indexOf(objectIntersectionOf);
        indexOfObjectUnionOf = str.indexOf(objectUnionOf);

        if (type.equals(objectIntersectionOf)) {
            str = str.substring(indexOfObjectIntersectionOf + objectIntersectionOf.length() + 1, str.length() - 1);
        } else if (type.equals(objectUnionOf)) {
            str = str.substring(indexOfObjectUnionOf + objectUnionOf.length() + 1, str.length() - 1);
        }
        String[] values = str.split(" ");

        List list = new ArrayList();

        for (int i = 0, j; i < values.length; i++, j++) {
            j = i + 1;
            String temp = values[i];
            int count = 0;
            for (int k = 0; k < values[i].length(); k++) {
                if (values[i].charAt(k) == '(') {
                    count++;
                } else if (values[i].charAt(k) == ')') {
                    count--;
                }
            }
            if (temp.startsWith(objectIntersectionOf) || temp.startsWith(objectUnionOf)) {
                for (; j < values.length; j++) {
                    temp += " " + values[j];
                    for (int k = 0; k < values[j].length(); k++) {
                        if (values[j].charAt(k) == '(') {
                            count++;
                        } else if (values[j].charAt(k) == ')') {
                            count--;
                        }
                    }
                    if (count == 0) {
                        break;
                    }
                }
                i = j;
                list.add(temp);
            } else {
                list.add(values[i]);
            }
        }
        String[] news = new String[list.size()];
        list.toArray(news);
        return news;
    }
    //////////////equivalent class////////////
    
    public String getPropertyURI(OWLProperty pro){
        return pro.getIRI().toURI().toString();
    }
    
    public String getPropertyName(OWLProperty pro){
        String URI = this.getPropertyURI(pro);
        String name = URI.substring(URI.indexOf("#")+1);
        return name;
    }
    
    public String getPropertyNameSpace(OWLProperty pro){
        String URI = this.getPropertyURI(pro);
        int idx = URI.indexOf("#");
        if(idx > 0){
            String name = URI.substring(0,idx+1);
            return name;
        }else{
            idx = URI.lastIndexOf("/");
            String name = URI.substring(0, idx+1);
            return name;
        }
    }
}
