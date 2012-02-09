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

package org.semanticweb.owlapi.reasoner.impl;

import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLAxiomVisitorEx;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDatatypeDefinitionAxiom;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointUnionAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLHasKeyAxiom;
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.SWRLRule;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 01-Aug-2009
 */
@SuppressWarnings("unused")
public class SatisfiabilityReducer implements OWLAxiomVisitorEx<OWLClassExpression> {

    private OWLDataFactory df;


    public SatisfiabilityReducer(OWLDataFactory dataFactory) {
        this.df = dataFactory;
    }

    public OWLClassExpression visit(OWLSubClassOfAxiom axiom) {
        return df.getOWLObjectIntersectionOf(axiom.getSubClass(), df.getOWLObjectComplementOf(axiom.getSuperClass()));
    }

    public OWLClassExpression visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
        return axiom.asOWLSubClassOfAxiom().accept(this);
    }

    public OWLClassExpression visit(OWLAsymmetricObjectPropertyAxiom axiom) {
        return null;
    }

    public OWLClassExpression visit(OWLReflexiveObjectPropertyAxiom axiom) {
        return axiom.asOWLSubClassOfAxiom().accept(this);
    }

    public OWLClassExpression visit(OWLDisjointClassesAxiom axiom) {
        return null;
    }

    public OWLClassExpression visit(OWLDataPropertyDomainAxiom axiom) {
        return axiom.asOWLSubClassOfAxiom().accept(this);
    }

    public OWLClassExpression visit(OWLObjectPropertyDomainAxiom axiom) {
        return axiom.asOWLSubClassOfAxiom().accept(this);
    }

    public OWLClassExpression visit(OWLEquivalentObjectPropertiesAxiom axiom) {
        return null;
    }

    public OWLClassExpression visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
        return axiom.asOWLSubClassOfAxiom().accept(this);
    }

    public OWLClassExpression visit(OWLDifferentIndividualsAxiom axiom) {
        return null;
    }

    public OWLClassExpression visit(OWLDisjointDataPropertiesAxiom axiom) {
        return null;
    }

    public OWLClassExpression visit(OWLDisjointObjectPropertiesAxiom axiom) {
        return null;
    }

    public OWLClassExpression visit(OWLObjectPropertyRangeAxiom axiom) {
        return axiom.asOWLSubClassOfAxiom().accept(this);
    }

    public OWLClassExpression visit(OWLObjectPropertyAssertionAxiom axiom) {
        return axiom.asOWLSubClassOfAxiom().accept(this);
    }

    public OWLClassExpression visit(OWLFunctionalObjectPropertyAxiom axiom) {
        return axiom.asOWLSubClassOfAxiom().accept(this);
    }

    public OWLClassExpression visit(OWLSubObjectPropertyOfAxiom axiom) {
        return null;
    }

    public OWLClassExpression visit(OWLDisjointUnionAxiom axiom) {
        return null;
    }

    public OWLClassExpression visit(OWLDeclarationAxiom axiom) {
        return null;
    }

    public OWLClassExpression visit(OWLSymmetricObjectPropertyAxiom axiom) {
        return null;
    }

    public OWLClassExpression visit(OWLDataPropertyRangeAxiom axiom) {
        return axiom.asOWLSubClassOfAxiom().accept(this);
    }

    public OWLClassExpression visit(OWLFunctionalDataPropertyAxiom axiom) {
        return axiom.asOWLSubClassOfAxiom().accept(this);
    }

    public OWLClassExpression visit(OWLEquivalentDataPropertiesAxiom axiom) {
        return null;
    }

    public OWLClassExpression visit(OWLClassAssertionAxiom axiom) {
        return axiom.asOWLSubClassOfAxiom().accept(this);
    }

    public OWLClassExpression visit(OWLEquivalentClassesAxiom axiom) {
        return null;
    }

    public OWLClassExpression visit(OWLDataPropertyAssertionAxiom axiom) {
        return axiom.asOWLSubClassOfAxiom().accept(this);
    }

    public OWLClassExpression visit(OWLTransitiveObjectPropertyAxiom axiom) {
        return null;
    }

    public OWLClassExpression visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
        return axiom.asOWLSubClassOfAxiom().accept(this);
    }

    public OWLClassExpression visit(OWLSubDataPropertyOfAxiom axiom) {
        return null;
    }

    public OWLClassExpression visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
        return axiom.asOWLSubClassOfAxiom().accept(this);
    }

    public OWLClassExpression visit(OWLSameIndividualAxiom axiom) {
        return null;
    }

    public OWLClassExpression visit(OWLSubPropertyChainOfAxiom axiom) {
        return null;
    }

    public OWLClassExpression visit(OWLInverseObjectPropertiesAxiom axiom) {
        return null;
    }

    public OWLClassExpression visit(OWLHasKeyAxiom axiom) {
        return null;
    }

    public OWLClassExpression visit(OWLDatatypeDefinitionAxiom axiom) {
        return null;
    }

    public OWLClassExpression visit(SWRLRule rule) {
        return null;
    }


    public OWLClassExpression visit(OWLAnnotationAssertionAxiom axiom) {
        return null;
    }

    public OWLClassExpression visit(OWLSubAnnotationPropertyOfAxiom axiom) {
        return null;
    }

    public OWLClassExpression visit(OWLAnnotationPropertyDomainAxiom axiom) {
        return null;
    }

    public OWLClassExpression visit(OWLAnnotationPropertyRangeAxiom axiom) {
        return null;
    }
}
