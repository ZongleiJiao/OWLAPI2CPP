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

package org.semanticweb.owlapi.expression;

import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.util.BidirectionalShortFormProvider;

/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 28-Nov-2007<br><br>
 * <p/>
 * An entity checker that maps from string to entities using a bidirectional
 * short form provider.
 */
public class ShortFormEntityChecker implements OWLEntityChecker {

    private BidirectionalShortFormProvider shortFormProvider;


    /**
     * Creates a short form entity checker, which uses the specified bidirectional
     * short form provider to map entity name strings to entities.
     *
     * @param shortFormProvider The BidirectionalShortFormProvider that should be
     *                          used to perform the required mapping.
     */
    public ShortFormEntityChecker(BidirectionalShortFormProvider shortFormProvider) {
        this.shortFormProvider = shortFormProvider;
    }

    public OWLClass getOWLClass(String name) {
        for (OWLEntity ent : shortFormProvider.getEntities(name)) {
            if (ent.isOWLClass()) {
                return ent.asOWLClass();
            }
        }
        return null;
    }


    public OWLDataProperty getOWLDataProperty(String name) {
        for (OWLEntity ent : shortFormProvider.getEntities(name)) {
            if (ent.isOWLDataProperty()) {
                return ent.asOWLDataProperty();
            }
        }
        return null;
    }


    public OWLDatatype getOWLDatatype(String name) {
        for (OWLEntity ent : shortFormProvider.getEntities(name)) {
            if (ent.isOWLDatatype()) {
                return ent.asOWLDatatype();
            }
        }
        return null;
    }


    public OWLNamedIndividual getOWLIndividual(String name) {
        for (OWLEntity ent : shortFormProvider.getEntities(name)) {
            if (ent.isOWLNamedIndividual()) {
                return ent.asOWLNamedIndividual();
            }
        }
        return null;
    }


    public OWLObjectProperty getOWLObjectProperty(String name) {
        for (OWLEntity ent : shortFormProvider.getEntities(name)) {
            if (ent.isOWLObjectProperty()) {
                return ent.asOWLObjectProperty();
            }
        }
        return null;
    }

    public OWLAnnotationProperty getOWLAnnotationProperty(String name) {
        for(OWLEntity ent : shortFormProvider.getEntities(name)) {
            if(ent.isOWLAnnotationProperty()) {
                return ent.asOWLAnnotationProperty();
            }
        }
        return null;
    }
}

