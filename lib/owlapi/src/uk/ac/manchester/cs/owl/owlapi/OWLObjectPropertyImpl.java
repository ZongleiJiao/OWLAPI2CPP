/*
 * This file is part of the OWL API.
 *
 * The contents of this file are subject to the LGPL License, Version 3.0.
 *
 * Copyright (C) 2011, The University of Manchester
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 *
 *
 * Alternatively, the contents of this file may be used under the terms of the Apache License, Version 2.0
 * in which case, the provisions of the Apache License Version 2.0 are applicable instead of those above.
 *
 * Copyright 2011, University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.manchester.cs.owl.owlapi;

import java.net.URI;
import java.util.Collections;
import java.util.Set;

import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEntityVisitor;
import org.semanticweb.owlapi.model.OWLEntityVisitorEx;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLNamedObjectVisitor;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectVisitor;
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLPropertyExpressionVisitor;
import org.semanticweb.owlapi.model.OWLPropertyExpressionVisitorEx;
import org.semanticweb.owlapi.model.OWLRuntimeException;
import org.semanticweb.owlapi.model.OWLSubPropertyAxiom;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 26-Oct-2006<br><br>
 */
public class OWLObjectPropertyImpl extends OWLObjectPropertyExpressionImpl implements OWLObjectProperty {

    private IRI iri;

    private boolean builtin;

    public OWLObjectPropertyImpl(OWLDataFactory dataFactory, IRI iri) {
        super(dataFactory);
        this.iri = iri;
        this.builtin = iri.equals(OWLRDFVocabulary.OWL_TOP_OBJECT_PROPERTY.getIRI()) || iri.equals(OWLRDFVocabulary.OWL_BOTTOM_OBJECT_PROPERTY.getIRI());
    }

    @Override
	public boolean isTopEntity() {
        return isOWLTopObjectProperty();
    }

    @Override
	public boolean isBottomEntity() {
        return isOWLBottomObjectProperty();
    }

    /**
     * Gets the entity type for this entity
     * @return The entity type
     */
    public EntityType<?> getEntityType() {
        return EntityType.OBJECT_PROPERTY;
    }

    /**
     * Gets an entity that has the same IRI as this entity but is of the specified type.
     * @param entityType The type of the entity to obtain.  This entity is not affected in any way.
     * @return An entity that has the same IRI as this entity and is of the specified type
     */
    public <E extends OWLEntity> E getOWLEntity(EntityType<E> entityType) {
        return getOWLDataFactory().getOWLEntity(entityType, iri);
    }

    /**
     * Tests to see if this entity is of the specified type
     * @param entityType The entity type
     * @return <code>true</code> if this entity is of the specified type, otherwise <code>false</code>.
     */
    public boolean isType(EntityType<?> entityType) {
        return getEntityType().equals(entityType);
    }

    /**
     * Returns a string representation that can be used as the ID of this entity.  This is the toString
     * representation of the IRI
     * @return A string representing the toString of the IRI of this entity.
     */
    public String toStringID() {
        return iri.toString();
    }

    public IRI getIRI() {
        return iri;
    }

    public URI getURI() {
        return iri.toURI();
    }


    public boolean isBuiltIn() {
        return builtin;
    }


    @Override
	public boolean equals(Object obj) {
        if (super.equals(obj)) {
            if (!(obj instanceof OWLObjectProperty)) {
                return false;
            }
            IRI otherIRI = ((OWLObjectProperty) obj).getIRI();
            return otherIRI.equals(this.iri);
        }
        return false;
    }


    public void accept(OWLEntityVisitor visitor) {
        visitor.visit(this);
    }


    public void accept(OWLPropertyExpressionVisitor visitor) {
        visitor.visit(this);
    }


    public void accept(OWLObjectVisitor visitor) {
        visitor.visit(this);
    }


    public void accept(OWLNamedObjectVisitor visitor) {
        visitor.visit(this);
    }


    public <O> O accept(OWLEntityVisitorEx<O> visitor) {
        return visitor.visit(this);
    }


    public <O> O accept(OWLPropertyExpressionVisitorEx<O> visitor) {
        return visitor.visit(this);
    }


    public <O> O accept(OWLObjectVisitorEx<O> visitor) {
        return visitor.visit(this);
    }


    public boolean isAnonymous() {
        return false;
    }


    public OWLObjectProperty asOWLObjectProperty() {
        return this;
    }


    @Override
	protected Set<? extends OWLSubPropertyAxiom<OWLObjectPropertyExpression>> getSubPropertyAxiomsForRHS(OWLOntology ont) {
        return ont.getObjectSubPropertyAxiomsForSuperProperty(this);
    }


    public Set<OWLAnnotation> getAnnotations(OWLOntology ontology) {
        return ImplUtils.getAnnotations(this, Collections.singleton(ontology));
    }


    public Set<OWLAnnotationAssertionAxiom> getAnnotationAssertionAxioms(OWLOntology ontology) {
        return ImplUtils.getAnnotationAxioms(this, Collections.singleton(ontology));
    }


    public Set<OWLAnnotation> getAnnotations(OWLOntology ontology, OWLAnnotationProperty annotationProperty) {
        return ImplUtils.getAnnotations(this, annotationProperty, Collections.singleton(ontology));
    }

    public OWLDataProperty asOWLDataProperty() {
        throw new OWLRuntimeException("Not a data property!");
    }


    public OWLDatatype asOWLDatatype() {
        throw new OWLRuntimeException("Not a data type!");
    }


    public OWLNamedIndividual asOWLNamedIndividual() {
        throw new OWLRuntimeException("Not an individual!");
    }


    public OWLClass asOWLClass() {
        throw new OWLRuntimeException("Not an OWLClass!");
    }


    public boolean isOWLClass() {
        return false;
    }


    public boolean isOWLDataProperty() {
        return false;
    }


    public boolean isOWLDatatype() {
        return false;
    }


    public boolean isOWLNamedIndividual() {
        return false;
    }


    public boolean isOWLObjectProperty() {
        return true;
    }

    public OWLAnnotationProperty asOWLAnnotationProperty() {
        throw new OWLRuntimeException("Not an annotation property");
    }

    public boolean isOWLAnnotationProperty() {
        return false;
    }

    @Override
	protected int compareObjectOfSameType(OWLObject object) {
        return iri.compareTo(((OWLObjectProperty) object).getIRI());
    }

    /**
     * Determines if this is the owl:topObjectProperty
     * @return <code>true</code> if this property is the owl:topObjectProperty otherwise <code>false</code>
     */
    public boolean isOWLTopObjectProperty() {
        return iri.equals(OWLRDFVocabulary.OWL_TOP_OBJECT_PROPERTY.getIRI());
    }

    /**
     * Determines if this is the owl:bottomObjectProperty
     * @return <code>true</code> if this property is the owl:bottomObjectProperty otherwise <code>false</code>
     */
    public boolean isOWLBottomObjectProperty() {
        return iri.equals(OWLRDFVocabulary.OWL_BOTTOM_OBJECT_PROPERTY.getIRI());
    }

    /**
     * Determines if this is the owl:topDataProperty
     * @return <code>true</code> if this property is the owl:topDataProperty otherwise <code>false</code>
     */
    public boolean isOWLTopDataProperty() {
        return false;
    }

    /**
     * Determines if this is the owl:bottomDataProperty
     * @return <code>true</code> if this property is the owl:bottomDataProperty otherwise <code>false</code>
     */
    public boolean isOWLBottomDataProperty() {
        return false;
    }


    public Set<OWLAxiom> getReferencingAxioms(OWLOntology ontology) {
        return ontology.getReferencingAxioms(this);
    }

    public Set<OWLAxiom> getReferencingAxioms(OWLOntology ontology, boolean includeImports) {
        return ontology.getReferencingAxioms(this, includeImports);
    }
}
