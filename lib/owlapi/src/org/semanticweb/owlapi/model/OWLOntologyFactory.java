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

import org.semanticweb.owlapi.io.OWLOntologyDocumentSource;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group
 * Date: 25-Oct-2006
 * <p/>
 * An ontology factory is responsible from creating new ontologies and creating ontologies
 * from ontology document IRIs.
 */
public interface OWLOntologyFactory {

    public void setOWLOntologyManager(OWLOntologyManager owlOntologyManager);

    /**
     * Creates an (empty) ontology.
     * @param ontologyID The ID of the ontology to create. This MUST NOT BE <code>null</code>.
     * @param documentIRI The document IRI of the ontology
     * @param handler The ontology creation handler that will be notified when the
     * ontology has been created.  @return The newly created ontology
     * @return The created ontology
     * @throws OWLOntologyCreationException if the ontology could not be created.
     */
    public OWLOntology createOWLOntology(OWLOntologyID ontologyID, IRI documentIRI, OWLOntologyCreationHandler handler) throws OWLOntologyCreationException;


    /**
     * Creates and loads an <code>OWLOntology</code>.
     * @param documentSource  The document source that provides the means of getting a representation of a document.
     * @param handler A pointer to an <code>OWLOntologyCreationHandler</code> which will be notified immediately
     * after an empty ontology has been created, but before the source data is read and the ontology is loaded
     * with axioms.
     * @return The newly created and loaded ontology
     * @throws OWLOntologyCreationException if the ontology could not be created.
     */
    public OWLOntology loadOWLOntology(OWLOntologyDocumentSource documentSource, OWLOntologyCreationHandler handler) throws OWLOntologyCreationException;

    /**
     * Creates and loads an <code>OWLOntology</code>.
     * @param documentSource The document source that provides the means of getting a representation of a document.
     * @param handler A pointer to an <code>OWLOntologyCreationHandler</code> which will be notified immediately after
     * and empty ontology has been created, but before the source data is read and the ontology is loaded with axioms.
     * @param configuration A configuration object which can be used to pass various options to the loader.
     * @return The newly created and loaded ontology.
     * @throws OWLOntologyCreationException if the ontology could not be created
     */
    public OWLOntology loadOWLOntology(OWLOntologyDocumentSource documentSource, OWLOntologyCreationHandler handler, OWLOntologyLoaderConfiguration configuration) throws OWLOntologyCreationException;

    /**
     * Determines if the factory can create an ontology for the specified ontology document IRI.
     * @param documentIRI The document IRI
     * @return <code>true</code> if the factory can create an ontology given the specified document IRI,
     *         or <code>false</code> if the factory cannot create an ontology given the specified document IRI.
     */
    public boolean canCreateFromDocumentIRI(IRI documentIRI);


    /**
     * Determines if the factory can load an ontology for the specified input souce
     * @param documentSource The input source from which to load the ontology
     * @return <code>true</code> if the factory can load from the specified input source.
     */
    public boolean canLoad(OWLOntologyDocumentSource documentSource);


    /**
     * An <code>OWLOntologyCreationHandler</code> gets notified when the factory has created an empty
     * ontology (during the loading process).  This may be needed to handle features such as cyclic imports.
     * For example if OntA and OntB are ontologies and OntA imports OntB and vice versa, OntA will probably be
     * partially loaded, but then will require the loading of OntB to ensure that all entities are declared.  OntB
     * will also require the partial loading of OntA for the same reason.  The handler allows a reference to
     * an ontology which is being loaded to be obtained before loading is finished.
     */
    public interface OWLOntologyCreationHandler {

        /**
         * The factory calls this method as soon as it has created an ontology.  If the
         * factory is loading an ontology then the ontology will not have been populated with
         * axioms at this stage.
         * @param ontology The newly created ontology.
         */
        void ontologyCreated(OWLOntology ontology);

        void setOntologyFormat(OWLOntology ontology, OWLOntologyFormat format);
    }
}
