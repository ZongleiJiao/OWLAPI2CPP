/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.monash.infotech.db;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;
import com.sun.org.apache.bcel.internal.generic.GETSTATIC;
import edu.monash.infotech.OWLAPIWrapper;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.semanticweb.owlapi.model.*;

/**
 *
 * @author ZongleiJiao
 */
public class OWLAPI2DB {

    private SQLiteConnection dbConnection;
    private SQLiteStatement st;
    private OntologyLoader loader;
    private String lastModifiedTime;
    private String currentTime;
    private int baseOntologyID;
    
    private final int TYPE_CLASS = 1;
    private final int TYPE_INDIVIDUAL = 2;
    private final int TYPE_PROPERTY = 3;
    private final int TYPE_ANNOTATION = 4;

    public OWLAPI2DB(String dbFile, String owlFile) {
        File db = new File(dbFile);
        File owl = new File(owlFile);
        dbConnection = new SQLiteConnection(db);
        loader = new OntologyLoader(owlFile);
        
        SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd H:MM");
        Date resultdate = new Date(owl.lastModified());
        lastModifiedTime = date_format.format(resultdate);
        
        Date now = new Date();
        currentTime = date_format.format(now);
        
        try {
            dbConnection.open();
        } catch (SQLiteException ex) {
            Logger.getLogger(OWLAPI2DB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void closeConnection() {
        if (this.dbConnection != null) {
            this.dbConnection.dispose();
        }
    }
/*7
    Create Table Ontologies(
	[ontologyid]		integer PRIMARY KEY autoincrement, 
	[name]			varchar(100),
	[namespace]		varchar(200),
	[URL]			varchar(300),
	[CreatedTime]		text,   --YYYY-MM-DD HH:MM
	[LastModifiedTime]	text,   --YYYY-MM-DD HH:MM
	[Description]		varchar(500),
	[NumOfClass]		integer,
	[NumOfIndividual]	integer,
	[NumOfProperty]		integer	
    );
    * 
    */
    public void saveOntology(OWLOntology thisOntology) {
        if (!this.isExistingOntology(thisOntology)) {
            try {
                String sql = "insert into Ontologies(name,namespace,URL,CreatedTime,"
                        + "LastModifiedTime,Description,NumOfClass,NumOfIndividual,"
                        + "NumOfProperty) values (?,?,?,?,?,?,?,?,?)";
                st = dbConnection.prepare(sql);

                st.bind(1, loader.getOntologyName(thisOntology));
                st.bind(2, loader.getOntologyNameSpace(thisOntology));
                st.bind(3, loader.getOntologyURI(thisOntology));
                st.bind(4, currentTime);
                st.bind(5, lastModifiedTime);
                st.bind(6, "NO Description");
                st.bind(7, loader.getNumOfClass(thisOntology));
                st.bind(8, loader.getNumOfIndividuals(thisOntology));
                st.bind(9, loader.getNumOfProperties(thisOntology));

                st.step();

                st.dispose();
            } catch (SQLiteException ex) {
                Logger.getLogger(OWLAPI2DB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private boolean isExistingOntology(OWLOntology thisOntology){
        boolean flag = false;
        
        try {
            String sql = "select * from Ontologies where URL = ?";
            st = this.dbConnection.prepare(sql);
            st.bind(1, loader.getOntologyURI(thisOntology));
            st.step();
            flag = st.hasRow();
            st.dispose();
            
            return flag;
        } catch (SQLiteException ex) {
            Logger.getLogger(OWLAPI2DB.class.getName()).log(Level.SEVERE, null, ex);
            return flag;
        }
    }

    private int getCurrentOntologyID() {
        try {
            String sql = "select max(ontologyid) from ontologies";
            st = this.dbConnection.prepare(sql);
            st.step();
            int id = st.columnInt(0);
            st.dispose();
            
            return id;
        } catch (SQLiteException ex) {
            Logger.getLogger(OWLAPI2DB.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }
    
    /*
    Create Table OntologyImports(
	[Ontologyid]	integer, 
	[ImportID]	integer ----- reference ontologyID from table Ontologies or import table
	); 
    
    * 
    */
    public void saveImports() {
        baseOntologyID = this.getOntologyIDByURI(loader.getOntologyURI(loader.myOntology));
        OWLOntology thisOntology = loader.myOntology;

        Set<OWLOntology> imports = loader.getImportedOntologies(thisOntology);
        Iterator<OWLOntology> it = imports.iterator();
        while (it.hasNext()) {
            OWLOntology onto = it.next();
            if (!this.isExistingOntology(onto)) {
                this.saveOntology(onto);
                int id = this.getCurrentOntologyID();
                this.saveImportOntology(id);

            }else{
                int id = this.getOntologyIDByURI(loader.getOntologyURI(onto));
                if(!this.isExistingImports(baseOntologyID, id)){
                    this.saveImportOntology(id);
                }
            }
        }
    }
    
    private boolean isExistingImports(int baseID, int importID){
        boolean flag = false;
        
        try {
            String sql = "select * from OntologyImports where Ontologyid = ? and ImportID = ?";
            st = this.dbConnection.prepare(sql);
            st.bind(1, baseID);
            st.bind(2, importID);
            st.step();
            flag = st.hasRow();
            st.dispose();
            
            return flag;
        } catch (SQLiteException ex) {
            Logger.getLogger(OWLAPI2DB.class.getName()).log(Level.SEVERE, null, ex);
            return flag;
        }
    }

    private void saveImportOntology(int currentID) {
        try {
            String sql = "insert into OntologyImports values (?,?)";
            st = dbConnection.prepare(sql);

            st.bind(1, baseOntologyID);
            st.bind(2, currentID);

            st.step();

            st.dispose();
        } catch (SQLiteException ex) {
            Logger.getLogger(OWLAPI2DB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private int getOntologyIDByURI(String URI){
        try {
            String sql = "select ontologyid from ontologies where URL = ?";
            st = this.dbConnection.prepare(sql);
            st.bind(1, URI);
            st.step();
//            System.out.println(sql);
            int id = st.columnInt(0);
            st.dispose();
            
            return id;
        } catch (SQLiteException ex) {
            Logger.getLogger(OWLAPI2DB.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }
    private int getOntologyIDByNamespace(String namespace){
        try {
            String sql = "select ontologyid from ontologies where namespace = ?";
            st = this.dbConnection.prepare(sql);
            st.bind(1, namespace);
            int id = -1;
            while(st.step())
            {
                id = st.columnInt(0);
            }
            st.dispose();
            
            return id;
        } catch (SQLiteException ex) {
            Logger.getLogger(OWLAPI2DB.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }
    /*
    Create Table Entities(
	[EntityID]	integer PRIMARY KEY autoincrement, 
	[OntologyID]	integer,
	[Type]		integer,	-- (class-1,individual-2,property-3,annotation-4)
	[isAnonymous]	INTEGER         -- 0-no , 1- yes
    );
    Create Table Classes(
	[EntityID]	INTEGER REFERENCES Entities(EntityID), --foreign key
	[OntologyID]	integer,
	[URI]		varchar(300),
	[shortname]	varchar(100),
	[namespace]	varchar(200),
    );
    */
    public void saveAllClasses(OWLOntology thisOntology){
        
        String URI = loader.getOntologyURI(thisOntology);
        int currentOntologyID = this.getOntologyIDByURI(URI);
        Set<OWLClass> classes = loader.getAllClasses(thisOntology);
        Iterator<OWLClass> it = classes.iterator();
        while(it.hasNext()){
            OWLClass cls = it.next();
            String clsURI = loader.getClassURI(cls);
            int id = this.getClassIDByURI(clsURI);
            if(id < 0){
                this.saveEntityClass(currentOntologyID, cls);
                int entityID = this.getCurrentEntityID();
                this.saveClass(currentOntologyID, entityID, cls);
            }
            //find all individuals of this class and save it along with the relations between them
            this.saveIndividualsOfOneClass(thisOntology, cls);
        }
        for(OWLClass clz:classes){
            //save sub class realtionships
            this.saveSubClassRelations(clz);
            
            //save disjoint class relationships
            this.saveDisjointClassRelations(clz);
            
            //save equivalent class relationships
            this.saveEquivalentClassRelations(clz);
        }
    }
    
    
    private void saveEntityClass(int ontologyID, OWLClass cls){
        try {
            String sql = "insert into Entities (OntologyID, Type, isAnonymous) values (?,?,?)";
            st = dbConnection.prepare(sql);
            st.bind(1, ontologyID);
            st.bind(2, this.TYPE_CLASS);
            st.bind(3, cls.isAnonymous()?1:0);
            st.step();
            st.dispose();
            
        } catch (SQLiteException ex) {
            Logger.getLogger(OWLAPI2DB.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    private int getCurrentEntityID(){
        try {
            String sql = "select max(EntityID) from Entities";
            st = this.dbConnection.prepare(sql);
            st.step();
            int id = st.columnInt(0);
            st.dispose();
            return id;
        } catch (SQLiteException ex) {
            Logger.getLogger(OWLAPI2DB.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }
    
    private void saveClass(int ontologyID, int entitiyID, OWLClass cls){
        try {
            String sql = "insert into Classes values (?,?,?,?,?)";
            st = dbConnection.prepare(sql);
            st.bind(1, entitiyID);
            st.bind(2, ontologyID);
            st.bind(3, loader.getClassURI(cls));
            st.bind(4, loader.getClassName(cls));
            st.bind(5, loader.getClassNameSpace(cls));
            st.step();
            st.dispose();
            
        } catch (SQLiteException ex) {
            Logger.getLogger(OWLAPI2DB.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    private int getClassIDByURI(String URI){
        try {
            String sql = "select EntityID from Classes where URI = ?";
            st = this.dbConnection.prepare(sql);
            st.bind(1, URI);
            int id = -1;
            while(st.step())
            {
                id = st.columnInt(0);
            }
            st.dispose();
            return id;
        } catch (SQLiteException ex) {
            Logger.getLogger(OWLAPI2DB.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }
    
    private int getOntologyIDByEntityID(int entID){
        try {
            String sql = "select OntologyID from Entities where EntityID = ?";
            st = this.dbConnection.prepare(sql);
            st.bind(1, entID);
            st.step();
            int id = st.columnInt(0);
            st.dispose();
            return id;
        } catch (SQLiteException ex) {
            Logger.getLogger(OWLAPI2DB.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }
    
        
    private void saveIndividualsOfOneClass(OWLOntology thisOntology, OWLClass cls) {
        String URI = loader.getOntologyURI(thisOntology);
        int currentOntologyID = this.getOntologyIDByURI(URI);
        int clzid = this.getClassIDByURI(loader.getClassURI(cls));

        Set<OWLIndividual> individuals = loader.getIndividuals(cls);
        if (individuals != null && individuals.size() > 0) {
            Iterator<OWLIndividual> it = individuals.iterator();
            while (it.hasNext()) {
                OWLIndividual oindividual = it.next();
                OWLNamedIndividual individual = oindividual.asOWLNamedIndividual();

                if (!this.isExistingIndividual(loader.getIndividualURI(individual))) {
                    this.saveEntityIndividual(currentOntologyID, individual);
                    int entityID = this.getCurrentEntityID();
                    this.saveIndividual(currentOntologyID, entityID, individual);

                    this.saveIndividualClassRelation(entityID, clzid);
                } else {
                    int id = this.getIndividualIDByURI(loader.getIndividualURI(individual));
                    if (!this.isExistingIndiClassRelation(id, clzid)) {
                        this.saveIndividualClassRelation(id, clzid);
                    }
                }

            }
        }
    }
    
    /*
     Create Table subSumption(
	[ParentID]	integer,
	[ChildID]	integer
     );
     */
    private void saveSubClassRelations(OWLClass cls){
        int clsID = this.getClassIDByURI(loader.getClassURI(cls));
        //get sub classes of this class, no deal with anonymout class right now
        Set<OWLClassExpression> subset = loader.getSubClasses(cls);
        if(subset != null && subset.size() > 0){
            Iterator<OWLClassExpression> it = subset.iterator();
            while(it.hasNext()){
                OWLClassExpression clsa = it.next();
                
                if(!clsa.isAnonymous()){
                    OWLClass clza = clsa.asOWLClass();
                    int clzID = this.getClassIDByURI(loader.getClassURI(clza));
                    //check wether the realtion is existing. if not, save it
                    if(!isExistingSubClassRelation(clsID,clzID)){
                        this.saveSubClassRelation(clsID, clzID);
                    }
                }else{
                    
                    String expression = loader.removeNameSpace(clsa.toString());
                    
                    if(this.isExistingAnonymousClass(expression)){
                        int anonID = this.getAnonymousClassIDByExpression(expression);
                        if(!this.isExistingSubClassRelation(clsID, anonID)){
                            this.saveDisjointClassRelation(clsID, anonID);
                        }
                    }else{
                        //save new anonymous class entity
                        int ontologyID = getOntologyIDByEntityID(clsID);
                        saveEntityAnonymousClass(ontologyID);
                        int entityID = this.getCurrentEntityID();
                        //save new anonymous class expression
                        this.saveAnonymousClassExpression(entityID, expression);
                        //save new relation
                        this.saveSubClassRelation(clsID, entityID);
                    }
                    
                }
            }
        }
        
        //get super classes of this class, no deal with anonymout class right now
        Set<OWLClassExpression> superSet = loader.getSuperClasses(cls);
        if(superSet != null && superSet.size() > 0){
            Iterator<OWLClassExpression> itb = superSet.iterator();
            while(itb.hasNext()){
                OWLClassExpression clsb = itb.next();
                if(!clsb.isAnonymous()){
                    OWLClass clzb = clsb.asOWLClass();
                    int clzbID = this.getClassIDByURI(loader.getClassURI(clzb));
                    //check wether the realtion is existing. if not, save it
                    if(!this.isExistingSubClassRelation(clzbID, clsID)){
                        this.saveSubClassRelation(clzbID, clsID);
                    }
                }else{
                    String expression = loader.removeNameSpace(clsb.toString());
                    
                    if(this.isExistingAnonymousClass(expression)){
                        int anonID = this.getAnonymousClassIDByExpression(expression);
                        if(!this.isExistingSubClassRelation(anonID, clsID)){
                            this.saveDisjointClassRelation(anonID, clsID);
                        }
                    }else{
                        //save new anonymous class entity
                        int ontologyID = getOntologyIDByEntityID(clsID);
                        saveEntityAnonymousClass(ontologyID);
                        int entityID = this.getCurrentEntityID();
                        //save new anonymous class expression
                        this.saveAnonymousClassExpression(entityID, expression);
                        //save new relation
                        this.saveSubClassRelation(entityID, clsID);
                    }
                }
            }
        }
    }
    
    private boolean isExistingSubClassRelation(int FID, int CID) {
        boolean flag = false;

        try {
            String sql = "select * from subSumption where ParentID = ? and ChildID = ?";
            st = this.dbConnection.prepare(sql);
            st.bind(1, FID);
            st.bind(2, CID);
            st.step();
            flag = st.hasRow();
            st.dispose();
            return flag;
        } catch (SQLiteException ex) {
            Logger.getLogger(OWLAPI2DB.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

    }
    
    private void saveSubClassRelation(int FID, int CID){
        try {
            String sql = "insert into subSumption values (?,?)";
            st = dbConnection.prepare(sql);
            st.bind(1, FID);
            st.bind(2, CID);
            st.step();
            st.dispose();
            
        } catch (SQLiteException ex) {
            Logger.getLogger(OWLAPI2DB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /*
     Create Table DisjointClasses(
	[ClassID]		integer,
	[DisjointClassID] integer
    );
    
     */
    private void saveDisjointClassRelations(OWLClass cls){
        int clsID = this.getClassIDByURI(loader.getClassURI(cls));
        Set<OWLClassExpression> disClass = loader.getDisjointClasses(cls);
        if(disClass != null && disClass.size() > 0){
            Iterator<OWLClassExpression> it = disClass.iterator();
            while(it.hasNext()){
                OWLClassExpression clse = it.next();
                if(!clse.isAnonymous()){
                    OWLClass clz = clse.asOWLClass();
                    int clzID = this.getClassIDByURI(loader.getClassURI(clz));
                    if(!this.isExistingDisjointClassRelation(clsID, clzID)){
                        this.saveDisjointClassRelation(clsID, clzID);
                    }
                }else{                    
                    String expression = loader.removeNameSpace(clse.toString());
                    
                    if(this.isExistingAnonymousClass(expression)){
                        int anonID = this.getAnonymousClassIDByExpression(expression);
                        if(!this.isExistingDisjointClassRelation(clsID, anonID)){
                            this.saveDisjointClassRelation(clsID, anonID);
                        }
                    }else{
                        //save new anonymous class entity
                        int ontologyID = getOntologyIDByEntityID(clsID);
                        saveEntityAnonymousClass(ontologyID);
                        int entityID = this.getCurrentEntityID();
                        //save new anonymous class expression
                        this.saveAnonymousClassExpression(entityID, expression);
                        //save new relation
                        this.saveDisjointClassRelation(clsID, entityID);
                    }
                }
                
            }
        }
    }
    
    private boolean isExistingDisjointClassRelation(int ClsID, int DisClsID){
        boolean flag = false;
        try {
            String sql = "select * from DisjointClasses where ClassID = ? and DisjointClassID = ?";
            st = this.dbConnection.prepare(sql);
            st.bind(1, ClsID);
            st.bind(2, DisClsID);
            st.step();
            flag = st.hasRow();
            st.dispose();
            return flag;
        } catch (SQLiteException ex) {
            Logger.getLogger(OWLAPI2DB.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    private void saveDisjointClassRelation(int ClsID, int DisClsID){
        try {
            String sql = "insert into DisjointClasses (ClassID,DisjointClassID) values (?,?)";
            st = dbConnection.prepare(sql);
            st.bind(1, ClsID);
            st.bind(2, DisClsID);
            st.step();
            st.dispose();
            
        } catch (SQLiteException ex) {
            Logger.getLogger(OWLAPI2DB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /*
     * Create Table EquivalentClasses(
	[ClassID]	integer,
	[EquivalentClassID] integer
       );
     */
    private void saveEquivalentClassRelations(OWLClass cls) {
        int clsID = this.getClassIDByURI(loader.getClassURI(cls));
        Set<OWLClassExpression> equClass = loader.getEquivalentClasses(cls);
        if(equClass != null && equClass.size() > 0){
            Iterator<OWLClassExpression> it = equClass.iterator();
            while(it.hasNext()){
                OWLClassExpression ce = it.next();
                if(!ce.isAnonymous()){
                    OWLClass clz = ce.asOWLClass();
                    int clzID = this.getClassIDByURI(loader.getClassURI(clz));
                    if(!this.isExistingEquivlaentClassRelation(clsID, clzID)){
                        this.saveEquivlaentClassRelation(clsID, clzID);
                    }
                }else{
                    String expression = loader.removeNameSpace(ce.toString());
                    
                    //check if this anonymous class expresion is existing, if true, then get its entityID to check whether this relation exist
                    // if false, save this as a new anonymous expression
                    if(this.isExistingAnonymousClass(expression)){
                        int anonID = this.getAnonymousClassIDByExpression(expression);
                        if(!this.isExistingEquivlaentClassRelation(clsID, anonID)){
                            this.saveEquivlaentClassRelation(clsID, anonID);
                        }
                    }else{
                        //save new anonymous class entity
                        int ontologyID = getOntologyIDByEntityID(clsID);
                        saveEntityAnonymousClass(ontologyID);
                        int entityID = this.getCurrentEntityID();
                        //save new anonymous class expression
                        this.saveAnonymousClassExpression(entityID, expression);
                        //save new relation
                        this.saveEquivlaentClassRelation(clsID, entityID);
                    }
                    
                }
            }
        }
    }
    
    private boolean isExistingEquivlaentClassRelation(int clsID, int equClsID){
        boolean flag = false;
        try {
            String sql = "select * from EquivalentClasses where ClassID = ? and EquivalentClassID = ?";
            st = this.dbConnection.prepare(sql);
            st.bind(1, clsID);
            st.bind(2, equClsID);
            st.step();
            flag = st.hasRow();
            st.dispose();
            return flag;
        } catch (SQLiteException ex) {
            Logger.getLogger(OWLAPI2DB.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    private void saveEquivlaentClassRelation(int clsID, int equClsID){
        try {
            String sql = "insert into EquivalentClasses (ClassID,EquivalentClassID) values (?,?)";
            st = dbConnection.prepare(sql);
            st.bind(1, clsID);
            st.bind(2, equClsID);
            st.step();
            st.dispose();
            
        } catch (SQLiteException ex) {
            Logger.getLogger(OWLAPI2DB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
//    private boolean isExistingEquivlaentClassRelation(int clsID, String Expression){
//        boolean flag = false;
//        try {
//            String sql = "select * from EquivalentClasses where ClassID = ? and Expression = ?";
//            st = this.dbConnection.prepare(sql);
//            st.bind(1, clsID);
//            st.bind(2, Expression);
//            st.step();
//            flag = st.hasRow();
//            st.dispose();
//            return flag;
//        } catch (SQLiteException ex) {
//            Logger.getLogger(OWLAPI2DB.class.getName()).log(Level.SEVERE, null, ex);
//            return false;
//        }
//    }
    
    
    
//    private void saveEquivlaentClassRelation(int clsID, String Expression){
//        try {
//            String sql = "insert into EquivalentClasses (ClassID,Expression) values (?,?)";
//            st = dbConnection.prepare(sql);
//            st.bind(1, clsID);
//            st.bind(2, Expression);
//            st.step();
//            st.dispose();
//            
//        } catch (SQLiteException ex) {
//            Logger.getLogger(OWLAPI2DB.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    
    /*
    Create table AnonymousClasses(
	[EntityID]	INTEGER REFERENCES Entities(EntityID), --foreign key
	[AnonymousType]	integer,---------What is this??????????
	[AnonymousExpression]	varchar(1000)
    );
    * 
    */
    private boolean isExistingAnonymousClass(String expression){
        boolean flag = false;
        try {
            String sql = "select * from AnonymousClasses where AnonymousExpression = ?";
            st = this.dbConnection.prepare(sql);
            st.bind(1, expression);
            st.step();
            flag = st.hasRow();
            st.dispose();
            return flag;
        } catch (SQLiteException ex) {
            Logger.getLogger(OWLAPI2DB.class.getName()).log(Level.SEVERE, null, ex);
            return flag;
        }
    }
    
    private int getAnonymousClassIDByExpression(String expression){
        try {
            String sql = "select EntityID from AnonymousClasses where AnonymousExpression = ?";
            st = this.dbConnection.prepare(sql);
            st.bind(1, expression);
            st.step();
            int id = st.columnInt(0);
            st.dispose();
            return id;
        } catch (SQLiteException ex) {
            Logger.getLogger(OWLAPI2DB.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }
    
    private void saveEntityAnonymousClass(int ontologyID){
        try {
            String sql = "insert into Entities (OntologyID, Type, isAnonymous) values (?,?,?)";
            st = dbConnection.prepare(sql);
            st.bind(1, ontologyID);
            st.bind(2, this.TYPE_CLASS);
            st.bind(3, 1);
            st.step();
            st.dispose();
            
        } catch (SQLiteException ex) {
            Logger.getLogger(OWLAPI2DB.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
        
    
    private void saveAnonymousClassExpression(int entityID, String expression){
        try {
            String sql = "insert into AnonymousClasses values (?,?,?)";
            st = dbConnection.prepare(sql);
            st.bind(1, entityID);
            st.bind(2, 1);
            st.bind(3, expression);
            st.step();
            st.dispose();
            
        } catch (SQLiteException ex) {
            Logger.getLogger(OWLAPI2DB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
            
    /*
    Create Table Individuals(
	[EntityID]	INTEGER REFERENCES Entities(EntityID),
	[OntologyID]	integer,
	[URI]		varchar(300),
	[name]		varchar(100)		
    );
    
    * 
    */
//    public void saveIndividuals(OWLOntology thisOntology){
//        String URI = loader.getOntologyURI(thisOntology);
//        int currentOntologyID = this.getOntologyIDByURI(URI);
//        Set<OWLNamedIndividual> individuals = loader.getAllIndividuals(thisOntology);
//        Iterator<OWLNamedIndividual> it = individuals.iterator();
//        while(it.hasNext()){
//            OWLNamedIndividual individual = it.next();
//            this.saveEntityIndividual(currentOntologyID, individual);
//            int entityID = this.getCurrentEntityID();
//            this.saveIndividual(currentOntologyID, entityID, individual);
//            Set<OWLClass> cls = individual.getClassesInSignature();
//            
//            Iterator<OWLClass> itt = cls.iterator();
//            while(itt.hasNext()){
//                OWLClass clz =  itt.next();
//                int clzid = this.getClassIDByURI(loader.getClassURI(clz));
//                this.saveIndividualClassRelation(entityID, clzid);
//            }
//        }
//    }
    
    private void saveEntityIndividual(int ontologyID, OWLNamedIndividual individual){
        try {
            String sql = "insert into Entities (OntologyID, Type, isAnonymous) values (?,?,?)";
            st = dbConnection.prepare(sql);
            st.bind(1, ontologyID);
            st.bind(2, this.TYPE_INDIVIDUAL);
            st.bind(3, individual.isAnonymous()?1:0);
            st.step();
            st.dispose();
            
        } catch (SQLiteException ex) {
            Logger.getLogger(OWLAPI2DB.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    private boolean isExistingIndividual(String URI){
        boolean flag = false;
        try {
            String sql = "select EntityID from Individuals where URI = ?";
            st = this.dbConnection.prepare(sql);
            st.bind(1, URI);
            st.step();
            flag = st.hasRow();
            st.dispose();
            return flag;
        } catch (SQLiteException ex) {
            Logger.getLogger(OWLAPI2DB.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    private int getIndividualIDByURI(String URI){
        try {
            String sql = "select * from Individuals where URI = ?";
            st = this.dbConnection.prepare(sql);
            st.bind(1, URI);
            st.step();
            int id = st.columnInt(0);
            st.dispose();
            return id;
        } catch (SQLiteException ex) {
            Logger.getLogger(OWLAPI2DB.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }
    
    private void saveIndividual(int ontologyID, int entitiyID, OWLNamedIndividual individual){
        try {
            String sql = "insert into Individuals values (?,?,?,?)";
            st = dbConnection.prepare(sql);
            st.bind(1, entitiyID);
            st.bind(2, ontologyID);
            st.bind(3, loader.getIndividualURI(individual));
            st.bind(4, loader.getIndividualName(individual));
            st.step();
            st.dispose();
            
        } catch (SQLiteException ex) {
            Logger.getLogger(OWLAPI2DB.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    /*
    Create Table IndividualRelations(
	[IndividualID]	integer, --reference entityid
	[ClassID]	integer --reference entityid
    );
    * 
    */
    public void saveIndividualClassRelation(int individualID, int classID){
        try {
            String sql = "insert into IndividualRelations values (?,?)";
            st = dbConnection.prepare(sql);
            st.bind(1, individualID);
            st.bind(2, classID);
            st.step();
            st.dispose();
            
        } catch (SQLiteException ex) {
            Logger.getLogger(OWLAPI2DB.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    private boolean isExistingIndiClassRelation(int indiID, int classID){
        boolean flag = false;
        try {
            String sql = "select * from IndividualRelations where IndividualID = ? and ClassID = ?";
            st = this.dbConnection.prepare(sql);
            st.bind(1, indiID);
            st.bind(2, classID);
            st.step();
            flag = st.hasRow();
            st.dispose();
            return flag;
        } catch (SQLiteException ex) {
            Logger.getLogger(OWLAPI2DB.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    /*
     * Create Table Properties(
	[EntityID]	INTEGER REFERENCES Entities(EntityID),
	[OntologyID]	integer,
	[URI]		varchar(300),
	[shortname]	varchar(100),
	[typeID]		integer,
	[encodedPropertyNameAndType]	varchar(300),
	[DataRange]	varchar(100)		
        );
     */
    private void saveOneProperty(int entityID, int ontoID, String URI, String name, int typeID){
        try {
            String sql = "INSERT INTO Properties (EntityID, OntologyID, URI, "
                    + "shortname, typeID) "
                    + "VALUES (?,?,?,?,?)";
            st = dbConnection.prepare(sql);
            st.bind(1, entityID);
            st.bind(2, ontoID);
            st.bind(3, URI);
            st.bind(4, name);
            st.bind(5, typeID);
            st.step();
            st.dispose();
            
        } catch (SQLiteException ex) {
            Logger.getLogger(OWLAPI2DB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void saveAllProperties(OWLOntology ontology){
                
        Set<OWLObjectProperty> objPro = loader.getAllObjectProperties(loader.myOntology);
        Iterator<OWLObjectProperty> it = objPro.iterator();
        while(it.hasNext()){
            OWLObjectProperty op = it.next();
            int typeID = this.getPropertyTypeID(op);
            if(typeID < 0){
                //save a new type
                this.savePropertyType(op);
                //get new id
                typeID = this.getPropertyTypeID(op);
            }
            
            String URI = loader.getPropertyURI(op);   
            String namespace = loader.getPropertyNameSpace(op);
            int ontoID = this.getOntologyIDByNamespace(namespace);
            //TODO one ontology has multiple ns, now just reference to the new added one
            if(ontoID < 0){
                System.out.println(URI);
                System.out.println(namespace);
                ontoID = this.getOntologyIDByURI(loader.getOntologyURI(loader.myOntology));
            }
            String name = loader.getPropertyName(op);
            //save a new property
            if (this.getPropertyIDByURI(URI) < 0) {
                this.saveEntityProperty(ontoID, op);
                int entityID = this.getCurrentEntityID();
                this.saveOneProperty(entityID, ontoID, URI, name, typeID);
            }
        }
        
        
        Set<OWLDataProperty> dataPro = loader.getAllDataProperties(loader.myOntology);
        Iterator<OWLDataProperty> itt = dataPro.iterator();
        while(itt.hasNext()){
            OWLDataProperty dp = itt.next();
            int typeID = this.getPropertyTypeID(dp);
            if(typeID < 0){
                //save a new type
                this.savePropertyType(dp);
                typeID = this.getPropertyTypeID(dp);
            }
//            System.out.println("===="+typeID);
            //save a new property
            String URI = loader.getPropertyURI(dp);            
            String namespace = loader.getPropertyNameSpace(dp);
            int ontoID = this.getOntologyIDByNamespace(namespace);
            //TODO one ontology has multiple ns, now just reference to the new added one
            if(ontoID < 0){
                System.out.println(URI);
                System.out.println(namespace);
                ontoID = this.getOntologyIDByURI(loader.getOntologyURI(loader.myOntology));
            }
            String name = loader.getPropertyName(dp);
            //save a new property
            if (this.getPropertyIDByURI(URI) < 0) {
                this.saveEntityProperty(ontoID, dp);
                int entityID = this.getCurrentEntityID();
                this.saveOneProperty(entityID, ontoID, URI, name, typeID);
            }
        }
        
        
        for(OWLObjectProperty op: objPro){
            //save domains
            savePropertyDomains(op);
            //save range
            saveObjectPropertyRanges(op);
            //save subs
            saveSubPropertyRelations(op);
            //save disjoints
            saveDisjointPropertyRelations(op);
            //save equivalents
            saveEquivalentPropertyRelations(op);
        }
        
        for(OWLDataProperty dp: dataPro){
            //save domains
            savePropertyDomains(dp);
            //save range
            saveDataPropertyRanges(dp);
            //save subs
            saveSubPropertyRelations(dp);
            //save disjoints
            saveDisjointPropertyRelations(dp);
            //save equivalents
            saveEquivalentPropertyRelations(dp);
        }
        
        
//        Set<OWLAnnotationProperty> anoPro = loader.getAllAnnoationProperties(loader.myOntology);
//        Iterator<OWLAnnotationProperty> itb = anoPro.iterator();
//        while(itb.hasNext()){
//            System.out.println(this.getPropertyTypeID(itb.next()));
//        }
        
    }
    
    /*
     * Create Table EquvalentProperties(
	[propertyID]	integer,
	[entityID]	varchar(1000)		
        );
     */
    private void saveEquivalentPropertyRelations(OWLProperty op) {
        
        int proID = this.getPropertyIDByURI(loader.getPropertyURI(op));
        
        Set<OWLProperty> equ = op.getEquivalentProperties(loader.myOntology);
        if(equ!= null && equ.size() > 0){
            Iterator<OWLProperty> it = equ.iterator();
            while(it.hasNext()){
                OWLProperty opa = it.next();
                if(!opa.isAnonymous()){
                    int opaID = this.getPropertyIDByURI(loader.getPropertyURI(opa));
                    if(!this.isExistingEquivlaentPropertyRelation(proID, opaID)){
                        this.saveEquivlaentPropertyRelation(proID, opaID);
                    }
                }else{
                    String expression = loader.removeNameSpace(opa.toString());
                    if(this.isExistingAnonymousProperty(expression)){
                        int anonID = this.getAnonymousPropertyIDByExpression(expression);
                        if(!this.isExistingEquivlaentPropertyRelation(proID, anonID)){
                            this.saveEquivlaentPropertyRelation(proID, anonID);
                        }
                    }else{
                        String ns = loader.getPropertyNameSpace(op);
                        int ontoID = this.getOntologyIDByNamespace(ns);
                        this.saveEntityAnonymousProperty(ontoID);
                        int entityID = this.getCurrentEntityID();
                        this.saveAnonymousPropertyExpression(entityID, expression);
                        this.saveEquivlaentPropertyRelation(proID, entityID);        
                    }
                }
            }
        }
        
    }
    
    private boolean isExistingEquivlaentPropertyRelation(int propertyID, int entityID){
        boolean flag = false;
        try {
            String sql = "select * from EquvalentProperties where propertyID = ? and entityID = ?";
            st = this.dbConnection.prepare(sql);
            st.bind(1, propertyID);
            st.bind(2, entityID);
            st.step();
            flag = st.hasRow();
            st.dispose();
            return flag;
        } catch (SQLiteException ex) {
            Logger.getLogger(OWLAPI2DB.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    private void saveEquivlaentPropertyRelation(int propertyID, int entityID){
        try {
            String sql = "insert into EquvalentProperties (propertyID,entityID) values (?,?)";
            st = dbConnection.prepare(sql);
            st.bind(1, propertyID);
            st.bind(2, entityID);
            st.step();
            st.dispose();
            
        } catch (SQLiteException ex) {
            Logger.getLogger(OWLAPI2DB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /*
     * Create Table disjointproperties(
	[propertyID]	integer,
	[EntityID]	integer		
        );
     */
    private void saveDisjointPropertyRelations(OWLProperty op){
        
        int proID = this.getPropertyIDByURI(loader.getPropertyURI(op));
        
        Set<OWLProperty> dis = op.getDisjointProperties(loader.myOntology);
        
        if(dis != null && dis.size() > 0){
            Iterator<OWLProperty> it = dis.iterator();
            while(it.hasNext()){
                OWLProperty opa = it.next();
                if(!opa.isAnonymous()){
                    int paID = this.getPropertyIDByURI(loader.getPropertyURI(opa));
                    if(!this.isExistingSubPropertyRelation(proID, paID)){
                        this.saveOneSubPropertyRelation(proID, paID);
                    }
                }else{
                    String expression = loader.removeNameSpace(opa.toString());
                    if(this.isExistingAnonymousProperty(expression)){
                        int anonID = this.getAnonymousPropertyIDByExpression(expression);
                        if(!this.isExistingSubPropertyRelation(proID, anonID)){
                            this.saveOneSubPropertyRelation(proID, anonID);
                        }
                    } else{
                        String ns = loader.getPropertyNameSpace(opa);
                        int ontoID = this.getOntologyIDByNamespace(ns);
                        this.saveEntityAnonymousProperty(ontoID);
                        int entityID = this.getCurrentEntityID();
                        this.saveAnonymousPropertyExpression(entityID, expression);
                        this.saveOneSubPropertyRelation(proID, entityID);
                    }
                }
            }
        }
        
    }
    
    private boolean isExistingDisjointPropertyRelation(int proID, int disProID){
        boolean flag = false;
        try {
            String sql = "select * from disjointproperties where propertyID = ? and EntityID = ?";
            st = this.dbConnection.prepare(sql);
            st.bind(1, proID);
            st.bind(2, disProID);
            st.step();
            flag = st.hasRow();
            st.dispose();
            return flag;
        } catch (SQLiteException ex) {
            Logger.getLogger(OWLAPI2DB.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    private void saveDisjointPropertyRelation(int proID, int disProID){
        try {
            String sql = "insert into disjointproperties (propertyID,EntityID) values (?,?)";
            st = dbConnection.prepare(sql);
            st.bind(1, proID);
            st.bind(2, disProID);
            st.step();
            st.dispose();
            
        } catch (SQLiteException ex) {
            Logger.getLogger(OWLAPI2DB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /*
     * Create Table SubProperties(
	[propertyID]	integer,
	[subPropertyID]	integer		
        );
     */
    
    private void saveSubPropertyRelations(OWLProperty op){
        int proID = this.getPropertyIDByURI(loader.getPropertyURI(op));
        
        Set<OWLProperty> subSet = op.getSubProperties(loader.myOntology);        
        if(subSet != null && subSet.size() > 0){
            Iterator<OWLProperty> ita = subSet.iterator();
            while(ita.hasNext()){
                OWLProperty opa = ita.next();
                if(!opa.isAnonymous()){
                    int paID = this.getPropertyIDByURI(loader.getPropertyURI(opa));
                    if(!this.isExistingSubPropertyRelation(proID, paID)){
                        this.saveOneSubPropertyRelation(proID, paID);
                    }
                }else{
                    String expression = loader.removeNameSpace(opa.toString());
                    if(this.isExistingAnonymousProperty(expression)){
                        int anonID = this.getAnonymousPropertyIDByExpression(expression);
                        if(!this.isExistingSubPropertyRelation(proID, anonID)){
                            this.saveOneSubPropertyRelation(proID, anonID);
                        }
                    } else{
                        String ns = loader.getPropertyNameSpace(opa);
                        int ontoID = this.getOntologyIDByNamespace(ns);
                        this.saveEntityAnonymousProperty(ontoID);
                        int entityID = this.getCurrentEntityID();
                        this.saveAnonymousPropertyExpression(entityID, expression);
                        this.saveOneSubPropertyRelation(proID, entityID);
                    }
                }
            }
        }
         
        Set<OWLProperty> superSet = op.getSuperProperties(loader.myOntology);
        if(superSet != null && superSet.size() > 0){
            Iterator<OWLProperty> itb = superSet.iterator();
            while(itb.hasNext()){
                OWLProperty opb = itb.next();
                if(!opb.isAnonymous()){
                    int pbID = this.getPropertyIDByURI(loader.getPropertyURI(opb));
                    if(!this.isExistingSubPropertyRelation(pbID, proID)){
                        this.saveOneSubPropertyRelation(pbID, proID);
                    }
                }else{
                    String expression = loader.removeNameSpace(opb.toString());
                    if(this.isExistingAnonymousProperty(expression)){
                        int anonID = this.getAnonymousPropertyIDByExpression(expression);
                        if(!this.isExistingSubPropertyRelation(anonID, proID)){
                            this.saveOneSubPropertyRelation(anonID, proID);
                        }
                    } else{
                        String ns = loader.getPropertyNameSpace(opb);
                        int ontoID = this.getOntologyIDByNamespace(ns);
                        this.saveEntityAnonymousProperty(ontoID);
                        int entityID = this.getCurrentEntityID();
                        this.saveAnonymousPropertyExpression(entityID, expression);
                        this.saveOneSubPropertyRelation(entityID, proID);
                    }
                }
            }
        }

    }
    
    private boolean isExistingSubPropertyRelation(int FID, int CID){
        boolean flag = false;

        try {
            String sql = "select * from SubProperties where propertyID = ? and subPropertyID = ?";
            st = this.dbConnection.prepare(sql);
            st.bind(1, FID);
            st.bind(2, CID);
            st.step();
            flag = st.hasRow();
            st.dispose();
            return flag;
        } catch (SQLiteException ex) {
            Logger.getLogger(OWLAPI2DB.class.getName()).log(Level.SEVERE, null, ex);
            return flag;
        }
    }
    
    private void saveOneSubPropertyRelation(int FID, int CID){
        try {
            String sql = "insert into SubProperties values (?,?)";
            st = dbConnection.prepare(sql);
            st.bind(1, FID);
            st.bind(2, CID);
            st.step();
            st.dispose();
            
        } catch (SQLiteException ex) {
            Logger.getLogger(OWLAPI2DB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /*
     * Create Table AnonymousProperties(
	[EntityID]	INTEGER REFERENCES Entities(EntityID),
	[Type]		integer,
	[InExpression]	integer
        );
     */
    
    private void saveEntityAnonymousProperty(int ontologyID){
        try {
            String sql = "insert into Entities (OntologyID, Type, isAnonymous) values (?,?,?)";
            st = dbConnection.prepare(sql);
            st.bind(1, ontologyID);
            st.bind(2, this.TYPE_PROPERTY);
            st.bind(3, 1);
            st.step();
            st.dispose();
            
        } catch (SQLiteException ex) {
            Logger.getLogger(OWLAPI2DB.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    private void saveAnonymousPropertyExpression(int entityID, String expression){
        try {
            String sql = "insert into AnonymousProperties values (?,?,?)";
            st = dbConnection.prepare(sql);
            st.bind(1, entityID);
            st.bind(2, 1);
            st.bind(3, expression);
            st.step();
            st.dispose();
            
        } catch (SQLiteException ex) {
            Logger.getLogger(OWLAPI2DB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private int getAnonymousPropertyIDByExpression(String expression){
        try {
            String sql = "select EntityID from AnonymousProperties where InExpression = ?";
            st = this.dbConnection.prepare(sql);
            st.bind(1, expression);
            st.step();
            int id = st.columnInt(0);
            st.dispose();
            return id;
        } catch (SQLiteException ex) {
            Logger.getLogger(OWLAPI2DB.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }
    
    private boolean isExistingAnonymousProperty(String expression){
        boolean flag = false;
        try {
            String sql = "select * from AnonymousProperties where InExpression = ?";
            st = this.dbConnection.prepare(sql);
            st.bind(1, expression);
            st.step();
            flag = st.hasRow();
            st.dispose();
            return flag;
        } catch (SQLiteException ex) {
            Logger.getLogger(OWLAPI2DB.class.getName()).log(Level.SEVERE, null, ex);
            return flag;
        }
    }
    
    /*
     * Create Table PropertyRanges(
	[propertyID]	integer,
	[ClassID]	integer		
        );
     */
    private void saveObjectPropertyRanges(OWLObjectProperty bp){
        Set<OWLClassExpression> ranges = bp.getRanges(loader.myOntology);
        Iterator<OWLClassExpression> it = ranges.iterator();
        while(it.hasNext()){
            OWLClassExpression ce = it.next();
            if(ce.isAnonymous()){
                String expression = loader.removeNameSpace(ce.toString());
                if(!this.isExistingAnonymousClass(expression)){
                    String ns = loader.getPropertyNameSpace(bp);
                    int ontoID = this.getOntologyIDByNamespace(ns);
                    saveEntityAnonymousClass(ontoID);
                    int entityID = this.getCurrentEntityID();
                    //save new anonymous class expression
                    this.saveAnonymousClassExpression(entityID, expression);
                }
                int clsID = this.getAnonymousClassIDByExpression(expression);
                int proID = this.getPropertyIDByURI(loader.getPropertyURI(bp));
                if(!this.isExistingRange(proID, clsID)){
                    this.saveOneObjPropertyRange(proID, clsID);
                }
            }else{// this range class is not anonymous
                OWLClass cls = ce.asOWLClass();
                //if this class does not exist, then save it
                if(this.getClassIDByURI(loader.getClassURI(cls)) < 0){
                    String ns = loader.getClassNameSpace(cls);
                    int ontoID = this.getOntologyIDByNamespace(ns);
                    this.saveEntityClass(ontoID, cls);
                    int entityID = this.getCurrentEntityID();
                    this.saveClass(ontoID, entityID, cls);
                }
                int clsID = this.getClassIDByURI(loader.getClassURI(cls));
                int proID = this.getPropertyIDByURI(loader.getPropertyURI(bp));
                if(!this.isExistingRange(proID, clsID)){
                    this.saveOneObjPropertyRange(proID, clsID);
                }
            }
        }
    }
    
    private boolean isExistingRange(int propertyID, int classID){
        boolean flag = false;
        try {
            String sql = "select * from PropertyRanges where propertyID = ? and ClassID = ?";
            st = this.dbConnection.prepare(sql);
            st.bind(1, propertyID);
            st.bind(2, classID);
            st.step();
            flag = st.hasRow();
            st.dispose();
            return flag;
        } catch (SQLiteException ex) {
            Logger.getLogger(OWLAPI2DB.class.getName()).log(Level.SEVERE, null, ex);
            return flag;
        }
    }
    
    private void saveOneObjPropertyRange(int propertyID, int classID){
        try {
            String sql = "insert into PropertyRanges values (?,?)";
            st = dbConnection.prepare(sql);
            st.bind(1, propertyID);
            st.bind(2, classID);
            st.step();
            st.dispose();
            
        } catch (SQLiteException ex) {
            Logger.getLogger(OWLAPI2DB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void saveDataPropertyRanges(OWLDataProperty dp){
        Set<OWLDataRange> ranges = dp.getRanges(loader.myOntology);
        Iterator<OWLDataRange> it = ranges.iterator();
        while(it.hasNext()){
            OWLDataRange range = it.next();
            int id = this.getPropertyIDByURI(loader.getPropertyURI(dp));
            String dRange = range.toString();
            saveOneDataPropertyRange(id, dRange);
        }
    }
    
    private void saveOneDataPropertyRange(int id,String dRange){
        try {
            String sql = "update Properties set DataRange = ? where EntityID = ?";
            st = dbConnection.prepare(sql);
            st.bind(1, dRange);
            st.bind(2, id);
            st.step();
            st.dispose();
            
        } catch (SQLiteException ex) {
            Logger.getLogger(OWLAPI2DB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /*
    * Create Table PropertyDomains(
            [propertyID]	integer,
            [ClassID]	integer		
        );
    */
    
    private void savePropertyDomains(OWLProperty op){
        Set<OWLClassExpression> domains = op.getDomains(loader.myOntology);
        Iterator<OWLClassExpression> it = domains.iterator();
        while(it.hasNext()){
            OWLClassExpression ce = it.next();
            if(ce.isAnonymous()){
                String expression = loader.removeNameSpace(ce.toString());
                if(!this.isExistingAnonymousClass(expression)){
                    String ns = loader.getPropertyNameSpace(op);
                    int ontoID = this.getOntologyIDByNamespace(ns);
                    saveEntityAnonymousClass(ontoID);
                    int entityID = this.getCurrentEntityID();
                    //save new anonymous class expression
                    this.saveAnonymousClassExpression(entityID, expression);
                }
                int clsID = this.getAnonymousClassIDByExpression(expression);
                int proID = this.getPropertyIDByURI(loader.getPropertyURI(op));
                if(!this.isExistingDomain(proID, clsID)){
                    this.saveOnePropertyDomain(proID, clsID);
                }
            }else{// this domain class is not anonymous
                OWLClass cls = ce.asOWLClass();
                //id this class does not exist, then save it
                if(this.getClassIDByURI(loader.getClassURI(cls)) < 0){
                    String ns = loader.getClassNameSpace(cls);
                    int ontoID = this.getOntologyIDByNamespace(ns);
                    this.saveEntityClass(ontoID, cls);
                    int entityID = this.getCurrentEntityID();
                    this.saveClass(ontoID, entityID, cls);
                }
                int clsID = this.getClassIDByURI(loader.getClassURI(cls));
                int proID = this.getPropertyIDByURI(loader.getPropertyURI(op));
                if(!this.isExistingDomain(proID, clsID)){
                    this.saveOnePropertyDomain(proID, clsID);
                }
            }
        }
    }
    
    private boolean isExistingDomain(int propertyID, int classID){
        boolean flag = false;
        try {
            String sql = "select * from PropertyDomains where propertyID = ? and classID = ?";
            st = this.dbConnection.prepare(sql);
            st.bind(1, propertyID);
            st.bind(2, classID);
            st.step();
            flag = st.hasRow();
            st.dispose();
            return flag;
        } catch (SQLiteException ex) {
            Logger.getLogger(OWLAPI2DB.class.getName()).log(Level.SEVERE, null, ex);
            return flag;
        }
    }
    
    private void saveOnePropertyDomain(int propertyID, int classID){
        try {
            String sql = "insert into PropertyDomains values (?,?)";
            st = dbConnection.prepare(sql);
            st.bind(1, propertyID);
            st.bind(2, classID);
            st.step();
            st.dispose();
            
        } catch (SQLiteException ex) {
            Logger.getLogger(OWLAPI2DB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void saveEntityProperty(int ontologyID, OWLProperty pro){
        try {
            String sql = "insert into Entities (OntologyID, Type, isAnonymous) values (?,?,?)";
            st = dbConnection.prepare(sql);
            st.bind(1, ontologyID);
            st.bind(2, this.TYPE_PROPERTY);
            st.bind(3, pro.isAnonymous()?1:0);
            st.step();
            st.dispose();
            
        } catch (SQLiteException ex) {
            Logger.getLogger(OWLAPI2DB.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    private int getPropertyIDByURI(String URI){
        try {
            String sql = "select EntityID from Properties where URI = ?";
            st = dbConnection.prepare(sql);
            st.bind(1, URI);
            int id = -1;
            while(st.step())
            {
                id = st.columnInt(0);
            }
            
            st.dispose();
            return id;
            
        } catch (SQLiteException ex) {
            Logger.getLogger(OWLAPI2DB.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }
    
    
    /*
     * Create Table PropertyTypes(
	[propertyTypeID] integer,
	[DataProperty]	integer,-- 0- no, 1- yes
	[ObjectProperty]	integer,
	[AnnotationProperty]	integer,
	[isFunctional]	integer, 
	[isInverse]		integer,
	[isInverseFunctional]	integer,
	[isSymmetric]		integer,
	[isAsymmetric]		integer,
	[isTransitive]		integer,
	[isReflexive]		integer,
	[isIrreflexive]		integer,
        )
     */
    private int getPropertyTypeID(OWLProperty op){
        try {
            String sql = "select propertyTypeID from PropertyTypes";
            if (op.isOWLObjectProperty()) {
                OWLObjectProperty obp = (OWLObjectProperty) op;

                sql += " where ObjectProperty = 1 and DataProperty = 0 and AnnotationProperty = 0";

                if (obp.isInverseFunctional(loader.myOntology)) {
                    sql += " and isInverseFunctional = 1";
                } else {
                    sql += " and isInverseFunctional = 0";
                }

                if (obp.isFunctional(loader.myOntology)) {
                    sql += " and isFunctional = 1";
                } else {
                    sql += " and isFunctional = 0";
                }
                
                if (!(obp.getInverses(loader.myOntology).isEmpty())) {
                    sql += " and isInverse = 1";
                } else {
                    sql += " and isInverse = 0";
                }
                

                if (obp.isAsymmetric(loader.myOntology)) {
                    sql += " and isAsymmetric = 1";
                } else {
                    sql += " and isAsymmetric = 0";
                }

                if (obp.isIrreflexive(loader.myOntology)) {
                    sql += " and isIrreflexive = 1";
                } else {
                    sql += " and isIrreflexive = 0";
                }

                if (obp.isReflexive(loader.myOntology)) {
                    sql += " and isReflexive = 1";
                } else {
                    sql += " and isReflexive = 0";
                }

                if (obp.isSymmetric(loader.myOntology)) {
                    sql += " and isSymmetric = 1";
                } else {
                    sql += " and isSymmetric = 0";
                }

                if (obp.isTransitive(loader.myOntology)) {
                    sql += " and isTransitive = 1";
                } else {
                    sql += " and isTransitive = 0";
                }

            } else if (op.isOWLDataProperty()) {
                OWLDataProperty odp = (OWLDataProperty) op;
                sql += " where DataProperty = 1"
                        + " and ObjectProperty = 0"
                        + " and AnnotationProperty = 0"
                        + " and isInverseFunctional = 0"
                        + " and isInverse = 0"
                        + " and isAsymmetric = 0"
                        + " and isIrreflexive = 0"
                        + " and isReflexive = 0"
                        + " and isSymmetric = 0"
                        + " and isTransitive = 0";
                if (odp.isFunctional(loader.myOntology)) {
                    sql += " and isFunctional = 1";
                } else {
                    sql += " and isFunctional = 0";
                }
            } 
//            else if (op.isOWLAnnotationProperty()){
//                OWLAnnotationProperty oap = (OWLAnnotationProperty) op;
//                
//                sql += " where AnnotationProperty = 1"
//                        + " and ObjectProperty = 0"
//                        + " and DataProperty = 0"
//                        + " and isInverseFunctional = 0"
//                        + " and isInverse = 0"
//                        + " and isAsymmetric = 0"
//                        + " and isIrreflexive = 0"
//                        + " and isReflexive = 0"
//                        + " and isSymmetric = 0"
//                        + " and isTransitive = 0"
//                        + " and isFunctional = 0";
//            }
//            System.out.println(sql);
            st = this.dbConnection.prepare(sql);
            int id = -1;
            while(st.step())
            {
                id = st.columnInt(0);
            }
            
            st.dispose();
            return id;

        } catch (SQLiteException ex) {
            Logger.getLogger(OWLAPI2DB.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }
    
    private void savePropertyType(OWLProperty op){
        try {
            String sql = "INSERT INTO PropertyTypes (DataProperty, ObjectProperty, AnnotationProperty"
                    + ", isFunctional, isInverse, isInverseFunctional, isSymmetric"
                    + ", isAsymmetric, isTransitive, isReflexive, isIrreflexive) "
                    + "VALUES (?,?,?,?,?,?,?,?,?,?,?)";
            st = this.dbConnection.prepare(sql);
            if (op.isOWLObjectProperty()) {
                OWLObjectProperty obp = (OWLObjectProperty) op;
                st.bind(1, 0);
                st.bind(2, 1);
                st.bind(3, 0);
               
                if (obp.isFunctional(loader.myOntology)) {
                    st.bind(4, 1);
                } else {
                    st.bind(4, 0);
                }
                
                if (!(obp.getInverses(loader.myOntology).isEmpty())) {
                    st.bind(5, 1);
                } else {
                    st.bind(5, 0);
                }
                
                if (obp.isInverseFunctional(loader.myOntology)) {
                    st.bind(6, 1);
                } else {
                    st.bind(6, 0);
                }

                if (obp.isSymmetric(loader.myOntology)) {
                    st.bind(7, 1);
                } else {
                    st.bind(7, 0);
                }
                
                if (obp.isAsymmetric(loader.myOntology)) {
                    st.bind(8, 1);
                } else {
                    st.bind(8, 0);
                }

                if (obp.isTransitive(loader.myOntology)) {
                    st.bind(9, 1);
                } else {
                    st.bind(9, 0);
                }
                
                if (obp.isReflexive(loader.myOntology)) {
                    st.bind(10, 1);
                } else {
                    st.bind(10, 0);
                }
                
                if (obp.isIrreflexive(loader.myOntology)) {
                    st.bind(11, 1);
                } else {
                    st.bind(11, 0);
                }
            } else if (op.isOWLDataProperty()) {
                OWLDataProperty odp = (OWLDataProperty) op;
                st.bind(1, 1);
                st.bind(2, 0);
                st.bind(3, 0);
                if (odp.isFunctional(loader.myOntology)) {
                    st.bind(4, 1);
                } else {
                    st.bind(4, 0);
                }
                st.bind(5, 0);
                st.bind(6, 0);
                st.bind(7, 0);
                st.bind(8, 0);
                st.bind(9, 0);
                st.bind(10, 0);
                st.bind(11, 0);
            } 
//            else if (op.isOWLAnnotationProperty()){
//                OWLAnnotationProperty oap = (OWLAnnotationProperty) op;
//                
//                sql += " where AnnotationProperty = 1"
//                        + " and ObjectProperty = 0"
//                        + " and DataProperty = 0"
//                        + " and isInverseFunctional = 0"
//                        + " and isInverse = 0"
//                        + " and isAsymmetric = 0"
//                        + " and isIrreflexive = 0"
//                        + " and isReflexive = 0"
//                        + " and isSymmetric = 0"
//                        + " and isTransitive = 0"
//                        + " and isFunctional = 0";
//            }
//            System.out.println(sql);
            st.step();
            st.dispose();

        } catch (SQLiteException ex) {
            Logger.getLogger(OWLAPI2DB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /*
     * SQLiteConnection db = new SQLiteConnection(new File("/tmp/database"));
     * db.open(true); ... SQLiteStatement st = db.prepare("SELECT order_id FROM
     * orders WHERE quantity >= ?"); try { st.bind(1, minimumQuantity); while
     * (st.step()) { orders.add(st.columnLong(0)); } } finally { st.dispose(); }
     * ... db.dispose();
     */

    public static void main(String[] args) {
        //ShakespearesChildren.owl has imports "http://xmlns.com/foaf/0.1/"
        //pizza.owl has sub property
        //stones.owl , some properties 's namespace is not the same as ontology
        //full-galen.owl takes 70mins+- to complelely load(23141classes, 951property)  1G memory
        OWLAPI2DB db = new OWLAPI2DB("db/ontology.db","owlfiles/WordNet.owl");
        db.saveOntology(db.loader.myOntology);
//        db.saveImports();
//        db.saveAllClasses(db.loader.myOntology);
//        db.saveAllProperties(db.loader.myOntology);
        db.closeConnection();
    }


}
