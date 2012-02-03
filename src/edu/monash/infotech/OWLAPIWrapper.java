package edu.monash.infotech;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;


/**
 *
 * @author Zonglei Jiao
 *
 */
public class OWLAPIWrapper {
    
    //sub types of axiom types
    public final String TYPE_CLASS = "(Class";
    public final String TYPE_DATA_PROPERTY = "(DataProperty";
    public final String TYPE_OBJECT_PROPERTY = "(ObjectProperty";
    
    private OWLDataFactory myFactory;
    private String nameSpace;
    private DefaultPrefixManager prefixManager;
    private OWLOntology myOntology;
    private OWLClass[] allOWLClasses;
    private OWLAxiom[] alDeclarationAxiom;

    private String[] allOWLClassNames;
    private String[] subClassNames;
    private String[] superClassNames;
    private String[] disjointClassNames;
    
    private OWLProperty[] objectProperties;
    private OWLProperty[] dataProperties;
    
//    private String[] equivalentClasses;
//    private String[] individuals;
//    private String[] dataProperties;
//    private String[] oObjectProperties;
//    private String classPrefix;
//    private int indexOfsharp;
    

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
            nameSpace = allOWLClasses[1].toString().substring(1,allOWLClasses[1].toString().indexOf("#")+1);
            prefixManager = new DefaultPrefixManager(nameSpace);
            
            //get Axioms of ontology
            Set<OWLDeclarationAxiom> set = myOntology.getAxioms(AxiomType.DECLARATION);
            alDeclarationAxiom = new OWLAxiom[set.size()];
            set.toArray(alDeclarationAxiom);
                        
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
        OWLClass cls = myFactory.getOWLClass(prefixManager.getIRI(className));
                
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
        OWLClass cls = myFactory.getOWLClass(prefixManager.getIRI(className));

        Set<OWLClassExpression> oceset = cls.getSuperClasses(myOntology);
        if (oceset.size() < 1) {
            return null;
        }
        
        OWLClassExpression[] expression = new OWLClassExpression[oceset.size()];
        oceset.toArray(expression);
        superClassNames = getClassShortNames(expression);
        
        return superClassNames;
    }

    public String[] getDisjointClasses(String className) {
        OWLClass cls = myFactory.getOWLClass(prefixManager.getIRI(className));
        Set<OWLClassExpression> oceset = cls.getDisjointClasses(myOntology);
        
        if (oceset.size() < 1) {
            return null;
        }
        
        OWLClassExpression[] expression = new OWLClassExpression[oceset.size()];
        oceset.toArray(expression);
        this.disjointClassNames = this.getClassShortNames(expression);
        return disjointClassNames;
    }

    //TODO -----
    public String[] getEquivalentClasses(String className) {
//        OWLClass cls = myFactory.getOWLClass(pm.getIRI(className));
//        Set<OWLClassExpression> oceset = cls.getEquivalentClasses(myOntology);System.out.println(cls.toString());
//        if (oceset.size() < 1) {
//            return null;
//        }
//        
//        OWLClassExpression[] expression = new OWLClassExpression[oceset.size()];
//        oceset.toArray(expression);
//        for(OWLClassExpression s:expression)
//        {
//            System.out.println(s.toString());
//        }
//        this.equivalentClasses = this.getClassShortNames(expression);   
//        return equivalentClasses;
        return null;
    }

    public String[] getIndividuals(String className) {
//        OWLClass clsA = myFactory.getOWLClass(IRI.create(classPrefix.substring(1) + className));
//        Set<OWLIndividual> set = clsA.getIndividuals(myOntology);
//        if (set.size() < 1) {
//            return null;
//        }
//        OWLIndividual[] express = new OWLIndividual[set.size()];
//        set.toArray(express);
//        this.individuals = new String[express.length];
//        for (int i = 0; i < express.length; i++) {
//            individuals[i] = express[i].toString();
//        }
//        return deleteSubClassNamePrefix(this.individuals);
        return null;
    }
    
    public OWLProperty[] getAllPropertiesByType(String type)
    {
        List properties = new ArrayList();
        for(int i=0;i<this.alDeclarationAxiom.length;i++)
        {
            if(this.alDeclarationAxiom[i].toString().contains(type))
            {
                OWLProperty dataProperty = new OWLProperty();
//System.out.println(alDeclarationAxiom[i].toString());
                int index_1 = alDeclarationAxiom[i].toString().indexOf(nameSpace);
                int index_2 = alDeclarationAxiom[i].toString().indexOf(">");
                dataProperty.propertyName = alDeclarationAxiom[i].toString().substring(index_1+nameSpace.length(), index_2);
                dataProperty.propertyType = type.substring(1);
//System.out.println(dataProperty.propertyName);
                properties.add(dataProperty);
            }
        }
        if(type.equals(this.TYPE_DATA_PROPERTY))
        {
            dataProperties = new OWLProperty[properties.size()];
            properties.toArray(this.dataProperties);
            return dataProperties;
        }else if(type.equals(this.TYPE_OBJECT_PROPERTY))
        {
            this.objectProperties = new OWLProperty[properties.size()];
            properties.toArray(this.objectProperties);
            return this.objectProperties;
        }
        else
        {
            return null;
        }
    }
    
    public String[] getDataProperties(String className) {
//        OWLClass cls = myFactory.getOWLClass(IRI.create(classPrefix.substring(1) + className));
//        Set<OWLDataProperty> set = cls.getDataPropertiesInSignature();
//        if (set.size() < 1) {
//            System.out.println(className + " null  null");
//            return null;
//        }
//        OWLDataProperty[] express = new OWLDataProperty[set.size()];
//        set.toArray(express);
//        this.dataProperties = new String[express.length];
//        for (int i = 0; i < express.length; i++) {
////            oObjectProperties[i] = express[i].toStringID();
//            System.out.println(express[i].toString() + "<----->" + express[i].toStringID());
//        }
        return null;

    }

    public String[] getObjectProperties(String className) {
//        OWLClass cls = myFactory.getOWLClass(IRI.create(classPrefix.substring(1) + className));
//        Set<OWLAnnotation> sss = cls.getAnnotations(myOntology);
//        System.out.println("----------->"+sss);
//        
//        Set<OWLObjectProperty> set = cls.getObjectPropertiesInSignature();
//        if (set.size() < 1) {
//            System.out.println(classPrefix.substring(1) + className + " null  null123");
//            return null;
//        }
//        OWLObjectProperty[] express = new OWLObjectProperty[set.size()];
//        set.toArray(express);
//        this.oObjectProperties = new String[express.length];
//        for (int i = 0; i < express.length; i++) {
////            oObjectProperties[i] = express[i].toStringID();
//            System.out.println(express[i].toString() + "<----->" + express[i].toStringID());
//        }
        return null;
    }

    private String[] getClassShortNames(OWLClass[] classes)
    {
        String[] temp = new String[classes.length];
        for(int i=0;i<classes.length;i++)
        {
            String shortName = prefixManager.getShortForm(classes[i]);
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
                String shortName = prefixManager.getShortForm(classExpression[i].asOWLClass());

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
        
        if(al.isEmpty()){
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
        System.out.println("Name Space : "+owl.nameSpace);

        System.out.println("=====================All Classes(Names)============================");
        String[] aoc = owl.getAllOWLClasses();
        System.out.println("Number of classes---->" + aoc.length);
        for (String name : aoc) {
            System.out.println(name);
        }

        System.out.println("=====================Sub Classes============================");
        String className1 = "Animal";
        String[] sc = owl.getSubClasses(className1);
        System.out.println(className1+"'s sub classes are:");

        if (sc != null) {
            for (String name : sc) {
                System.out.println(name);
            }
        }else{
            System.out.println("No sub classes");
        }
        
        System.out.println("=====================Super classes============================");
        String className2 = "GraduateStudent";
        String[] suc = owl.getSuperClasses(className2);
        System.out.println(className2+"'s super classes are:");
        
        if (sc != null) {
            for (String name : suc) {
                System.out.println(name);
            }
        }else{
            System.out.println("No super classes");
        }
        
        System.out.println("=====================Disjoint classes============================");
        String className3 = "Person";
        String[] dc = owl.getDisjointClasses(className3);
        System.out.println(className3+"'s disjoint classes are:");
        
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
        
        
        
        System.out.println("=====================Object Property============================");
        OWLProperty[] op = owl.getAllPropertiesByType(owl.TYPE_OBJECT_PROPERTY);
        
        if(op != null)
        {
            for(OWLProperty p : op)
            {
                System.out.println(p.propertyType+"::"+p.propertyName);
            }
        }else{
            System.out.println("Wrong type or no properties of this type");
        }

        
        System.out.println("=====================Data Property============================");
        
        OWLProperty[] dp = owl.getAllPropertiesByType(owl.TYPE_DATA_PROPERTY);
        
        if(dp != null)
        {
            for(OWLProperty p: dp)
            {
                System.out.println(p.propertyType+"::"+p.propertyName);
            }
        }else{
            System.out.println("Wrong type or no properties of this type");   
        }
        
        
        System.out.println("=====================Axioms============================");
        
//        OWLClass cls = owl.myFactory.getOWLClass(owl.pm.getIRI("Animal"));
//        Set<OWLAxiom> set = cls.getReferencingAxioms(owl.myOntology);
//        
        Set<OWLDeclarationAxiom> set = owl.myOntology.getAxioms(AxiomType.DECLARATION);
        OWLAxiom[] oa = new OWLAxiom[set.size()];
        set.toArray(oa);
        int i=0;
        for(OWLAxiom ooa : oa)
        {
            
          if(!ooa.toString().contains("(Class"))
          {
          System.out.println(ooa.toString()); 
              i++;
          }
          
          
//          if(ooa.getAxiomType().getName().equals(AxiomType.DECLARATION.getName()) )
//          {
//              System.out.println("============Declaration");i++;
//          }
        }
        System.out.println(i);
        
//        OWLObjectProperty op = owl.myFactory.getOWLObjectProperty(owl.pm.getIRI("hasGender"));
        
        
        
//        System.out.println(op..getSignature().toArray(new OWLEntity[1])[0]);
        
    }
}
