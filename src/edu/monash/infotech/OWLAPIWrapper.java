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
    
    //types of property types
    public final String ENTITIY_TYPE_CLASS = "Class";
    public final String ENTITIY_TYPE_DATA_PROPERTY = "DataProperty";
    public final String ENTITIY_TYPE_OBJECT_PROPERTY = "ObjectProperty";
    public final String PROPERTY_TYPE_FUNCTIONAL = "Functional";
    public final String PROPERTY_TYPE_INVERSE = "Inverse";
    public final String PROPERTY_TYPE_INVERSE_FUNCTIONAL = "InverseFunctional";
    public final String PROPERTY_TYPE_SYMMETRIC = "Symmetric";
    public final String PROPERTY_TYPE_ASYMMETRIC = "Asymmetric";
    public final String PROPERTY_TYPE_TRANSITIVE = "Transitive";
    public final String PROPERTY_TYPE_REFLEXIVE = "Reflexive";
    public final String PROPERTY_TYPE_IRREFLEXIVE = "Irreflexive";
    public final String SPLITER = ":-=-:";
    //short for property types and entity types
    public final String ENTITIY_TYPE_CLASS_SHORT = "C";
    public final String ENTITIY_TYPE_DATA_PROPERTY_SHORT = "D";
    public final String ENTITIY_TYPE_OBJECT_PROPERTY_SHORT = "O";   
    public final String PROPERTY_TYPE_FUNCTIONAL_SHORT = "FU";
    public final String PROPERTY_TYPE_INVERSE_SHORT = "IN";
    public final String PROPERTY_TYPE_INVERSE_FUNCTIONAL_SHORT = "IF";
    public final String PROPERTY_TYPE_SYMMETRIC_SHORT = "SY";
    public final String PROPERTY_TYPE_ASYMMETRIC_SHORT = "AS";
    public final String PROPERTY_TYPE_TRANSITIVE_SHORT = "TR";
    public final String PROPERTY_TYPE_REFLEXIVE_SHORT = "RE";
    public final String PROPERTY_TYPE_IRREFLEXIVE_SHORT = "IR"; 
    
    private String defaultNameSpace;        
    private OWLDataFactory myFactory;
    private DefaultPrefixManager prefixManager;
    private OWLOntology myOntology;
    private OWLClass[] allOWLClasses;
    private OWLAxiom[] alDeclarationAxiom;

    //all short names
    private String[] allOWLClassNames;
    private String[] subClassNames;
    private String[] superClassNames;
    private String[] disjointClassNames;
    private String[] individualNames;
    private String[] allIndividualNames;
    
    // every 4 items make a small group information of one property. 
    //1-name 2-type 3-domain 4-range. 
    //if any item is not provied by ontology, one " " will be writen instead
    private String[] properties;    
    //format: 1->is DataProperty(D) or ObjectProperty(O), 
    //2->number(y) sub types does the property have(if it's 2, means this property has two sub types. eg: functional and symmetric)
    //3~X(X=(y+1)*2)-> shortcut of all sub types that this property has.eg:FU(functional) or FUSY(functional and symmetric)
    //the name of this property will be appended in the end
    private String[] propertyNameAndType;
    private String[] subPropertyNames;
    private String[] superPropertyNames;
    private String[] disjointPropertyNames;
    
     
//    private String[] equivalentClasses;
//    private String[] dataProperties;
//    private String[] oObjectProperties;
    

    //TODO 1.multiple namespaces 2.multiple superclasses 3.anonymous classes 
    //4.equivalent classes 5.property realationships 6.empty domain or range of property
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
            
//System.out.println("DefaultNameSpace: "+ooxnm.getDefaultNamespace());
//System.out.println("NameSpace: "+ooxnm.getNamespaces().toString());
//System.out.println("Prefix: "+ooxnm.getPrefixes().toString());
//System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

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
            defaultNameSpace = ooxnm.getDefaultNamespace();
            prefixManager = new DefaultPrefixManager(defaultNameSpace);
            
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
        allOWLClassNames = getShortNames(allOWLClasses);
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
        System.out.println(oceset.size()+this.defaultNameSpace);
        String str = expression[0].toString();
        System.out.println(str);
        str = str.replaceAll(this.defaultNameSpace, "");
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
        
        OWLNamedIndividual[] express = new OWLNamedIndividual[set.size()];
        set.toArray(express);

        this.individualNames = this.getShortNames(express);
        return this.individualNames;
    }
    
    public String[] getAllIndividuals()
    {
        Set<OWLNamedIndividual> set = this.myOntology.getIndividualsInSignature();
        OWLNamedIndividual[] oni = new OWLNamedIndividual[set.size()];
        set.toArray(oni);
        this.allIndividualNames = this.getShortNames(oni);
        return allIndividualNames;
    }
    
//    public String[] getClassesByIndividuals(String name)
//    {
//        OWLNamedIndividual indl = myFactory.getOWLNamedIndividual(this.prefixManager.getIRI(name));
////        Set<OWLClassExpression> set = indl..getNestedClassExpressions();
//        
//        System.out.println(indl.toString()+set.size()+set.toString());
//        return null;
//    }
    
    public String getDefaultNameSpace()
    {
        return this.defaultNameSpace;
    }
    
    public String getPropertyTypeByName(String entityType, String propertyName)
    {
        OWLProperty op = null;
        if(entityType.equals(this.ENTITIY_TYPE_DATA_PROPERTY))
        {
            op = this.myFactory.getOWLDataProperty(this.prefixManager.getIRI(propertyName));
        }else if(entityType.equals(this.ENTITIY_TYPE_OBJECT_PROPERTY))
        {
            op = this.myFactory.getOWLObjectProperty(this.prefixManager.getIRI(propertyName));
        }
        
        //format: 1->is DataProperty(D) or ObjectProperty(O), 
        //2->number(y) sub types does the property have(if it's 2, means this property has two sub types. eg: functional and symmetric)
        //3~X(X=(y+1)*2)-> shortcut of all sub types that this property has.eg:FU(functional) or FUSY(functional and symmetric)
        String propertyType = "";
        int count = 0;
        
        if(op.isObjectPropertyExpression())
        {
            OWLObjectProperty obp = (OWLObjectProperty) op;
            
            if(obp.isInverseFunctional(myOntology))
            {
                propertyType += this.PROPERTY_TYPE_INVERSE_FUNCTIONAL_SHORT;
                count++;
            }else{
                if(op.isFunctional(myOntology))
                {
                    propertyType += this.PROPERTY_TYPE_FUNCTIONAL_SHORT;
                    count++;
                }
                if(!(obp.getInverses(myOntology).isEmpty()))
                {
                    propertyType += this.PROPERTY_TYPE_INVERSE_SHORT;
                    count++;
                }
            }
            
            if(obp.isAsymmetric(myOntology))
            {
                propertyType += this.PROPERTY_TYPE_ASYMMETRIC_SHORT;
                count++;
            }
            if(obp.isIrreflexive(myOntology))
            {
                propertyType += this.PROPERTY_TYPE_IRREFLEXIVE_SHORT;
                count++;
            }
            if(obp.isReflexive(myOntology))
            {
                propertyType += this.PROPERTY_TYPE_REFLEXIVE_SHORT;
                count++;
            }
            if(obp.isSymmetric(myOntology))
            {
                propertyType += this.PROPERTY_TYPE_SYMMETRIC_SHORT;
                count++;
            }
            if(obp.isTransitive(myOntology))
            {
                propertyType += this.PROPERTY_TYPE_TRANSITIVE_SHORT;
                count++;            
            }           
            
            propertyType = this.ENTITIY_TYPE_OBJECT_PROPERTY_SHORT+count+propertyType;
        }else if(op.isDataPropertyExpression())
        {
            OWLDataProperty odp = (OWLDataProperty) op;
            if(odp.isFunctional(myOntology))
            {
                propertyType += this.PROPERTY_TYPE_FUNCTIONAL_SHORT;
                count++;
            }
            
            propertyType = this.ENTITIY_TYPE_DATA_PROPERTY_SHORT+count+propertyType;
        }
        return propertyType;
    }
    
    public String[] getPropertyDomainsByName(String entityType, String propertyName)
    {
        String[] domains = null;
        OWLProperty op = null;
        if(entityType.equals(this.ENTITIY_TYPE_DATA_PROPERTY))
        {
            op = this.myFactory.getOWLDataProperty(this.prefixManager.getIRI(propertyName));
        }else if(entityType.equals(this.ENTITIY_TYPE_OBJECT_PROPERTY))
        {
            op = this.myFactory.getOWLObjectProperty(this.prefixManager.getIRI(propertyName));
        }
        int i=0;
        Set<OWLClassExpression> set = op.getDomains(myOntology);
        domains = new String[set.size()];
        try{
            Iterator it = set.iterator();
            OWLClassExpression e = (OWLClassExpression) it.next();
            domains[i] = this.getClassShortName(e);

            while(it.hasNext())
            {
                i++;
                OWLClassExpression e2 = (OWLClassExpression) it.next();
                domains[i] = this.getClassShortName(e2);
            }
        }catch(Exception e)
        {
            domains = new String[]{""};
//            e.printStackTrace();
        }finally{            
//            domains = new String[]{""};
        }
        return domains;
    }
    
    public String[] getPropertyRangesByName(String entityType, String propertyName)
    {
        String[] ranges = null;
        OWLProperty op = null;
        if(entityType.equals(this.ENTITIY_TYPE_DATA_PROPERTY))
        {
            op = this.myFactory.getOWLDataProperty(this.prefixManager.getIRI(propertyName));
        }else if(entityType.equals(this.ENTITIY_TYPE_OBJECT_PROPERTY))
        {
            op = this.myFactory.getOWLObjectProperty(this.prefixManager.getIRI(propertyName));
        }
        
        Set set = op.getRanges(myOntology);
        ranges = new String[set.size()];
        try{
            Iterator it = set.iterator();
            int i=0;
            if(op.isObjectPropertyExpression())
            {
                ranges[i] = this.getClassShortName((OWLClassExpression)it.next());
                while(it.hasNext())
                {
                    i++;
                    ranges[i] = this.getClassShortName((OWLClassExpression)it.next());
                }
            }else if(op.isDataPropertyExpression())
            {
                ranges[i] = it.next().toString();
                while(it.hasNext())
                {
                    ranges[i] = it.next().toString();;
                }
            }
        }catch(Exception e)
        {
            
            ranges = new String[]{""};
//            e.printStackTrace();
        }finally{
        }
        return ranges;
    }
    
    public String[] getAllPropertiesNameAndSubType()
    {
        List list = new ArrayList();
        for(int i=0;i<this.alDeclarationAxiom.length;i++)
        {
            if(this.alDeclarationAxiom[i].toString().contains("("+this.ENTITIY_TYPE_OBJECT_PROPERTY))
            {
                int index_1 = alDeclarationAxiom[i].toString().indexOf(defaultNameSpace);
                int index_2 = alDeclarationAxiom[i].toString().indexOf(">");
                String propertyName = alDeclarationAxiom[i].toString().substring(index_1+defaultNameSpace.length(), index_2);
                String propertyType = this.getPropertyTypeByName(this.ENTITIY_TYPE_OBJECT_PROPERTY, propertyName);
                list .add(propertyType+propertyName);
            }
            if(this.alDeclarationAxiom[i].toString().contains("("+this.ENTITIY_TYPE_DATA_PROPERTY))
            {
                int index_1 = alDeclarationAxiom[i].toString().indexOf(defaultNameSpace);
                int index_2 = alDeclarationAxiom[i].toString().indexOf(">");
                String propertyName = alDeclarationAxiom[i].toString().substring(index_1+defaultNameSpace.length(), index_2);
                String propertyType = this.getPropertyTypeByName(this.ENTITIY_TYPE_DATA_PROPERTY, propertyName);
                list .add(propertyType+propertyName);
            }
        }
        this.propertyNameAndType = new String[list.size()];
        list.toArray(propertyNameAndType);
        return propertyNameAndType;
    }
    
    private OWLProperty createProperty(String type, String name)
    {
        OWLProperty op = null;
        if(type.equals(this.ENTITIY_TYPE_DATA_PROPERTY))
        {
            op = this.myFactory.getOWLDataProperty(this.prefixManager.getIRI(name));
        }else if(type.equals(this.ENTITIY_TYPE_OBJECT_PROPERTY))
        {
            op = this.myFactory.getOWLObjectProperty(this.prefixManager.getIRI(name));
        }
        return op;
    }
    
    public String[] getSubProperites(String type, String name)
    {
        OWLProperty op = this.createProperty(type, name);        
        Set set = op.getSubProperties(myOntology);
        OWLProperty[] ops = new OWLProperty[set.size()];
        set.toArray(ops);
        this.subPropertyNames = new String[set.size()];
        this.subPropertyNames = this.getShortNames(ops);
        return subPropertyNames;
    }
    
    public String[] getSuperProperites(String type, String name)
    {
        OWLProperty op = this.createProperty(type, name);        
        Set set = op.getSuperProperties(myOntology);
        OWLProperty[] ops = new OWLProperty[set.size()];
        set.toArray(ops);
        this.superPropertyNames = new String[set.size()];
        this.superPropertyNames = this.getShortNames(ops);
        return superPropertyNames;
    }
    
    public String[] getDisjointProperties(String type, String name)
    {
        OWLProperty op = this.createProperty(type, name);        
        Set set = op.getDisjointProperties(myOntology);
        OWLProperty[] ops = new OWLProperty[set.size()];
        set.toArray(ops);
        this.disjointPropertyNames = new String[set.size()];
        this.disjointPropertyNames = this.getShortNames(ops);
        return disjointPropertyNames;
    }
    
    //TODO getEquivalentProperties()
    public String[] getEquivalentProperties(String type, String name)
    {
        return null;
    }
    
    private String getPropertySubType(OWLProperty op)
    {
        String propertyType = "";
        
        if(op.isObjectPropertyExpression())
        {
            OWLObjectProperty obp = (OWLObjectProperty) op;
            propertyType = this.ENTITIY_TYPE_OBJECT_PROPERTY;
            
            if(op.isFunctional(myOntology))
            {
                propertyType = this.PROPERTY_TYPE_FUNCTIONAL + propertyType;
            }else if(obp.isAsymmetric(myOntology))
            {
                propertyType = this.PROPERTY_TYPE_ASYMMETRIC + propertyType;
            }else if(obp.isInverseFunctional(myOntology))
            {
                propertyType = this.PROPERTY_TYPE_INVERSE_FUNCTIONAL + propertyType;
            }else if(obp.isIrreflexive(myOntology))
            {
                propertyType = this.PROPERTY_TYPE_IRREFLEXIVE + propertyType;
            }else if(obp.isReflexive(myOntology))
            {
                propertyType = this.PROPERTY_TYPE_REFLEXIVE + propertyType;
            }else if(obp.isSymmetric(myOntology))
            {
                propertyType = this.PROPERTY_TYPE_SYMMETRIC + propertyType;
            }else if(obp.isTransitive(myOntology))
            {
                propertyType = this.PROPERTY_TYPE_TRANSITIVE + propertyType;            
            }else if(!(obp.getInverses(myOntology).isEmpty()))
            {
                propertyType = this.PROPERTY_TYPE_INVERSE + propertyType;
            }
        }else if(op.isDataPropertyExpression())
        {
            OWLDataProperty odp = (OWLDataProperty) op;
            propertyType = this.ENTITIY_TYPE_DATA_PROPERTY;
            if(odp.isFunctional(myOntology))
            {
                propertyType = this.PROPERTY_TYPE_FUNCTIONAL + propertyType;
            }
        }
        return propertyType;
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
        if(propertyType.equals(this.ENTITIY_TYPE_DATA_PROPERTY))
        {
            op = this.myFactory.getOWLDataProperty(this.prefixManager.getIRI(propertyName));
        }else if(propertyType.equals(this.ENTITIY_TYPE_OBJECT_PROPERTY))
        {
            op = this.myFactory.getOWLObjectProperty(this.prefixManager.getIRI(propertyName));
        }
        
//        info[0] = this.getPropertySubType(op);
//        info[1] = this.getPropertyDomains(op);
//        info[2] = this.getPropertyRanges(op); 
        info[0] = this.getPropertyTypeByName(propertyType,propertyName);
        info[1] = this.getPropertyDomainsByName(propertyType,propertyName)[0];
        info[2] = this.getPropertyRangesByName(propertyType,propertyName)[0];
        return info;
    }
    
    public String[] getAllPropertiesByType(String type)
    {
        List list = new ArrayList();
        for(int i=0;i<this.alDeclarationAxiom.length;i++)
        {
            if(this.alDeclarationAxiom[i].toString().contains("("+type))
            {
                int index_1 = alDeclarationAxiom[i].toString().indexOf(defaultNameSpace);
                int index_2 = alDeclarationAxiom[i].toString().indexOf(">");
                String propertyName = alDeclarationAxiom[i].toString().substring(index_1+defaultNameSpace.length(), index_2);
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
    
    public String[] getAllDataProperties() {
        return this.getAllPropertiesByType(this.ENTITIY_TYPE_DATA_PROPERTY);
    }

    public String[] getObjectProperties() {
        return this.getAllPropertiesByType(this.ENTITIY_TYPE_OBJECT_PROPERTY);
    }
    
    private String[] getShortNames(OWLEntity[] ents)
    {
        String[] temp = new String[ents.length];
        for(int i=0;i<ents.length;i++)
        {
            String shortName = prefixManager.getShortForm(ents[i]);
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
    
    //TODO anonymous class
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

    public OWLClass[] getAllOWLClass() {
        return allOWLClasses;
    }    
    
}
