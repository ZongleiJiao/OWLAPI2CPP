/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.monash.infotech.test;

import edu.monash.infotech.OWLAPIWrapper;
import org.semanticweb.owlapi.model.OWLClass;

/**
 *
 * @author ZongleiJiao
 */
public class TestOWLAPIWrapper {
    
    private static OWLAPIWrapper owl = new OWLAPIWrapper();

    
    private static void loadOntology(String fileName){
        String ontostr = owl.loadOntologyFile(fileName);
        System.out.println("loading ontology: "+ontostr);
        System.out.println("Default Name Space : "+owl.getDefaultNameSpace());
        
        for(OWLClass oc:owl.getAllOWLClass())
        {
            String name = oc.getIRI().toURI().toString();
            System.out.println(name);
            
            String sn = name.substring(0,name.indexOf("#")+1);
            System.out.println(sn);
        }
    }
    
    private static void getAllClassNames() {
        String[] aoc = owl.getAllOWLClasses();
        System.out.println("Number of classes---->" + aoc.length+"\n");
        for (String name : aoc) {
            System.out.println(name);
        }
    }

    private static void getSubClassNames(String className) {
        String[] sc = owl.getSubClasses(className);
        System.out.println(className+"'s sub classes are:"+"\n");
        if (sc != null) {
            for (String name : sc) {
                System.out.println(name);
            }
        }else{
            System.out.println("No sub classes");
        }
    }

    private static void getSuperClasses(String className) {
        String[] suc = owl.getSuperClasses(className);
        System.out.println(className+"'s super classes are:"+"\n");
        
        if (suc != null) {
            for (String name : suc) {
                System.out.println(name);
            }
        }else{
            System.out.println("No super classes");
        }
    }
    
    private static void getDisjointClasses(String className) {
        String[] dc = owl.getDisjointClasses(className);
        System.out.println(className+"'s disjoint classes are:"+"\n");
        
        if (dc != null) {
            for (String name : dc) {
                System.out.println(name);
            }
        }
    }
    
    
    private static void getEquivalentClasses(String className) {
        System.out.println(className+"'s equivalent classes are:"+"\n");
        String ec = owl.getEquivalentClasses(className);
        System.out.println(ec);
    }
    
    private static void getAllIndividuals() {
        String[] ins = owl.getAllIndividuals();
        System.out.println("Number of individuals---->" + ins.length+"\n");
        if (ins != null) {
            for (String s8 : ins) {
                System.out.println(s8);
            }
        }
    }
    
    private static void getIndividualsByClass(String className) {
        System.out.println(className+"'s individuals are:"+"\n");
        String[] in = owl.getIndividuals(className);
        if (in != null) {
            for (String s8 : in) {
                System.out.println(s8);
            }
        }
    }

    private static void getAllProperties() {
        String[] nop = owl.getAllPropertiesNameAndSubType();
        System.out.println("Number of properties---->" + nop.length+"\n");
        if(nop !=null)
        {
            for(String p:nop)
            {
                System.out.println(p);
            }
        }
    }

    private static void getSubProperties(String propertyType,String propertyName) {
        String[] sp = owl.getSubProperites(propertyType,propertyName);
        System.out.println(propertyType+" "+propertyName+"'s sub Properies: \n");
        if(sp != null){
            for(String s:sp){
                System.out.println(s);
            }
        }
    }
    
    private static void getSuperProperty(String propertyType,String propertyName) {
        String[] sup = owl.getSuperProperites(propertyType,propertyName);
        System.out.println(propertyType+" "+propertyName+"'s super Properies: \n");
        if(sup != null){
            for(String s:sup){
                System.out.println(s);
            }
        }
    }

    private static void getDisjointProperty(String propertyType,String propertyName) {
        String[] dps = owl.getDisjointProperties(propertyType,propertyName);
        System.out.println(propertyType+" "+propertyName+"'s disjoint Properies: \n");
        if(dps != null){
            for(String s:dps){
                System.out.println(s);
            }
        }
    }

    private static void getPropertiesByType(String type) {
        String[] op = owl.getAllPropertiesByType(type);
        
        if(op != null)
        {
            int i=0;
            for(String p : op)
            {
                System.out.println(p);
                i++;
                if(i%4==0)
                {
                    System.out.println("-------------------");
                }
            }
        }else{
            System.out.println("Wrong type or no properties of this type");
        }
    }

    private static void getAxioms(){
        
//        OWLClass cls = owl.myFactory.getOWLClass(owl.pm.getIRI("Animal"));
//        Set<OWLAxiom> set = cls.getReferencingAxioms(owl.myOntology);
//        
//        Set<OWLDeclarationAxiom> set = owl.myOntology.getAxioms(AxiomType.DECLARATION);
        
        
        
//        Set<OWLAxiom> set = owl.myOntology.getAxioms();
//        OWLAxiom[] oa = new OWLAxiom[set.size()];
//        set.toArray(oa);
//        int i=0;
//        for(OWLAxiom ooa : oa)
//        {
//            
//          if(!ooa.toString().contains("(Class"))
//          {
//          System.out.println(ooa.toString());
//              i++;
//          }
//        }
//        System.out.println(i);
        
        
        
          
//          
//          if(ooa.getAxiomType().getName().equals(AxiomType.DECLARATION.getName()) )
//          {
//              System.out.println("============Declaration");i++;
//          }
        
//        OWLObjectProperty op = owl.myFactory.getOWLObjectProperty(owl.pm.getIRI("hasGender"));
        
        
        
//        System.out.println(op..getSignature().toArray(new OWLEntity[1])[0]);
    }
          
    
    public static void main(String[] args) {
        
        System.out.println("=====================Load Ontology============================");
        loadOntology("owlfiles/koala.owl");
//        loadOntology("owlfiles/pizza.owl");
        
        
//        System.out.println("=====================All Classes(Shrot Names)============================");
//        getAllClassNames();
        

//        System.out.println("=====================Sub Classes============================");
//        getSubClassNames("Animal");
        
        
//        System.out.println("=====================Super classes============================");
//        getSuperClasses("GraduateStudent");
        
        
//        System.out.println("=====================Disjoint classes============================");
//        getDisjointClasses("Person");
         

//        System.out.println("=====================Equivalent classes(To be Finished)============================");
        getEquivalentClasses("Student");
//        getEquivalentClasses("MaleStudentWith3Daughters");
//        owl.getEquivalentClassesFormula("MaleStudentWith3Daughters");
        
        
//        System.out.println("=====================All Individuals============================");
//        getAllIndividuals();
//        
//        
//        System.out.println("=====================Individuals BY Class============================");
//        getIndividualsByClass("Degree");
//         
//        
//        System.out.println("=====================All Property============================");
//        getAllProperties();
//        
//                
//        System.out.println("=====================Sub Property============================");
//        getSubProperties(owl.ENTITIY_TYPE_OBJECT_PROPERTY,"hasIngredient");
//        
//        
//        System.out.println("=====================Super Property============================");
//        getSuperProperty(owl.ENTITIY_TYPE_OBJECT_PROPERTY,"hasBase");
//        
//        
//        System.out.println("=====================Disjoint Property============================");
//        getDisjointProperty(owl.ENTITIY_TYPE_OBJECT_PROPERTY,"hasBase");
//        
//        
//        System.out.println("=====================Equivalent Property(To be Finished)============================");
//        
//        
//        System.out.println("=====================Object Property(Only Test)============================");
//        getPropertiesByType(owl.ENTITIY_TYPE_OBJECT_PROPERTY);
//        
//        
//        System.out.println("=====================Data Property(Only Test)============================");
//        getPropertiesByType(owl.ENTITIY_TYPE_DATA_PROPERTY);
//        
//        
//        System.out.println("=====================Axioms (Only Test)============================");
//        getAxioms();
        
    }


    
}
