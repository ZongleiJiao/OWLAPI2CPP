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

package org.semanticweb.owlapi.model;

import java.util.Set;

/**
 * Author: Matthew Horridge<br> The University of Manchester<br> Information Management Group<br>
 * Date: 13-Jan-2009
 * </p>
 * Represents an <a href="http://www.w3.org/TR/2009/REC-owl2-syntax-20091027/#Annotation_Properties">AnnotationProperty</a>
 * in the OWL 2 specification.
 */
public interface OWLAnnotationProperty extends OWLEntity {

    /**
     * Determines if this annotation property has an IRI corresponding to <code>rdfs:comment</code>.
     * @return <code>true</code> if the IRI of this annotation property is
     *         <code>rdfs:comment</code>, where <code>rdfs:</code> expands to the usual prefix, otherwise <code>false</code>.
     */
    boolean isComment();

    /**
     * Determines if this annotation property has an IRI corresponding to <code>rdfs:label</code>.
     * @return <code>true</code> if the IRI of this annotation property is
     *         <code>rdfs:label</code>, where <code>rdfs:</code> expands to the usual prefix, otherwise <code>false</code>.
     */
    boolean isLabel();

    /**
     * Determines if this annotation property has an IRI corresponding to <code>owl:deprecated</code>. An annotation
     * along the <code>owl:deprecated</code> property which has a value of <code>"true"^^xsd:boolean</code> can be
     * used to deprecate IRIs. (See <a href="Section 5.5">http://www.w3.org/TR/2009/REC-owl2-syntax-20091027/#Annotation_Properties</a>
     * of the OWL 2 specification.
     * @return <code>true</code> if the IRI of this annotation property is
     *         <code>owl:deprecated</code>, where <code>owl:</code> expands to the usual prefix, otherwise <code>false</code>.
     */
    boolean isDeprecated();

    /**
     * Determines if this property is a built in annotation property.  i.e. one of the following
     * <ul>
     * <li>rdfs:label</li>
     * <li>rdfs:comment</li>
     * <li>rdfs:seeAlso</li>
     * <li>rdfs:isDefinedBy</li>
     * <li>owl:deprecated</li>
     * <li>owl:priorVersion</li>
     * <li>owl:backwardCompatibleWith</li>
     * <li>owl:incompatibleWith</li>
     * </ul>
     * @return <code>true</code> if the property is a built in annotation property, otherwise <code>false</code>
     */
    boolean isBuiltIn();

    /**
     * Gets the annotation properties which are asserted to be sub-properties of this annotation property in
     * the specified ontology.
     * @param ontology The ontology to be examined for <code>SubAnnotationPropertyOf</code> axioms.
     * @return A set of annotation properties such that for each property <code>p</code> in the set, it is
     *         the case that <code>ontology</code> contains an <code>SubAnnotationPropertyOf(p, this)</code> axiom
     *         where <code>this</code> refers to this annotation property.
     * @since 3.2
     */
    Set<OWLAnnotationProperty> getSubProperties(OWLOntology ontology);

    /**
     * Gets the annotation properties which are asserted to be sub-properties of this annotation property in
     * the specified ontology and potentially its imports closure.
     * @param ontology The ontology to be examined for <code>SubAnnotationPropertyOf</code> axioms.
     * @param includeImportsClosure if <code>true</code> then the imports closure of this ontology is searched
     * for <code>SubAnnotationPropertyOf</code> axioms that assert this property is the super property of some
     * other annotation property.  If <code>false</code> then only <code>ontology</code> is searched.
     * @return If <code>includeImportsClosure</code> is <code>true</code>, a set of annotation properties such that for
     *         each property <code>p</code> in the set, it is
     *         the case that at least one <code>ontology</code> in the imports closure contains a
     *         <code>SubAnnotationPropertyOf(p, this)</code> axiom
     *         where <code>this</code> refers to this annotation property.
     *         If <code>includeImportsClosure</code> is <code>false</code>, a set of annotation properties such that for
     *         each property <code>p</code> in the set, it is
     *         the case that <code>ontology</code> contains a
     *         <code>SubAnnotationPropertyOf(p, this)</code> axiom
     *         where <code>this</code> refers to this annotation property.
     * @since 3.2
     */
    Set<OWLAnnotationProperty> getSubProperties(OWLOntology ontology, boolean includeImportsClosure);

    /**
     * Gets the annotation properties which are asserted to be sub-properties of this annotation property in
     * the specified set of ontologies.
     * @param ontologies The set of ontologies to be examined for <code>SubAnnotationPropertyOf</code> axioms.
     * @return A set of annotation properties such that for
     *         each property <code>p</code> in the set, it is
     *         the case that at least one <code>ontology</code> in <code>ontologies</code> contains a
     *         <code>SubAnnotationPropertyOf(p, this)</code> axiom
     *         where <code>this</code> refers to this annotation property.
     * @since 3.2
     */
    Set<OWLAnnotationProperty> getSubProperties(Set<OWLOntology> ontologies);
    
    /**
     * Gets the annotation properties which are asserted to be super-properties of this annotation property in
     * the specified ontology.
     * @param ontology The ontology to be examined for <code>SubAnnotationPropertyOf</code> axioms.
     * @return A set of annotation properties such that for each property <code>p</code> in the set, it is
     *         the case that <code>ontology</code> contains an <code>SubAnnotationPropertyOf(this, p)</code> axiom
     *         where <code>this</code> refers to this annotation property.
     * @since 3.2
     */
    Set<OWLAnnotationProperty> getSuperProperties(OWLOntology ontology);

    /**
     * Gets the annotation properties which are asserted to be super-properties of this annotation property in
     * the specified ontology and potentially its imports closure.
     * @param ontology The ontology to be examined for <code>SubAnnotationPropertyOf</code> axioms.
     * @param includeImportsClosure if <code>true</code> then the imports closure of this ontology is searched
     * for <code>SubAnnotationPropertyOf</code> axioms that assert this property is the super property of some
     * other annotation property.  If <code>false</code> then only <code>ontology</code> is searched.
     * @return If <code>includeImportsClosure</code> is <code>true</code>, a set of annotation properties such that for
     *         each property <code>p</code> in the set, it is
     *         the case that at least one <code>ontology</code> in the imports closure contains a
     *         <code>SuperAnnotationPropertyOf(this, p)</code> axiom
     *         where <code>this</code> refers to this annotation property.
     *         If <code>includeImportsClosure</code> is <code>false</code>, a set of annotation properties such that for
     *         each property <code>p</code> in the set, it is
     *         the case that <code>ontology</code> contains a
     *         <code>SuperAnnotationPropertyOf(this, p)</code> axiom
     *         where <code>this</code> refers to this annotation property.
     * @since 3.2
     */
    Set<OWLAnnotationProperty> getSuperProperties(OWLOntology ontology, boolean includeImportsClosure);

    /**
     * Gets the annotation properties which are asserted to be super-properties of this annotation property in
     * the specified set of ontologies.
     * @param ontologies The set of ontologies to be examined for <code>SubAnnotationPropertyOf</code> axioms.
     * @return A set of annotation properties such that for
     *         each property <code>p</code> in the set, it is
     *         the case that at least one <code>ontology</code> in <code>ontologies</code> contains a
     *         <code>SubAnnotationPropertyOf(this, p)</code> axiom
     *         where <code>this</code> refers to this annotation property.
     * @since 3.2
     */
    Set<OWLAnnotationProperty> getSuperProperties(Set<OWLOntology> ontologies);

}
