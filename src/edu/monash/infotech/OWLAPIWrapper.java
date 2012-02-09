package edu.monash.infotech;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
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
 * @author Zonglei Jiao
 *
 */
public class OWLAPIWrapper {
    
    //sub types of axiom types
    public final String TYPE_CLASS = "Class";
    public final String TYPE_DATA_PROPERTY = "DataProperty";
    public final String TYPE_OBJECT_PROPERTY = "ObjectProperty";    
    public final String SPLITER = ":-=-:";
    public final String PROPERTY_TYPE_FUNCTIONAL = "Functional";
    public final String PROPERTY_TYPE_INVERSE = "Inverse";
    public final String PROPERTY_TYPE_INVERSE_FUNCTIONAL = "InverseFunctional";
    public final String PROPERTY_TYPE_SYMMETRIC = "Symmetric";
    public final String PROPERTY_TYPE_ASYMMETRIC = "Asymmetric";
    public final String PROPERTY_TYPE_TRANSITIVE = "Transitive";
    public final String PROPERTY_TYPE_REFLEXIVE = "Reflexive";
    public final String PROPERTY_TYPE_IRREFLEXIVE = "Irreflexive";
    
    public String nameSpace;
        
    private OWLDataFactory myFactory;
    private DefaultPrefixManager prefixManager;
    private OWLOntology myOntology;
    private OWLClass[] allOWLClasses;
    private OWLAxiom[] alDeclarationAxiom;

    private String[] allOWLClassNames;
    private String[] subClassNames;
    private String[] superClassNames;
    private String[] disjointClassNames;
    private String[] individualNames;
    
    // every 4 items make a small group information of one property. 
    //1-name 2-type 3-domain 4-range. 
    //if any item is not provied by ontology, one " " will be writen instead
    private String[] properties;
    
     
//    private String[] equivalentClasses;
//    private String[] dataProperties;
//    private String[] oObjectProperties;
    

    public OWLAPIWrapper() {
    }

    //loading ontology file, initialize Ontologies(class,individual,property)
    public String loadOntologyFile(String filename) {
        try {
            File file = new File(filename);
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            myFactory = manager.getOWLDataFactory();
            myOntology = manager.loadOntologyFromOntologyDocument(file);
            
            OWLOntologyXMLNamespaceManager ooxnm = new OWLOntologyXMLNamespaceManager(manager,myOntology);
            
System.out.println("DefaultNameSpace: "+ooxnm.getDefaultNamespace());
System.out.println("NameSpace: "+ooxnm.getNamespaces().toString());
System.out.println("Prefix: "+ooxnm.getPrefixes().toString());


System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

            //get all classes from ontology.
            Set<OWLClass> ocset = myOntology.getClassesInSignature();
            allOWLClasses = new OWLClass[ocset.size()];
            ocset.toArray(allOWLClasses);
//System.out.println("Number of Owl classes"+ocset.size());            
//for(OWLClass oc: allOWLClasses)
//{
//    System.out.println("toStringID------>"+oc.toStringID());
//    System.out.println("toString------>"+oc.toString());
//}
            
//            //get ontology base,initialize prefixmanager
//            if(this.allOWLClasses[0].toString().equalsIgnoreCase("owl:Thing") && ocset.size()==1)
//            {
//                nameSpace = allOWLClasses[0].toStringID().substring(1,allOWLClasses[0].toStringID().indexOf("#")+1);
//            }
//            if(this.allOWLClasses[0].toString().equalsIgnoreCase("owl:Thing") && ocset.size()>1)
//            {
//                nameSpace = allOWLClasses[1].toStringID().substring(1,allOWLClasses[1].toStringID().indexOf("#")+1);
//            }else if(!(this.allOWLClasses[0].toString().equalsIgnoreCase("owl:Thing")) && ocset.size()>1)
//            {
//                nameSpace = allOWLClasses[0].toStringID().substring(1,allOWLClasses[0].toStringID().indexOf("#")+1);
//            }
            nameSpace = ooxnm.getDefaultNamespace();
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
    //TODO one child has multiple super classes
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
        OWLClass cls = myFactory.getOWLClass(this.prefixManager.getIRI(className));
        Set<OWLClassExpression> oceset = cls.getEquivalentClasses(myOntology);System.out.println(cls.toString());
        if (oceset.size() < 1) {
            return null;
        }
        
        OWLClassExpression[] expression = new OWLClassExpression[oceset.size()];
        oceset.toArray(expression);
        System.out.println(oceset.size()+this.nameSpace);
        String str = expression[0].toString();
        System.out.println(str);
        str = str.replaceAll(this.nameSpace, "");
        System.out.println(str+"\n");
        OWLEquivalentClassesAxiom oeca = this.myFactory.getOWLEquivalentClassesAxiom(expression);
        System.out.println(oeca.toString());
        Set<OWLClassExpression> oceset2 = oeca.getNestedClassExpressions();System.out.println(oceset2.size());
        for(OWLClassExpression s:oceset2)
        {
            System.out.println(s.toString());
        }
        
//        for(OWLClassExpression s:expression)
//        {
//            System.out.println(s.toString());
//        }
//        this.equivalentClasses = this.getClassShortNames(expression);   
//        return equivalentClasses;
        return null;
    }

    public String[] getIndividuals(String className) {
        OWLClass clsA = myFactory.getOWLClass(this.prefixManager.getIRI(className));
        Set<OWLIndividual> set = clsA.getIndividuals(myOntology);
        if (set.size() < 1) {
            return null;
        }
        
        OWLIndividual[] express = new OWLIndividual[set.size()];
        set.toArray(express);

        this.individualNames = getIndividualShortNames(express);
        return this.individualNames;
    }
    
    private String getPropertySubType(OWLProperty op)
    {
        String subType = "";
        
        if(op.isObjectPropertyExpression())
        {
            OWLObjectProperty obp = (OWLObjectProperty) op;
            subType = this.TYPE_OBJECT_PROPERTY;
            
            if(op.isFunctional(myOntology))
            {
                subType = this.PROPERTY_TYPE_FUNCTIONAL + subType;
            }else if(obp.isAsymmetric(myOntology))
            {
                subType = this.PROPERTY_TYPE_ASYMMETRIC + subType;
            }else if(obp.isInverseFunctional(myOntology))
            {
                subType = this.PROPERTY_TYPE_INVERSE_FUNCTIONAL + subType;
            }else if(obp.isIrreflexive(myOntology))
            {
                subType = this.PROPERTY_TYPE_IRREFLEXIVE + subType;
            }else if(obp.isReflexive(myOntology))
            {
                subType = this.PROPERTY_TYPE_REFLEXIVE + subType;
            }else if(obp.isSymmetric(myOntology))
            {
                subType = this.PROPERTY_TYPE_SYMMETRIC + subType;
            }else if(obp.isTransitive(myOntology))
            {
                subType = this.PROPERTY_TYPE_TRANSITIVE + subType;            
            }else if(!(obp.getInverses(myOntology).isEmpty()))
            {
                subType = this.PROPERTY_TYPE_INVERSE + subType;
            }
        }else if(op.isDataPropertyExpression())
        {
            subType = this.TYPE_DATA_PROPERTY;
            if(op.isFunctional(myOntology))
            {
                subType = this.PROPERTY_TYPE_FUNCTIONAL + subType;
            }
        }
        return subType;
    }
    
    private String getPropertyDomains(OWLProperty op)
    {
        String domain = "";
        try{
            
            
        
        Set<OWLClassExpression> set = op.getDomains(myOntology);
//        OWLClassExpression[] ooo = new OWLClassExpression[set.size()];
//        set.toArray(ooo);
//        try{
//System.out.print(set.size()+"------------");        
        Iterator it = set.iterator();
        OWLClassExpression e = (OWLClassExpression) it.next();
//System.out.println("----->"+e);
        domain = this.getClassShortName(e);
        
        while(it.hasNext())
        {
            OWLClassExpression e2 = (OWLClassExpression) it.next();
            domain += this.SPLITER+this.getClassShortName(e2);
        }
        
        
        }catch(Exception e)
        {
//            e.printStackTrace();
        }
        return domain;
    }
    
    private String getPropertyRanges(OWLProperty op)
    {
        String range = "";
        
        try{
            
            
            
        Set set = op.getRanges(myOntology);
        Iterator it = set.iterator();
        if(op.isObjectPropertyExpression())
        {
            range = this.getClassShortName((OWLClassExpression)it.next());
            while(it.hasNext())
            {
                range += this.SPLITER+this.getClassShortName((OWLClassExpression)it.next());
            }
        }else if(op.isDataPropertyExpression())
        {
            range = it.next().toString();
            while(it.hasNext())
            {
                range += this.SPLITER+it.next().toString();;
            }
        }
        
        
        }catch(Exception e)
        {
//            e.printStackTrace();
        }
        return range;
    }
    
    private String[] getPropertyInformation(String propertyType, String propertyName)
    {
        OWLProperty op = null;
        String[] info = new String[]{" "," "," "};
        if(propertyType.equals(this.TYPE_DATA_PROPERTY))
        {
            op = this.myFactory.getOWLDataProperty(this.prefixManager.getIRI(propertyName));
        }else if(propertyType.equals(this.TYPE_OBJECT_PROPERTY))
        {
            op = this.myFactory.getOWLObjectProperty(this.prefixManager.getIRI(propertyName));
        }
        
        info[0] = this.getPropertySubType(op);
        info[1] = this.getPropertyDomains(op);
        info[2] = this.getPropertyRanges(op); 
        
        return info;
    }
    
    public String[] getAllPropertiesByType(String type)
    {
        List list = new ArrayList();
        for(int i=0;i<this.alDeclarationAxiom.length;i++)
        {
            if(this.alDeclarationAxiom[i].toString().contains("("+type))
            {
                int index_1 = alDeclarationAxiom[i].toString().indexOf(nameSpace);
                int index_2 = alDeclarationAxiom[i].toString().indexOf(">");
                String propertyName = alDeclarationAxiom[i].toString().substring(index_1+nameSpace.length(), index_2);
                String[] info = this.getPropertyInformation(type, propertyName);
                list.add(propertyName);
                list.add(info[0]);
                list.add(info[1]);
                list.add(info[2]);
            }
        }
        properties = new String[list.size()];
        list.toArray(properties);
        return properties;
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
    
    private String[] getIndividualShortNames(OWLIndividual[] individual)
    {
        String[] temp = new String[individual.length];
        for(int i=0;i<individual.length;i++)
        {
            String shortName = prefixManager.getShortForm(individual[i].asOWLNamedIndividual());
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
    
    private String getClassShortName(OWLClassExpression classExpression)
    {
        String shortName = prefixManager.getShortForm(classExpression.asOWLClass());
        if(shortName.equals("owl:Thing"))
        {
            shortName = "Thing";
        }else
        {
            shortName = shortName.substring(1);
        }
        
        return shortName;
    }
    
    public static void main(String[] args) {
        
        System.out.println("=====================Load Ontology============================");
        OWLAPIWrapper owl = new OWLAPIWrapper();
        String ontostr = owl.loadOntologyFile("owlfiles/koala.owl");
        System.out.println("loading ontology: "+ontostr);
        System.out.println("Name Space : "+owl.nameSpace);
        
        for(OWLClass oc:owl.allOWLClasses)
        {
            System.out.println(oc.toString());
        }
        
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

        System.out.println("=====================Equivalent classes============================");
        String[] ec = owl.getEquivalentClasses("Student");
//        if (ec != null) {
//            for (String name : ec) {
//                System.out.println(name);
//            }
//        }

////        System.out.println(owl.superClasses);
        
        System.out.println("=====================Individuals============================");
        String[] in = owl.getIndividuals("Gender");
        if (in != null) {
            for (String s8 : in) {
                System.out.println(s8);
            }
        }
        
        
        
        System.out.println("=====================Object Property============================");
        String[] op = owl.getAllPropertiesByType(owl.TYPE_OBJECT_PROPERTY);
        
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

        
        System.out.println("=====================Data Property============================");
        
        String[] dp = owl.getAllPropertiesByType(owl.TYPE_DATA_PROPERTY);
        
        if(dp != null)
        {
            int i=0;
            for(String p: dp)
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
        
        
        System.out.println("=====================Axioms============================");
        
//        OWLClass cls = owl.myFactory.getOWLClass(owl.pm.getIRI("Animal"));
//        Set<OWLAxiom> set = cls.getReferencingAxioms(owl.myOntology);
//        
//        Set<OWLDeclarationAxiom> set = owl.myOntology.getAxioms(AxiomType.DECLARATION);
        Set<OWLAxiom> set = owl.myOntology.getAxioms();
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
        }
        System.out.println(i);
          
//          
//          if(ooa.getAxiomType().getName().equals(AxiomType.DECLARATION.getName()) )
//          {
//              System.out.println("============Declaration");i++;
//          }
        
//        OWLObjectProperty op = owl.myFactory.getOWLObjectProperty(owl.pm.getIRI("hasGender"));
        
        
        
//        System.out.println(op..getSignature().toArray(new OWLEntity[1])[0]);
        
    }
}
