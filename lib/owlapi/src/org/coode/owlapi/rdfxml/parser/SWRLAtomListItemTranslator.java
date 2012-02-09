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

package org.coode.owlapi.rdfxml.parser;

import static org.semanticweb.owlapi.vocab.SWRLVocabulary.*;

import java.util.List;
import java.util.logging.Logger;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.SWRLAtom;
import org.semanticweb.owlapi.model.SWRLDArgument;
import org.semanticweb.owlapi.model.SWRLIArgument;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 18-Feb-2007<br><br>
 */
public class SWRLAtomListItemTranslator implements ListItemTranslator<SWRLAtom> {

    private static final Logger logger = Logger.getLogger(SWRLAtomListItemTranslator.class.getName());


    private OWLRDFConsumer consumer;

    private OWLDataFactory dataFactory;


    public SWRLAtomListItemTranslator(OWLRDFConsumer consumer) {
        this.consumer = consumer;
        dataFactory = consumer.getDataFactory();
    }


    public SWRLAtom translate(IRI firstObject) {
        if (consumer.isSWRLBuiltInAtom(firstObject)) {
            IRI builtInIRI = consumer.getResourceObject(firstObject, BUILT_IN.getIRI(), true);
            IRI mainIRI = consumer.getResourceObject(firstObject, ARGUMENTS.getIRI(), true);
            OptimisedListTranslator<SWRLDArgument> listTranslator = new OptimisedListTranslator<SWRLDArgument>(
                    consumer,
                    new SWRLAtomDObjectListItemTranslator());
            List<SWRLDArgument> args = listTranslator.translateList(mainIRI);
            return dataFactory.getSWRLBuiltInAtom(builtInIRI, args);
        }
        else if (consumer.isSWRLClassAtom(firstObject)) {
            // C(?x) or C(ind)
            SWRLIArgument iObject = translateSWRLAtomIObject(firstObject, ARGUMENT_1.getIRI());
            IRI classIRI = consumer.getResourceObject(firstObject, CLASS_PREDICATE.getIRI(), true);
            OWLClassExpression desc = consumer.translateClassExpression(classIRI);
            return dataFactory.getSWRLClassAtom(desc, iObject);
        }
        else if (consumer.isSWRLDataRangeAtom(firstObject)) {
            // DR(?x) or DR(val)
            SWRLDArgument dObject = translateSWRLAtomDObject(firstObject, ARGUMENT_1.getIRI());
            IRI dataRangeIRI = consumer.getResourceObject(firstObject, DATA_RANGE.getIRI(), true);
            OWLDataRange dataRange = consumer.translateDataRange(dataRangeIRI);
            return dataFactory.getSWRLDataRangeAtom(dataRange, dObject);
        }
        else if (consumer.isSWRLDataValuedPropertyAtom(firstObject)) {
            SWRLIArgument arg1 = translateSWRLAtomIObject(firstObject, ARGUMENT_1.getIRI());
            SWRLDArgument arg2 = translateSWRLAtomDObject(firstObject, ARGUMENT_2.getIRI());
            IRI dataPropertyIRI = consumer.getResourceObject(firstObject, PROPERTY_PREDICATE.getIRI(), true);
            OWLDataPropertyExpression prop = consumer.translateDataPropertyExpression(dataPropertyIRI);
            return dataFactory.getSWRLDataPropertyAtom(prop, arg1, arg2);
        }
        else if (consumer.isSWRLIndividualPropertyAtom(firstObject)) {
            SWRLIArgument arg1 = translateSWRLAtomIObject(firstObject, ARGUMENT_1.getIRI());
            SWRLIArgument arg2 = translateSWRLAtomIObject(firstObject, ARGUMENT_2.getIRI());
            IRI objectPropertyIRI = consumer.getResourceObject(firstObject, PROPERTY_PREDICATE.getIRI(), true);
            OWLObjectPropertyExpression prop = consumer.translateObjectPropertyExpression(objectPropertyIRI);
            return dataFactory.getSWRLObjectPropertyAtom(prop, arg1, arg2);
        }
        else if (consumer.isSWRLSameAsAtom(firstObject)) {
            SWRLIArgument arg1 = translateSWRLAtomIObject(firstObject, ARGUMENT_1.getIRI());
            SWRLIArgument arg2 = translateSWRLAtomIObject(firstObject, ARGUMENT_2.getIRI());
            return dataFactory.getSWRLSameIndividualAtom(arg1, arg2);
        }
        else if (consumer.isSWRLDifferentFromAtom(firstObject)) {
            SWRLIArgument arg1 = translateSWRLAtomIObject(firstObject, ARGUMENT_1.getIRI());
            SWRLIArgument arg2 = translateSWRLAtomIObject(firstObject, ARGUMENT_2.getIRI());
            return dataFactory.getSWRLDifferentIndividualsAtom(arg1, arg2);
        }
        throw new RuntimeException("Don't know how to translate SWRL Atom: " + firstObject);
    }


    public SWRLAtom translate(OWLLiteral firstObject) {
        throw new RuntimeException("Unexpected literal in atom list: " + firstObject);
    }


    private SWRLIArgument translateSWRLAtomIObject(IRI mainIRI, IRI argPredicateIRI) {
        IRI argIRI = consumer.getResourceObject(mainIRI, argPredicateIRI, true);
        if (argIRI != null) {
            if (consumer.isSWRLVariable(argIRI)) {
                return dataFactory.getSWRLVariable(argIRI);
            }
            else {
                return dataFactory.getSWRLIndividualArgument(consumer.getOWLIndividual(argIRI));
            }
        }
        else {
            throw new RuntimeException("Cannot translate SWRL Atom I-Object for " + argPredicateIRI + " Triple not found.");
        }
    }


    private SWRLDArgument translateSWRLAtomDObject(IRI mainIRI, IRI argPredicateIRI) {
        IRI argIRI = consumer.getResourceObject(mainIRI, argPredicateIRI, true);
        if (argIRI != null) {
            // Must be a variable -- double check
            if (!consumer.isSWRLVariable(argIRI)) {
                logger.info("Expected SWRL variable for SWRL Data Object: " + argIRI + "(possibly untyped)");
            }
            return dataFactory.getSWRLVariable(argIRI);
        }
        else {
            // Must be a literal
            OWLLiteral con = consumer.getLiteralObject(mainIRI, argPredicateIRI, true);
            if (con != null) {
                return dataFactory.getSWRLLiteralArgument(con);
            }
        }
        throw new IllegalStateException("Could not translate SWRL Atom D-Object");
    }


    private class SWRLAtomDObjectListItemTranslator implements ListItemTranslator<SWRLDArgument> {

        public SWRLDArgument translate(IRI firstObject) {
            return dataFactory.getSWRLVariable(firstObject);
        }


        public SWRLDArgument translate(OWLLiteral firstObject) {
            return dataFactory.getSWRLLiteralArgument(firstObject);
        }
    }
}
