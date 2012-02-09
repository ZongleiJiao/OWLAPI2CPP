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

package org.coode.owlapi.latex;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLClassExpressionVisitor;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataCardinalityRestriction;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectCardinalityRestriction;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectHasSelf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;

/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 15-Jun-2007<br><br>
 */
@SuppressWarnings("unused")
public class LatexBracketChecker implements OWLClassExpressionVisitor {

    private boolean requiresBracket;

    private static LatexBracketChecker instance = new LatexBracketChecker();

    private LatexBracketChecker() {

    }

    public void visit(OWLObjectIntersectionOf node) {
        requiresBracket = true;
    }

    public void visit(OWLDataAllValuesFrom node) {
        requiresBracket = true;
    }

    public void visit(OWLDataCardinalityRestriction node) {
        requiresBracket = true;
    }

    public void visit(OWLDataSomeValuesFrom node) {
        requiresBracket = true;
    }

    public void visit(OWLDataHasValue node) {
        requiresBracket = true;
    }

    public void visit(OWLObjectAllValuesFrom node) {
        requiresBracket = true;
    }

    public void visit(OWLObjectCardinalityRestriction node) {
        requiresBracket = true;
    }

    public void visit(OWLObjectSomeValuesFrom node) {
        requiresBracket = true;
    }

    public void visit(OWLObjectHasValue node) {
        requiresBracket = true;
    }

    public void visit(OWLObjectComplementOf node) {
        requiresBracket = false;
    }

    public void visit(OWLObjectUnionOf node) {
        requiresBracket = true;
    }

    public void visit(OWLClass node) {
        requiresBracket = false;
    }

    public void visit(OWLObjectOneOf node) {
        requiresBracket = true;
    }


    public void visit(OWLDataExactCardinality desc) {
        requiresBracket = true;
    }


    public void visit(OWLDataMaxCardinality desc) {
        requiresBracket = true;
    }


    public void visit(OWLDataMinCardinality desc) {
        requiresBracket = true;
    }


    public void visit(OWLObjectExactCardinality desc) {
        requiresBracket = true;
    }


    public void visit(OWLObjectMaxCardinality desc) {
        requiresBracket = true;
    }


    public void visit(OWLObjectMinCardinality desc) {
        requiresBracket = true;
    }


    public void visit(OWLObjectHasSelf owlHasSelf) {
        requiresBracket = true;
    }


    public static boolean requiresBracket(OWLClassExpression classExpression) {
        
        instance.requiresBracket = true;
        classExpression.accept(instance);
        return instance.requiresBracket;

    }
}
