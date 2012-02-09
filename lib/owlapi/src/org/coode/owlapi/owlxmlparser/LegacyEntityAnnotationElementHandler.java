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

package org.coode.owlapi.owlxmlparser;

import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 21-May-2009
 */
public class LegacyEntityAnnotationElementHandler extends AbstractOWLAxiomElementHandler {

    private OWLEntity entity;

    private OWLAnnotation annotation;

    public LegacyEntityAnnotationElementHandler(OWLXMLParserHandler handler) {
        super(handler);
    }

    @Override
	protected OWLAxiom createAxiom() throws OWLXMLParserException {
        OWLAnnotationAssertionAxiom toReturn = getOWLDataFactory().getOWLAnnotationAssertionAxiom(annotation.getProperty(), entity.getIRI(), annotation.getValue());
        annotation = null;
        entity = null;
		return toReturn;
    }

    @Override
	public void handleChild(AbstractClassExpressionElementHandler handler) throws OWLXMLParserException {
        entity = handler.getOWLObject().asOWLClass();
    }

    @Override
	public void handleChild(OWLDataPropertyElementHandler handler) throws OWLXMLParserException {
        entity = handler.getOWLObject().asOWLDataProperty();
    }

    @Override
	public void handleChild(OWLIndividualElementHandler handler) throws OWLXMLParserException {
        entity = handler.getOWLObject();
    }

    @Override
	public void handleChild(AbstractOWLObjectPropertyElementHandler handler) throws OWLXMLParserException {
        entity = handler.getOWLObject().asOWLObjectProperty();
    }

    @Override
	public void handleChild(OWLAnnotationElementHandler handler) throws OWLXMLParserException {
        if (entity == null) {
            super.handleChild(handler);
        }
        else {
            annotation = handler.getOWLObject();
        }
    }
}

