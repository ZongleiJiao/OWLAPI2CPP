/*
 * This file is part of the OWL API.
 *
 * The contents of this file are subject to the LGPL License, Version 3.0.
 *
 * Copyright (C) 2011, Clark & Parsia, LLC
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
 * Copyright 2011, Clark & Parsia, LLC
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

package com.clarkparsia.owlapi.explanation;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLAxiomVisitor;
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
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLRuntimeException;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.SWRLRule;

public class SatisfiabilityConverter {

    private class AxiomConverter implements OWLAxiomVisitor {

        private OWLClassExpression result;


        private OWLObjectIntersectionOf and(OWLClassExpression desc1, OWLClassExpression desc2) {
            return factory.getOWLObjectIntersectionOf(set(desc1, desc2));
        }


        private OWLObjectIntersectionOf and(Set<OWLClassExpression> set) {
            return factory.getOWLObjectIntersectionOf(set);
        }


        OWLClassExpression getResult() {
            return result;
        }


        private OWLObjectComplementOf not(OWLClassExpression desc) {
            return factory.getOWLObjectComplementOf(desc);
        }


        private OWLObjectOneOf oneOf(OWLIndividual ind) {
            return factory.getOWLObjectOneOf(Collections.singleton(ind));
        }


        private OWLObjectUnionOf or(OWLClassExpression desc1, OWLClassExpression desc2) {
            return factory.getOWLObjectUnionOf(set(desc1, desc2));
        }


        void reset() {
            result = null;
        }


        private <T> Set<T> set(T desc1, T desc2) {
            Set<T> set = new HashSet<T>();
            set.add(desc1);
            set.add(desc2);

            return set;
        }


        public void visit(OWLAsymmetricObjectPropertyAxiom axiom) {
            throw new OWLRuntimeException("Not implemented: Cannot generate explanation for " + axiom);
        }


        public void visit(OWLClassAssertionAxiom axiom) {
            OWLIndividual ind = axiom.getIndividual();
            OWLClassExpression c = axiom.getClassExpression();

            result = and(oneOf(ind), not(c));
        }


        public void visit(OWLDataPropertyAssertionAxiom axiom) {
            OWLClassExpression sub = oneOf(axiom.getSubject());
            OWLClassExpression sup = factory.getOWLDataHasValue(axiom.getProperty(), axiom.getObject());
            OWLSubClassOfAxiom ax = factory.getOWLSubClassOfAxiom(sub, sup);
            ax.accept(this);
        }


        public void visit(OWLDataPropertyDomainAxiom axiom) {
            OWLClassExpression sub = factory.getOWLDataSomeValuesFrom(axiom.getProperty(), factory.getTopDatatype());
            result = and(sub, not(axiom.getDomain()));
        }


        public void visit(OWLDataPropertyRangeAxiom axiom) {
            result = factory.getOWLDataSomeValuesFrom(axiom.getProperty(),
                    factory.getOWLDataComplementOf(axiom.getRange()));
        }


        public void visit(OWLSubDataPropertyOfAxiom axiom) {
            throw new OWLRuntimeException("Not implemented: Cannot generate explanation for " + axiom);
        }


        public void visit(OWLDeclarationAxiom axiom) {
            throw new OWLRuntimeException("Not implemented: Cannot generate explanation for " + axiom);
        }


        public void visit(OWLDifferentIndividualsAxiom axiom) {
            Set<OWLClassExpression> nominals = new HashSet<OWLClassExpression>();
            for (OWLIndividual ind : axiom.getIndividuals()) {
                nominals.add(oneOf(ind));
            }
            result = factory.getOWLObjectIntersectionOf(nominals);
        }


        public void visit(OWLDisjointClassesAxiom axiom) {
            result = and(axiom.getClassExpressions());
        }


        public void visit(OWLDisjointDataPropertiesAxiom axiom) {
            throw new OWLRuntimeException("Not implemented: Cannot generate explanation for " + axiom);
        }


        public void visit(OWLDisjointObjectPropertiesAxiom axiom) {
            throw new OWLRuntimeException("Not implemented: Cannot generate explanation for " + axiom);
        }


        public void visit(OWLDisjointUnionAxiom axiom) {
            throw new OWLRuntimeException("Not implemented: Cannot generate explanation for " + axiom);
        }


        public void visit(OWLAnnotationAssertionAxiom axiom) {
            throw new OWLRuntimeException("Not implemented: Cannot generate explanation for " + axiom);
        }


        public void visit(OWLEquivalentClassesAxiom axiom) {
            Iterator<OWLClassExpression> classes = axiom.getClassExpressions().iterator();
            OWLClassExpression c1 = classes.next();
            OWLClassExpression c2 = classes.next();

            if (classes.hasNext())
                logger.warning("EquivalentClassesAxiom with more than two elements not supported!");

            // apply simplification for the cases where either concept is owl:Thing or owlapi:Nothin
            if (c1.isOWLNothing())
                result = c2;
            else if (c2.isOWLNothing())
                result = c1;
            else if (c1.isOWLThing())
                result = not(c2);
            else if (c2.isOWLThing())
                result = not(c1);
            else
                result = or(and(c1, not(c2)), and(not(c1), c2));
        }


        public void visit(OWLEquivalentDataPropertiesAxiom axiom) {
            throw new OWLRuntimeException("Not implemented: Cannot generate explanation for " + axiom);
        }


        public void visit(OWLEquivalentObjectPropertiesAxiom axiom) {
            throw new OWLRuntimeException("Not implemented: Cannot generate explanation for " + axiom);
        }


        public void visit(OWLFunctionalDataPropertyAxiom axiom) {
            throw new OWLRuntimeException("Not implemented: Cannot generate explanation for " + axiom);
        }


        public void visit(OWLFunctionalObjectPropertyAxiom axiom) {
            throw new OWLRuntimeException("Not implemented: Cannot generate explanation for " + axiom);
        }


//        public void visit(OWLImportsDeclaration axiom) {
//            throw new OWLRuntimeException("Not implemented: Cannot generate explanation for " + axiom);
//        }


        public void visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
            throw new OWLRuntimeException("Not implemented: Cannot generate explanation for " + axiom);
        }


        public void visit(OWLInverseObjectPropertiesAxiom axiom) {
            throw new OWLRuntimeException("Not implemented: Cannot generate explanation for " + axiom);
        }


        public void visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
            throw new OWLRuntimeException("Not implemented: Cannot generate explanation for " + axiom);
        }


        public void visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
            OWLClassExpression sub = oneOf(axiom.getSubject());
            OWLClassExpression sup = factory.getOWLDataHasValue(axiom.getProperty(), axiom.getObject());
            factory.getOWLSubClassOfAxiom(sub, not(sup)).accept(this);
        }


        public void visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
            OWLClassExpression sub = oneOf(axiom.getSubject());
            OWLClassExpression sup = factory.getOWLObjectHasValue(axiom.getProperty(), axiom.getObject());
            factory.getOWLSubClassOfAxiom(sub, not(sup)).accept(this);
        }


        public void visit(OWLObjectPropertyAssertionAxiom axiom) {
            OWLClassExpression sub = oneOf(axiom.getSubject());
            OWLClassExpression sup = factory.getOWLObjectHasValue(axiom.getProperty(), axiom.getObject());
            OWLSubClassOfAxiom ax = factory.getOWLSubClassOfAxiom(sub, sup);
            ax.accept(this);
        }


        public void visit(OWLSubPropertyChainOfAxiom axiom) {
            throw new OWLRuntimeException("Not implemented: Cannot generate explanation for " + axiom);
        }


        public void visit(OWLObjectPropertyDomainAxiom axiom) {
            result = and(factory.getOWLObjectSomeValuesFrom(axiom.getProperty(), factory.getOWLThing()),
                    not(axiom.getDomain()));
        }


        public void visit(OWLObjectPropertyRangeAxiom axiom) {
            result = factory.getOWLObjectSomeValuesFrom(axiom.getProperty(), not(axiom.getRange()));
        }


        public void visit(OWLSubObjectPropertyOfAxiom axiom) {
            throw new OWLRuntimeException("Not implemented: Cannot generate explanation for " + axiom);
        }

        public void visit(OWLReflexiveObjectPropertyAxiom axiom) {
            throw new OWLRuntimeException("Not implemented: Cannot generate explanation for " + axiom);
        }


        public void visit(OWLSameIndividualAxiom axiom) {
            Set<OWLClassExpression> nominals = new HashSet<OWLClassExpression>();
            for (OWLIndividual ind : axiom.getIndividuals()) {
                nominals.add(not(oneOf(ind)));
            }
            result = and(nominals);
        }


        public void visit(OWLSubClassOfAxiom axiom) {
            OWLClassExpression sub = axiom.getSubClass();
            OWLClassExpression sup = axiom.getSuperClass();

            if (sup.isOWLNothing())
                result = sub;
            else if (sub.isOWLThing())
                result = not(sup);
            else
                result = and(sub, not(sup));
        }


        public void visit(OWLSymmetricObjectPropertyAxiom axiom) {
            throw new OWLRuntimeException("Not implemented: Cannot generate explanation for " + axiom);
        }


        public void visit(OWLTransitiveObjectPropertyAxiom axiom) {
            throw new OWLRuntimeException("Not implemented: Cannot generate explanation for " + axiom);
        }


        public void visit(SWRLRule rule) {
            throw new OWLRuntimeException("Not implemented: Cannot generate explanation for " + rule);
        }

        public void visit(OWLHasKeyAxiom axiom) {
            throw new OWLRuntimeException("Not implemented: Cannot generate explanation for " + axiom);
        }

        public void visit(OWLAnnotationPropertyDomainAxiom axiom) {
            throw new OWLRuntimeException("Not implemented: Cannot generate explanation for " + axiom);
        }

        public void visit(OWLAnnotationPropertyRangeAxiom axiom) {
            throw new OWLRuntimeException("Not implemented: Cannot generate explanation for " + axiom);
        }

        public void visit(OWLSubAnnotationPropertyOfAxiom axiom) {
            throw new OWLRuntimeException("Not implemented: Cannot generate explanation for " + axiom);
        }


        public void visit(OWLDatatypeDefinitionAxiom axiom) {
            throw new OWLRuntimeException("Not implemented: Cannot generate explanation for " + axiom);
        }
    }


    private static final Logger logger = Logger.getLogger(SatisfiabilityConverter.class.getName());

    private AxiomConverter converter;

    protected OWLDataFactory factory;


    public SatisfiabilityConverter(OWLDataFactory factory) {
        this.factory = factory;

        converter = new AxiomConverter();
    }


    public OWLClassExpression convert(OWLAxiom axiom) {
        converter.reset();

        axiom.accept(converter);

        OWLClassExpression result = converter.getResult();

        if (result == null)
            throw new RuntimeException("Not supported yet");

        return result;
    }
}
