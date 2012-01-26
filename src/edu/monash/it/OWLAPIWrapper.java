package edu.monash.it;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Chris
 */
public class OWLAPIWrapper {

    private String[] allClasses;
    private String[] superClasses;
    private String[] subClasses;
    private String[] disjointClasses;
    private String[] equivalentClasses;
    private String[] individuals;
    private String[] dataProperties;
    private String[] oObjectProperties;
    private String classPrefix;
    private int indexOfsharp;
    public OWLOntology myOntology;
    private IRI myIRI;
    private OWLDataFactory myFactory;

    public OWLAPIWrapper() {
    }

    //loading ontology file, initialize Ontologies(class,individual,property)
    public String loadOntologyFile(String filename) {
        try {
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            myFactory = manager.getOWLDataFactory();

            File file = new File(filename);

            myOntology = manager.loadOntologyFromOntologyDocument(file);
                       
            
//            myIRI = manager.getOntologyDocumentIRI(myOntology);
//            myIRI = IRI.create("http://protege.stanford.edu/plugins/owl/owl-library/koala.owl");
            myIRI = manager.getOntologyDocumentIRI(myOntology);

            Set<OWLClass> ocset = myOntology.getClassesInSignature();
            OWLClass[] ocarr = new OWLClass[ocset.size()];
            ocset.toArray(ocarr);
            this.allClasses = new String[ocset.size()];
            for (int i = 0; i < ocarr.length; i++) {
                allClasses[i] = ocarr[i].toString();
//                OWLClass cc = ocarr[i];


//                Iterator<OWLClassExpression> ss = cc.getSubClasses(myOntology).iterator();

//                while (ss.hasNext()) {
//                    System.out.println("---1111---"+ss.next().toString());
//                }
//                System.out.println("<" + i + ">-=-=-=-=-=-=-=-=-=-" + ocarr[i]);
            }
            return myOntology.toString();

        } catch (OWLOntologyCreationException ex) {
            Logger.getLogger(OWLAPIWrapper.class.getName()).log(Level.SEVERE, null, ex);
            return "Error! Contact authors!(No.1)";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error! Contact authors!(No.2)";
        }
    }

    //TODO include Thing????
    public String[] getAllOWLClasses() {
        return this.deleteClassNamePrefix(allClasses);
    }

    //TODO duplicate issue (class names)????
    public String[] getSubClasses(String className) {
        OWLClass clsA = myFactory.getOWLClass(IRI.create(classPrefix.substring(1) + className));
//        OWLClass clsA = myFactory.getOWLClass(IRI.create(myIRI + "#"+className));
//        System.out.println(clsA);
//        Iterator<OWLClassExpression> ss = clsA.getSubClasses(myOntology).iterator();
//        while(ss.hasNext())
//        {
//            System.out.println("---2222---"+ss.next().toString());
////            this.subClasses = ss.next().toString();
//        }
        Set<OWLClassExpression> sset = clsA.getSubClasses(myOntology);
        if (sset.size() < 1) {
            return null;
        }
//        System.out.println("========123123========="+sset.size());
        OWLClassExpression[] express = new OWLClassExpression[sset.size()];
        sset.toArray(express);
        this.subClasses = new String[express.length];
        for (int i = 0; i < express.length; i++) {
            subClasses[i] = express[i].toString();
        }
        return deleteSubClassNamePrefix(subClasses);
    }

    public String[] getSuperClasses(String className) {
        OWLClass clsA = myFactory.getOWLClass(IRI.create(classPrefix.substring(1) + className));
//        Iterator<OWLClassExpression> ss = clsA.getSuperClasses(myOntology).iterator();
        Set<OWLClassExpression> sset = clsA.getSuperClasses(myOntology);
        if (sset.size() < 1) {
//            System.out.println("123123131231");
            return null;
        }
        OWLClassExpression[] express = new OWLClassExpression[sset.size()];
        sset.toArray(express);
        this.superClasses = new String[express.length];
        for (int i = 0; i < express.length; i++) {
            superClasses[i] = express[i].toString();
        }
        return deleteSubClassNamePrefix(superClasses);
    }

    public String[] getDisjointClasses(String className) {
        OWLClass clsA = myFactory.getOWLClass(IRI.create(classPrefix.substring(1) + className));
        Set<OWLClassExpression> sset = clsA.getDisjointClasses(myOntology);
        if (sset.size() < 1) {
            return null;
        }
//        System.out.println("========123123========="+sset.size());
        OWLClassExpression[] express = new OWLClassExpression[sset.size()];
        sset.toArray(express);
        this.disjointClasses = new String[express.length];
        for (int i = 0; i < express.length; i++) {
            disjointClasses[i] = express[i].toString();
        }
        return deleteSubClassNamePrefix(disjointClasses);
    }

    public String[] getEquivalentClasses(String className) {
        OWLClass clsA = myFactory.getOWLClass(IRI.create(classPrefix.substring(1) + className));
        Set<OWLClassExpression> sset = clsA.getEquivalentClasses(myOntology);
        if (sset.size() < 1) {
            return null;
        }
//        System.out.println("========123123========="+sset.size());
        OWLClassExpression[] express = new OWLClassExpression[sset.size()];
        sset.toArray(express);
        this.equivalentClasses = new String[express.length];
        for (int i = 0; i < express.length; i++) {
            equivalentClasses[i] = express[i].toString();
        }
//        return deleteSubClassNamePrefix(equivalentClasses);   
        return this.equivalentClasses;
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
        return deleteSubClassNamePrefix(this.individuals);
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
        Set<OWLObjectProperty> set = cls.getObjectPropertiesInSignature();
        if (set.size() < 1) {
            System.out.println(className + " null  null");
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

    private String[] deleteClassNamePrefix(String[] classes) {
        String[] classWithoutPrefix = new String[classes.length];

        String tempName = classes[1];
        this.indexOfsharp = tempName.indexOf("#");
        this.classPrefix = tempName.substring(0, indexOfsharp + 1);
//        String modifiedName = tempName.substring(indexOfsharp+1, tempName.length()-1);
//        System.out.println("----->"+this.classPrefix);
//        System.out.println("----->"+modifiedName);

        classWithoutPrefix[0] = "Thing";
        for (int i = 0, j = 1; i < classes.length; i++, j++) {
            if (classes[i].equals("owl:Thing")) {
                j--;
            } else {
                classWithoutPrefix[j] = classes[i].substring(indexOfsharp + 1, classes[i].length() - 1);
            }
        }

        return classWithoutPrefix;
    }

    private String[] deleteSubClassNamePrefix(String[] classes) {
        if (classes == null || classes.length == 0) {
            return null;
        }

        String[] classWithoutPrefix = new String[classes.length];
//        System.out.println(classes[0]);
//        String tempName = classes[0];
//        int indexOfsharp = tempName.indexOf("#");
//        this.classPrefix = tempName.substring(0, indexOfsharp+1);
//        String modifiedName = tempName.substring(indexOfsharp+1, tempName.length()-1);
//        System.out.println("----->"+this.classPrefix);
//        System.out.println("----->"+modifiedName);

        for (int i = 0; i < classes.length; i++) {
            classWithoutPrefix[i] = classes[i].substring(indexOfsharp + 1, classes[i].length() - 1);
        }

        return classWithoutPrefix;
    }

//    private String addClassNamePrefix(String className)
//    {
//        return classPrefix + className + ">";
//    }
    public static void main(String[] args) {
        OWLAPIWrapper owl = new OWLAPIWrapper();
        String str = owl.loadOntologyFile("owlfiles/koala.owl");

//        OWLAPIWrapper owl = new OWLAPIWrapper("./lib/koala.owl");
//        String str = owl.loadOntologyFile();
        System.out.println(str);

        System.out.println("=====================All Classes============================" + owl.allClasses.length);

        for (String s : owl.allClasses) {
            System.out.println(s);
        }

        System.out.println("=====================All Classes(Names)============================");
        String[] temp = owl.getAllOWLClasses();
        System.out.println("---->" + temp.length);
        for (String s1 : temp) {
            System.out.println(s1);
        }

        System.out.println("=====================Sub Classes============================");
        String[] cc = owl.getSubClasses("Animal");


//        System.out.println();
        if (cc != null) {
            for (String s4 : cc) {
                System.out.println(s4);
            }
        }


        System.out.println("=====================Disjoint classes============================");
        String[] dc = owl.getDisjointClasses("Person");
        if (dc != null) {
            for (String s5 : dc) {
                System.out.println(s5);
            }
        }

        System.out.println("=====================Equivalent classes============================");
        String[] ec = owl.getEquivalentClasses("Parent");
        if (ec != null) {
            for (String s6 : ec) {
                System.out.println(s6);
            }
        }
        System.out.println("=====================Super classes============================");
        String[] sc = owl.getSuperClasses("Person");
        if (sc != null) {
            for (String s7 : sc) {
                System.out.println(s7);
            }
        }
        System.out.println("=====================Individuals============================");
        String[] in = owl.getIndividuals("Gender");
        if (in != null) {
            for (String s8 : in) {
                System.out.println(s8);
            }
        }
        System.out.println("=====================Object Property============================");
        String[] op = owl.getDataProperties("Animal");

        
        System.out.println("=====================Axioms============================");
        Set<OWLAxiom> set = owl.myOntology.getAxioms();
        OWLAxiom[] oa = new OWLAxiom[set.size()];
        set.toArray(oa);
        for(OWLAxiom ooa : oa)
        {
          System.out.println(ooa.getAxiomType()+" ------ "+ooa.toString());  
          
          
        }
    }
}
