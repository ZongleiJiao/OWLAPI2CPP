package edu.monash.it;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLRuntimeException;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Zonglei Jiao
 *
 */
public class OWLAPIWrapper {

    private String[] allOWLClassNames;
    private String[] superClasses;
    private String[] subClassNames;
    private String[] disjointClasses;
    private String[] equivalentClasses;
    private String[] individuals;
    private String[] dataProperties;
    private String[] oObjectProperties;
    private String classPrefix;
    private int indexOfsharp;
    public OWLOntology myOntology;
//    private IRI myIRI;
    private OWLDataFactory myFactory;
    private String base;
    private DefaultPrefixManager pm;
    
    private OWLClass[] allOWLClasses;

    public OWLAPIWrapper() {
    }

    //loading ontology file, initialize Ontologies(class,individual,property)
    public String loadOntologyFile(String filename) {
        try {
            File file = new File(filename);
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            myFactory = manager.getOWLDataFactory();
            myOntology = manager.loadOntologyFromOntologyDocument(file);

            //get all classes from ontology.
            Set<OWLClass> ocset = myOntology.getClassesInSignature();
            allOWLClasses = new OWLClass[ocset.size()];
            ocset.toArray(allOWLClasses);
            
            //get ontology base,initialize prefixmanager
            base = allOWLClasses[1].toString().substring(1,allOWLClasses[1].toString().indexOf("#")+1);
            pm = new DefaultPrefixManager(base);
                        
            return myOntology.toString();

        } catch (OWLOntologyCreationException ex) {
            Logger.getLogger(OWLAPIWrapper.class.getName()).log(Level.SEVERE, null, ex);
            return "Error! Contact authors!(No.1)";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error! Contact authors!(No.2)";
        }
    }

    //get all class names from the ontology provided
    public String[] getAllOWLClasses() {
        allOWLClassNames = getClassShortNames(allOWLClasses);
        return allOWLClassNames;
    }

    //get all subclasses of the class provided
    public String[] getSubClasses(String className) {
        OWLClass cls = myFactory.getOWLClass(pm.getIRI(className));
        Set<OWLClassExpression> oceset = cls.getSubClasses(myOntology);
      
        if (oceset.size() < 1) {
            return null;
        }
        
        OWLClassExpression[] expression = new OWLClassExpression[oceset.size()];
        oceset.toArray(expression);
        subClassNames = this.getClassShortNames(expression);
        return subClassNames;
    }

    //get all superclasses of the class provided
    public String[] getSuperClasses(String className) {
        OWLClass cls = myFactory.getOWLClass(pm.getIRI(className));

        Set<OWLClassExpression> oceset = cls.getSuperClasses(myOntology);
        if (oceset.size() < 1) {
            return null;
        }
        
        OWLClassExpression[] expression = new OWLClassExpression[oceset.size()];
        oceset.toArray(expression);
        superClasses = getClassShortNames(expression);
        
        return superClasses;
    }

    public String[] getDisjointClasses(String className) {
        OWLClass cls = myFactory.getOWLClass(pm.getIRI(className));
        Set<OWLClassExpression> oceset = cls.getDisjointClasses(myOntology);
        
        if (oceset.size() < 1) {
            return null;
        }
        
        OWLClassExpression[] expression = new OWLClassExpression[oceset.size()];
        oceset.toArray(expression);
        this.disjointClasses = this.getClassShortNames(expression);
        return disjointClasses;
    }

    //TODO -----
    public String[] getEquivalentClasses(String className) {
//        OWLClass cls = myFactory.getOWLClass(pm.getIRI(className));
//        Set<OWLClassExpression> oceset = cls.getEquivalentClasses(myOntology);
//        if (oceset.size() < 1) {
//            return null;
//        }
//        
//        OWLClassExpression[] expression = new OWLClassExpression[oceset.size()];
//        oceset.toArray(expression);
//        this.equivalentClasses = this.getClassShortNames(expression);   
//        return equivalentClasses;
        return null;
    }

    public String[] getIndividuals(String className) {
        OWLClass clsA = myFactory.getOWLClass(IRI.create(classPrefix.substring(1) + className));
        Set<OWLIndividual> set = clsA.getIndividuals(myOntology);
        if (set.size() < 1) {
            return null;
        }
        OWLIndividual[] express = new OWLIndividual[set.size()];
        set.toArray(express);
        this.individuals = new String[express.length];
        for (int i = 0; i < express.length; i++) {
            individuals[i] = express[i].toString();
        }
//        return deleteSubClassNamePrefix(this.individuals);
        return null;
    }

    public String[] getDataProperties(String className) {
        OWLClass cls = myFactory.getOWLClass(IRI.create(classPrefix.substring(1) + className));
        Set<OWLDataProperty> set = cls.getDataPropertiesInSignature();
        if (set.size() < 1) {
            System.out.println(className + " null  null");
            return null;
        }
        OWLDataProperty[] express = new OWLDataProperty[set.size()];
        set.toArray(express);
        this.dataProperties = new String[express.length];
        for (int i = 0; i < express.length; i++) {
//            oObjectProperties[i] = express[i].toStringID();
            System.out.println(express[i].toString() + "<----->" + express[i].toStringID());
        }
        return null;

    }

    public String[] getObjectProperties(String className) {
        OWLClass cls = myFactory.getOWLClass(IRI.create(classPrefix.substring(1) + className));
        Set<OWLAnnotation> sss = cls.getAnnotations(myOntology);
        System.out.println("----------->"+sss);
        
        Set<OWLObjectProperty> set = cls.getObjectPropertiesInSignature();
        if (set.size() < 1) {
            System.out.println(classPrefix.substring(1) + className + " null  null123");
            return null;
        }
        OWLObjectProperty[] express = new OWLObjectProperty[set.size()];
        set.toArray(express);
        this.oObjectProperties = new String[express.length];
        for (int i = 0; i < express.length; i++) {
//            oObjectProperties[i] = express[i].toStringID();
            System.out.println(express[i].toString() + "<----->" + express[i].toStringID());
        }
        return null;
    }

    private String[] getClassShortNames(OWLClass[] classes)
    {
        String[] temp = new String[classes.length];
        for(int i=0;i<classes.length;i++)
        {
            String shortName = pm.getShortForm(classes[i]);
            if(shortName.equals("owl:Thing"))
            {
                shortName = "Thing";
            }else
            {
                shortName = shortName.substring(1);
            }
            temp[i] = shortName;            
        }
        return temp;
    }
    
    private String[] getClassShortNames(OWLClassExpression[] classExpression)
    {
        String[] temp = null;
        ArrayList al = new ArrayList();
        for(int i=0;i<classExpression.length;i++)
        {
            if(!(classExpression[i].isAnonymous()))
            {
                String shortName = pm.getShortForm(classExpression[i].asOWLClass());

                if(shortName.equals("owl:Thing"))
                {
                    shortName = "Thing";
                }else
                {
                    shortName = shortName.substring(1);
                }
                al.add(shortName);
            }
        }
        
        if(al.size()==0){
            temp = new String[1];
            temp[0] = "Thing";
        }else{
            temp = new String[al.size()];
            al.toArray(temp);
        }       
          
        return temp;
    }
    
    public static void main(String[] args) {
        
        System.out.println("=====================Load Ontology============================");
        OWLAPIWrapper owl = new OWLAPIWrapper();
        String ontostr = owl.loadOntologyFile("owlfiles/koala.owl");
        System.out.println("loading ontology: "+ontostr);

        System.out.println("=====================All Classes(Names)============================");
        String[] aoc = owl.getAllOWLClasses();
        System.out.println("Number of classes---->" + aoc.length);
        for (String name : aoc) {
            System.out.println(name);
        }

        System.out.println("=====================Sub Classes============================");
        String[] sc = owl.getSubClasses("Animal");

        if (sc != null) {
            for (String name : sc) {
                System.out.println(name);
            }
        }else{
            System.out.println("No sub classes");
        }
        
        System.out.println("=====================Super classes============================");
        String[] suc = owl.getSuperClasses("GraduateStudent");
        if (sc != null) {
            for (String name : suc) {
                System.out.println(name);
            }
        }else{
            System.out.println("No super classes");
        }
        
        System.out.println("=====================Disjoint classes============================");
        String[] dc = owl.getDisjointClasses("Person");
        if (dc != null) {
            for (String name : dc) {
                System.out.println(name);
            }
        }

//        System.out.println("=====================Equivalent classes============================");
//        String[] ec = owl.getEquivalentClasses("MaleStudentWith3Daughters");
//        if (ec != null) {
//            for (String name : ec) {
//                System.out.println(name);
//            }
//        }

////        System.out.println(owl.superClasses);
//        System.out.println("=====================Individuals============================");
//        String[] in = owl.getIndividuals("Gender");
//        if (in != null) {
//            for (String s8 : in) {
//                System.out.println(s8);
//            }
//        }
//        System.out.println("=====================Object Property============================");
//        String[] op = owl.getObjectProperties("Person");
//
//        
//        System.out.println("=====================Axioms============================");
//        Set<OWLAxiom> set = owl.myOntology.getAxioms();
//        OWLAxiom[] oa = new OWLAxiom[set.size()];
//        set.toArray(oa);
//        for(OWLAxiom ooa : oa)
//        {
//          System.out.println(ooa.getAxiomType()+" ------ "+ooa.toString());  
//          if((ooa.getAxiomType().isAxiomType("FunctionalDataProperty")) == true) 
//          {
//              System.out.println("============FunctionalDataProperty");
//          }
//        }
    }
}
